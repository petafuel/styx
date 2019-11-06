package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import net.petafuel.styx.core.xs2a.XS2APaymentInitiationRequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.BulkPaymentInitiationJsonRequestSerializer;
import java.util.Date;
import java.util.List;

public class BulkPaymentInitiationJsonRequest extends XS2APaymentInitiationRequest {

    /** Body attributes */
    private List<Payment> payments;
    private boolean batchBookingPreferred;
    private Date requestedExecutionDate;

    public BulkPaymentInitiationJsonRequest(PaymentProduct paymentProduct, List<Payment> payments, PSU psu, boolean batchBookingPreferred) {
        super(paymentProduct, PaymentService.BULK_PAYMENTS, psu);
        this.payments = payments;
        this.batchBookingPreferred = batchBookingPreferred;
    }

    @Override
    public String getRawBody() {

        JsonSerializer serializer = new BulkPaymentInitiationJsonRequestSerializer();
        Gson gson = new GsonBuilder().registerTypeAdapter(this.getClass(), serializer).create();
        return gson.toJson(this);
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void setPayments(List<Payment> payments) {
        this.payments = payments;
    }

    public boolean isBatchBookingPreferred() {
        return batchBookingPreferred;
    }

    public void setBatchBookingPreferred(boolean batchBookingPreferred) {
        this.batchBookingPreferred = batchBookingPreferred;
    }

    public Date getRequestedExecutionDate() {
        return requestedExecutionDate;
    }

    public void setRequestedExecutionDate(Date requestedExecutionDate) {
        this.requestedExecutionDate = requestedExecutionDate;
    }
}
