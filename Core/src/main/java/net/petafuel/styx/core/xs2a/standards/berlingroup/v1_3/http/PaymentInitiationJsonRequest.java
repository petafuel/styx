package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.petafuel.styx.core.xs2a.contracts.XS2AHeader;
import net.petafuel.styx.core.xs2a.contracts.XS2ARequest;
import net.petafuel.styx.core.xs2a.entities.PSU;
import net.petafuel.styx.core.xs2a.entities.Payment;
import net.petafuel.styx.core.xs2a.entities.PaymentProduct;
import net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers.PaymentInitiationJsonRequestSerializer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.UUID;

public class PaymentInitiationJsonRequest implements XS2ARequest {
	/**
	 * Headers
	 */
	@XS2AHeader(nested = true)
	private PSU psu;

	@XS2AHeader(XS2AHeader.X_REQUEST_ID)
	private String xRequestId;

	@XS2AHeader(XS2AHeader.TPP_REDIRECT_PREFERRED)
	private boolean tppRedirectPreferred;

	@XS2AHeader(XS2AHeader.TPP_REDIRECT_URL)
	private String tppRedirectUri;

	@XS2AHeader(XS2AHeader.DATE)
	private String date;

	/**
	 * Accumulated Headers
	 */
	private LinkedHashMap<String, String> headers = new LinkedHashMap<>();

	private Payment body;
	private PaymentProduct paymentProduct;

	public PaymentInitiationJsonRequest(PaymentProduct paymentProduct, Payment body, PSU psu) {
		this.paymentProduct = paymentProduct;
		this.body = body;
		this.psu = psu;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EE, d MM yyyy HH:mm:ss zz");
		this.date = simpleDateFormat.format(new Date());
		this.xRequestId = String.valueOf(UUID.randomUUID());
	}

	@Override
	public String getRawBody() {
		Gson gson = new GsonBuilder().registerTypeAdapter(PaymentInitiationJsonRequest.class, new PaymentInitiationJsonRequestSerializer()).create();
		return gson.toJson(this);
	}

	@Override
	public void setHeader(String key, String value) {
		this.headers.put(key, value);
	}

	@Override
	public LinkedHashMap<String, String> getHeaders() {
		return this.headers;
	}

	public Payment getBody() {
		return body;
	}

	public void setBody(Payment body) {
		this.body = body;
	}

	public PaymentProduct getPaymentProduct() {
		return paymentProduct;
	}

	public void setPaymentProduct(PaymentProduct paymentProduct) {
		this.paymentProduct = paymentProduct;
	}

	public PSU getPsu() {
		return psu;
	}

	public void setPsu(PSU psu) {
		this.psu = psu;
	}

	public String getxRequestId() {
		return xRequestId;
	}

	public void setxRequestId(String xRequestId) {
		this.xRequestId = xRequestId;
	}

	public boolean isTppRedirectPreferred() {
		return tppRedirectPreferred;
	}

	public void setTppRedirectPreferred(boolean tppRedirectPreferred) {
		this.tppRedirectPreferred = tppRedirectPreferred;
	}

	public String getTppRedirectUri() {
		return tppRedirectUri;
	}

	public void setTppRedirectUri(String tppRedirectUri) {
		this.tppRedirectUri = tppRedirectUri;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public void setHeaders(LinkedHashMap<String, String> headers) {
		this.headers = headers;
	}
}
