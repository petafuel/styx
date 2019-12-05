package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.entities.Transaction;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadAccountDetailsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadAccountListRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadBalancesRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadTransactionDetailsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadTransactionsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.TransactionsSerializer;
import org.junit.Assert;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AISTest {
    private static final String FIDUCIA_GAD_BASE_API = "https://xs2a-test.fiduciagad.de/xs2a";
    private static final String DEUTSCHE_BANK_BASE_API = "https://simulator-xs2a.db.com/";

    private static final String CONSENT_ID = "3869582906101910204***REMOVED***CO4960JJ";
    private static final String ACCOUNT_ID = "3dc3d5b3-7023-4848-9853-f5400a64e80f";

    @Test
    @Tag("integration")
    public void testAccountList() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setAis(new BerlinGroupAIS(FIDUCIA_GAD_BASE_API, new BerlinGroupSigner()));
        ReadAccountListRequest r1 = new ReadAccountListRequest(CONSENT_ID);
        r1.setWithBalance(true);
        List<Account> list = standard.getAis().getAccountList(r1);
    }

    @Test
    @Tag("integration")
    public void testAccountDetails() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setAis(new BerlinGroupAIS(FIDUCIA_GAD_BASE_API, new BerlinGroupSigner()));
        ReadAccountDetailsRequest r1 = new ReadAccountDetailsRequest(ACCOUNT_ID, CONSENT_ID);
        r1.setWithBalance(true);

        Account result = standard.getAis().getAccount(r1);
    }

    @Test
    @Tag("integration")
    public void testBalances() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setAis(new BerlinGroupAIS(FIDUCIA_GAD_BASE_API, new BerlinGroupSigner()));

        ReadBalancesRequest r1 = new ReadBalancesRequest(ACCOUNT_ID, CONSENT_ID);

        Object result = standard.getAis().getBalancesByAccount(r1);
    }

    @Test
    @Tag("integration")
    public void testTransactions() {
        XS2AStandard standard = new XS2AStandard();
        standard.setAis(new BerlinGroupAIS(DEUTSCHE_BANK_BASE_API, new BerlinGroupSigner()));

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date from = format.parse("2019-03-03");
            Date to = new Date();
            ReadTransactionsRequest r1 = new ReadTransactionsRequest(ACCOUNT_ID, CONSENT_ID, "booked", from, to);
            Object result = standard.getAis().getTransactionsByAccount(r1);
        } catch (Exception ignored) {
        }
    }

    @Test
    @Tag("integration")
    public void testTransactionDetails() throws BankRequestFailedException {
        XS2AStandard standard = new XS2AStandard();
        standard.setAis(new BerlinGroupAIS("https://xs2a.banking.co.at/xs2a-sandbox/m002", new BerlinGroupSigner()));

        ReadTransactionDetailsRequest r1 = new ReadTransactionDetailsRequest(ACCOUNT_ID, "3603140611280910256***REMOVED***CO4960JJ", CONSENT_ID);
        Object result = standard.getAis().getTransaction(r1);
    }

    @Test
    @Tag("integration")
    public void testTransactionSerializer() throws Exception{
        String transactionId1 = "1234567";
        String creditorName = "John Miles";
        String creditorIban = "DE67100100101306118605";
        Currency currency = Currency.EUR;
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date bookingDate = format.parse("2017-10-25");
        Date valueDate = format.parse("2017-10-26");
        float amount1 = (float) 256.67;
        String reason1 = "Example 1";

        String transactionId2 = "1234568";
        String debtorName = "Paul Simpson";
        String debtorIban = "DE67100100101306118605";
        float amount2 = (float) 343.01;
        String reason2 = "Example 2";

        String json = "{\n" +
                "  \"account\": {\n" +
                "    \"iban\": \"DE2310010010123456788\"\n" +
                "  },\n" +
                "  \"transactions\": {\n" +
                "    \"booked\": [\n" +
                "      {\n" +
                "        \"transactionId\": \"" + transactionId1 + "\",\n" +
                "        \"creditorName\": \"" + creditorName + "\",\n" +
                "        \"creditorAccount\": {\n" +
                "          \"iban\": \"" + creditorIban + "\"\n" +
                "        },\n" +
                "        \"transactionAmount\": {\n" +
                "          \"currency\": \"" + currency.toString() + "\",\n" +
                "          \"amount\": \"" + amount1 + "\"\n" +
                "        },\n" +
                "        \"bookingDate\": \"" + format.format(bookingDate) + "\",\n" +
                "        \"valueDate\": \"" + format.format(valueDate) + "\",\n" +
                "        \"remittanceInformationUnstructured\": \"" + reason1 + "\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"transactionId\": \"" + transactionId2 + "\",\n" +
                "        \"debtorName\": \"" + debtorName + "\",\n" +
                "        \"debtorAccount\": {\n" +
                "          \"iban\": \"" + debtorIban + "\"\n" +
                "        },\n" +
                "        \"transactionAmount\": {\n" +
                "          \"currency\": \"" + currency.toString() + "\",\n" +
                "          \"amount\": \"" + amount2 + "\"\n" +
                "        },\n" +
                "        \"bookingDate\": \"" + format.format(bookingDate) + "\",\n" +
                "        \"valueDate\": \"" + format.format(valueDate) + "\",\n" +
                "        \"remittanceInformationUnstructured\": \"" + reason2 + "\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"_links\": {\n" +
                "      \"account\": {\n" +
                "        \"href\": \"/v1/accounts/3dc3d5b3-7023-4848-9853-f5400a64e80f\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        Type type = new TypeToken<ArrayList<Transaction>>(){}.getType();
        Gson gson = new GsonBuilder().registerTypeAdapter(type, new TransactionsSerializer()).create();
        ArrayList<Transaction> transactions = gson.fromJson(json, type);
        Transaction transaction1 = transactions.get(0);
        Transaction transaction2 = transactions.get(1);

        Assert.assertEquals(2, transactions.size());

        Assert.assertEquals(transactionId1, transaction1.getTransactionId());
        Assert.assertEquals(Transaction.Type.CREDIT, transaction1.getType());
        Assert.assertEquals(creditorName, transaction1.getAccount().getName());
        Assert.assertEquals(Account.Type.IBAN, transaction1.getAccount().getType());
        Assert.assertEquals(creditorIban, transaction1.getAccount().getIdentifier());
        Assert.assertEquals(currency, transaction1.getCurrency());
        Assert.assertEquals(0, Float.compare(amount1, transaction1.getAmount()));
        Assert.assertEquals(reason1, transaction1.getRemittanceInformationUnstructured());
        Assert.assertEquals(bookingDate, transaction1.getBookingDate());
        Assert.assertEquals(valueDate, transaction1.getValueDate());

        Assert.assertEquals(transactionId2, transaction2.getTransactionId());
        Assert.assertEquals(Transaction.Type.DEBIT, transaction2.getType());
        Assert.assertEquals(debtorName, transaction2.getAccount().getName());
        Assert.assertEquals(Account.Type.IBAN, transaction2.getAccount().getType());
        Assert.assertEquals(debtorIban, transaction2.getAccount().getIdentifier());
        Assert.assertEquals(currency, transaction2.getCurrency());
        Assert.assertEquals(0, Float.compare(amount2, transaction2.getAmount()));
        Assert.assertEquals(reason2, transaction2.getRemittanceInformationUnstructured());
        Assert.assertEquals(bookingDate, transaction2.getBookingDate());
        Assert.assertEquals(valueDate, transaction2.getValueDate());
    }
}
