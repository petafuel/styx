package net.petafuel.styx.api.exception;

import javax.json.bind.annotation.JsonbProperty;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ResponseEntity implements Serializable {
    @JsonbProperty("origin")
    private ResponseOrigin origin;

    @JsonbProperty("category")
    private ResponseCategory category;

    @JsonbProperty("code")
    private ResponseConstant code;

    @JsonbProperty("message")
    private String message;

    @JsonbProperty(value = "links", nillable = true)
    private Map<String, String> links;

    /**
     * only for default constructor in json binding
     */
    public ResponseEntity() {
        //ctor for json binding
    }

    public ResponseEntity(String message, ResponseConstant statusType, ResponseCategory responseCategory, ResponseOrigin responseOrigin) {
        this.message = !"".equals(message) ? message : statusType.getReasonPhrase();
        this.code = statusType;
        category = responseCategory;
        origin = responseOrigin;
        links = new HashMap<>();
    }

    public ResponseEntity(ResponseConstant statusType, ResponseCategory responseCategory, ResponseOrigin responseOrigin) {
        this.message = statusType.getReasonPhrase();
        this.code = statusType;
        category = responseCategory;
        origin = responseOrigin;
        links = new HashMap<>();
    }

    public ResponseCategory getCategory() {
        return category;
    }

    public void setCategory(ResponseCategory category) {
        this.category = category;
    }

    public ResponseConstant getCode() {
        return code;
    }

    public void setCode(ResponseConstant code) {
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

    public ResponseOrigin getOrigin() {
        return origin;
    }

    public void setOrigin(ResponseOrigin origin) {
        this.origin = origin;
    }
}
