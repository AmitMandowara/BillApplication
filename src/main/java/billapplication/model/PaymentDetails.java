package billapplication.model;

import java.time.LocalDate;

public class PaymentDetails {
	
	private Integer id;
	private InvoiceDetails invoiceDetails;
	private Customer customer;
	private Long amountDue;
	private Long amountReceived;
	private Long balancePending;
	private Long currentAmountPaid;
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
	public Long getAmountDue() {
		return amountDue;
	}
	public void setAmountDue(Long amountDue) {
		this.amountDue = amountDue;
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
	public Long getCurrentAmountPaid() {
		return currentAmountPaid;
	}
	public void setCurrentAmountPaid(Long currentAmountPaid) {
		this.currentAmountPaid = currentAmountPaid;
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
	public LocalDate getDateOfReceipt() {
		return dateOfReceipt;
	}
	public void setDateOfReceipt(LocalDate dateOfReceipt) {
		this.dateOfReceipt = dateOfReceipt;
	}
	@Override
	public String toString() {
		return "PaymentDetails [id=" + id + ", amountDue=" + amountDue + ", amountReceived=" + amountReceived
				+ ", balancePending=" + balancePending + ", currentAmountPaid=" + currentAmountPaid + ", cgst=" + cgst
				+ ", sgst=" + sgst + ", igst=" + igst + ", taxableValue=" + taxableValue + ", discount=" + discount
				+ ", tds=" + tds + "]";
	}

}
