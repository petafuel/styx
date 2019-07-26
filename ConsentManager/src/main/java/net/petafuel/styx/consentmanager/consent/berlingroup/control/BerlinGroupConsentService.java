package net.petafuel.styx.consentmanager.consent.berlingroup.control;

import net.petafuel.styx.consentmanager.consent.berlingroup.boundary.ASPSPConsentInterface;
import net.petafuel.styx.consentmanager.consent.berlingroup.entity.ConsentCreateResponse;
import net.petafuel.styx.consentmanager.consent.berlingroup.entity.GenericDummyResponse;
import net.petafuel.styx.utils.banklookup.BankInterfaceDescription;
import net.petafuel.styx.utils.http.ASPSPServiceFactory;
import net.petafuel.styx.utils.http.Signer;
import net.petafuel.styx.consentmanager.consent.ConsentServiceInterface;
import net.petafuel.styx.utils.http.XS2ARequest;
import net.petafuel.styx.utils.http.XS2AResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import retrofit2.Response;

import java.io.IOException;
import java.net.SocketTimeoutException;


public class BerlinGroupConsentService implements ConsentServiceInterface
{
    private Signer signer;
    private BankInterfaceDescription bankInterfaceDescription;
    private static final Logger LOG = LogManager.getLogger(BerlinGroupConsentService.class);

    public BerlinGroupConsentService(Signer signer, BankInterfaceDescription bankInterfaceDescription)
    {
        this.signer = signer;
        this.bankInterfaceDescription = bankInterfaceDescription;
    }

    @Override
    public XS2AResponse createConsent(XS2ARequest postConsent)
    {
        signer.sign(postConsent);

        ASPSPServiceFactory aspspServiceFactory = new ASPSPServiceFactory(this.bankInterfaceDescription.getUrl().toString());
        ASPSPConsentInterface service = aspspServiceFactory.createService(ASPSPConsentInterface.class);
        try
        {
            Response<ConsentCreateResponse> response = service.createConsent(postConsent.getHeaders(), postConsent.getBody()).execute();
            if (response.code() != 201)
            {
                LOG.error("Service request was unsuccessful: " + response.errorBody().string());
            }
            else
            {
                LOG.debug(response.raw());
                return response.body();
            }
        } catch (SocketTimeoutException socketTimeoutException)
        {
            LOG.error("ASPSP Endpoint was not reachable: " + socketTimeoutException.getMessage());

        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public GenericDummyResponse getConsent(XS2ARequest getConsent)
    {
        return null;
    }

    @Override
    public GenericDummyResponse getStatus(XS2ARequest getConsentStatus)
    {
        return null;
    }

    @Override
    public GenericDummyResponse deleteConsent(XS2ARequest deleteConsent)
    {
        return null;
    }
}
