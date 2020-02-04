package net.petafuel.styx.api.exception;

import javax.json.bind.annotation.JsonbProperty;
import javax.ws.rs.core.Response;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ErrorEntity implements Serializable {
    @JsonbProperty("category")
    private ErrorCategory category;

    @JsonbProperty("code")
    private Response.Status code;

    @JsonbProperty("message")
    private String message;

    @JsonbProperty("links")
    private Map<String, String> links;

    public ErrorEntity(String message, Response.Status errorCode, ErrorCategory errorCategory) {
        this.message = message;
        this.code = errorCode;
        category = errorCategory;
        links = new HashMap<>();
    }

    public ErrorCategory getCategory() {
        return category;
    }

    public void setCategory(ErrorCategory category) {
        this.category = category;
    }

    public Response.Status getCode() {
        return code;
    }

    public void setCode(Response.Status code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getLinks() {
        return links;
    }

    public void setLinks(Map<String, String> links) {
        this.links = links;
    }

    /**
     * Adds a link to a map, CAUTION: existing key will get overwritten
     *
     * @param key   name for the link
     * @param value href location of the link
     */
    public void addLinks(String key, String value) {
        this.links.put(key, value);
    }
}
