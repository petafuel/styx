package net.petafuel.styx.core.xs2a.factory;

import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PSUData;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;

import java.util.Date;

/**
 * this is a general container class to hold all information a xs2aRequestFactory would need to initialise a Request
 *
 * @see XS2ARequestFactory
 * also used by the IOProcessor
 */
public class XS2AFactoryInput {
    private PSU psu;
    private InitializablePayment payment;
    private PaymentProduct paymentProduct;
    private PaymentService paymentService;
    private String paymentId;
    private String authorisationId;

    private String transactionId;
    private String consentId;
    private String accountId;
    private Consent consent;
    private String bookingStatus;
    private Date dateFrom;
    private Date dateTo;
    private Boolean withBalance;
    private String entryReferenceFrom;
    private Boolean deltaList;

    private String scaAuthenticationData;
    private String authorisationMethodId;
    private PSUData psuData;

    public PSUData getPsuData() {
        return psuData;
    }

    public void setPsuData(PSUData psuData) {
        this.psuData = psuData;
    }

    public String getAuthorisationMethodId() {
        return authorisationMethodId;
    }

    public void setAuthorisationMethodId(String authorisationMethodId) {
        this.authorisationMethodId = authorisationMethodId;
    }

    public String getScaAuthenticationData() {
        return scaAuthenticationData;
    }

    public void setScaAuthenticationData(String scaAuthenticationData) {
        this.scaAuthenticationData = scaAuthenticationData;
    }

    public Consent getConsent() {
        return consent;
    }

    public void setConsent(Consent consent) {
        this.consent = consent;
    }

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAuthorisationId() {
        return authorisationId;
    }

    public void setAuthorisationId(String authorisationId) {
        this.authorisationId = authorisationId;
    }

    public PSU getPsu() {
        return psu;
    }

    public void setPsu(PSU psu) {
        this.psu = psu;
    }

    public InitializablePayment getPayment() {
        return payment;
    }

    public void setPayment(InitializablePayment payment) {
        this.payment = payment;
    }

    public PaymentProduct getPaymentProduct() {
        return paymentProduct;
    }

    public void setPaymentProduct(PaymentProduct paymentProduct) {
        this.paymentProduct = paymentProduct;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }

    public void setDateTo(Date dateTo) {
        this.dateTo = dateTo;
    }

    public Boolean getWithBalance() {
        return withBalance;
    }

    public void setWithBalance(Boolean withBalance) {
        this.withBalance = withBalance;
    }

    public String getEntryReferenceFrom() {
        return entryReferenceFrom;
    }

    public void setEntryReferenceFrom(String entryReferenceFrom) {
        this.entryReferenceFrom = entryReferenceFrom;
    }

    public Boolean getDeltaList() {
        return deltaList;
    }

    public void setDeltaList(Boolean deltaList) {
        this.deltaList = deltaList;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}
