package net.petafuel.styx.core.xs2a.entities;

import javax.json.bind.annotation.JsonbProperty;
import java.util.List;

/**
 * This class holds Challenge data which will be presented to a PSU in order to solve a SCA
 */
public class Challenge {
    /**
     * PNG (max 512 kb in size) file, base64encoded
     * optional
     */
    private String image;

    /**
     * arbitrary data for the challenge
     * optional
     */
    private List<String> data;

    /**
     * A link to the ASPSP which will return the challenge image data
     * optional
     */
    private String imageLink;

    /**
     * Max characters that can be typed by the PSU for this OTP
     * optional
     */
    private Integer otpMaxLength;

    /**
     * Defines whether the OTP challenge solution should be integers or characters
     * optional
     */
    private OTP_FORMAT otpFormat;

    /**
     * Additional info and explanation for the PSU. TPPs are obliged to show this to the PSU
     * optional
     */
    private String additionalInformation;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public Integer getOtpMaxLength() {
        return otpMaxLength;
    }

    public void setOtpMaxLength(Integer otpMaxLength) {
        this.otpMaxLength = otpMaxLength;
    }

    public OTP_FORMAT getOtpFormat() {
        return otpFormat;
    }

    @JsonbProperty("otpFormat")
    public void setOtpFormatJson(String otpFormat) {
        this.otpFormat = OTP_FORMAT.valueOf(otpFormat.toUpperCase());
    }

    public void setOtpFormat(OTP_FORMAT otpFormat) {
        this.otpFormat = otpFormat;
    }

    public String getAdditionalInformation() {
        return additionalInformation;
    }

    public void setAdditionalInformation(String additionalInformation) {
        this.additionalInformation = additionalInformation;
    }

    public enum OTP_FORMAT {
        CHARACTERS("characters"),
        INTEGER("integer");

        private final String jsonValue;

        OTP_FORMAT(String jsonValue) {
            this.jsonValue = jsonValue;
        }

        public String value() {
            return jsonValue;
        }
    }
}
