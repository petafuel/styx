package net.petafuel.styx.api.v1.account.boundary;

import net.petafuel.styx.api.filter.AcceptsPreStepAuth;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.rest.RestResource;
import net.petafuel.styx.api.util.AspspUrlMapper;
import net.petafuel.styx.api.util.io.IOProcessor;
import net.petafuel.styx.api.v1.account.control.AccountListResponseAdapter;
import net.petafuel.styx.api.v1.account.control.TransactionListResponseAdapter;
import net.petafuel.styx.api.v1.account.entity.AccountDetailResponse;
import net.petafuel.styx.api.v1.account.entity.TransactionListRequestBean;
import net.petafuel.styx.core.xs2a.contracts.AISRequest;
import net.petafuel.styx.core.xs2a.entities.AccountDetails;
import net.petafuel.styx.core.xs2a.entities.BalanceContainer;
import net.petafuel.styx.core.xs2a.entities.TransactionContainer;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.factory.AISRequestFactory;
import net.petafuel.styx.core.xs2a.factory.XS2AFactoryInput;
import net.petafuel.styx.spi.tokentypemapper.api.XS2ATokenType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@RequiresBIC
@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken(allowedServices = {XS2ATokenType.AISPIS, XS2ATokenType.AIS})
public class AccountResource extends RestResource {
    private static final Logger LOG = LogManager.getLogger(AccountResource.class);
    private final XS2AFactoryInput xs2AFactoryInput = new XS2AFactoryInput();

    /**
     * Returns a List of Accounts
     *
     * @param consentId consentId with access to the requested account list
     * @return returns an account list
     * @documented https://confluence.petafuel.intern/display/TOOL/Styx+AIS+-+Interface+Definition#StyxAISInterfaceDefinition-YellowGET/v1/accounts
     * @see AccountListResponseAdapter
     */
    @AcceptsPreStepAuth
    @GET
    @Path("/accounts")
    public Response processAccountList(@NotNull @NotBlank @HeaderParam("consentId") String consentId) throws BankRequestFailedException {
        xs2AFactoryInput.setConsentId(consentId);
        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        AISRequest accountListRequest = new AISRequestFactory().create(getXS2AStandard().getRequestClassProvider().accountList(), xs2AFactoryInput);
        accountListRequest.getHeaders().putAll(getAdditionalHeaders());

        ioProcessor.modifyRequest(accountListRequest, xs2AFactoryInput);

        List<AccountDetails> accountList = getXS2AStandard().getAis().getAccountList(accountListRequest);
        LOG.info("Successfully fetched account list for bic={}", getXS2AStandard().getAspsp().getBic());
        return Response.status(200).entity(new AccountListResponseAdapter(accountList)).build();
    }


    /**
     * Returns AccountDetails for a single account
     *
     * @param accountId the xs2a account id
     * @return returns an account object
     * @documented https://confluence.petafuel.intern/display/TOOL/Styx+AIS+-+Interface+Definition#StyxAISInterfaceDefinition-YellowGET/v1/accounts/{resourceId}
     * @see AccountDetails
     */
    @AcceptsPreStepAuth
    @GET
    @Path("/accounts/{resourceId}")
    public Response getAccountDetails(@NotNull @NotBlank @HeaderParam("consentId") String consentId, @NotNull @NotBlank @PathParam("resourceId") String accountId) throws BankRequestFailedException {
        xs2AFactoryInput.setConsentId(consentId);
        xs2AFactoryInput.setAccountId(accountId);

        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        AISRequest accountDetailsRequest = new AISRequestFactory().create(getXS2AStandard().getRequestClassProvider().accountDetails(), xs2AFactoryInput);
        accountDetailsRequest.getHeaders().putAll(getAdditionalHeaders());

        ioProcessor.modifyRequest(accountDetailsRequest, xs2AFactoryInput);

        AccountDetails account = getXS2AStandard().getAis().getAccount(accountDetailsRequest);
        account.setLinks(new AspspUrlMapper(account.getResourceId()).map(account.getLinks()));
        LOG.info("Successfully fetched account details bic={}", getXS2AStandard().getAspsp().getBic());
        return Response.status(200).entity(new AccountDetailResponse(account)).build();
    }

    /**
     * Fetch a list of balances(mandatory) with an optional linked account
     *
     * @param accountId the xs2a account id
     * @param consentId a consent with status VALID
     * @return returns a list of balances, optionally a linked account
     * @documented https://confluence.petafuel.intern/display/TOOL/Styx+AIS+-+Interface+Definition#StyxAISInterfaceDefinition-YellowGET/v1/accounts/{resourceId}/balances
     */
    @AcceptsPreStepAuth
    @GET
    @Path("/accounts/{resourceId}/balances")
    public Response fetchBalances(@NotNull @NotBlank @HeaderParam("consentId") String consentId, @NotNull @NotBlank @PathParam("resourceId") String accountId) throws BankRequestFailedException {
        xs2AFactoryInput.setConsentId(consentId);
        xs2AFactoryInput.setAccountId(accountId);

        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        AISRequest readBalancesRequest = new AISRequestFactory().create(getXS2AStandard().getRequestClassProvider().accountBalances(), xs2AFactoryInput);
        readBalancesRequest.getHeaders().putAll(getAdditionalHeaders());

        ioProcessor.modifyRequest(readBalancesRequest, xs2AFactoryInput);

        BalanceContainer balances = getXS2AStandard().getAis().getBalancesByAccount(readBalancesRequest);
        LOG.info("Successfully fetched balances bic={}", getXS2AStandard().getAspsp().getBic());
        return Response.status(200).entity(balances).build();
    }

    /**
     * This yields the transactions for the specified account. The amount of data varies heavily between banks
     *
     * @param consentId                  consent which has access to the transactions of the specified account
     * @param accountId                  account that containes the requested transactions
     * @param transactionListRequestBean QueryParams bookingStatus, dateFrom and dateTo
     * @return TransactionContainer which holds the transactions/revenues grouped by booking status
     * @throws BankRequestFailedException in case the Request to the Bank failed
     * @documented https://confluence.petafuel.intern/display/TOOL/Styx+AIS+-+Interface+Definition#StyxAISInterfaceDefinition-YellowGET/v1/accounts/{resourceId}/transactions
     */
    @AcceptsPreStepAuth
    @GET
    @Path("/accounts/{resourceId}/transactions")
    public Response fetchTransactions(@NotNull @NotBlank @HeaderParam("consentId") String consentId,
                                      @NotNull @NotBlank @PathParam("resourceId") String accountId,
                                      @BeanParam @Valid TransactionListRequestBean transactionListRequestBean) throws BankRequestFailedException {
        xs2AFactoryInput.setConsentId(consentId);
        xs2AFactoryInput.setAccountId(accountId);
        xs2AFactoryInput.setBookingStatus(transactionListRequestBean.getBookingStatus());
        xs2AFactoryInput.setDateFrom(transactionListRequestBean.getDateFrom());
        xs2AFactoryInput.setDateTo(transactionListRequestBean.getDateTo());

        IOProcessor ioProcessor = new IOProcessor(getXS2AStandard());
        ioProcessor.modifyInput(xs2AFactoryInput);

        AISRequest readTransactionsRequest = new AISRequestFactory().create(getXS2AStandard().getRequestClassProvider().accountTransactionList(), xs2AFactoryInput);
        readTransactionsRequest.getHeaders().putAll(getAdditionalHeaders());

        ioProcessor.modifyRequest(readTransactionsRequest, xs2AFactoryInput);

        TransactionContainer transactionContainer = getXS2AStandard().getAis().getTransactionsByAccount(readTransactionsRequest);
        LOG.info("Successfully fetched transactions bic={}", getXS2AStandard().getAspsp().getBic());
        TransactionListResponseAdapter transactionListResponseAdapter = new TransactionListResponseAdapter(transactionContainer);
        return Response.status(200).entity(transactionListResponseAdapter).build();
    }
}
