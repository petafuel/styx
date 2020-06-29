package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.petafuel.styx.core.xs2a.entities.Address;
import net.petafuel.styx.core.xs2a.entities.XS2AJsonKeys;

import java.lang.reflect.Type;

public class AddressSerializer implements JsonDeserializer<Address> {
    @Override
    public Address deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) {
        JsonObject addressObject = jsonElement.getAsJsonObject();

        String street = addressObject.get(XS2AJsonKeys.STREET.value()) != null
                && !addressObject.get(XS2AJsonKeys.STREET.value()).isJsonNull()
                ? addressObject.get(XS2AJsonKeys.STREET.value()).getAsString()
                : null;
        String buildingNumber = addressObject.get(XS2AJsonKeys.BUILDING_NUMBER.value()) != null
                && !addressObject.get(XS2AJsonKeys.BUILDING_NUMBER.value()).isJsonNull()
                ? addressObject.get(XS2AJsonKeys.BUILDING_NUMBER.value()).getAsString()
                : null;
        String city = addressObject.get(XS2AJsonKeys.CITY.value()) != null
                && !addressObject.get(XS2AJsonKeys.CITY.value()).isJsonNull()
                ? addressObject.get(XS2AJsonKeys.CITY.value()).getAsString()
                : null;
        Integer postalCode = addressObject.get(XS2AJsonKeys.POSTAL_CODE.value()) != null
                && !addressObject.get(XS2AJsonKeys.POSTAL_CODE.value()).isJsonNull()
                ? addressObject.get(XS2AJsonKeys.POSTAL_CODE.value()).getAsInt()
                : null;
        String country = addressObject.get(XS2AJsonKeys.COUNTRY.value()) != null
                && !addressObject.get(XS2AJsonKeys.COUNTRY.value()).isJsonNull()
                ? addressObject.get(XS2AJsonKeys.COUNTRY.value()).getAsString()
                : null;

        Address address = new Address();
        address.setStreet(street);
        address.setBuildingNumber(buildingNumber);
        address.setCity(city);
        address.setPostalCode(postalCode);
        address.setCountry(country);

        return address;
    }
}
