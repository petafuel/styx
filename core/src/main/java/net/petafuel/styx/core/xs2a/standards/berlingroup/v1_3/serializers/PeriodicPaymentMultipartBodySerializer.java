package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import net.petafuel.jsepa.model.Document;
import net.petafuel.styx.core.xs2a.entities.AccountReference;
import net.petafuel.styx.core.xs2a.entities.Amount;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PeriodicPaymentMultipartBodySerializer {
    /**
     * This class only allows static Method access
     */
    private PeriodicPaymentMultipartBodySerializer() {
    }

    public static PeriodicPayment xmlDeserialize(Document sepaDocument, PeriodicPayment periodicPayment) throws ParseException {
        if (periodicPayment.getDebtorAccount() == null) {
            periodicPayment.setDebtorAccount(new AccountReference());
        }
        AccountReference debtorAccount = periodicPayment.getDebtorAccount();
        periodicPayment.setDebtorName(sepaDocument.getCctInitiation().getPmtInfos().get(0).getDebitor().getName());
        debtorAccount.setIban(sepaDocument.getCctInitiation().getPmtInfos().get(0).getDebtorAccountIBAN());
        debtorAccount.setCurrency(Currency.EUR);

        String creditorAccountName = sepaDocument.getCctInitiation().getPmtInfos().get(0)
                .getCreditTransferTransactionInformationVector().get(0).getCreditorName();
        String creditorAccountIdentifier = sepaDocument.getCctInitiation().getPmtInfos().get(0)
                .getCreditTransferTransactionInformationVector().get(0).getCreditorIBAN();

        String creditorAgent = sepaDocument.getCctInitiation().getPmtInfos().get(0)
                .getCreditTransferTransactionInformationVector().get(0).getCreditorAgent();
        if (periodicPayment.getCreditorAccount() == null) {
            periodicPayment.setCreditorAccount(new AccountReference());
        }
        AccountReference creditorAccount = periodicPayment.getCreditorAccount();
        periodicPayment.setCreditorName(creditorAccountName);
        creditorAccount.setIban(creditorAccountIdentifier);
        creditorAccount.setCurrency(Currency.EUR);
        periodicPayment.setCreditorAgent(creditorAgent);

        String amount = Double.toString(sepaDocument.getCctInitiation().getPmtInfos().get(0)
                .getCreditTransferTransactionInformationVector().get(0).getAmount());
        String remittanceInformationUnstructured = sepaDocument.getCctInitiation().getPmtInfos().get(0)
                .getCreditTransferTransactionInformationVector().get(0).getVwz();
        String endToEndIdentification = sepaDocument.getCctInitiation().getPmtInfos().get(0)
                .getCreditTransferTransactionInformationVector().get(0).getEndToEndID();
        Date requestExecutionDate = new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value()).parse(sepaDocument
                .getCctInitiation().getPmtInfos().get(0).getRequestedExecutionDate());

        periodicPayment.setInstructedAmount(new Amount(amount));
        periodicPayment.setRemittanceInformationUnstructured(remittanceInformationUnstructured);
        periodicPayment.setEndToEndIdentification(endToEndIdentification);
        periodicPayment.setRequestedExecutionDate(requestExecutionDate);
        return periodicPayment;
    }
}
