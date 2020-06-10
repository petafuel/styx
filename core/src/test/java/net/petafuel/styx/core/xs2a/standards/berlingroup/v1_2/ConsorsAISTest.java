package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.banklookup.exceptions.BankLookupFailedException;
import net.petafuel.styx.core.banklookup.exceptions.BankNotFoundException;
import net.petafuel.styx.core.banklookup.sad.SAD;
import net.petafuel.styx.core.xs2a.contracts.AISRequest;
import net.petafuel.styx.core.xs2a.entities.AccountDetails;
import net.petafuel.styx.core.xs2a.entities.BalanceContainer;
import net.petafuel.styx.core.xs2a.entities.Transaction;
import net.petafuel.styx.core.xs2a.entities.TransactionContainer;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.factory.AISRequestFactory;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setConsentId(CONSENT);
        AISRequest aisRequest = new AISRequestFactory().create(standard.getRequestClassProvider().accountList(), xs2AFactoryInput);
        aisRequest.setWithBalance(true);

        List<AccountDetails> list = standard.getAis().getAccountList(aisRequest);
        Assertions.assertTrue(list.size() >= 1);
    }

    @Test
    @Order(2)
    public void testAccountDetails() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setAccountId(ACCOUNT_ID);
        xs2AFactoryInput.setConsentId(CONSENT);
        xs2AFactoryInput.setWithBalance(true);
        AISRequest aisRequest = new AISRequestFactory().create(standard.getRequestClassProvider().accountDetails(), xs2AFactoryInput);

        AccountDetails result = standard.getAis().getAccount(aisRequest);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getIban());
    }

    @Test
    @Order(3)
    public void testBalances() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);

        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setAccountId(ACCOUNT_ID);
        xs2AFactoryInput.setConsentId(CONSENT);
        AISRequest aisRequest = new AISRequestFactory().create(standard.getRequestClassProvider().accountBalances(), xs2AFactoryInput);

        BalanceContainer result = standard.getAis().getBalancesByAccount(aisRequest);
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.getBalances().size() >= 1);
    }

    @Test
    @Order(4)
    public void testTransactions() throws BankLookupFailedException, BankNotFoundException, ParseException, BankRequestFailedException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);


        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date from = format.parse("2019-03-03");
        Date to = new Date();

        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setAccountId(ACCOUNT_ID);
        xs2AFactoryInput.setConsentId(CONSENT);
        xs2AFactoryInput.setBookingStatus("booked");
        xs2AFactoryInput.setDateFrom(from);
        xs2AFactoryInput.setDateTo(to);
        AISRequest aisRequest = new AISRequestFactory().create(standard.getRequestClassProvider().accountTransactionList(), xs2AFactoryInput);

        TransactionContainer result = standard.getAis().getTransactionsByAccount(aisRequest);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getTransactions());
    }

    @Test
    @Order(5)
    public void testTransactionDetails() throws BankRequestFailedException, BankLookupFailedException, BankNotFoundException {
        XS2AStandard standard = (new SAD()).getBankByBIC(BIC, true);
        XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();
        xs2AFactoryInput.setAccountId(ACCOUNT_ID);
        xs2AFactoryInput.setTransactionId(TRANSACTION_ID);
        xs2AFactoryInput.setConsentId(CONSENT);
        AISRequest aisRequest = new AISRequestFactory().create(standard.getRequestClassProvider().accountTransactionDetails(), xs2AFactoryInput);

        Transaction result = standard.getAis().getTransaction(aisRequest);
        Assertions.assertNotNull(result);
        Assertions.assertNotNull(result.getDebtorAccount());
    }

}
