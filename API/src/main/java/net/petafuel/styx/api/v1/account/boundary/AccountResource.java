package net.petafuel.styx.api.v1.account.boundary;

import net.petafuel.styx.api.filters.CheckAccessToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken
public class AccountResource extends Application {

    private static final Logger LOG = LogManager.getLogger(AccountResource.class);

//    Reads the accounts of the available payment account depending on the consent granted.
    @GET
    @Path("/account/list")
    public Response processAccountList() {
        String message = "Getting List of Accounts";
        LOG.info(message);
        return Response.status(200).entity(message).build();
    }

//    Reads details about an account.
    @GET
    @Path("/account/details/{account_id}")
    public Response processAccountDetails(@PathParam("account_id") String accountId) {
        String message = "Getting Details of Account with the ID: " + accountId;
        LOG.info(message);
        return Response.status(200).entity(message).build();
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
