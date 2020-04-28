package net.petafuel.styx.core.xs2a.entities;

/**
 * This is a response entity ASPSP return from their xs2a REST interface in case an error occurred
 */
public class TPPMessageInformation {
    private Category category;
    private String code;
    private String path;
    private String text;

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public enum Category {
        ERROR,
        WARNING
    }
}
