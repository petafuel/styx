package net.petafuel.styx.api.exception;

import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.Arrays;

public enum ResponseConstant implements Response.StatusType, Serializable {
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

    STYX_MISSING_CLIENT_TOKEN(400, "Styx API Token was not contained or empty in request"),
    STYX_INVALID_TOKEN_FORMAT(400, "Styx API Token value has an invalid format"),
    STYX_PREAUTH_HEADER_REQUIRED(400, "preAuthId Header missing, the requested ASPSP requires a pre-step authorisation"),
    STYX_PREAUTH_NOT_AVAILABLE(400, "preAuthId has no linked Authorisation, the requested ASPSP requires a pre-step authorisation"),
    STYX_PREAUTH_EXPIRED(400, "preAuthId is linked to an expired Authorisation, another pre-step is required"),
    STYX_TOKEN_EXPIRED_OR_REVOKED(401, "Styx API Token is expired or revoked"),
    STYX_TOKEN_ACCESS_VIOLATION(403, "Styx API Token is not authorized to invoke the request"),
    SAD_ASPSP_NOT_FOUND(404, "The requested ASPSP was not found within SAD"),
    STYX_PREAUTH_NOT_FOUND(404, "preAuthId not found");

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
