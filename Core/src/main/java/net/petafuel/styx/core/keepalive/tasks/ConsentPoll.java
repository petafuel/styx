package net.petafuel.styx.core.keepalive.tasks;

import net.petafuel.styx.core.keepalive.contracts.WorkableTask;
import net.petafuel.styx.core.keepalive.entities.TaskFailureException;
import net.petafuel.styx.core.keepalive.entities.TaskSuccessException;
import net.petafuel.styx.core.persistence.layers.PersistentConsent;
import net.petafuel.styx.core.xs2a.contracts.CSInterface;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.GetConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.StatusConsentRequest;
import net.petafuel.styx.core.xs2a.utils.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.UUID;
import java.util.stream.IntStream;

public class ConsentPoll extends WorkableTask {

    private static final Logger LOG = LogManager.getLogger(ConsentPoll.class);
    private UUID id;
    private String signature;
    private Consent consent;
    private CSInterface csInterface;
    private PersistentConsent persistentConsent;
    private boolean consentExsists;
    private int maxRetries;
    private int timeoutBetweenRetries;


    public ConsentPoll(Consent consent, CSInterface csInterface) {
        this.consent = consent;
        this.csInterface = csInterface;
        id = UUID.randomUUID();
        signature = consent.getId();
        maxRetries = Integer.parseInt(Config.getInstance().getProperties().getProperty("keepalive.tasks.consentpoll.amountRetries", "12"));
        timeoutBetweenRetries = Integer.parseInt(Config.getInstance().getProperties().getProperty("keepalive.tasks.consentpoll.timoutBetweenRetriesMS", "5000"));
        persistentConsent = new PersistentConsent();
        consentExsists = true;
    }

    public void execute() {
        LOG.debug("Executing Task id:{} signature:{}", getId(), getSignature());
        Consent currentConsent = persistentConsent.get(consent);
        if (currentConsent == null) {
            consentExsists = false;
        } else if (currentConsent.getState() == Consent.State.VALID) {
            LOG.debug("Consent with id {} is already on state valid, no polling required", consent.getId());
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
                Consent validConsent = csInterface.getConsent(getConsentRequest);
                if (consentExsists) {
                    persistentConsent.update(validConsent);
                } else {
                    persistentConsent.create(validConsent);
                }
                LOG.info("Successfully updated consent");
            } catch (BankRequestFailedException e) {
                LOG.error("Unable to get consent information after polling was successful -> task marked as failed");
                throw new TaskFailureException("Unable to get consent information after polling was successful", e);
            }
        } catch (Exception unknown) {
            throw new TaskFailureException("An unknown exception occured while executing the task: " + unknown.getMessage(), unknown);
        }
        throw new TaskFailureException("Task ultimately failed, no success condition was satisfied", new Throwable());
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
