package net.petafuel.styx.core.xs2a.utils.sepa.camt052.control;

import net.petafuel.jsepa.exception.SEPAParsingException;
import net.petafuel.styx.core.xs2a.entities.AccountReference;
import net.petafuel.styx.core.xs2a.entities.AccountReport;
import net.petafuel.styx.core.xs2a.entities.Amount;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.entities.Transaction;
import net.petafuel.styx.core.xs2a.entities.TransactionContainer;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.AccountReport11;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.BankToCustomerAccountReportV02;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.BranchAndFinancialInstitutionIdentification4;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.CreditDebitCode;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.DateAndDateTimeChoice;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.Document;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.EntryDetails1;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.EntryStatus2Code;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.EntryTransaction2;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.FinancialInstitutionIdentification7;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.ObjectFactory;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.PartyIdentification32;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.ReportEntry2;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.TransactionAgents2;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.TransactionParty2;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.TransactionReferences2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.StringReader;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Optional;

public class Camt052Converter {
    private static final Logger LOG = LogManager.getLogger(Camt052Converter.class);

    public Camt052Converter() {
        //Empty Constructor is needed for serialization purposes
    }

    public TransactionContainer processReport(String xmlData) throws SEPAParsingException {
        BankToCustomerAccountReportV02 originalReport = parseReport(xmlData);
        TransactionContainer transactionContainer = new TransactionContainer();
        transactionContainer.setTransactions(new AccountReport());
        transactionContainer.getTransactions().setBooked(new ArrayList<>());
        transactionContainer.getTransactions().setPending(new ArrayList<>());
        transactionContainer.getTransactions().setInformation(new ArrayList<>());

        for (AccountReport11 generalInfoAndTXNs : originalReport.getRpt()) {
            for (ReportEntry2 transactionInfo : generalInfoAndTXNs.getNtry()) {
                convertTransaction(transactionContainer.getTransactions(), transactionInfo);
            }
        }

        return transactionContainer;
    }

    private void convertTransaction(AccountReport accountReport, ReportEntry2 originalPayment) {
        List<EntryDetails1> entryDetails = originalPayment.getNtryDtls();
        if (entryDetails != null && !entryDetails.isEmpty()) {
            List<EntryTransaction2> transactionDetails = entryDetails.get(0).getTxDtls();
            if (transactionDetails.isEmpty()) {
                LOG.info("Transaction has no IBAN/BIC, sender/receiver information");
                return;
            }
            if (transactionDetails.size() > 1) {
                LOG.error("Input contains more than one NtryDtls/TxDtls, only FIRST processed!");
            }
            // this will be the new transaction element in JSON array
            Transaction transaction = new Transaction();
            //from here, extract data from XML and map to JSON object

            // status
            EntryStatus2Code camtBookingStatus = originalPayment.getSts();
            if (camtBookingStatus.equals(EntryStatus2Code.BOOK) || camtBookingStatus.equals(EntryStatus2Code.INFO)) {
                accountReport.getBooked().add(transaction);
            } else {
                accountReport.getPending().add(transaction);
            }

            // bookingDate if present
            Optional.ofNullable(originalPayment.getBookgDt())
                    .map(DateAndDateTimeChoice::getDt)
                    .map(XMLGregorianCalendar::toGregorianCalendar)
                    .map(GregorianCalendar::getTime)
                    .ifPresent(transaction::setBookingDate);
            // valueDate if present
            Optional.ofNullable(originalPayment.getValDt())
                    .map(DateAndDateTimeChoice::getDt)
                    .map(XMLGregorianCalendar::toGregorianCalendar)
                    .map(GregorianCalendar::getTime)
                    .ifPresent(transaction::setValueDate);

            // creditDebit
            // amount
            // currency
            transaction.setTransactionAmount(new Amount());
            if (originalPayment.getCdtDbtInd().equals(CreditDebitCode.DBIT)) {
                BigDecimal amount = originalPayment.getAmt().getValue().negate();
                transaction.getTransactionAmount().setAmount(amount.toString());
                transaction.getTransactionAmount().setCurrency(Currency.valueOf(originalPayment.getAmt().getCcy()));
            }
            EntryTransaction2 firstTransactionDetails = transactionDetails.get(0);

            // debtor account
            TransactionParty2 transactionParty = firstTransactionDetails.getRltdPties();
            transaction.setDebtorAccount(new AccountReference(transactionParty.getDbtrAcct().getId().getIBAN(), AccountReference.Type.IBAN));
            //optionally set debtor name
            Optional.ofNullable(transactionParty.getDbtr())
                    .map(PartyIdentification32::getNm)
                    .ifPresent(transaction::setDebtorName);
            // set debtor agent if present
            Optional.ofNullable(firstTransactionDetails.getRltdAgts())
                    .map(TransactionAgents2::getDbtrAgt)
                    .map(BranchAndFinancialInstitutionIdentification4::getFinInstnId)
                    .map(FinancialInstitutionIdentification7::getBIC)
                    .ifPresent(transaction::setDebtorAgent);

            // creditor account
            transaction.setCreditorAccount(new AccountReference(firstTransactionDetails.getRltdPties().getCdtrAcct().getId().getIBAN(), AccountReference.Type.IBAN));
            //set creditor name if present
            Optional.ofNullable(transactionParty.getCdtr())
                    .map(PartyIdentification32::getNm)
                    .ifPresent(transaction::setCreditorName);
            //set creditor agent if present
            Optional.ofNullable(firstTransactionDetails.getRltdAgts())
                    .map(TransactionAgents2::getCdtrAgt)
                    .map(BranchAndFinancialInstitutionIdentification4::getFinInstnId)
                    .map(FinancialInstitutionIdentification7::getBIC)
                    .ifPresent(transaction::setCreditorAgent);

            // reference
            transaction.setRemittanceInformationUnstructured(String.join("\n", firstTransactionDetails.getRmtInf().getUstrd()));

            //set e2e reference if present
            Optional.ofNullable(firstTransactionDetails.getRefs())
                    .map(TransactionReferences2::getEndToEndId)
                    .ifPresent(transaction::setEndToEndId);
            //set additional information
            transaction.setAdditionalInformation(originalPayment.getAddtlNtryInf());

        }

    }

    private BankToCustomerAccountReportV02 parseReport(String xmlData) throws SEPAParsingException {
        try {
            // schema factory is required to initialize the validator object (XSD based validator)
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            // extract XSD file from the JAR bundle
            URL resource = getClass().getClassLoader().getResource("schema/camt.052.001.02.xsd");
            // schema object will be initialized for validation
            Schema schema = sf.newSchema(resource);
            // jaxb context is required to initialize the unmarshaller, here we have to pass the GENERATED ObjectFactory class
            JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
            // now, the unmarshaller (make java object out of XML string) itself gets initialized
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            // connect the validator and unmarshaller. from here, the validation is activated
            unmarshaller.setSchema(schema);
            // now, validate and unmarshal the XML string
            Document document = ((JAXBElement<Document>) unmarshaller.unmarshal(new StringReader(xmlData))).getValue();

            LOG.info("camt052 xml parsing successful");

            // extract relevant element from the whole XML. this steps is specific to the XML format we work with
            return document.getBkToCstmrAcctRpt();
        } catch (JAXBException | SAXException error) {
            LOG.error("parseReport failed, invalid input", error);
            throw new SEPAParsingException("Camt052 XML unmarshalling failed!", error);
        }
    }
}
