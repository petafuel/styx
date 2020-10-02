package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3;

import net.petafuel.jsepa.SEPAParser;
import net.petafuel.jsepa.exception.SEPAParsingException;
import net.petafuel.jsepa.facades.ReportConverter;
import net.petafuel.jsepa.model.pain002.TransactionReport;
import net.petafuel.styx.core.xs2a.contracts.IXS2AHttpSigner;
import net.petafuel.styx.core.xs2a.contracts.PISInterface;
import net.petafuel.styx.core.xs2a.contracts.PISRequest;
import net.petafuel.styx.core.xs2a.contracts.SCARequest;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.entities.BulkPayment;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.InitiatedPayment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PaymentStatus;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.entities.SCA;
import net.petafuel.styx.core.xs2a.entities.SinglePayment;
import net.petafuel.styx.core.xs2a.entities.TransactionStatus;
import net.petafuel.styx.core.xs2a.exceptions.BankRequestFailedException;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;
import net.petafuel.styx.core.xs2a.sca.SCAUtils;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.AuthoriseTransactionRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetAuthorisationsRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.GetSCAStatusRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.SelectAuthenticationMethodRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.StartAuthorisationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.UpdatePSUAuthenticationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http.UpdatePSUIdentificationRequest;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.PaymentSerializer;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.PeriodicPaymentMultipartBodySerializer;
import okhttp3.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.mail.BodyPart;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.ws.rs.core.MediaType;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;

public class BerlinGroupPIS extends BasicAuthorisationService implements PISInterface {
    private static final Logger LOG = LogManager.getLogger(BerlinGroupPIS.class);

    public BerlinGroupPIS(String url, IXS2AHttpSigner signer) {
        super(LOG, url, signer);
    }

    @Override
    public InitiatedPayment initiatePayment(PISRequest xs2ARequest) throws BankRequestFailedException {
        PaymentProduct product = xs2ARequest.getPaymentProduct();

        if (product.isXml()) {
            if (xs2ARequest.getPaymentService().equals(PaymentService.PERIODIC_PAYMENTS)) {
                //in case of multipart http message get boundary from request
                okhttp3.MediaType mt = okhttp3.MediaType.get("multipart/form-data; boundary=" + xs2ARequest.getMultipartBoundary());
                this.createBody(RequestType.POST, mt, xs2ARequest);
            } else {
                this.createBody(RequestType.POST, XML, xs2ARequest);
            }
        } else {
            this.createBody(RequestType.POST, JSON, xs2ARequest);
        }

        this.setUrl(this.url + xs2ARequest.getServicePath());
        this.createHeaders(xs2ARequest);

        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String body = extractResponseBody(response, 201);

            InitiatedPayment payment = jsonb.fromJson(body, InitiatedPayment.class);
            payment.setxRequestId(UUID.fromString(xs2ARequest.getHeaders().get("x-request-id")));

            payment.getSca().setApproach(SCAUtils.parseSCAApproach(payment.getLinks(), response));
            return payment;
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }

    @Override
    public PaymentStatus getPaymentStatus(PISRequest xs2AGetRequest) throws BankRequestFailedException {
        this.setUrl(this.url + xs2AGetRequest.getServicePath());
        this.createBody(RequestType.GET);
        if (xs2AGetRequest.getPaymentProduct().isXml()) {
            xs2AGetRequest.addHeader(XS2AHeader.ACCEPT, XML.toString());
        } else {
            xs2AGetRequest.addHeader(XS2AHeader.ACCEPT, JSON.toString());
        }
        this.createHeaders(xs2AGetRequest);
        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String contentType;
            if ((contentType = response.headers().get("content-type")) == null) {
                throw new BankRequestFailedException("Content-Type Header is not set, parsing of json or xml is not possible", response.code());
            }
            String responseBody = extractResponseBody(response, 200);

            if (contentType.contains(MediaType.APPLICATION_JSON)) {
                return jsonb.fromJson(responseBody, PaymentStatus.class);
            } else {
                ReportConverter converter = new ReportConverter();
                TransactionReport report = converter.processReport(responseBody);
                return new PaymentStatus(TransactionStatus.valueOf(report.getStatus()), null);
            }
        } catch (IOException | SEPAParsingException e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        } catch (Exception e) {
            throw new SerializerException("Unable to deserialize json to PaymentStatus", e);
        }
    }

    //not possible to avoid code complexity at this point
    @SuppressWarnings("squid:S3776")
    @Override
    public InitializablePayment getPayment(PISRequest xs2AGetRequest) throws BankRequestFailedException {
        this.setUrl(this.url + xs2AGetRequest.getServicePath());
        this.createBody(RequestType.GET);
        if (xs2AGetRequest.getPaymentProduct().isXml()) {
            xs2AGetRequest.addHeader(XS2AHeader.ACCEPT, XML.toString());
        } else {
            xs2AGetRequest.addHeader(XS2AHeader.ACCEPT, JSON.toString());
        }
        this.createHeaders(xs2AGetRequest);
        try (Response response = this.execute(); Jsonb jsonb = JsonbBuilder.create()) {
            String contentType;
            if ((contentType = response.headers().get("content-type")) == null) {
                throw new BankRequestFailedException("Content-Type Header is not set, parsing of json or xml is not possible", response.code());
            }
            String responseBody = extractResponseBody(response, 200);
            if (contentType.contains(MediaType.APPLICATION_JSON)) {
                if (xs2AGetRequest.getPaymentService().equals(PaymentService.PERIODIC_PAYMENTS)) {
                    return jsonb.fromJson(responseBody, PeriodicPayment.class);
                } else if (xs2AGetRequest.getPaymentService().equals(PaymentService.PAYMENTS)) {
                    return jsonb.fromJson(responseBody, SinglePayment.class);
                } else {
                    return jsonb.fromJson(responseBody, BulkPayment.class);
                }
            } else {
                if (xs2AGetRequest.getPaymentService().equals(PaymentService.PERIODIC_PAYMENTS)) {
                    ByteArrayDataSource datasource = new ByteArrayDataSource(responseBody, "multipart/form-data");
                    MimeMultipart multipart = new MimeMultipart(datasource);

                    int count = multipart.getCount();
                    StringBuilder xmlSb = new StringBuilder();
                    StringBuilder jsonStringBuilder = new StringBuilder();
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
                                jsonStringBuilder.append(str);
                            }
                        }
                    }
                    SEPAParser sepaParser = new SEPAParser(xmlSb.toString());
                    String jsonString = jsonStringBuilder.toString();

                    return PeriodicPaymentMultipartBodySerializer.xmlDeserialize(sepaParser.parseSEPA()
                            .getSepaDocument(), jsonb.fromJson(jsonString, PeriodicPayment.class));
                } else {
                    SEPAParser sepaParser = new SEPAParser(responseBody);
                    return PaymentSerializer.xmlDeserialize(sepaParser.parseSEPA().getSepaDocument(), xs2AGetRequest.getPaymentService());
                }
            }
        } catch (Exception e) {
            throw new BankRequestFailedException(e.getMessage(), e);
        }
    }


    @Override
    public SCA startAuthorisation(SCARequest xs2ARequest) throws BankRequestFailedException {
        return super.startAuthorisation((StartAuthorisationRequest) xs2ARequest);
    }

    @Override
    public List<String> getAuthorisations(SCARequest xs2AAuthorisationRequest) throws BankRequestFailedException {
        return super.getAuthorisations((GetAuthorisationsRequest) xs2AAuthorisationRequest);
    }

    @Override
    public SCA.Status getSCAStatus(SCARequest xs2AAuthorisationRequest) throws BankRequestFailedException {
        return super.getSCAStatus((GetSCAStatusRequest) xs2AAuthorisationRequest);
    }

    @Override
    public SCA updatePSUIdentification(SCARequest xs2ARequest) throws BankRequestFailedException {
        return super.updatePSUIdentification((UpdatePSUIdentificationRequest) xs2ARequest);
    }

    @Override
    public SCA updatePSUAuthentication(SCARequest xs2ARequest) throws BankRequestFailedException {
        return super.updatePSUAuthentication((UpdatePSUAuthenticationRequest) xs2ARequest);
    }

    @Override
    public SCA selectAuthenticationMethod(SCARequest xs2ARequest) throws BankRequestFailedException {
        return super.selectAuthenticationMethod((SelectAuthenticationMethodRequest) xs2ARequest);
    }

    @Override
    public SCA authoriseTransaction(SCARequest xs2AAuthorisationRequest) throws BankRequestFailedException {
        return super.authoriseTransaction((AuthoriseTransactionRequest) xs2AAuthorisationRequest);
    }
}
