package net.petafuel.styx.api.v1.account.control;

import net.petafuel.styx.api.v1.account.entity.TransactionAdapted;
import net.petafuel.styx.core.xs2a.entities.BookingStatus;
import net.petafuel.styx.core.xs2a.entities.Links;
import net.petafuel.styx.core.xs2a.entities.Transaction;
import net.petafuel.styx.core.xs2a.entities.TransactionContainer;

import javax.json.bind.annotation.JsonbProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Convert the Transaction Structure into the Styx Interface Spec structure
 */
public class TransactionListResponseAdapter {
    private List<TransactionAdapted> transactions;
    private Links links = new Links();

    public TransactionListResponseAdapter() {
        //Default ctor for json binding
    }

    public TransactionListResponseAdapter(TransactionContainer transactionContainer) {
        transactions = new ArrayList<>();
        if (transactionContainer.getTransactions() != null) {
            if (transactionContainer.getTransactions().getBooked() != null) {
                transactionContainer.getTransactions().getBooked().forEach(bookedTransaction -> convertTransaction(bookedTransaction, BookingStatus.BOOKED));
            }
            if (transactionContainer.getTransactions().getPending() != null) {
                transactionContainer.getTransactions().getPending().forEach(pendingTransaction -> convertTransaction(pendingTransaction, BookingStatus.PENDING));
            }
        } else {
            links.setDownload(transactionContainer.getLinks().getDownload());
        }

    }

    private void convertTransaction(Transaction transaction, BookingStatus status) {
        TransactionAdapted transactionAdapted = new TransactionAdapted();
        transactionAdapted.setBookingDate(transaction.getBookingDate());
        transactionAdapted.setBookingStatus(status);
        transactionAdapted.setCreditorAccount(transaction.getCreditorAccount());
        transactionAdapted.setCreditorName(transaction.getCreditorName());
        transactionAdapted.setDebtorAccount(transaction.getDebtorAccount());
        transactionAdapted.setDebtorName(transaction.getDebtorName());
        transactionAdapted.setTransactionAmount(transaction.getTransactionAmount());
        transactionAdapted.setValueDate(transaction.getValueDate());

        //go down the vwz/remittance fields
        String remittanceStr = "";
        if (transaction.getRemittanceInformationUnstructured() != null) {
            remittanceStr = transaction.getRemittanceInformationUnstructured();
        } else if (transaction.getRemittanceInformationStructured() != null) {
            remittanceStr = transaction.getRemittanceInformationStructured();
        } else if (transaction.getRemittanceInformationStructuredArray() != null) {
            remittanceStr = transaction.getRemittanceInformationStructuredArray()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n"));
        } else if (transaction.getRemittanceInformationUnstructuredArray() != null) {
            remittanceStr = transaction.getRemittanceInformationUnstructuredArray()
                    .stream()
                    .map(Object::toString)
                    .collect(Collectors.joining("\n"));
        }
        transactionAdapted.setPurpose(remittanceStr);

        this.transactions.add(transactionAdapted);
    }

    public List<TransactionAdapted> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionAdapted> transactions) {
        this.transactions = transactions;
    }

    @JsonbProperty("links")
    public Links getLinks() {
        return links;
    }

    @JsonbProperty("links")
    public void setLinks(Links links) {
        this.links = links;
    }
}
