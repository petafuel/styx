package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import net.petafuel.jsepa.SEPAWriter;
import net.petafuel.jsepa.exception.SEPAWriteException;
import net.petafuel.jsepa.model.PAIN00100303Document;
import net.petafuel.jsepa.model.PaymentInstructionInformation;
import net.petafuel.styx.core.xs2a.contracts.PISRequest;
import net.petafuel.styx.core.xs2a.entities.BulkPayment;
import net.petafuel.styx.core.xs2a.entities.BulkPaymentAdapter;
import net.petafuel.styx.core.xs2a.entities.InitializablePayment;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.entities.PaymentService;
import net.petafuel.styx.core.xs2a.entities.PeriodicPayment;
import net.petafuel.styx.core.xs2a.entities.SinglePayment;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;
import net.petafuel.styx.core.xs2a.utils.PaymentXMLSerializer;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okio.Buffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public class PaymentInitiationRequest extends PISRequest {
    private static final Logger LOG = LogManager.getLogger(PaymentInitiationRequest.class);
    private String body;

    public PaymentInitiationRequest(PaymentService paymentService, PaymentProduct paymentProduct, PSU psu, InitializablePayment payment) {
        super(paymentService, paymentProduct, psu, payment);
        this.body = null;
        setMultipartBoundary(UUID.randomUUID().toString());
    }

    @Override
    public Optional<String> getRawBody() {
        //if the body was already written within this request instance, do not write it again
        if (body != null) {
            return Optional.of(body);
        }

        Optional<String> requestBody;
        switch (paymentService) {
            case PAYMENTS:
                requestBody = createSinglePayment();
                break;
            case BULK_PAYMENTS:
                requestBody = createBulkPayment();
                break;
            case PERIODIC_PAYMENTS:
                requestBody = createPeriodicPayment();
                break;
            default:
                throw new IllegalArgumentException("Unknown payment service. Cannot create Request body");
        }
        requestBody.ifPresent(concreteBody -> body = concreteBody);
        return requestBody;
    }

    @Override
    public String getServicePath() {
        return String.format("/v1/%s/%s", getPaymentService().getValue(), getPaymentProduct().getValue());
    }

    /*
     * @return payment request body for sepa-single-payments in xml or json as String
     */
    private Optional<String> createSinglePayment() {
        if (getPaymentProduct().isXml()) {
            PAIN00100303Document document = (new PaymentXMLSerializer()).serialize(UUID.randomUUID().toString(), (SinglePayment) payment);
            PaymentInstructionInformation xmlPayment = document.getCctInitiation().getPmtInfos().get(0);

            if (this.getPaymentProduct().equals(PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS) && (xmlPayment.getRequestedExecutionDate() == null || "".equals(xmlPayment.getRequestedExecutionDate()))) {
                xmlPayment.setRequestedExecutionDate(document.getCctInitiation().getGrpHeader().getCreationTime());
                document.getCctInitiation().getPmtInfos().set(0, xmlPayment);
            }

            SEPAWriter writer = new SEPAWriter(document);
            try {
                return Optional.of(new String(writer.writeSEPA()));
            } catch (SEPAWriteException exception) {
                LOG.warn("Error creating raw body for single payment initiation message={} cause={}", exception.getMessage(), exception.getCause().getCause());
                return Optional.empty();
            }
        } else {
            JsonbConfig jsonbConfig = new JsonbConfig();
            jsonbConfig.withNullValues(false);
            jsonbConfig.withLocale(Locale.GERMANY);
            jsonbConfig.withDateFormat("yyyy-MM-dd", Locale.GERMANY);
            try (Jsonb jsonb = JsonbBuilder.create(jsonbConfig)) {
                String paymentBodyJson = jsonb.toJson(payment);
                LOG.debug("Outgoing Single Payment Body={}", paymentBodyJson);
                return Optional.ofNullable(paymentBodyJson);
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }

    private Optional<String> createBulkPayment() {
        if (getPaymentProduct().isXml()) {
            PAIN00100303Document document = (new PaymentXMLSerializer()).serialize(UUID.randomUUID().toString(), (BulkPayment) payment);
            PaymentInstructionInformation xmlPayment = document.getCctInitiation().getPmtInfos().get(0);

            if (this.getPaymentProduct().equals(PaymentProduct.PAIN_001_SEPA_CREDIT_TRANSFERS) && (xmlPayment.getRequestedExecutionDate() == null || "".equals(xmlPayment.getRequestedExecutionDate()))) {
                xmlPayment.setRequestedExecutionDate(document.getCctInitiation().getGrpHeader().getCreationTime());
                document.getCctInitiation().getPmtInfos().set(0, xmlPayment);
            }

            SEPAWriter writer = new SEPAWriter(document);
            try {
                return Optional.of(new String(writer.writeSEPA()));
            } catch (SEPAWriteException exception) {
                LOG.warn("Error creating raw body for PaymentInitiationPain001Request message={} cause={}", exception.getMessage(), exception.getCause().getCause());
                return Optional.empty();
            }
        } else {
            try (Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withAdapters(new BulkPaymentAdapter()))) {
                return Optional.ofNullable(jsonb.toJson(payment));
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }

    private Optional<String> createPeriodicPayment() {
        if (getPaymentProduct().isXml()) {
            PAIN00100303Document document = (new PaymentXMLSerializer()).serialize(UUID.randomUUID().toString(), (PeriodicPayment) payment);
            SEPAWriter writer = new SEPAWriter(document);
            String painXmlBody;
            try {
                painXmlBody = new String(writer.writeSEPA());
            } catch (SEPAWriteException e) {
                throw new SerializerException("Unable to create xml body for periodic payment initiation multipart http message");
            }
            Headers headersXml = new Headers.Builder()
                    .add("Content-Disposition", "form-data; name=\"xml_sct\"")
                    .build();
            MultipartBody.Part xmlPart = MultipartBody.Part.create(headersXml, RequestBody.create(painXmlBody, MediaType.get("application/xml; charset=utf-8")));

            Headers headersJson = new Headers.Builder()
                    .add("Content-Disposition", "form-data; name=\"json_standingorderType\"")
                    .build();

            MultipartBody.Part jsonPart;
            try (Jsonb jsonb = JsonbBuilder.create()) {
                jsonPart = MultipartBody.Part.create(headersJson, RequestBody.create(jsonb.toJson(payment), MediaType.get("application/json; charset=utf-8")));
            } catch (Exception e) {
                throw new SerializerException(e.getMessage());
            }

            //add a uuid as boundary to avoid boundary repetition in http message
            RequestBody requestBody = new MultipartBody.Builder(getMultipartBoundary())
                    .addPart(xmlPart)
                    .addPart(jsonPart)
                    .setType(MultipartBody.FORM)
                    .build();

            final Buffer buffer = new Buffer();
            try {
                //build raw multipartBody with xml and json
                requestBody.writeTo(buffer);
                return Optional.of(buffer.readUtf8());
            } catch (IOException e) {
                LOG.warn("Error creating raw body for periodic payment initiation message={} cause={}", e.getMessage(), e.getCause().getCause());
                return Optional.empty();
            }
        } else {
            try (Jsonb jsonb = JsonbBuilder.create()) {
                return Optional.ofNullable(jsonb.toJson(payment));
            } catch (Exception e) {
                return Optional.empty();
            }
        }
    }
}
