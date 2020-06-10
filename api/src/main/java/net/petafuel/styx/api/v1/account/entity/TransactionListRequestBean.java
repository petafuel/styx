package net.petafuel.styx.api.v1.account.entity;

import net.petafuel.styx.api.v1.account.control.StringDateHelper;
import net.petafuel.styx.api.validator.ValidateDateFormat;
import net.petafuel.styx.api.validator.ValidateString;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.QueryParam;
import java.text.ParseException;
import java.util.Date;


public class TransactionListRequestBean {
    @QueryParam("dateFrom")
    @ValidateDateFormat(message = "dateFrom requires date format of yyyy-MM-dd")
    private String dateFrom;

    @QueryParam("dateTo")
    @ValidateDateFormat(message = "dateTo requires date format of yyyy-MM-dd")
    private String dateTo;

    @NotNull
    @NotBlank
    @ValidateString(allowedValues = {"booked", "pending", "both", "information"}, message = "bookingStatus must be one of 'booked', 'pending', 'both', 'information'")
    @QueryParam("bookingStatus")
    private String bookingStatus;

    public Date getDateFrom() {
        try {
            return dateFrom != null ? StringDateHelper.fromString(dateFrom) : null;
        } catch (ParseException e) {
            throw new SerializerException("Unable to serialize query parameter dateFrom to a valid date");
        }
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateTo() {
        try {
            return dateTo != null ? StringDateHelper.fromString(dateTo) : null;
        } catch (ParseException e) {
            throw new SerializerException("Unable to serialize query parameter dateFrom to a valid date");
        }
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }
}
