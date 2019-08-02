package net.petafuel.styx.consentmanager.consent.berlingroup.entity;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import net.petafuel.styx.utils.http.XS2AHeader;
import net.petafuel.styx.utils.http.XS2ARequestModel;
import net.petafuel.styx.utils.standards.berlingroup.entity.Customer;
import net.petafuel.styx.utils.standards.berlingroup.entity.PSU;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConsentCreateModel implements JsonSerializer<ConsentCreateModel>, XS2ARequestModel
{

    //Headers
    @XS2AHeader("x-request-id")
    private String xRequestId;

    @XS2AHeader(nested = true)
    private PSU psu;

    @XS2AHeader("date")
    private String date;

    //Body
    private List<Customer> accounts;
    private List<Customer> balances;
    private List<Customer> transactions;
    private Date validUntil;
    private int frequencyPerDay;
    private boolean combinedServiceIndicator;
    private boolean recurringIndicator;

    public ConsentCreateModel(PSU psu, Date validUntil, int frequencyPerDay, boolean combinedServiceIndicator, boolean recurringIndicator)
    {
        this.psu = psu;
        this.validUntil = validUntil;
        this.frequencyPerDay = frequencyPerDay;
        this.combinedServiceIndicator = combinedServiceIndicator;
        this.recurringIndicator = recurringIndicator;

        this.accounts = new ArrayList<>();
        this.balances = new ArrayList<>();
        this.transactions = new ArrayList<>();
    }

    public List<Customer> getAccounts()
    {
        return accounts;
    }

    public void setAccounts(List<Customer> accounts)
    {
        this.accounts = accounts;
    }

    public List<Customer> getBalances()
    {
        return balances;
    }

    public void setBalances(List<Customer> balances)
    {
        this.balances = balances;
    }

    public List<Customer> getTransactions()
    {
        return transactions;
    }

    public void setTransactions(List<Customer> transactions)
    {
        this.transactions = transactions;
    }

    public Date getValidUntil()
    {
        return validUntil;
    }

    public int getFrequencyPerDay()
    {
        return frequencyPerDay;
    }

    public boolean isCombinedServiceIndicator()
    {
        return combinedServiceIndicator;
    }

    public boolean isRecurringIndicator()
    {
        return recurringIndicator;
    }

    public String getxRequestId()
    {
        return xRequestId;
    }

    public void setxRequestId(String xRequestId)
    {
        this.xRequestId = xRequestId;
    }

    public PSU getPsu()
    {
        return psu;
    }

    public void setPsu(PSU psu)
    {
        this.psu = psu;
    }

    public void setDate(Date date)
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM y HH:mm:ss");
        this.date = simpleDateFormat.format(date);
    }

    @Override
    public JsonElement serialize(ConsentCreateModel src, Type typeOfSrc, JsonSerializationContext context)
    {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Customer.class, new Customer())
                .create();
        JsonObject jsonOutput = new JsonObject();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-d");

        JsonObject jsonAccess = new JsonObject();

        if (!src.getAccounts().isEmpty())
        {
            JsonElement jsonAccounts = gson.toJsonTree(src.getAccounts(), new TypeToken<ArrayList<Customer>>()
            {
            }.getType());
            jsonAccess.add("accounts", jsonAccounts);
        }
        if (!src.getBalances().isEmpty())
        {
            JsonElement jsonBalances = gson.toJsonTree(src.getBalances(), new TypeToken<ArrayList<Customer>>()
            {
            }.getType());
            jsonAccess.add("balances", jsonBalances);
        }
        if (!src.getTransactions().isEmpty())
        {
            JsonElement jsonTransactions = gson.toJsonTree(src.getTransactions(), new TypeToken<ArrayList<Customer>>()
            {
            }.getType());
            jsonAccess.add("transactions", jsonTransactions);
        }
        jsonOutput.add("access", jsonAccess);
        jsonOutput.addProperty("validUntil", dateFormat.format(src.getValidUntil()));
        jsonOutput.addProperty("frequencyPerDay", src.getFrequencyPerDay());
        jsonOutput.addProperty("combinedServiceIndicator", src.isCombinedServiceIndicator());
        jsonOutput.addProperty("recurringIndicator", src.isRecurringIndicator());

        return jsonOutput;
    }

    public String toJson()
    {
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ConsentCreateModel.class, this)
                .create();
        return gson.toJson(this);
    }
}
