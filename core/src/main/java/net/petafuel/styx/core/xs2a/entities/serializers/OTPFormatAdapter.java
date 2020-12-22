package net.petafuel.styx.core.xs2a.entities.serializers;

import net.petafuel.styx.core.xs2a.entities.Challenge;

import javax.json.bind.adapter.JsonbAdapter;

public class OTPFormatAdapter implements JsonbAdapter<Challenge.OTP_FORMAT, String> {

    @Override
    public String adaptToJson(Challenge.OTP_FORMAT value) {
        return value.toString();
    }

    @Override
    public Challenge.OTP_FORMAT adaptFromJson(String s) {
        return Challenge.OTP_FORMAT.getValue(s);
    }
}
