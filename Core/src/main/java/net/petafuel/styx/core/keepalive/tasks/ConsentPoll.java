package net.petafuel.styx.core.keepalive.tasks;

import com.google.gson.JsonObject;
import net.petafuel.styx.core.keepalive.contracts.WorkableTask;
import net.petafuel.styx.core.keepalive.entities.TaskFinalFailureCode;
import net.petafuel.styx.core.keepalive.entities.TaskFinalFailureException;
import net.petafuel.styx.core.keepalive.entities.TaskRetryFailureException;
import net.petafuel.styx.core.keepalive.entities.TaskSuccessException;
import net.petafuel.styx.core.persistence.layers.PersistentConsent;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.CSInterface;
import net.petafuel.styx.core.xs2a.contracts.IBerlinGroupSigner;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.GetConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.StatusConsentRequest;
import net.petafuel.styx.core.xs2a.utils.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.UUID;
import java.util.stream.IntStream;

/**
 * Task to poll for a consent
 */
public class ConsentPoll extends WorkableTask {

    private static final Logger LOG = LogManager.getLogger(ConsentPoll.class);
    private UUID id;
    private String signature;
    private Consent consent;
    private CSInterface csInterface;
    private PersistentConsent persistentConsent;
    private int maxRetries;
    private int timeoutBetweenRetries;


    public ConsentPoll(Consent consent, CSInterface csInterface) {
        this.consent = consent;
        this.csInterface = csInterface;
        id = UUID.randomUUID();
        signature = id.toString() + "-" + consent.getId();
        maxRetries = Integer.parseInt(Config.getInstance().getProperties().getProperty("keepalive.tasks.consentpoll.amountRetries", "12"));
        timeoutBetweenRetries = Integer.parseInt(Config.getInstance().getProperties().getProperty("keepalive.tasks.consentpoll.timoutBetweenRetriesMS", "5000"));
        persistentConsent = new PersistentConsent();
    }

    public void execute() {

        LOG.debug("Executing Task id:{} signature:{}", getId(), getSignature());
        Consent currentConsent = persistentConsent.get(consent);
        if (currentConsent == null) {
            throw new TaskFinalFailureException("Consent queued for polling does not exist in the styx database, cannot poll", TaskFinalFailureCode.POLL_ON_NOT_EXISTING_CONSENT);
        } else if (currentConsent.getState() == Consent.State.VALID) {
            LOG.debug("Consent with id {} is already on state valid, no polling required", consent.getId());
            //TODO maybe define exit codes for monitoring -> get saved as exit code in the database
            return;
        }

        StatusConsentRequest statusConsentRequest = new StatusConsentRequest();
        statusConsentRequest.setConsentId(consent.getId());
        statusConsentRequest.setPsu(consent.getPsu());

        try {
            Iterator<Integer> retryIterator = IntStream.range(0, maxRetries).iterator();
            while (retryIterator.hasNext()) {
                try {
                    Consent.State currentStatus = csInterface.getStatus(statusConsentRequest);
                    if (currentStatus == Consent.State.VALID) {
                        LOG.debug("Consent is valid, SCA was successful");
                        throw new TaskSuccessException("Consent status changed to valid");
                    } else if (currentStatus != Consent.State.RECEIVED) {
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
                    LOG.error("Unable to sleep until next retry");
                    Thread.currentThread().interrupt();
                }
            }
        } catch (TaskSuccessException success) {
            LOG.info("Polling was successful -> getting consent informations");
            GetConsentRequest getConsentRequest = new GetConsentRequest();
            getConsentRequest.setConsentId(consent.getId());
            try {
                Consent aspspConsent = csInterface.getConsent(getConsentRequest);
                currentConsent.setFrequencyPerDay(aspspConsent.getFrequencyPerDay());
                currentConsent.setValidUntil(aspspConsent.getValidUntil());
                currentConsent.setState(aspspConsent.getState());
                persistentConsent.update(currentConsent);
                LOG.info("Successfully updated consent");
                return;
            } catch (BankRequestFailedException e) {
                LOG.error("Unable to get consent information after polling was successful -> task marked as failed");
                throw new TaskRetryFailureException("Unable to get consent information after polling was successful", e);
            }
        } catch (Exception unknown) {
            throw new TaskRetryFailureException("An unknown exception occured while executing the task: " + unknown.getMessage(), unknown);
        }
        throw new TaskRetryFailureException("Task ultimately failed, no success condition was satisfied", new Throwable());
    }

    @Override
    public JsonObject getGoal() {
        String fullClassName = this.csInterface.getClass().getName();
        String[] classParts = fullClassName.split("\\.");
        String className = classParts[classParts.length - 1];
        String versionPackage = classParts[classParts.length - 2];
        String standardPackage = classParts[classParts.length - 3];
        String url = ((BasicService) this.csInterface).getUrl();
        String signerName = ((BasicService) this.csInterface).getSigner().getClass().getSimpleName();
        JsonObject jsonGoal = new JsonObject();
        jsonGoal.addProperty("standard", standardPackage);
        jsonGoal.addProperty("version", versionPackage);
        jsonGoal.addProperty("class", className);
        jsonGoal.addProperty("signer", signerName);
        jsonGoal.addProperty("url", url);
        jsonGoal.addProperty("consentId", this.consent.getId());
        return jsonGoal;
    }

    @Override
    public WorkableTask buildFromRecovery(JsonObject goal) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String standardPackage = "net.petafuel.styx.core.xs2a.standards"
                + ("." + goal.get("standard").getAsString())
                + ("." + goal.get("version").getAsString());

        String csServiceClass = standardPackage + ("." + goal.get("class").getAsString());
        String signerClass = standardPackage + ("." + goal.get("signer").getAsString());

        String url = goal.get("url").getAsString();
        String consentId = goal.get("consentId").getAsString();

        Class clazzCsInterface = Class.forName(csServiceClass);
        Class clazzSigner = Class.forName(signerClass);
        IBerlinGroupSigner signer = (IBerlinGroupSigner) clazzSigner.getDeclaredConstructor().newInstance();
        CSInterface csInterface = (CSInterface) clazzCsInterface.getDeclaredConstructor(String.class, IBerlinGroupSigner.class).newInstance(url, signer);
        Consent getFromDB = new Consent();
        getFromDB.setId(consentId);
        return new ConsentPoll(new PersistentConsent().get(getFromDB), csInterface);
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getSignature() {
        return signature;
    }
}
