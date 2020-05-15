package net.petafuel.styx.core.xs2a.standards.berlingroup.v1_3.serializers;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.petafuel.styx.core.xs2a.entities.Account;
import net.petafuel.styx.core.xs2a.entities.Currency;
import net.petafuel.styx.core.xs2a.entities.TransactionDeprecated;
import net.petafuel.styx.core.xs2a.exceptions.SerializerException;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class TransactionsSerializer implements JsonDeserializer<List<TransactionDeprecated>> {

    public List<TransactionDeprecated> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
        ArrayList<TransactionDeprecated> result = new ArrayList<>();

        // Checking if it's a Read Transaction List request or Read Transaction Details request
        if (json.getAsJsonObject().getAsJsonObject("transactions") != null) {
            JsonObject response = json.getAsJsonObject().getAsJsonObject("transactions");
            JsonArray booked = response.getAsJsonArray("booked");
            JsonArray pending = response.getAsJsonArray("pending");
            if (booked != null) {
                for (JsonElement element : booked) {
                    result.add(this.mapToModel((JsonObject) element, TransactionDeprecated.BookingStatus.BOOKED));
                }
            }
            if (pending != null) {
                for (JsonElement element : pending) {
                    result.add(this.mapToModel((JsonObject) element, TransactionDeprecated.BookingStatus.PENDING));
                }
            }
        } else {
            JsonObject object = json.getAsJsonObject().getAsJsonObject("transactionsDetails");
            TransactionDeprecated.BookingStatus bookingStatus = (object.get("bookingDate") != null) ? TransactionDeprecated.BookingStatus.BOOKED : TransactionDeprecated.BookingStatus.PENDING;

            result.add(this.mapToModel(object, bookingStatus));
        }
        return result;
    }

    private TransactionDeprecated mapToModel(JsonObject object, TransactionDeprecated.BookingStatus bookingStatus) {

        String transactionId = object.get("transactionId").getAsString();

        JsonObject amountObj = object.getAsJsonObject("transactionAmount");
        float amount = amountObj.get("amount").getAsFloat();
        Currency currency = Currency.valueOf(amountObj.get("currency").getAsString().toUpperCase());
        TransactionDeprecated.Type type;
        String name;
        Date bookingDate = null;
        Date valueDate = null;
        Account account;
        String remittanceInformationUnstructured = object.get("remittanceInformationUnstructured").getAsString();
        String mandateId = object.get("mandateId") != null ? object.get("mandateId").getAsString() : null;
        String bankTransactionCode = object.get("bankTransactionCode") != null ? object.get("bankTransactionCode").getAsString() : null;

        JsonObject accountObj;
        if (object.get("creditorName") != null) {
            type = TransactionDeprecated.Type.CREDIT;
            name = object.get("creditorName").getAsString();
            accountObj = object.getAsJsonObject("creditorAccount");
        } else {
            type = TransactionDeprecated.Type.DEBIT;
            name = object.get("debtorName").getAsString();
            accountObj = object.getAsJsonObject("debtorAccount");
        }
        Account.Type accountType = Arrays.stream(Account.Type.values())
                .filter(idType -> accountObj.get(idType.getJsonKey()) != null)
                .findFirst()
                .orElse(null);

        if (accountType == null) {
            throw new SerializerException("Unable to deserialize the Account Object: accountType did not match any Account.Type");
        }
        String accountIdentifier = accountObj.get(accountType.getJsonKey()).getAsString();
        account = new Account(accountIdentifier, currency, accountType);
        account.setName(name);

        try {
            String bookingDateString = object.get("valueDate").getAsString();
            valueDate = new SimpleDateFormat("yyyy-MM-dd").parse(bookingDateString);
        } catch (Exception ignored) {
            //ignored - optional value
        }

        if (bookingStatus == TransactionDeprecated.BookingStatus.BOOKED) {
            try {
                String bookingDateString = object.get("bookingDate").getAsString();
                bookingDate = new SimpleDateFormat("yyyy-MM-dd").parse(bookingDateString);
            } catch (Exception ignored) {
                //ignored - optional value
            }
        }

        TransactionDeprecated t1 = new TransactionDeprecated(transactionId, bookingStatus, type, account, currency, amount, remittanceInformationUnstructured);
        t1.setValueDate(valueDate);
        t1.setBookingDate(bookingDate);
        t1.setMandateId(mandateId);
        t1.setBankTransactionCode(bankTransactionCode);
        return t1;
    }
}
