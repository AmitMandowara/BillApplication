package billapplication.model;

import java.time.LocalDate;

public class PaymentDetailsHistory {
	private Integer id;
	private InvoiceDetails invoiceDetails;
	private Customer customer;
	private Long invoiceValue;
	private Long amountReceived;
	private Long balancePending;
	private Long amountDue;
	private Long cgst;
	private Long sgst;
	private Long igst;
	private Long taxableValue;
	private LocalDate dateOfReceipt;
	private Long discount;
	private Long tds;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public InvoiceDetails getInvoiceDetails() {
		return invoiceDetails;
	}
	public void setInvoiceDetails(InvoiceDetails invoiceDetails) {
		this.invoiceDetails = invoiceDetails;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	@Override
	public String toString() {
		return "PaymentDetailsHistory [id=" + id + ", invoiceDetailsEntity=" + invoiceDetails + ", customerEntity="
				+ customer + "]";
	}
	public Long getCgst() {
		return cgst;
	}

	public void setCgst(Long cgst) {
		this.cgst = cgst;
	}

	public Long getSgst() {
		return sgst;
	}

	public void setSgst(Long sgst) {
		this.sgst = sgst;
	}

	public Long getIgst() {
		return igst;
	}

	public void setIgst(Long igst) {
		this.igst = igst;
	}

	public Long getTaxableValue() {
		return taxableValue;
	}

	public void setTaxableValue(Long taxableValue) {
		this.taxableValue = taxableValue;
	}

	public LocalDate getDateOfReceipt() {
		return dateOfReceipt;
	}

	public void setDateOfReceipt(LocalDate dateOfReceipt) {
		this.dateOfReceipt = dateOfReceipt;
	}
	public Long getInvoiceValue() {
		return invoiceValue;
	}
	public void setInvoiceValue(Long invoiceValue) {
		this.invoiceValue = invoiceValue;
	}
	public Long getAmountReceived() {
		return amountReceived;
	}
	public void setAmountReceived(Long amountReceived) {
		this.amountReceived = amountReceived;
	}
	public Long getBalancePending() {
		return balancePending;
	}
	public void setBalancePending(Long balancePending) {
		this.balancePending = balancePending;
	}
	public Long getAmountDue() {
		return amountDue;
	}
	public void setAmountDue(Long amountDue) {
		this.amountDue = amountDue;
	}
	public Long getDiscount() {
		return discount;
	}
	public void setDiscount(Long discount) {
		this.discount = discount;
	}
	public Long getTds() {
		return tds;
	}
	public void setTds(Long tds) {
		this.tds = tds;
	}
}
