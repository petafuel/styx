package net.petafuel.styx.consentmanager.consent.berlingroup.boundary;

import net.petafuel.styx.consentmanager.consent.berlingroup.entity.ConsentCreateResponse;
import net.petafuel.styx.consentmanager.consent.berlingroup.entity.GenericDummyResponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.Map;

public interface ASPSPConsentInterface
{
    @POST("v1/consents/")
    Call<ConsentCreateResponse> createConsent(@HeaderMap Map<String, String> headers, @Body RequestBody requestBody);

    @GET("v1/consents/{consentId}/")
    Call<GenericDummyResponse> getConsent(@HeaderMap Map<String, String> headers, @Path("consentId") String consentId, @Body RequestBody requestBody);

    @GET("v1/consents/{consentId}/status")
    Call<GenericDummyResponse> getConsentStatus(@HeaderMap Map<String, String> headers, @Path("consentId") String consentId, @Body RequestBody requestBody);

    @DELETE("v1/consents/{consentId}")
    Call<GenericDummyResponse> deleteConsent(@HeaderMap Map<String, String> headers, @Path("consentId") String consentId, @Body RequestBody requestBody);
}
