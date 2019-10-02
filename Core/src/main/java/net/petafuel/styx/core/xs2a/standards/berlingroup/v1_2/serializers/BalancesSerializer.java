package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers;

import com.google.gson.*;
import net.petafuel.styx.core.xs2a.entities.Balance;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BalancesSerializer implements JsonDeserializer<List<Balance>> {

    public List<Balance> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        ArrayList<Balance> result = new ArrayList<>();
        JsonObject response = json.getAsJsonObject();
        JsonArray jsonArray = response.getAsJsonArray("balances");
        for (JsonElement element : jsonArray) {
            result.add(this.mapToModel((JsonObject) element));
        }
        return result;
    }

    public Balance mapToModel(JsonObject object) {

        if (object.get("balanceType") == null) {
            throw new SerializerException("Unable to deserialize the Balance Object: Attribute balanceType not given");
        }
        Balance.Type type = Balance.Type.valueOf(
                object.get("balanceType").getAsString()
                        // camelCase to SNAKE_CASE
                        .replaceAll("(.)(\\p{Upper})", "$1_$2").toUpperCase()
        );
        if (object.get("balanceAmount") == null) {
            throw new SerializerException("Unable to deserialize the Balance Object: 'balanceAmount' not given");
        }
        JsonObject amountObj = object.get("balanceAmount").getAsJsonObject();
        Currency currency = Currency.valueOf(amountObj.get("currency").getAsString().toUpperCase());
        float amount = amountObj.get("amount").getAsFloat();
        Balance b1 = new Balance(amount, type, currency);

        try {
            String referenceDateString = object.get("referenceDate").getAsString();
            Date referenceDate = new SimpleDateFormat("yyyy-MM-dd").parse(referenceDateString);
            b1.setReferenceDate(referenceDate);
        } catch (Exception ignored) {
        }

        try {
            String lastChangeDateTime = object.get("lastChangeDateTime").getAsString();
            Date referenceDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(lastChangeDateTime);
            b1.setLastChangeDateTime(referenceDate);
        } catch (Exception ignored) {
        }

        return b1;
    }
}
