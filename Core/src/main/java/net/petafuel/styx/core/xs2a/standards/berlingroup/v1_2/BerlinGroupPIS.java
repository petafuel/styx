package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.contracts.XS2AAuthorisationRequest;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.SCAUtils;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.SCASerializer;
import okhttp3.Response;

import java.util.Map;

/**
 * supressing warning because of PIS 1.2 is the same as 1.3
 **/
@SuppressWarnings("squid:S2176")
public class BerlinGroupPIS extends net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.BerlinGroupPIS {
    public BerlinGroupPIS(String url, IXS2AHttpSigner signer) {
        super(url, signer);
    }

    @Override
    /*
     * BerlinGroup 1.2: the authorisationId of an Authorisation Resource is not a json key in the start SCA Request
     * Therefore we take it out of an authorisation link that contains this id
     */
    public SCA startAuthorisation(XS2AAuthorisationRequest xs2ARequest) throws BankRequestFailedException {
        this.setUrl(this.url + xs2ARequest.getServiceURL());

        this.createBody(RequestType.POST, JSON, xs2ARequest);
        this.createHeaders(xs2ARequest);

        try (Response response = this.execute()) {
            String responseBody = extractResponseBody(response, 201);
            Gson gson = new GsonBuilder().registerTypeAdapter(SCA.class, new SCASerializer()).create();
            SCA sca = gson.fromJson(responseBody, SCA.class);
            SCAUtils.parseSCAApproach(sca, response);
            //extract the authorisation id out of an Href Object that contains the authorisations/... route
            for (Map.Entry<SCA.LinkType, String> entry : sca.getLinks().entrySet()) {
                if (entry.getValue().contains("authorisations/")) {
                    String[] routeParts = entry.getValue().split("/");
                    sca.setAuthorisationId(routeParts[routeParts.length - 1]);
                    break;
                }
            }
            return sca;
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }
}
