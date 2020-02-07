package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.petafuel.styx.core.xs2a.entities.Address;

import java.lang.reflect.Type;

public class AddressSerializer implements JsonDeserializer<Address> {
    @Override
    public Address deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject addressObject = jsonElement.getAsJsonObject();

        JsonElement streetElement = addressObject.get("street");
        String street = streetElement != null && !streetElement.isJsonNull() ? streetElement.getAsString() : null;

        JsonElement buildingNumberElement = addressObject.get("buildingNumber");
        Integer buildingNumber = buildingNumberElement != null && !buildingNumberElement.isJsonNull() ? buildingNumberElement.getAsInt() : null;

        JsonElement cityElement = addressObject.get("city");
        String city = cityElement != null && !cityElement.isJsonNull() ? cityElement.getAsString() : null;

        JsonElement postalCodeElement = addressObject.get("postalCode");
        Integer postalCode = postalCodeElement != null && !postalCodeElement.isJsonNull() ? postalCodeElement.getAsInt() : null;

        JsonElement countryElement = addressObject.get("country");
        String country = countryElement != null && !countryElement.isJsonNull() ? countryElement.getAsString() : null;

        Address address = new Address();
        address.setStreet(street);
        address.setBuildingNumber(buildingNumber);
        address.setCity(city);
        address.setPostalCode(postalCode);
        address.setCountry(country);

        return address;
    }
}
