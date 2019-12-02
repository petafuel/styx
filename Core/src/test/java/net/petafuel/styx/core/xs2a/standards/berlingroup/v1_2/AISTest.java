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
    public void testTransactionSerializer(){
        String creditorName = "John Miles";
        String creditorIban = "DE67100100101306118605";
        Currency currency = Currency.EUR;
        float amount1 = (float) 256.67;
        String reason1 = "Example 1";

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
                "        \"transactionId\": \"1234567\",\n" +
                "        \"creditorName\": \"" + creditorName + "\",\n" +
                "        \"creditorAccount\": {\n" +
                "          \"iban\": \"" + creditorIban + "\"\n" +
                "        },\n" +
                "        \"transactionAmount\": {\n" +
                "          \"currency\": \"" + currency.toString() + "\",\n" +
                "          \"amount\": \"" + amount1 + "\"\n" +
                "        },\n" +
                "        \"bookingDate\": \"2017-10-25\",\n" +
                "        \"valueDate\": \"2017-10-26\",\n" +
                "        \"remittanceInformationUnstructured\": \"" + reason1 + "\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"transactionId\": \"1234568\",\n" +
                "        \"debtorName\": \"" + debtorName + "\",\n" +
                "        \"debtorAccount\": {\n" +
                "          \"iban\": \"" + debtorIban + "\"\n" +
                "        },\n" +
                "        \"transactionAmount\": {\n" +
                "          \"currency\": \"" + currency.toString() + "\",\n" +
                "          \"amount\": \"" + amount2 + "\"\n" +
                "        },\n" +
                "        \"bookingDate\": \"2017-10-25\",\n" +
                "        \"valueDate\": \"2017-10-26\",\n" +
                "        \"remittanceInformationUnstructured\": \"" + reason2 +"\"\n" +
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
        Object o = gson.fromJson(json, type);

        Assert.assertTrue(o instanceof ArrayList);

        ArrayList<Transaction> transactions = (ArrayList<Transaction>) o;

        Assert.assertEquals(2, transactions.size());

        Transaction t1 = transactions.get(0);
        Transaction t2 = transactions.get(1);

        Assert.assertEquals(Transaction.Type.CREDIT, t1.getType());
        Assert.assertEquals(creditorName, t1.getAccount().getName());
        Assert.assertEquals(Account.Type.IBAN, t1.getAccount().getType());
        Assert.assertEquals(creditorIban, t1.getAccount().getIdentifier());
        Assert.assertEquals(currency, t1.getCurrency());
        Assert.assertEquals(0, Float.compare(amount1, t1.getAmount()));
        Assert.assertEquals(reason1, t1.getRemittanceInformationUnstructured());

        Assert.assertEquals(Transaction.Type.DEBIT, t2.getType());
        Assert.assertEquals(Account.Type.IBAN, t2.getAccount().getType());
        Assert.assertEquals(debtorIban, t2.getAccount().getIdentifier());
        Assert.assertEquals(currency, t2.getCurrency());
        Assert.assertEquals(0, Float.compare(amount2, t2.getAmount()));
        Assert.assertEquals(reason2, t2.getRemittanceInformationUnstructured());
    }
}
