package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http;

import net.petafuel.styx.core.xs2a.contracts.XS2AGetRequest;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2AQueryParameter;

import java.util.Date;

public class ReadTransactionsRequest extends XS2AGetRequest {
    /**
     * Headers
     */

    @XS2AHeader("header")
    private String accept;

    /**
     * Body
     */

    private String accountId;

    @XS2AQueryParameter("bookingStatus")
    private String bookingStatus;

    @XS2AQueryParameter("dateFrom")
    private Date fromDate;

    @XS2AQueryParameter("dateTo")
    private Date toDate;

    @XS2AQueryParameter("withBalance")
    private boolean withBalance;

    // optional if supported by API Provider
    @XS2AQueryParameter("entryReferenceFrom")
    private String entryReferenceFrom;
    // optional if supported by API Provider
    @XS2AQueryParameter("deltaList")
    private boolean deltaList;


    public ReadTransactionsRequest(String accountId, String consentId, String bookingStatus, Date fromDate, Date toDate) {
        super(consentId);
        this.accountId = accountId;
        this.bookingStatus = bookingStatus;
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public ReadTransactionsRequest(String accountId, String consentId, String bookingStatus, boolean deltaList) {
        super(consentId);
        this.accountId = accountId;
        this.bookingStatus = bookingStatus;
        this.deltaList = deltaList;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getAccept() {
        return accept;
    }

    public void setAccept(String accept) {
        this.accept = accept;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
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
}
