package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.BulkPaymentInitiationJsonRequest;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BulkPaymentInitiationJsonRequestSerializer implements JsonSerializer<BulkPaymentInitiationJsonRequest>{

    @Override
    public JsonElement serialize(BulkPaymentInitiationJsonRequest src, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject result = new JsonObject();
        JsonObject debtorAccount = new JsonObject();
        JsonArray payments = new JsonArray();
        Account debtor = src.getPayments().get(0).getDebtor();
        debtorAccount.addProperty(XS2AJsonKeys.CURRENCY.value(), debtor.getCurrency().toString());
        debtorAccount.addProperty(debtor.getType().getJsonKey(), debtor.getIdentifier());

        for (Payment payment: src.getPayments()) {

            JsonObject paymentObj = new JsonObject();
            JsonObject instructedAmountObj = new JsonObject();
            JsonObject creditorAccountObj = new JsonObject();
            Account creditor = payment.getCreditor();
            paymentObj.addProperty("endToEndIdentification", payment.getEndToEndIdentification());
            instructedAmountObj.addProperty("amount", payment.getAmount());
            instructedAmountObj.addProperty(XS2AJsonKeys.CURRENCY.value(), payment.getCurrency().toString());
            paymentObj.add("instructedAmount", instructedAmountObj);
            creditorAccountObj.addProperty(XS2AJsonKeys.CURRENCY.value(), creditor.getCurrency().toString());
            creditorAccountObj.addProperty(creditor.getType().getJsonKey(), creditor.getIdentifier());
            paymentObj.add("creditorAccount", creditorAccountObj);
            paymentObj.addProperty("creditorName", payment.getCreditor().getName());
            paymentObj.addProperty("remittanceInformationUnstructured", payment.getRemittanceInformationUnstructured());
            payments.add(paymentObj);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate;
        if (src.getRequestedExecutionDate() != null) {
            formattedDate = format.format(src.getRequestedExecutionDate());
        } else {
            formattedDate = format.format(new Date());
        }

        result.addProperty("batchBookingPreferred", Boolean.toString(src.isBatchBookingPreferred()));
        result.addProperty("requestedExecutionDate", formattedDate);
        result.add("debtorAccount", debtorAccount);
        result.add("payments", payments);

        return result;
    }

}
