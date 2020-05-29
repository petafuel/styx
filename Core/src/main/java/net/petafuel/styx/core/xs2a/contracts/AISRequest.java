package net.petafuel.styx.core.xs2a.contracts;

import net.petafuel.styx.core.xs2a.entities.Consent;

import java.util.Date;
import java.util.Optional;

public abstract class AISRequest extends XS2ARequest {
    @XS2AHeader(XS2AHeader.CONSENT_ID)
    private String consentId;

    @XS2AQueryParameter("bookingStatus")
    private String bookingStatus;

    @XS2AQueryParameter("dateFrom")
    private Date dateFrom;

    @XS2AQueryParameter("dateTo")
    private Date dateTo;

    @XS2AQueryParameter("withBalance")
    private Boolean withBalance;

    // optional if supported by API Provider
    @XS2AQueryParameter("entryReferenceFrom")
    private String entryReferenceFrom;
    // optional if supported by API Provider
    @XS2AQueryParameter("deltaList")
    private Boolean deltaList;

    private String transcationId;
    private String accountId;
    private Consent consent;

    protected AISRequest(Consent consent, String consentId, String accountId, String transactionId) {
        super();
        this.consentId = consentId;
        this.accountId = accountId;
        this.consent = consent;
        this.transcationId = transactionId;
    }

    public abstract Optional<String> getRawBody();

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }

    public String getTranscationId() {
        return transcationId;
    }

    public void setTranscationId(String transcationId) {
        this.transcationId = transcationId;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public Consent getConsent() {
        return consent;
    }

    public void setConsent(Consent consent) {
        this.consent = consent;
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

    public Boolean isWithBalance() {
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

    public Boolean isDeltaList() {
        return deltaList;
    }

    public void setDeltaList(Boolean deltaList) {
        this.deltaList = deltaList;
    }
}
