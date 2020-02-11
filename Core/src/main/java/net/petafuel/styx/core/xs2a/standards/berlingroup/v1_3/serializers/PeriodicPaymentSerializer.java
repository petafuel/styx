package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Address;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.InstructedAmount;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class PeriodicPaymentSerializer implements JsonSerializer<InitializablePayment>, JsonDeserializer<InitializablePayment> {

    @Override
    public JsonElement serialize(InitializablePayment initializablePayment, Type typeOfSrc, JsonSerializationContext context) {
        Payment payment = (Payment) initializablePayment;

        Gson gson = new GsonBuilder().registerTypeAdapter(PeriodicPayment.class, new PaymentSerializer(PaymentService.PERIODIC_PAYMENTS)).create();

        JsonObject paymentJsonObject = gson.toJsonTree(payment).getAsJsonObject();

        gson = new GsonBuilder().registerTypeAdapter(PeriodicPayment.class, new PeriodicPaymentMultipartBodySerializer()).create();
        JsonObject periodicJsonObject = gson.toJsonTree(payment).getAsJsonObject();

        //merge payment data and period data together
        periodicJsonObject.entrySet().forEach(jsonElementEntry -> paymentJsonObject.add(jsonElementEntry.getKey(), jsonElementEntry.getValue()));

        return paymentJsonObject;
    }

    @Override
    public InitializablePayment deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value());

        JsonObject debtorAccountJson = jsonObject.getAsJsonObject("debtorAccount");
        Gson accountGson = new GsonBuilder()
                .registerTypeAdapter(Account.class, new AccountSerializer())
                .create();
        Account debtorAccount = accountGson.fromJson(debtorAccountJson, Account.class);

        Gson addressGson = new GsonBuilder()
                .registerTypeAdapter(Address.class, new AddressSerializer())
                .create();

        String remittanceInformationUnstructured = jsonObject.get(XS2AJsonKeys.REMITTANCE_INFORMATION_UNSTRUCTURED.value()).getAsString();

        String endToEndIdentification = jsonObject.get(XS2AJsonKeys.END_TO_END_IDENTIFICATION.value()) != null
                && !jsonObject.get(XS2AJsonKeys.END_TO_END_IDENTIFICATION.value()).isJsonNull()
                ? jsonObject.get(XS2AJsonKeys.END_TO_END_IDENTIFICATION.value()).getAsString()
                : null;

        String amount = jsonObject.get(XS2AJsonKeys.INSTRUCTED_AMOUNT.value()).getAsJsonObject().get(XS2AJsonKeys.AMOUNT.value())
                .getAsString();

        JsonObject creditorAccountJson = jsonObject.getAsJsonObject(XS2AJsonKeys.CREDITOR_ACCOUNT.value());
        creditorAccountJson.addProperty("name", jsonObject.get(XS2AJsonKeys.CREDITOR_NAME.value()).getAsString());

        JsonObject addressJson = jsonObject.getAsJsonObject("creditorAddress");

        Currency currency = Currency.valueOf(jsonObject.get(XS2AJsonKeys.INSTRUCTED_AMOUNT.value()).getAsJsonObject()
                .get("currency").getAsString().toUpperCase());

        Account creditorAccount = accountGson.fromJson(creditorAccountJson, Account.class);
        Address creditorAddress = addressGson.fromJson(addressJson, Address.class);
        creditorAccount.setAddress(creditorAddress);

        PeriodicPayment periodicPayment = new PeriodicPayment();
        InstructedAmount instructedAmount = new InstructedAmount(amount);
        instructedAmount.setCurrency(currency);
        periodicPayment.setInstructedAmount(instructedAmount);
        periodicPayment.setCreditor(creditorAccount);
        periodicPayment.setDebtor(debtorAccount);
        periodicPayment.setRemittanceInformationUnstructured(remittanceInformationUnstructured);
        periodicPayment.setEndToEndIdentification(endToEndIdentification);
        try {
            periodicPayment.setStartDate(simpleDateFormat.parse(jsonObject.get("startDate").getAsString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            periodicPayment.setEndDate(simpleDateFormat.parse(jsonObject.get("endDate").getAsString()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        periodicPayment.setDayOfExecution(jsonObject.get("dayOfExecution").getAsString());
        periodicPayment.setExecutionRule(PeriodicPayment.ExecutionRule.valueOf(jsonObject.get("executionRule").getAsString()));
        periodicPayment.setFrequency(jsonObject.get("frequency").getAsString());

        return periodicPayment;
    }
}
