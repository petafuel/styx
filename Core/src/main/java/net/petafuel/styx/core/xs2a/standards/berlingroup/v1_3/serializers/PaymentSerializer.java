package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.petafuel.jsepa.model.CreditTransferTransactionInformation;
import net.petafuel.jsepa.model.Document;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Address;
import net.petafuel.styx.core.xs2a.entities.BulkPayment;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaymentSerializer implements JsonSerializer<InitializablePayment>, JsonDeserializer<InitializablePayment> {
    private PaymentService paymentService;

    public PaymentSerializer(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public static InitializablePayment xmlDeserialize(Document sepaDocument, PaymentService paymentService) throws ParseException {
        ArrayList<Payment> payments = new ArrayList<>();
        Account debtorAccount = new Account();
        debtorAccount.setName(sepaDocument.getCctInitiation().getPmtInfos().get(0).getDebitor().getName());
        debtorAccount.setIdentifier(sepaDocument.getCctInitiation().getPmtInfos().get(0).getDebtorAccountIBAN());
        debtorAccount.setCurrency(Currency.EUR);
        debtorAccount.setType(Account.Type.IBAN);

        Date requestedExecutionDate = new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value()).parse(sepaDocument
                .getCctInitiation().getPmtInfos().get(0).getRequestedExecutionDate());

        for (CreditTransferTransactionInformation ctti : sepaDocument.getCctInitiation().getPmtInfos().get(0)
                .getCreditTransferTransactionInformationVector()) {
            String creditorAccountName = ctti.getCreditorName();
            String creditorAccountIdentifier = ctti.getCreditorIBAN();

            Account creditorAccount = new Account();
            creditorAccount.setName(creditorAccountName);
            creditorAccount.setIdentifier(creditorAccountIdentifier);
            creditorAccount.setCurrency(Currency.EUR);
            creditorAccount.setType(Account.Type.IBAN);
            creditorAccount.setAgent(ctti.getCreditorAgent());

            String amount = Double.toString(ctti.getAmount());
            String remittanceInformationUnstructured = ctti.getVwz();
            String endToEndIdentification = ctti.getEndToEndID();

            Payment payment = new Payment();
            payment.setAmount(amount);
            payment.setCreditor(creditorAccount);
            payment.setDebtor(debtorAccount);
            payment.setCurrency(Currency.EUR);
            payment.setRemittanceInformationUnstructured(remittanceInformationUnstructured);
            payment.setEndToEndIdentification(endToEndIdentification);
            payment.setRequestedExecutionDate(requestedExecutionDate);
            payments.add(payment);
        }

        if (paymentService.equals(PaymentService.BULK_PAYMENTS)) {
            BulkPayment bulkPayment = new BulkPayment(payments);
            bulkPayment.setRequestedExecutionDate(requestedExecutionDate);
            return bulkPayment;
        }
        return payments.get(0);
    }

    @Override
    public JsonElement serialize(InitializablePayment initializablePayment, Type typeOfSrc, JsonSerializationContext context) {
        Payment payment = (Payment) initializablePayment;

        JsonObject object = new JsonObject();
        JsonObject creditorAccount = new JsonObject();
        JsonObject debtorAccount = new JsonObject();
        JsonObject instructedAmount = new JsonObject();
        SimpleDateFormat format = new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value());

        creditorAccount.addProperty(payment.getCreditor().getType().getJsonKey(), payment.getCreditor().getIdentifier());
        creditorAccount.addProperty(XS2AJsonKeys.CURRENCY.value(), payment.getCreditor().getCurrency().toString());
        object.add(XS2AJsonKeys.CREDITOR_ACCOUNT.value(), creditorAccount);

        debtorAccount.addProperty(payment.getDebtor().getType().getJsonKey(), payment.getDebtor().getIdentifier());
        debtorAccount.addProperty(XS2AJsonKeys.CURRENCY.value(), payment.getDebtor().getCurrency().toString());
        object.addProperty(XS2AJsonKeys.CREDITOR_NAME.value(), payment.getCreditor().getName());
        object.add("debtorAccount", debtorAccount);

        instructedAmount.addProperty(XS2AJsonKeys.AMOUNT.value(), payment.getAmount());
        instructedAmount.addProperty(XS2AJsonKeys.CURRENCY.value(), payment.getCurrency().toString());
        object.add(XS2AJsonKeys.INSTRUCTED_AMOUNT.value(), instructedAmount);

        if (payment.getRequestedExecutionDate() != null) {
            String formattedDate = format.format(payment.getRequestedExecutionDate());
            object.addProperty("requestedExecutionDate", formattedDate);
        }

        object.addProperty(XS2AJsonKeys.REMITTANCE_INFORMATION_UNSTRUCTURED.value(), payment.getRemittanceInformationUnstructured());

        return object;
    }

    /**
     * code complexity not possible to avoid at this point
     **/
    @SuppressWarnings("squid:S3776")
    @Override
    public InitializablePayment deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        JsonObject debtorAccountJson = jsonObject.getAsJsonObject("debtorAccount");
        Gson accountGson = new GsonBuilder()
                .registerTypeAdapter(Account.class, new AccountSerializer())
                .create();
        Account debtorAccount = accountGson.fromJson(debtorAccountJson, Account.class);

        Gson addressGson = new GsonBuilder()
                .registerTypeAdapter(Address.class, new AddressSerializer())
                .create();

        if (paymentService.equals(PaymentService.PAYMENTS)) {
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

            Payment payment = new Payment();
            payment.setDebtor(debtorAccount);
            payment.setCreditor(creditorAccount);
            payment.setCurrency(currency);
            payment.setEndToEndIdentification(endToEndIdentification);
            payment.setAmount(amount);
            payment.setRemittanceInformationUnstructured(remittanceInformationUnstructured);

            return payment;
        } else {
            ArrayList<Payment> payments = new ArrayList<>();
            for (JsonElement paymentElement : jsonObject.get("payments").getAsJsonArray()) {
                JsonObject paymentJson = (JsonObject) paymentElement;
                Payment payment = new Payment();
                payment.setDebtor(debtorAccount);

                JsonObject creditorAccountJson = paymentJson.getAsJsonObject(XS2AJsonKeys.CREDITOR_ACCOUNT.value());
                creditorAccountJson.addProperty("name", paymentJson.get(XS2AJsonKeys.CREDITOR_NAME.value()).getAsString());
                Account creditorAccount = accountGson.fromJson(creditorAccountJson, Account.class);
                JsonObject addressJson = paymentJson.getAsJsonObject("creditorAddress");
                Address creditorAddress = addressGson.fromJson(addressJson, Address.class);
                creditorAccount.setAddress(creditorAddress);
                payment.setCreditor(creditorAccount);

                JsonObject instructedAmountJson = paymentJson.get(XS2AJsonKeys.INSTRUCTED_AMOUNT.value()).getAsJsonObject();
                Currency currency = Currency.valueOf(instructedAmountJson.get("currency").getAsString().toUpperCase());
                payment.setCurrency(currency);

                String endToEndIdentification =  paymentJson.get(XS2AJsonKeys.END_TO_END_IDENTIFICATION.value()) != null
                        && !paymentJson.get(XS2AJsonKeys.END_TO_END_IDENTIFICATION.value()).isJsonNull()
                        ? paymentJson.get(XS2AJsonKeys.END_TO_END_IDENTIFICATION.value()).getAsString()
                        : null;
                payment.setEndToEndIdentification(endToEndIdentification);

                payment.setAmount(instructedAmountJson.get(XS2AJsonKeys.AMOUNT.value()).getAsString());
                if (paymentJson.get(XS2AJsonKeys.REMITTANCE_INFORMATION_UNSTRUCTURED.value()) != null) {
                    payment.setRemittanceInformationUnstructured(paymentJson.get(XS2AJsonKeys.REMITTANCE_INFORMATION_UNSTRUCTURED.value()).getAsString());
                }

                payments.add(payment);
            }

            BulkPayment bulkPayment = new BulkPayment(payments);
            try {
                bulkPayment.setRequestedExecutionDate(new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value()).parse(jsonObject.get("requestedExecutionDate").getAsString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            bulkPayment.setBatchBookingPreferred(jsonObject.get("batchBookingPreferred").getAsBoolean());

            return bulkPayment;
        }

    }
}
