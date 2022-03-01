package net.petafuel.styx.api.exception;

import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.Arrays;

public enum ResponseConstant implements Response.StatusType, Serializable {
    /*
    Generic codes
    General codes that may happen on any occasion
     */
    OK(200, "OK"),
    CREATED(201, "Created"),
    ACCEPTED(202, "Accepted"),
    NO_CONTENT(204, "No Content"),
    RESET_CONTENT(205, "Reset Content"),
    PARTIAL_CONTENT(206, "Partial Content"),
    MOVED_PERMANENTLY(301, "Moved Permanently"),
    FOUND(302, "Found"),
    SEE_OTHER(303, "See Other"),
    NOT_MODIFIED(304, "Not Modified"),
    USE_PROXY(305, "Use Proxy"),
    TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    PAYMENT_REQUIRED(402, "Payment Required"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    REQUEST_TIMEOUT(408, "Request Timeout"),
    CONFLICT(409, "Conflict"),
    GONE(410, "Gone"),
    LENGTH_REQUIRED(411, "Length Required"),
    PRECONDITION_FAILED(412, "Precondition Failed"),
    REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
    REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested Range Not Satisfiable"),
    EXPECTATION_FAILED(417, "Expectation Failed"),
    PRECONDITION_REQUIRED(428, "Precondition Required"),
    TOO_MANY_REQUESTS(429, "Too Many Requests"),
    REQUEST_HEADER_FIELDS_TOO_LARGE(431, "Request Header Fields Too Large"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    BAD_GATEWAY(502, "Bad Gateway"),
    SERVICE_UNAVAILABLE(503, "Service Unavailable"),
    GATEWAY_TIMEOUT(504, "Gateway Timeout"),
    HTTP_VERSION_NOT_SUPPORTED(505, "HTTP Version Not Supported"),
    NETWORK_AUTHENTICATION_REQUIRED(511, "Network Authentication Required"),

    /*
    Styx codes
    Whether Styx encountered a problem
    */
    STYX_MISSING_CLIENT_TOKEN(401, "Styx API Token was not contained or empty in request"),
    STYX_INVALID_TOKEN_FORMAT(401, "Styx API Token value has an invalid format"),
    STYX_PREAUTH_HEADER_REQUIRED(409, "preAuthId Header missing, the requested ASPSP requires a pre-step authorisation"),
    STYX_PREAUTH_NOT_AVAILABLE(409, "preAuthId has no linked Authorisation, the requested ASPSP requires a pre-step authorisation"),
    STYX_PREAUTH_EXPIRED(409, "preAuthId is linked to an expired Authorisation, another pre-step is required"),
    STYX_TOKEN_EXPIRED_OR_REVOKED(401, "Styx API Token is expired or revoked"),
    STYX_TOKEN_ACCESS_VIOLATION(409, "Styx API Token is not authorized to invoke the request"),
    SAD_ASPSP_NOT_FOUND(409, "The requested ASPSP was not found within SAD"),
    STYX_PREAUTH_NOT_FOUND(409, "preAuthId not found"),
    SAD_ASPSP_INACTIVE(409, "Requested ASPSP is flagged inactive by styx"),
    STYX_TOKEN_ACCESS_EXEEDED(409, "Max usages of token reached"),
    STYX_MASTER_TOKEN_RESTRICTED(409, "Master token does not allow service type"),

    /*
    ASPSP client forwarded codes
    These codes will be forwared by styx to the client as the aspsp encountered a problem that was caused by a client
    and/or PSU. The ASPSP will provide additional informations within the message field
    */
    PAYMENT_FAILED(406, "Generic failure during payment initiation"),
    CONSENT_UNKNOWN(406, "The consent is not found within the ASPSP system"),
    CONSENT_INVALID(406, "Consent was not completed by the PSU or is used in a wrong/forbidden context"),
    CONSENT_EXPIRED(406, "Consent is expired and needs to be renewed"),
    PSU_CREDENTIALS_INVALID(406, "PSU unknown to the ASPSP, blocked, wrong password etc."),
    EXECUTION_DATE_INVALID(406, "Execution date in payment body is invalid"),
    ACCESS_EXCEEDED(406, "Consent access per-day without PSU involvment exceeded"),
    PERIOD_INVALID(406, "AIS requested period of transactions or balance is invalid"),
    FORMAT_ERROR(406, "Generic wrong format in payload body or headers"),

    /*
    ASPSP styx mapped codes
    These codes may happen if an error was encountered between styx and the aspsp that does not concern a client and/or
    may not be solved by a client
     */
    // Service unspecific codes
    CERTIFICATE_INVALID(500, "Styx TPP certificate is not valid"),
    ROLE_INVALID(500, "PSD2 role is not valid"),
    CERTIFICATE_EXPIRED(500, ""),
    CERTIFICATE_BLOCKED(500, ""),
    CERTIFICATE_REVOKED(500, ""),
    CERTIFICATE_MISSING(500, ""),
    SIGNATURE_INVALID	(500, "Application level eIDAS signature invalid"),
    SIGNATURE_MISSING(500, ""),
    PARAMETER_NOT_CONSISTENT(500, "Query parameters were not consistent"),
    PARAMETER_NOT_SUPPORTED(500, "An optional parameter is used but not supported by the bank"),
    SERVICE_INVALID(500, ""),
    SERVICE_BLOCKED(500, ""),
    CORPORATE_ID_INVALID(500, ""),
    TOKEN_UNKNOWN(500, ""),
    TOKEN_INVALID(500, ""),
    TOKEN_EXPIRED(500, ""),
    RESOURCE_UNKNOWN(500, ""),
    RESOURCE_EXPIRED(500, ""),
    RESOURCE_BLOCKED(500, ""),
    TIMESTAMP_INVALID(500, ""),
    SCA_METHOD_UNKNOWN(500, ""),
    SCA_INVALID(500, ""),
    STATUS_INVALID(500, ""),
    // PIS service
    PRODUCT_INVALID(500, ""),
    PRODUCT_UNKNOWN(500, ""),
    REQUIRED_KID_MISSING(500, ""),
    CANCELLATION_INVALID(500, ""),
    // AIS service
    SESSIONS_NOT_SUPPORTED(500, ""),
    REQUESTED_FORMATS_INVALID(500, ""),
    // PIIS service
    CARD_INVALID(500, ""),
    NO_PIIS_ACTIVATION(500, ""),
    // Signing Basket
    REFERENCE_MIX_INVALID(500, ""),
    REFERENCE_MIX_STATUS(500, "");


    private final int code;
    private final String reason;
    private final Response.Status.Family family;

    ResponseConstant(int statusCode, String reasonPhrase) {
        this.code = statusCode;
        this.reason = reasonPhrase;
        this.family = Response.Status.Family.familyOf(statusCode);
    }

    public static ResponseConstant fromStatusCode(int statusCode) {
        return Arrays.asList(values()).parallelStream()
                .filter(responseConstant -> responseConstant.code == statusCode)
                .findFirst()
                .orElse(null);
    }

    public static ResponseConstant getEnumByString(String code){
        for(ResponseConstant e : ResponseConstant.values()){
            if(e.name().equals(code)) return e;
        }
        return null;
    }

    @Override
    public int getStatusCode() {
        return code;
    }

    @Override
    public Response.Status.Family getFamily() {
        return family;
    }

    @Override
    public String getReasonPhrase() {
        return toString();
    }

    @Override
    public String toString() {
        return this.reason;
    }
}
