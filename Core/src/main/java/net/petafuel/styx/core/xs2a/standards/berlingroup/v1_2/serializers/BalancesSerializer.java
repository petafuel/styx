package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_2.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.petafuel.styx.core.xs2a.entities.BalanceDeprecated;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BalancesSerializer implements JsonDeserializer<List<BalanceDeprecated>> {

    public List<BalanceDeprecated> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        ArrayList<BalanceDeprecated> result = new ArrayList<>();
        JsonObject response = json.getAsJsonObject();
        JsonArray jsonArray = response.getAsJsonArray("balances");
        for (JsonElement element : jsonArray) {
            result.add(this.mapToModel((JsonObject) element));
        }
        return result;
    }

    public BalanceDeprecated mapToModel(JsonObject object) {

        if (object.get("balanceType") == null) {
            throw new SerializerException("Unable to deserialize the Balance Object: Attribute balanceType not given");
        }
        BalanceDeprecated.Type type = BalanceDeprecated.Type.valueOf(
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
        BalanceDeprecated b1 = new BalanceDeprecated(amount, type, currency);

        try {
            String referenceDateString = object.get("referenceDate").getAsString();
            Date referenceDate = new SimpleDateFormat("yyyy-MM-dd").parse(referenceDateString);
            b1.setReferenceDate(referenceDate);
        } catch (Exception ignored) {
            //ignore - optional value
        }

        try {
            String lastChangeDateTime = object.get("lastChangeDateTime").getAsString();
            Date referenceDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(lastChangeDateTime);
            b1.setLastChangeDateTime(referenceDate);
        } catch (Exception ignored) {
            //ignore - optional value
        }

        return b1;
    }
}
