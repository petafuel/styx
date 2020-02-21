package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.petafuel.jsepa.SEPAParser;
import net.petafuel.jsepa.exception.SEPAParsingException;
import net.petafuel.jsepa.facades.ReportConverter;
import net.petafuel.jsepa.model.pain002.TransactionReport;
import net.petafuel.styx.core.xs2a.XS2APaymentInitiationRequest;
import net.petafuel.styx.core.xs2a.contracts.BasicService;
import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.contracts.PISInterface;
import net.petafuel.styx.core.xs2a.contracts.XS2AGetRequest;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;
import net.petafuel.styx.core.xs2a.sca.SCAUtils;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.PeriodicPaymentInitiationXMLRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.ReadPaymentStatusRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.InitiatedPaymentSerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.PaymentSerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.PaymentStatusSerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.PeriodicPaymentMultipartBodySerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.PeriodicPaymentSerializer;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.UUID;

public class BerlinGroupPIS extends BasicService implements PISInterface {

    private static final Logger LOG = LogManager.getLogger(BerlinGroupPIS.class);

    // PIS Endpoints
    private static final String INITIATE_PAYMENT = "/v1/%s/%s";
    private static final String GET_PAYMENT_STATUS = "/v1/%s/%s/%s/status";
    private static final String GET_PAYMENT = "/v1/%s/%s/%s";

    public BerlinGroupPIS(String url, IXS2AHttpSigner signer) {
        super(LOG, url, signer);
    }

    @Override
    public InitiatedPayment initiatePayment(XS2APaymentInitiationRequest xs2ARequest) throws BankRequestFailedException {

        PaymentProduct product = xs2ARequest.getPaymentProduct();
        PaymentService service = xs2ARequest.getPaymentService();

        if (product.isXml()) {
            if (xs2ARequest instanceof PeriodicPaymentInitiationXMLRequest) {
                //in case of multipart http message requestBody already created in request object
                this.createBody(RequestType.POST, ((PeriodicPaymentInitiationXMLRequest) xs2ARequest).getBody());
            } else {
                this.createBody(RequestType.POST, XML, xs2ARequest);
            }
        } else {
            this.createBody(RequestType.POST, JSON, xs2ARequest);
        }

        this.setUrl(this.url + String.format(INITIATE_PAYMENT, service.getValue(), product.getValue()));
        this.createHeaders(xs2ARequest);

        try (Response response = this.execute()) {
            String body = extractResponseBody(response, 201);

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(InitiatedPayment.class, new InitiatedPaymentSerializer())
                    .create();
            InitiatedPayment payment = gson.fromJson(body, InitiatedPayment.class);
            payment.setxRequestId(UUID.fromString(xs2ARequest.getHeaders().get("x-request-id")));

            //if the sca method was not set by previously parsing the body, use the bank supplied header
            SCAUtils.parseSCAApproach(payment.getSca(), response);
            return payment;
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public PaymentStatus getPaymentStatus(XS2ARequest xs2AGetRequest) throws BankRequestFailedException {

        ReadPaymentStatusRequest request = (ReadPaymentStatusRequest) xs2AGetRequest;

        this.setUrl(this.url + String.format(GET_PAYMENT_STATUS,
                request.getPaymentService().getValue(),
                request.getPaymentProduct().getValue(),
                request.getPaymentId()));
        this.createBody(RequestType.GET);
        if (request.getPaymentProduct().isXml()) {
            request.addHeader(XS2AHeader.ACCEPT, XML.toString());
        } else {
            request.addHeader(XS2AHeader.ACCEPT, JSON.toString());
        }
        this.createHeaders(request);
        try (Response response = this.execute()) {
            String contentType;
            if ((contentType = response.headers().get("content-type")) == null) {
                throw new BankRequestFailedException("Content-Type Header is not set, parsing of json or xml is not possible", response.code());
            }
            String responseBody = extractResponseBody(response, 200);

            if (contentType.contains(MediaType.APPLICATION_JSON)) {
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(PaymentStatus.class, new PaymentStatusSerializer())
                        .create();
                return gson.fromJson(responseBody, PaymentStatus.class);
            } else {
                ReportConverter converter = new ReportConverter();
                TransactionReport report = converter.processReport(responseBody);
                return new PaymentStatus(TransactionStatus.valueOf(report.getStatus()), null);
            }
        } catch (IOException | SEPAParsingException e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    //not possible to avoid code complexity at this point
    @SuppressWarnings("squid:S3776")
    @Override
    public InitializablePayment getPayment(XS2AGetRequest xs2AGetRequest) throws BankRequestFailedException {

        ReadPaymentRequest request = (ReadPaymentRequest) xs2AGetRequest;

        this.setUrl(this.url + String.format(GET_PAYMENT,
                request.getPaymentService().getValue(),
                request.getPaymentProduct().getValue(),
                request.getPaymentId())
        );
        this.createBody(RequestType.GET);
        if (request.getPaymentProduct().isXml()) {
            request.addHeader(XS2AHeader.ACCEPT, XML.toString());
        } else {
            request.addHeader(XS2AHeader.ACCEPT, JSON.toString());
        }
        this.createHeaders(request);
        try (Response response = this.execute()) {
            String contentType;
            if ((contentType = response.headers().get("content-type")) == null) {
                throw new BankRequestFailedException("Content-Type Header is not set, parsing of json or xml is not possible", response.code());
            }
            String responseBody = extractResponseBody(response, 200);
            if (contentType.contains(MediaType.APPLICATION_JSON)) {
                if (request.getPaymentService().equals(PaymentService.PERIODIC_PAYMENTS)) {
                    Gson periodicPaymentGson = new GsonBuilder()
                            .registerTypeAdapter(PeriodicPayment.class, new PeriodicPaymentSerializer())
                            .create();
                    return periodicPaymentGson.fromJson(responseBody, PeriodicPayment.class);
                } else if (request.getPaymentService().equals(PaymentService.PAYMENTS)) {
                    try (Jsonb jsonb = JsonbBuilder.create()) {
                        return jsonb.fromJson(responseBody, Payment.class);
                    } catch (Exception e) {
                        throw new SerializerException("Cannot parse aspsp response", e);
                    }
                }
                Gson gson = new GsonBuilder()
                        .registerTypeAdapter(InitializablePayment.class, new PaymentSerializer(request.getPaymentService()))
                        .create();
                return gson.fromJson(responseBody, InitializablePayment.class);
            } else {
                if (request.getPaymentService().equals(PaymentService.PERIODIC_PAYMENTS)) {
                    ByteArrayDataSource datasource = new ByteArrayDataSource(responseBody, "multipart/form-data");
                    MimeMultipart multipart = new MimeMultipart(datasource);

                    int count = multipart.getCount();
                    StringBuilder xmlSb = new StringBuilder();
                    StringBuilder jSonSb = new StringBuilder();
                    String str;
                    for (int i = 0; i < count; i++) {
                        BodyPart bodyPart = multipart.getBodyPart(i);
                        BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) bodyPart
                                .getContent()));
                        if (bodyPart.getContentType().contains("xml")) {
                            while ((str = reader.readLine()) != null) {
                                xmlSb.append(str);
                            }
                        } else {
                            while ((str = reader.readLine()) != null) {
                                jSonSb.append(str);
                            }
                        }
                    }
                    SEPAParser sepaParser = new SEPAParser(xmlSb.toString());
                    String jsonString = jSonSb.toString();
                    JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

                    return PeriodicPaymentMultipartBodySerializer.xmlDeserialize(sepaParser.parseSEPA()
                            .getSepaDocument(), jsonObject);
                } else {
                    SEPAParser sepaParser = new SEPAParser(responseBody);
                    return PaymentSerializer.xmlDeserialize(sepaParser.parseSEPA().getSepaDocument(), request.getPaymentService());
                }
            }
        } catch (IOException | SEPAParsingException | ParseException | MessagingException e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }
}
