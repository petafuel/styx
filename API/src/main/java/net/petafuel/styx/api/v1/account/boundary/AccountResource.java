package net.petafuel.styx.api.v1.account.boundary;

import net.petafuel.styx.api.filter.AcceptsPreStepAuth;
import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.rest.RestResource;
import net.petafuel.styx.api.util.AspspUrlMapper;
import net.petafuel.styx.api.util.io.IOProcessor;
import net.petafuel.styx.api.util.io.contracts.IOInputContainerAIS;
import net.petafuel.styx.api.v1.account.control.AccountListResponseAdapter;
import net.petafuel.styx.api.v1.account.control.TransactionListResponseAdapter;
import net.petafuel.styx.api.v1.account.entity.AccountDetailResponse;
import net.petafuel.styx.api.v1.account.entity.TransactionListRequestBean;
import net.petafuel.styx.core.persistence.models.AccessToken;
import net.petafuel.styx.core.xs2a.entities.AccountDetails;
import net.petafuel.styx.core.xs2a.entities.BalanceContainer;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.TransactionContainer;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadAccountDetailsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadAccountListRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadBalancesRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadTransactionsRequest;
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
@CheckAccessToken(allowedServices = {AccessToken.ServiceType.AISPIS, AccessToken.ServiceType.AIS})
public class AccountResource extends RestResource {
    private static final Logger LOG = LogManager.getLogger(AccountResource.class);


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
        ReadAccountListRequest accountListRequest = new ReadAccountListRequest(consentId);
        accountListRequest.getHeaders().putAll(getAdditionalHeaders());
        IOInputContainerAIS ioInputContainerAIS = new IOInputContainerAIS(getXS2AStandard(), new PSU());
        ioInputContainerAIS.setAisRequest(accountListRequest);
        IOProcessor ioProcessor = new IOProcessor(ioInputContainerAIS);
        accountListRequest = (ReadAccountListRequest) ioProcessor.applyOptions();
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
        ReadAccountDetailsRequest accountDetailsRequest = new ReadAccountDetailsRequest(accountId, consentId);
        accountDetailsRequest.getHeaders().putAll(getAdditionalHeaders());
        IOInputContainerAIS ioInputContainerAIS = new IOInputContainerAIS(getXS2AStandard(), new PSU());
        ioInputContainerAIS.setAisRequest(accountDetailsRequest);
        IOProcessor ioProcessor = new IOProcessor(ioInputContainerAIS);
        accountDetailsRequest = (ReadAccountDetailsRequest) ioProcessor.applyOptions();
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
        ReadBalancesRequest readBalancesRequest = new ReadBalancesRequest(accountId, consentId);
        readBalancesRequest.getHeaders().putAll(getAdditionalHeaders());
        IOInputContainerAIS ioInputContainerAIS = new IOInputContainerAIS(getXS2AStandard(), new PSU());
        ioInputContainerAIS.setAisRequest(readBalancesRequest);
        IOProcessor ioProcessor = new IOProcessor(ioInputContainerAIS);
        readBalancesRequest = (ReadBalancesRequest) ioProcessor.applyOptions();
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
        ReadTransactionsRequest readTransactionsRequest = new ReadTransactionsRequest(
                accountId,
                consentId,
                transactionListRequestBean.getBookingStatus(),
                transactionListRequestBean.getDateFrom(),
                transactionListRequestBean.getDateTo());

        readTransactionsRequest.getHeaders().putAll(getAdditionalHeaders());
        IOInputContainerAIS ioInputContainerAIS = new IOInputContainerAIS(getXS2AStandard(), new PSU());
        ioInputContainerAIS.setAisRequest(readTransactionsRequest);
        IOProcessor ioProcessor = new IOProcessor(ioInputContainerAIS);
        readTransactionsRequest = (ReadTransactionsRequest) ioProcessor.applyOptions();

        TransactionContainer transactionContainer = getXS2AStandard().getAis().getTransactionsByAccount(readTransactionsRequest);
        LOG.info("Successfully fetched transactions bic={}", getXS2AStandard().getAspsp().getBic());
        TransactionListResponseAdapter transactionListResponseAdapter = new TransactionListResponseAdapter(transactionContainer);
        return Response.status(200).entity(transactionListResponseAdapter).build();
    }
}
