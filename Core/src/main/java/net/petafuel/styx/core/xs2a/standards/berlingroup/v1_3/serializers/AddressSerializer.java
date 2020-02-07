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

        String street = addressObject.get("street").getAsString();
        Integer buildingNumber = addressObject.get("buildingNumber").getAsInt();
        String city = addressObject.get("city").getAsString();
        Integer postalCode = addressObject.get("postalCode").getAsInt();
        String country = addressObject.get("country").getAsString();

        Address address = new Address();
        address.setStreet(street);
        address.setBuildingNumber(buildingNumber);
        address.setCity(city);
        address.setPostalCode(postalCode);
        address.setCountry(country);

        return address;
    }
}
