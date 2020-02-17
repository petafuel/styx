package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import net.petafuel.jsepa.model.Document;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.entities.InstructedAmount;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PeriodicPaymentMultipartBodySerializer implements JsonSerializer<PeriodicPayment> {

    public static PeriodicPayment xmlDeserialize(Document sepaDocument, JsonObject jsonObject) throws ParseException {
        Account debtorAccount = new Account();
        debtorAccount.setName(sepaDocument.getCctInitiation().getPmtInfos().get(0).getDebitor().getName());
        debtorAccount.setIdentifier(sepaDocument.getCctInitiation().getPmtInfos().get(0).getDebtorAccountIBAN());
        debtorAccount.setCurrency(Currency.EUR);
        debtorAccount.setType(Account.Type.IBAN);

        Date startDate = new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value()).parse(jsonObject.get("startDate").getAsString());
        Date endDate = new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value()).parse(jsonObject.get("endDate").getAsString());
        String executionRule = jsonObject.get("executionRule").getAsString().toUpperCase();
        String frequency = jsonObject.get("frequency").getAsString();
        String dayOfExecution = jsonObject.get("dayOfExecution").getAsString();

        String creditorAccountName = sepaDocument.getCctInitiation().getPmtInfos().get(0)
                .getCreditTransferTransactionInformationVector().get(0).getCreditorName();
        String creditorAccountIdentifier = sepaDocument.getCctInitiation().getPmtInfos().get(0)
                .getCreditTransferTransactionInformationVector().get(0).getCreditorIBAN();

        String creditorAgent = sepaDocument.getCctInitiation().getPmtInfos().get(0)
                .getCreditTransferTransactionInformationVector().get(0).getCreditorAgent();

        Account creditorAccount = new Account();
        creditorAccount.setName(creditorAccountName);
        creditorAccount.setIdentifier(creditorAccountIdentifier);
        creditorAccount.setCurrency(Currency.EUR);
        creditorAccount.setType(Account.Type.IBAN);
        creditorAccount.setAgent(creditorAgent);

        String amount = Double.toString(sepaDocument.getCctInitiation().getPmtInfos().get(0)
                .getCreditTransferTransactionInformationVector().get(0).getAmount());
        String remittanceInformationUnstructured = sepaDocument.getCctInitiation().getPmtInfos().get(0)
                .getCreditTransferTransactionInformationVector().get(0).getVwz();
        String endToEndIdentification = sepaDocument.getCctInitiation().getPmtInfos().get(0)
                .getCreditTransferTransactionInformationVector().get(0).getEndToEndID();
        Date requestExecutionDate = new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value()).parse(sepaDocument
                .getCctInitiation().getPmtInfos().get(0).getRequestedExecutionDate());

        PeriodicPayment periodicPayment = new PeriodicPayment();
        periodicPayment.setInstructedAmount(new InstructedAmount(amount));
        periodicPayment.setCreditor(creditorAccount);
        periodicPayment.setDebtor(debtorAccount);
        periodicPayment.setRemittanceInformationUnstructured(remittanceInformationUnstructured);
        periodicPayment.setEndToEndIdentification(endToEndIdentification);
        periodicPayment.setRequestedExecutionDate(requestExecutionDate);
        periodicPayment.setStartDate(startDate);
        periodicPayment.setEndDate(endDate);
        periodicPayment.setDayOfExecution(dayOfExecution);
        periodicPayment.setExecutionRule(PeriodicPayment.ExecutionRule.valueOf(executionRule));
        periodicPayment.setFrequency(frequency);

        return periodicPayment;
    }

    @Override
    public JsonElement serialize(PeriodicPayment payment, Type typeOfSrc, JsonSerializationContext context) {

        JsonObject object = new JsonObject();
        SimpleDateFormat format = new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value());

        //Periodic Payment Serialization
        String formattedStartDate = format.format(payment.getStartDate());
        object.addProperty("startDate", formattedStartDate);
        if (payment.getExecutionRule() != null) {
            object.addProperty("executionRule", payment.getExecutionRule().getValue());
        }
        if (payment.getEndDate() != null) {
            String formattedEndDate = format.format(payment.getEndDate());
            object.addProperty("endDate", formattedEndDate);
        }
        object.addProperty("frequency", payment.getFrequency());
        object.addProperty("dayOfExecution", payment.getDayOfExecution());

        return object;
    }
}
