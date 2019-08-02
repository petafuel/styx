package net.petafuel.styx.utils.http;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

public class ASPSPServiceFactory
{
    private final String baseUrl;

    public ASPSPServiceFactory(String baseUrl)
    {
        this.baseUrl = baseUrl;
    }

    public <T> T createService(Class<T> xs2aService)
    {
        OkHttpClient.Builder okClientBuilder = new OkHttpClient.Builder().readTimeout(1000, TimeUnit.SECONDS);

        return new Retrofit.Builder()
                .baseUrl(this.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okClientBuilder.build())
                .build()
                .create(xs2aService);
    }
}