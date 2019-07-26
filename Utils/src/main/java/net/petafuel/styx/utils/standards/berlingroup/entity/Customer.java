package net.petafuel.styx.utils.standards.berlingroup.entity;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class Customer implements JsonSerializer<Customer>
{
    public enum CustomerType
    {
        IBAN,
        MASKED_PAN
    }


    private String identifier;
    private CustomerType customerType;
    private String currency;

    public Customer(){}
    public Customer(String identifier, CustomerType customerType)
    {
        this(identifier, customerType, "EUR");
    }

    public Customer(String identifier, CustomerType customerType, String currency)
    {
        this.identifier = identifier;
        this.customerType = customerType;
        this.currency = currency;
    }

    public String getIdentifier()
    {
        return identifier;
    }

    public CustomerType getCustomerType()
    {
        return customerType;
    }

    public String getCurrency()
    {
        return currency;
    }

    @Override
    public JsonElement serialize(Customer src, Type typeOfSrc, JsonSerializationContext context)
    {
        JsonObject jsonOutput = new JsonObject();
        switch (src.getCustomerType())
        {
            case IBAN:
                jsonOutput.addProperty("iban", src.getIdentifier());
                break;
            case MASKED_PAN:
                jsonOutput.addProperty("maskedPan", src.getIdentifier());
                break;
            default:
                break;
        }

        jsonOutput.addProperty("currency", src.getCurrency());
        return jsonOutput;
    }

}
