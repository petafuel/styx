package net.petafuel.styx.keepalive.tasks;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.ioprocessing.IOProcessor;
import net.petafuel.styx.core.ioprocessing.StyxExecutionContext;
import net.petafuel.styx.core.persistence.PersistenceEmptyResultSetException;
import net.petafuel.styx.core.persistence.layers.PersistentOAuthSession;
import net.petafuel.styx.core.xs2a.contracts.PISRequest;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.exceptions.OAuthTokenExpiredException;
import net.petafuel.styx.core.xs2a.factory.PISRequestFactory;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import net.petafuel.styx.core.xs2a.oauth.OAuthService;
import net.petafuel.styx.core.xs2a.oauth.entities.OAuthSession;
import net.petafuel.styx.keepalive.contracts.Properties;
import net.petafuel.styx.keepalive.contracts.WorkableTask;
import net.petafuel.styx.keepalive.entities.TaskFinalFailureCode;
import net.petafuel.styx.keepalive.entities.TaskFinalFailureException;
import net.petafuel.styx.spi.paymentstatushook.PaymentStatusHookService;
import net.petafuel.styx.spi.paymentstatushook.api.HookStatus;
import net.petafuel.styx.spi.paymentstatushook.spi.PaymentStatusHookSPI;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PaymentStatusPoll extends WorkableTask {
    private static final Logger LOG = LogManager.getLogger(PaymentStatusPoll.class);

    private int maxRequestFailures;
    private int currentRequestFailures;
    private PaymentStatusHookSPI hookImpl;
    private XS2AStandard xs2AStandard;
    private PISRequest paymentStatusRequest;
    private String signature;
    private XS2AFactoryInput xs2aFactoryInput;
    private long startTimestamp;
    private long maxExecutionTime;
    private long timeoutBetweenRetries;
    private ScheduledFuture<?> future;
    private InitializablePayment payment = null;
    private UUID xRequestId;
    private IOProcessor ioProcessor;

    /**
     * empty constructor for recovery
     */
    public PaymentStatusPoll() {
    }

    public PaymentStatusPoll(XS2AFactoryInput xs2aFactoryInput, String bic, UUID xRequestId) {
        ThreadContext.put("requestUUID", xRequestId.toString());
        this.xs2aFactoryInput = xs2aFactoryInput;
        hookImpl = new PaymentStatusHookService().provider(System.getProperty(Properties.PAYMENT_STATUS_HOOK_SERVICE,
                "net.petafuel.styx.spi.paymentstatushook.impl.PaymentStatusHookImpl"));
        hookImpl.initialize(xs2aFactoryInput.getPaymentService(), xs2aFactoryInput.getPaymentProduct(),
                xs2aFactoryInput.getPaymentId(), bic);
        signature = getId() + "-" + xs2aFactoryInput.getPaymentId();
        maxExecutionTime = Long
                .parseLong(System.getProperty(Properties.PAYMENT_STATUS_POLL_MAX_EXECUTION_TIME, "60000"));
        maxRequestFailures = Integer.parseInt(System.getProperty(Properties.PAYMENT_STATUS_MAX_REQUEST_FAILURES, "3"));
        timeoutBetweenRetries = Long
                .parseLong(System.getProperty(Properties.PAYMENT_STATUS_TIMEOUT_BETWEEN_RETRIES, "2000"));
        currentRequestFailures = 0;
        this.xRequestId = xRequestId;

        String authorisationHeader = this.checkAccessToken(xRequestId);

        try {
            xs2AStandard = new SAD().getBankByBIC(bic,
                    Boolean.parseBoolean(System.getProperty("styx.api.sad.sandbox.enabled", "true")));
        } catch (BankLookupFailedException | BankNotFoundException e) {
            throw new TaskFinalFailureException(e.getMessage());
        }

        this.ioProcessor = new IOProcessor(xs2AStandard, StyxExecutionContext.KEEP_ALIVE);

        this.ioProcessor.modifyInput(xs2aFactoryInput);
        paymentStatusRequest = new PISRequestFactory().create(xs2AStandard.getRequestClassProvider().paymentStatus(),
                xs2aFactoryInput);
        paymentStatusRequest.setAuthorization(authorisationHeader);
        paymentStatusRequest.setPsu(xs2aFactoryInput.getPsu());

        // Fetch payment once on Task startup - some ASPSP do not allow payment
        // retrieval after successful SCA
        PISRequest paymentRetrievalRequest = new PISRequestFactory()
                .create(xs2AStandard.getRequestClassProvider().paymentRetrieval(), xs2aFactoryInput);
        paymentRetrievalRequest.setAuthorization(authorisationHeader);

        try {
            this.ioProcessor.modifyRequest(paymentRetrievalRequest, xs2aFactoryInput);
            payment = xs2AStandard.getPis().getPayment(paymentRetrievalRequest);
            this.ioProcessor.modifyResponse(payment, xs2aFactoryInput);
        } catch (BankRequestFailedException e) {
            LOG.error("Failed to retrieve payment on task startup BankRequestFailedException", e);
        }
    }

    @Override
    public String getSignature() {
        return signature;
    }

    @Override
    public void execute() throws Throwable {
        startTimestamp = new Date().getTime();
        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        try {
            // Execute polling method at a fixed rate
            future = executorService.scheduleAtFixedRate(this::poll, 0, timeoutBetweenRetries, TimeUnit.MILLISECONDS);
            // wait until the future gets canceled
            future.get();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new TaskFinalFailureException(e.getMessage(), TaskFinalFailureCode.EXECUTING_TASK_INTERRUPTED);
        } catch (ExecutionException e) {
            // throw cause
            throw e.getCause();
        } catch (CancellationException e) {
            // log task was cancelled
            LOG.debug("Task was canceled because of success/failure", e);
        } finally {
            executorService.shutdown();
        }
        LOG.debug("PaymentStatusPoll Task finished");
    }

    private void poll() {
        if (((new Date().getTime()) - startTimestamp) >= maxExecutionTime) {
            throw new TaskFinalFailureException(String.format("Max execution time of %s was reached", maxExecutionTime),
                    TaskFinalFailureCode.EXCEEDED_MAX_EXECUTION_TIME);
        } else if (maxRequestFailures != 0 && currentRequestFailures >= maxRequestFailures) {
            throw new TaskFinalFailureException(String.format("Max xs2a request failures reached %s out of %s",
                    currentRequestFailures, maxRequestFailures),
                    TaskFinalFailureCode.EXCEEDED_MAX_XS2A_REQUEST_FAILURES);
        }

        HookStatus hookStatus;
        PaymentStatus paymentStatus;
        try {
            String authorisationHeader = this.checkAccessToken(xRequestId);
            paymentStatusRequest.setAuthorization(authorisationHeader);
            this.ioProcessor.modifyRequest(paymentStatusRequest, xs2aFactoryInput);
            paymentStatus = xs2AStandard.getPis().getPaymentStatus(paymentStatusRequest);
            this.ioProcessor.modifyResponse(paymentStatus, xs2aFactoryInput);
            hookStatus = hookImpl.onStatusUpdate(paymentStatus);
        } catch (BankRequestFailedException e) {
            currentRequestFailures += 1;
            LOG.warn("Request towards the ASPSP failed. maxRequestFailures={}, currentRequestFailures={}",
                    maxRequestFailures, currentRequestFailures, e);
            return;
        }
        if (hookStatus == HookStatus.SUCCESS) {
            LOG.info("PaymentStatus Hook was successful, calling Service Provider onSuccess and cancel task execution");
            hookImpl.onSuccess(payment);
            future.cancel(true);
        } else if (hookStatus == HookStatus.FAILURE) {
            LOG.info("PaymentStatus Hook failed, calling Service Provider onFailure and cancel task execution");
            hookImpl.onFailure(payment);
            future.cancel(true);
        }
    }

    @Override
    public JsonObject getGoal() {
        JsonObjectBuilder jsonObjectBuilder = Json.createObjectBuilder()
                .add("paymentService", xs2aFactoryInput.getPaymentService().name())
                .add("paymentProduct", xs2aFactoryInput.getPaymentProduct().name())
                .add("paymentId", xs2aFactoryInput.getPaymentId())
                .add("bic", xs2AStandard.getAspsp().getBic())
                .add("xRequestId", String.valueOf(xRequestId));

        if (xs2aFactoryInput.getPsu() != null && xs2aFactoryInput.getPsu().getId() != null) {
            jsonObjectBuilder.add("psuId", xs2aFactoryInput.getPsu().getId());
        }

        if (paymentStatusRequest.getAuthorization() != null) {
            jsonObjectBuilder.add("authorisationHeader", paymentStatusRequest.getAuthorization());
        }
        return jsonObjectBuilder.build();
    }

    @Override
    public WorkableTask buildFromRecovery(JsonObject goal) {
        XS2AFactoryInput input = new XS2AFactoryInput();
        input.setPaymentService(PaymentService.valueOf(goal.getString("paymentService")));
        input.setPaymentProduct(PaymentProduct.valueOf(goal.getString("paymentProduct")));
        input.setPaymentId(goal.getString("paymentId"));
        input.setPsu(new PSU(goal.getString("psuId", null)));
        return new PaymentStatusPoll(input, goal.getString("bic"), UUID.fromString(goal.getString("xRequestId")));
    }

    /**
     * Method to check if a accessToken is available and still valid
     *
     * @param xRequestId xRequestId
     * @return null|String
     */
    private String checkAccessToken(UUID xRequestId) {
        try {
            OAuthSession oAuthSession = PersistentOAuthSession.getByXRequestId(xRequestId);
            if (oAuthSession.getAccessToken() != null &&
                    oAuthSession.getAccessTokenExpiresAt().before(new Date()) &&
                    oAuthSession.getRefreshTokenExpiresAt().after(new Date())) {
                return OAuthService.refreshToken(oAuthSession).getAccessToken();
            } else {
                return oAuthSession.getAccessToken();
            }
        } catch (PersistenceEmptyResultSetException e) {
            return null;
        } catch (OAuthTokenExpiredException e) {
            LOG.error("Refresh token expired, cannot refresh access token for xRequestId={}", xRequestId, e);
            throw new TaskFinalFailureException("Refresh token expired");
        }
    }
}
