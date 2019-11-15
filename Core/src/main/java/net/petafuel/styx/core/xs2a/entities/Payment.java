package net.petafuel.styx.core.xs2a.entities;

import java.util.Date;

public class Payment
{
	private Account creditor;
	private Account debtor;
	private String amount;
	private Currency currency;
	private String reference;
	private String endToEndIdentification;
	private Date requestedExecutionDate;

	public Payment() {
		this.requestedExecutionDate = new Date();
	}

	public Account getCreditor() { return creditor; }

	public void setCreditor(Account creditor) {
		this.creditor = creditor;
	}

	public Account getDebtor() {
		return debtor;
	}

	public void setDebtor(Account debtor) {
		this.debtor = debtor;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Currency getCurrency() {
		return currency;
	}

	public void setCurrency(Currency currency) {
		this.currency = currency;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getEndToEndIdentification() {
		return endToEndIdentification;
	}

	public void setEndToEndIdentification(String endToEndIdentification) {
		this.endToEndIdentification = endToEndIdentification;
	}

	public Date getRequestedExecutionDate() {
		return requestedExecutionDate;
	}

	/**
	 * Set date for bank-side payment execution
	 * @param requestedExecutionDate
	 */
	public void setRequestedExecutionDate(Date requestedExecutionDate) {
		this.requestedExecutionDate = requestedExecutionDate;
	}
}
