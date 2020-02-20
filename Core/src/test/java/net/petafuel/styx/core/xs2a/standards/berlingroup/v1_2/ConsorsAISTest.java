package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("integration")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ConsorsAISTest {

    private static final String BIC = "CSDBDE71";
    private static final String CONSENT = "YTYcQTMAWsNhJL-iAJ5DRSvD4Wkq4-rI0vZuXPGnTrBLwxruB4iBC2rLdxe_JmLVmNUZrDfhkFiwk2pKoYyLcw==_=_bS6p6XvTWI";
    private static final String ACCOUNT_ID = "9b86539d-589b-4082-90c2-d725c019777f";
    private static final String TRANSACTION_ID = "9b86539d-589b-4082-90c2-d725c019777f";

    @BeforeAll
    public void prepare() {
        //TODO setup valid consent
    }

    @Test
    @Order(1)
    public void testAccountList() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
        ReadAccountListRequest r1 = new ReadAccountListRequest(CONSENT);
        r1.setWithBalance(true);
        List<Account> list = standard.getAis().getAccountList(r1);
    }

    @Test
    @Order(2)
    public void testAccountDetails() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
        ReadAccountDetailsRequest r1 = new ReadAccountDetailsRequest(ACCOUNT_ID, CONSENT);
        r1.setWithBalance(true);

        Account result = standard.getAis().getAccount(r1);
    }

    @Test
    @Order(3)
    public void testBalances() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        ReadBalancesRequest r1 = new ReadBalancesRequest(ACCOUNT_ID, CONSENT);

        Object result = standard.getAis().getBalancesByAccount(r1);
    }

    @Test
    @Order(4)
    public void testTransactions() throws BankLookupFailedException, BankNotFoundException, ParseException, BankRequestFailedException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date from = format.parse("2019-03-03");
        Date to = new Date();
        ReadTransactionsRequest r1 = new ReadTransactionsRequest(ACCOUNT_ID, CONSENT, "booked", from, to);
        Object result = standard.getAis().getTransactionsByAccount(r1);

    }

    @Test
    @Order(5)
    public void testTransactionDetails() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
        ReadTransactionDetailsRequest r1 = new ReadTransactionDetailsRequest(ACCOUNT_ID, TRANSACTION_ID, CONSENT);
        Object result = standard.getAis().getTransaction(r1);
    }

    @Test
    @Order(6)
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
