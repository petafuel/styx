package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AQueryParameter;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;

import java.util.Date;
import java.util.Optional;

public class ReadTransactionsRequest extends XS2ARequest {

    private String accountId;

    @XS2AQueryParameter("bookingStatus")
    private String bookingStatus;

    @XS2AQueryParameter("dateFrom")
    private Date dateFrom;

    @XS2AQueryParameter("dateTo")
    private Date dateTo;

    @XS2AQueryParameter("withBalance")
    private boolean withBalance;

    // optional if supported by API Provider
    @XS2AQueryParameter("entryReferenceFrom")
    private String entryReferenceFrom;
    // optional if supported by API Provider
    @XS2AQueryParameter("deltaList")
    private boolean deltaList;


    public ReadTransactionsRequest(String accountId, String consentId, String bookingStatus, Date dateFrom, Date dateTo) {
        this.accountId = accountId;
        this.bookingStatus = bookingStatus;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
        this.setConsentId(consentId);
    }

    public ReadTransactionsRequest(String accountId, String consentId, String bookingStatus, boolean deltaList) {
        this.accountId = accountId;
        this.bookingStatus = bookingStatus;
        this.deltaList = deltaList;
        this.setConsentId(consentId);
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
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

    public String getEntryReferenceFrom() {
        return entryReferenceFrom;
    }

    public void setEntryReferenceFrom(String entryReferenceFrom) {
        this.entryReferenceFrom = entryReferenceFrom;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public boolean isDeltaList() {
        return deltaList;
    }

    public void setDeltaList(boolean deltaList) {
        this.deltaList = deltaList;
    }

    public boolean isWithBalance() {
        return withBalance;
    }

    public void setWithBalance(boolean withBalance) {
        this.withBalance = withBalance;
    }

    @Override
    public Optional<String> getRawBody() {
        return Optional.empty();
    }
}
