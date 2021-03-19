package net.petafuel.styx.core.xs2a.utils.sepa.camt052.facades;

import net.petafuel.jsepa.exception.SEPAParsingException;
import net.petafuel.styx.core.xs2a.entities.TransactionContainer;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.AccountReport11;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.ActiveOrHistoricCurrencyAndAmount;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.BankToCustomerAccountReportV02;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.CreditDebitCode;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.DateAndDateTimeChoice;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.Document;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.EntryDetails1;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.EntryTransaction2;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.ObjectFactory;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.ReportEntry2;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.model.TransactionDetailed;
import net.petafuel.styx.core.xs2a.utils.sepa.camt052.model.TransactionReport;
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
import java.net.URL;
import java.util.List;

public class Camt052Converter {
    private static final Logger LOG = LogManager.getLogger(Camt052Converter.class);

    public Camt052Converter()
    {
        //Empty Constructor is needed for serialization purposes
    }

    public TransactionReport processReport(String xmlData) throws SEPAParsingException
    {
        BankToCustomerAccountReportV02 originalReport = parseReport(xmlData);
        TransactionReport convertedReport = new TransactionReport();

        for (AccountReport11 generalInfoAndTXNs : originalReport.getRpt())
        {
            for (ReportEntry2 transactionInfo : generalInfoAndTXNs.getNtry())
            {
                convertTransaction(convertedReport, transactionInfo);
            }
        }

        return convertedReport;
    }

    private void convertTransaction(TransactionReport convertedReport, ReportEntry2 originalPayment)
    {
        List<EntryDetails1> entryDetails = originalPayment.getNtryDtls();
        if (entryDetails != null)
        {
            List<EntryTransaction2> transactionDetails = entryDetails.get(0).getTxDtls();
            if (transactionDetails.isEmpty())
            {
                LOG.info("Transaction has no IBAN/BIC, sender/receiver information");
                return;
            }
            if(transactionDetails.size() > 1) {
                LOG.error("Input contains more than one NtryDtls/TxDtls, only FIRST processed!");
            }
            // this will be the new transaction element in JSON array
            TransactionDetailed convertedTransactionInfo = new TransactionDetailed();
            // add new transaction to the whole JSON response
            convertedReport.add(convertedTransactionInfo);

            //from here, extract data from XML and map to JSON object

            // status
            convertedTransactionInfo.setStatus(originalPayment.getSts().value());

            // bookingDate
            convertedTransactionInfo.setBookingDate(convertDate(originalPayment.getBookgDt()));

            // valueDate
            convertedTransactionInfo.setValueDate(convertDate(originalPayment.getValDt()));

            // creditDebit
            CreditDebitCode creditDebitCode = originalPayment.getCdtDbtInd();
            convertedTransactionInfo.setCreditDebit(creditDebitCode.value());

            // amount
            ActiveOrHistoricCurrencyAndAmount activeOrHistoricCurrencyAndAmount = originalPayment.getAmt();
            convertedTransactionInfo.setAmount(activeOrHistoricCurrencyAndAmount.getValue().toString());

            // currency
            convertedTransactionInfo.setCurrency(activeOrHistoricCurrencyAndAmount.getCcy());

            // creditorIban
            EntryTransaction2 firstTransactionDetails = transactionDetails.get(0);
            convertedTransactionInfo.setCreditorIban(firstTransactionDetails.getRltdPties().getCdtrAcct().getId().getIBAN());

            // creditorBic
            convertedTransactionInfo.setCreditorBic(firstTransactionDetails.getRltdAgts().getCdtrAgt().getFinInstnId().getBIC());

            // reference
            convertedTransactionInfo.setReference(convertReference(firstTransactionDetails.getRmtInf().getUstrd()));

            // debtorName
            convertedTransactionInfo.setDebtorName(firstTransactionDetails.getRltdPties().getDbtr().getNm());

            // debtorIban
            convertedTransactionInfo.setDebtorIban(firstTransactionDetails.getRltdPties().getDbtrAcct().getId().getIBAN());

            // debtorBic
            convertedTransactionInfo.setDebtorBic(firstTransactionDetails.getRltdAgts().getDbtrAgt().getFinInstnId().getBIC());

            // creditorName
            convertedTransactionInfo.setCreditorName(firstTransactionDetails.getRltdPties().getCdtr().getNm());

        }
    }

    /**
     * Non structured remittance information.
     * If available, the content of tag /REMI/ or tags ?20-
     ?29 and ?60-?63 from field-86 from an incoming SWIFT
     MT942.
     * @param references
     * @return
     */
    private String convertReference(List<String> references)
    {
        StringBuilder buffer = new StringBuilder();
        for(String reference : references) {
            buffer.append(reference).append('?');
        }
        buffer.deleteCharAt(buffer.length()-1);
        return buffer.toString();
    }

    private String convertDate(DateAndDateTimeChoice xmlDate)
    {
        if (xmlDate != null)
        {
            XMLGregorianCalendar dateTime = xmlDate.getDtTm();
            if (dateTime != null)
            {
                return String.format("%02d-%02d-%02d", dateTime.getYear(), dateTime.getMonth(), dateTime.getDay());
            }
            XMLGregorianCalendar date = xmlDate.getDt();
            if (date != null)
            {
                return String.format("%02d-%02d-%02d", date.getYear(), date.getMonth(), date.getDay());
            }
        }
        return null;
    }

    private BankToCustomerAccountReportV02 parseReport(String xmlData) throws SEPAParsingException
    {
        try
        {
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
        } catch (JAXBException | SAXException error)
        {
            LOG.error("parseReport failed, invalid input", error);
            throw new SEPAParsingException("Camt052 XML unmarshalling failed!", error);
        }
    }

    public TransactionContainer getTransactionContainer(TransactionReport transactionReport) {
        // TODO
        return null;
    }
}
