package net.petafuel.styx.api.v1.consent.boundary;

import net.petafuel.styx.api.filter.CheckAccessToken;
import net.petafuel.styx.core.banklookup.XS2AStandard;
import net.petafuel.styx.core.keepalive.tasks.ConsentPoll;
import net.petafuel.styx.core.keepalive.threads.ThreadManager;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Consent;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.Redirect;
import net.petafuel.styx.core.xs2a.sca.SCAApproach;
import net.petafuel.styx.core.xs2a.sca.SCAHandler;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.http.CreateConsentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.BerlinGroupCS;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.BerlinGroupSigner;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@ApplicationPath("/")
@Path("/v1")
@Produces({MediaType.APPLICATION_JSON + ";charset=UTF-8"})
@CheckAccessToken
public class ConsentResource extends Application {

    private static final Logger LOG = LogManager.getLogger(ConsentResource.class);

//    Reads the status of an account information consent resource.
    @GET
    @Path("/consent/status")
    public Response processAccountList() {
        String message= "Getting the Status of the Consent";
        LOG.info(message);
        return Response.status(200).entity(message).build();
    }

    @GET
    @Path("/consent/create")
    public Response createConsent() {
        LOG.info("create a consent");
        XS2AStandard standard = new XS2AStandard();
        standard.setCs(new BerlinGroupCS("https://xs2a-sndbx.consorsbank.de", new BerlinGroupSigner()));

        List<Account> balances = new LinkedList<>();
        balances.add(new Account("DE60760300800500123456"));

        List<Account> transactions = new LinkedList<>();
        transactions.add(new Account("DE60760300800500123456"));

        PSU psu = new PSU("PSU-Successful");
        Consent consent = new Consent();
        consent.getAccess().setBalances(balances);
        consent.getAccess().setTransactions(transactions);
        consent.setPsu(psu);
        consent.setCombinedServiceIndicator(false);
        consent.setRecurringIndicator(false);
        consent.setFrequencyPerDay(4);
        consent.setValidUntil(new Date());
        // build Request Body
        CreateConsentRequest createConsentRequest = new CreateConsentRequest(consent);
        createConsentRequest.setTppRedirectPreferred(true);

        try {
            consent = standard.getCs().createConsent(createConsentRequest);
        } catch (BankRequestFailedException e) {
            e.printStackTrace();
        }
        //Erhalte die für den Kunden relevante SCA Variante
        SCAApproach redirectSCA = SCAHandler.decision(consent);
        //Einreihen des ConsentPoll Tasks um den Consent abzufragen
        ThreadManager.getInstance().queueTask(new ConsentPoll(consent, standard.getCs()));
        //Gebe relevante informationen an den Client zurück
        String redirectLink = ((Redirect) redirectSCA).getAuthoriseLink();
        return Response.status(200).entity( redirectLink + "?psu-id=PSU-Successful").build();
    }
}
