package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import net.petafuel.jsepa.model.CreditTransferTransactionInformation;
import net.petafuel.jsepa.model.Document;
import net.petafuel.styx.core.xs2a.entities.AccountReference;
import net.petafuel.styx.core.xs2a.entities.Amount;
import net.petafuel.styx.core.xs2a.entities.BulkPayment;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.SinglePayment;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PaymentSerializer {
    /**
     * This class only allows static Method access
     */
    private PaymentSerializer() {
    }

    public static InitializablePayment xmlDeserialize(Document sepaDocument, PaymentService paymentService) throws ParseException {
        ArrayList<SinglePayment> payments = new ArrayList<>();
        AccountReference debtorAccount = new AccountReference();
        debtorAccount.setName(sepaDocument.getCctInitiation().getPmtInfos().get(0).getDebtorName());
        debtorAccount.setIban(sepaDocument.getCctInitiation().getPmtInfos().get(0).getDebtorAccountIBAN());
        debtorAccount.setCurrency(Currency.EUR);

        Date requestedExecutionDate = new SimpleDateFormat(XS2AJsonKeys.DATE_FORMAT.value()).parse(sepaDocument
                .getCctInitiation().getPmtInfos().get(0).getRequestedExecutionDate());

        for (CreditTransferTransactionInformation ctti : sepaDocument.getCctInitiation().getPmtInfos().get(0)
                .getCreditTransferTransactionInformationVector()) {
            String creditorAccountName = ctti.getCreditorName();
            String creditorAccountIdentifier = ctti.getCreditorIBAN();

            AccountReference creditorAccount = new AccountReference(creditorAccountIdentifier, AccountReference.Type.IBAN);
            creditorAccount.setName(creditorAccountName);
            creditorAccount.setCurrency(Currency.EUR);

            String amount = Double.toString(ctti.getAmount());
            String remittanceInformationUnstructured = ctti.getVwz();
            String endToEndIdentification = ctti.getEndToEndID();

            SinglePayment payment = new SinglePayment();
            payment.setInstructedAmount(new Amount(amount, Currency.EUR));
            payment.setCreditorAccount(creditorAccount);
            payment.setDebtorAccount(debtorAccount);
            payment.setRemittanceInformationUnstructured(remittanceInformationUnstructured);
            payment.setEndToEndIdentification(endToEndIdentification);
            payment.setRequestedExecutionDate(requestedExecutionDate);
            payments.add(payment);
        }

        if (paymentService.equals(PaymentService.BULK_PAYMENTS)) {
            BulkPayment bulkPayment = new BulkPayment();
            bulkPayment.setPayments(payments);
            bulkPayment.setDebtorAccount(debtorAccount);
            bulkPayment.setRequestedExecutionDate(requestedExecutionDate);
            return bulkPayment;
        }
        return payments.get(0);
    }

}
