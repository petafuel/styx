package net.petafuel.styx.keepalive.tasks;

import net.petafuel.styx.core.persistence.layers.PersistentConsent;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.CSInterface;
import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.GetConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.StatusConsentRequest;
import net.petafuel.styx.keepalive.contracts.Properties;
import net.petafuel.styx.keepalive.contracts.WorkableTask;
import net.petafuel.styx.keepalive.entities.TaskFinalFailureCode;
import net.petafuel.styx.keepalive.entities.TaskFinalFailureException;
import net.petafuel.styx.keepalive.entities.TaskRetryFailureException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.stream.IntStream;


/**
 * Task to poll for a consent
 */
public final class ConsentPoll extends WorkableTask {

    private static final Logger LOG = LogManager.getLogger(ConsentPoll.class);
    private String signature;
    private Consent consent;
    private CSInterface csInterface;
    private PersistentConsent persistentConsent;
    private int maxRequestRetries;
    private int timeoutBetweenRetries;

    //Empty constructor for TaskRecovery instantiation from Reflection
    public ConsentPoll() {
    }

    public ConsentPoll(Consent consent, CSInterface csInterface) {
        this.consent = consent;
        this.csInterface = csInterface;
        signature = getId() + "-" + consent.getId();
        maxRequestRetries = Integer.parseInt(System.getProperty(Properties.TASKS_CONSENTPOLL_AMOUNT_RETRIES, "12"));
        timeoutBetweenRetries = Integer.parseInt(System.getProperty(Properties.TASKS_CONSENTPOLL_TIMEOUT_BETWEEN_RETRIES, "5000"));
        persistentConsent = new PersistentConsent();
    }

    public void execute() {

        LOG.debug("Executing Task id:{} signature:{}", getId(), getSignature());
        Consent currentConsent = persistentConsent.get(consent);
        if (currentConsent == null) {
            throw new TaskFinalFailureException("Consent queued for polling does not exist in the styx database, cannot poll", TaskFinalFailureCode.POLL_ON_NOT_EXISTING_CONSENT);
        } else if (currentConsent.getState() == Consent.State.VALID) {
            throw new TaskFinalFailureException("Consent with id " + currentConsent.getId() + " is already on state valid, no polling required", TaskFinalFailureCode.POLL_ON_ALREADY_VALID_CONSENT);
        }
        //TODO make the request type as a parameter
        StatusConsentRequest statusConsentRequest = new StatusConsentRequest(null, consent.getId(), null, null);
        statusConsentRequest.setPsu(consent.getPsu());

        Iterator<Integer> retryIterator = IntStream.range(0, maxRequestRetries).iterator();
        while (retryIterator.hasNext()) {
            if (Thread.interrupted()) {
                throw new TaskFinalFailureException("Task Thread was interrupted");
            }
            try {
                Consent.State currentStatus = this.csInterface.getStatus(statusConsentRequest);
                if (currentStatus == Consent.State.VALID) {
                    LOG.debug("Consent is valid, SCA was successful");
                    break;
                } else if (!(currentStatus.equals(Consent.State.RECEIVED) || currentStatus.equals(Consent.State.PARTIALLY_AUTHORISED))) {
                    currentConsent.setState(currentStatus);
                    persistentConsent.update(currentConsent);
                    throw new TaskFinalFailureException("Consent cannot be polled anymore due to unrecoverable status: " + currentStatus.toString(), TaskFinalFailureCode.UNRECOVERABLE_STATUS);
                }
                LOG.debug("Consent status was not valid: {}", currentStatus);

            } catch (BankRequestFailedException e) {
                LOG.warn("Trying to poll consent resulted in an error: {} status: {} retry-iteration: {}", e.getMessage(), e.getHttpStatusCode(), retryIterator.next());
            }

            try {
                Thread.sleep(timeoutBetweenRetries);
            } catch (InterruptedException e) {
                LOG.error("Task {} execution was interrupted: {}", getId(), e.getMessage());
                Thread.currentThread().interrupt();
            }
            retryIterator.next();
        }

        //TODO make GetConsentRequest class as a parameter
        GetConsentRequest getConsentRequest = new GetConsentRequest(null, consent.getId(), null, null);
        try {
            Consent aspspConsent = csInterface.getConsent(getConsentRequest);
            if (!aspspConsent.getState().equals(Consent.State.VALID)) {
                throw new TaskRetryFailureException("Upon Consent Poll completion, consent is still not authorized by PSU");
            }
            currentConsent.setFrequencyPerDay(aspspConsent.getFrequencyPerDay());
            currentConsent.setValidUntil(aspspConsent.getValidUntil());
            currentConsent.setState(aspspConsent.getState());
            persistentConsent.update(currentConsent);
            LOG.info("Successfully updated consent");
        } catch (BankRequestFailedException e) {
            LOG.error("Unable to get consent information after polling was successful -> task marked as failed");
            throw new TaskRetryFailureException("Unable to get consent information after polling was successful", e);
        }
    }

    @Override
    public JsonObject getGoal() {
        final String fullClassName = this.csInterface.getClass().getName();
        String[] classParts = fullClassName.split("\\.");
        String className = classParts[classParts.length - 1];
        String versionPackage = classParts[classParts.length - 2];
        String standardPackage = classParts[classParts.length - 3];
        String url = ((BasicService) this.csInterface).getUrl();
        String signerName = ((BasicService) this.csInterface).getSigner().getClass().getSimpleName();
        return Json.createObjectBuilder()
                .add("standard", standardPackage)
                .add("version", versionPackage)
                .add("class", className)
                .add("signer", signerName)
                .add("url", url)
                .add("consentId", this.consent.getId()).build();
    }

    @Override
    //class names are generated and read from the styx database as trustet source
    @SuppressWarnings("squid:S1523")
    public WorkableTask buildFromRecovery(JsonObject goal) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String standardPackage = "net.petafuel.styx.core.xs2a.standards"
                + ("." + goal.getString("standard"))
                + ("." + goal.getString("version"));
        //TODO use SAD to initialize services based on the bic
        String csServiceClass = standardPackage + ("." + goal.getString("class"));
        String signerClass = standardPackage + ("." + goal.getString("signer"));

        String url = goal.getString("url");
        String consentId = goal.getString("consentId");

        Class<?> clazzCsInterface = Class.forName(csServiceClass);
        Class<?> clazzSigner = Class.forName(signerClass);
        IXS2AHttpSigner signer = (IXS2AHttpSigner) clazzSigner.getDeclaredConstructor().newInstance();
        CSInterface csInterfaceEx = (CSInterface) clazzCsInterface.getDeclaredConstructor(String.class, IXS2AHttpSigner.class).newInstance(url, signer);
        Consent getFromDB = new Consent();
        getFromDB.setId(consentId);
        return new ConsentPoll(new PersistentConsent().get(getFromDB), csInterfaceEx);
    }

    @Override
    public String getSignature() {
        return signature;
    }
}
