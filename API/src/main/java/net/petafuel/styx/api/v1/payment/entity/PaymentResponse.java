package net.petafuel.styx.api.v1.payment.entity;

import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.bind.annotation.JsonbProperty;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class PaymentResponse {

    private static final Logger LOG = LogManager.getLogger(PaymentResponse.class);

    @JsonbProperty("transactionStatus")
    private TransactionStatus transactionStatus;

    @JsonbProperty("paymentId")
    private String paymentId;

    @JsonbProperty("links")
    private Map<SCA.LinkType, String> links;

    @JsonbProperty("psuMessage")
    private String psuMessage;

    public PaymentResponse(InitiatedPayment initiatedPayment) {
        transactionStatus = initiatedPayment.getStatus();
        paymentId = initiatedPayment.getPaymentId();
        links = initiatedPayment.getSca().getLinks();
        psuMessage = initiatedPayment.getSca().getPsuMessage();
        if (Boolean.parseBoolean(System.getProperty("styx.proxy.enabled"))) {
            for (Map.Entry<SCA.LinkType, String> entry : links.entrySet()) {
                if (entry.getKey().equals(SCA.LinkType.SCA_REDIRECT) || entry.getKey().equals(SCA.LinkType.SCA_OAUTH)) {
                    continue;
                }
                try {
                    URL aspspUrl = new URL(entry.getValue());
                    URL styxWrapper = new URL(System.getProperty("styx.proxy.schema"), System.getProperty("styx.proxy.hostname"), Integer.parseInt(System.getProperty("styx.proxy.port")), aspspUrl.getFile());
                    entry.setValue(styxWrapper.toString());
                } catch (MalformedURLException e) {
                    LOG.error("Unable to wrap aspsp rest response links object to styx urls", e);
                }
            }
        }
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public Map<SCA.LinkType, String> getLinks() {
        return links;
    }

    public void setLinks(Map<SCA.LinkType, String> links) {
        this.links = links;
    }

    public String getPsuMessage() {
        return psuMessage;
    }

    public void setPsuMessage(String psuMessage) {
        this.psuMessage = psuMessage;
    }
}
