package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2;

import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.contracts.SCARequest;
import net.petafuel.styx.core.xs2a.entities.LinkType;
import net.petafuel.styx.core.xs2a.entities.Links;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.sca.SCAUtils;
import okhttp3.Response;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import java.util.Map;

@SuppressWarnings("squid:S2176")
public class BerlinGroupCS extends net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.BerlinGroupCS {
    public BerlinGroupCS(String url, IXS2AHttpSigner signer) {
        super(url, signer);
    }

    @Override
    /*
     * BerlinGroup 1.2: the authorisationId of an Authorisation Resource is not a json key in the start SCA Request
     * Therefore we take it out of an authorisation link that contains this id
     */
    public SCA startAuthorisation(SCARequest xs2ARequest) throws BankRequestFailedException {
        this.setUrl(this.url + xs2ARequest.getServicePath());

        this.createBody(RequestType.POST, JSON, xs2ARequest);
        this.createHeaders(xs2ARequest);

        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String responseBody = extractResponseBody(response, 201);
            SCA sca = jsonb.fromJson(responseBody, SCA.class);
            SCAUtils.parseSCAApproach(sca, response);
            //extract the authorisation id out of an Href Object that contains the authorisations/... route
            for (Map.Entry<LinkType, Links.Href> entry : sca.getLinks().getUrlMapping().entrySet()) {
                if (entry.getValue().getUrl().contains("authorisations/")) {
                    String[] routeParts = entry.getValue().getUrl().split("/");
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