package net.petafuel.styx.api.v1.account.boundary;

import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.api.filter.RequiresBIC;
import net.petafuel.styx.api.rest.PSUResource;
import net.petafuel.styx.api.service.SADService;
import net.petafuel.styx.api.util.AspspUrlMapper;
import net.petafuel.styx.api.v1.account.entity.AccountDetailResponse;
import net.petafuel.styx.core.persistence.models.AccessToken;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.AccountDetails;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadAccountDetailsRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.petafuel.styx.core.xs2a.entities.AccountListResponse;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.ReadAccountListRequest;

import javax.inject.Inject;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.ws.rs.ApplicationPath;
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
public class AccountResource extends PSUResource {

    private static final Logger LOG = LogManager.getLogger(AccountResource.class);
    @Inject
    private SADService sadService;

    /**
     * Returns a List of Accounts
     *
     * @param consentId
     * @return returns an account list
     * @documented https://confluence.petafuel.intern/display/TOOL/Styx+AIS+-+Interface+Definition#StyxAISInterfaceDefinition-YellowGET/v1/accounts
     * @see AccountListResponse
     */
    @GET
    @Path("/accounts")
    public Response processAccountList(@NotNull @NotBlank @HeaderParam("consentId") String consentId) throws BankRequestFailedException {
        ReadAccountListRequest accountListRequest = new ReadAccountListRequest(consentId);
        accountListRequest.getHeaders().putAll(getSandboxHeaders());
        List<Account> accountList = sadService.getXs2AStandard().getAis().getAccountList(accountListRequest);

        return Response.status(200).entity(new AccountListResponse(accountList)).build();
    }


    /**
     * Returns AccountDetails for a single account
     *
     * @param accountId the xs2a account id
     * @return returns an account object
     * @documented https://confluence.petafuel.intern/display/TOOL/Styx+AIS+-+Interface+Definition#StyxAISInterfaceDefinition-YellowGET/v1/accounts/{resourceId}
     * @see AccountDetails
     */
    @GET
    @Path("/accounts/{resourceId}")
    public Response getAccountDetails(@NotNull @NotBlank @HeaderParam("consentId") String consentId, @NotNull @NotBlank @PathParam("resourceId") String accountId) throws BankRequestFailedException {
        ReadAccountDetailsRequest accountDetailsRequest = new ReadAccountDetailsRequest(accountId, consentId);
        accountDetailsRequest.getHeaders().putAll(getSandboxHeaders());
        AccountDetails account = sadService.getXs2AStandard().getAis().getAccount(accountDetailsRequest);
        account.setLinks(new AspspUrlMapper(account.getResourceId()).map(account.getLinks()));

        LOG.info("Successfully fetched account details bic={}", sadService.getXs2AStandard().getAspsp().getBic());

        return Response.status(200).entity(new AccountDetailResponse(account)).build();
    }

//    Reads account data from a given account addressed by account id.
    @GET
    @Path("/account/balances/{account_id}")
    public Response processAccountBalances(@PathParam("account_id") String accountId) {
        String message = "Getting Balance of Account with the ID: " + accountId;
        LOG.info(message);
        return Response.status(200).entity(message).build();
    }

//    Reads a transaction list of booked transactions of a given account addressed by account id.
    @GET
    @Path("/account/transactions/{account_id}")
    public Response processAccountTransactions(@PathParam("account_id") String accountId) {
        String message = "Getting Transactions of Account with the ID: " + accountId;
        LOG.info(message);
        return Response.status(200).entity(message).build();
    }
}
