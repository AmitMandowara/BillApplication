package billapplication.entity;

import java.time.LocalDate;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payment_details_history")
public class PaymentDetailsHistoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@OneToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinColumn(name = "invoiceno")
	private InvoiceDetailsEntity invoiceDetailsEntity;

	@ManyToOne(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	@JoinColumn(name = "cid")
	private CustomerEntity customerEntity;

	@Column(name = "invoice_value")
	private Long invoiceValue;

	@Column(name = "amount_received")
	private Long amountReceived;
	
	@Column(name = "balance_pending")
	private Long balancePending;
	
	@Column(name = "amount_due")
	private Long amountDue;

	@Column(name = "cgst")
	private Long cgst;

	@Column(name = "sgst")
	private Long sgst;

	@Column(name = "igst")
	private Long igst;

	@Column(name = "taxable_value")
	private Long taxableValue;

	@Column(name = "date_of_receipt")
	private LocalDate dateOfReceipt;
	
	@Column(name = "discount")
	private Long discount;
	
	@Column(name = "tds")
	private Long tds;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public InvoiceDetailsEntity getInvoiceDetailsEntity() {
		return invoiceDetailsEntity;
	}

	public void setInvoiceDetailsEntity(InvoiceDetailsEntity invoiceDetailsEntity) {
		this.invoiceDetailsEntity = invoiceDetailsEntity;
	}

	public Long getAmountReceived() {
		return amountReceived;
	}

	public void setAmountReceived(Long amountPaid) {
		this.amountReceived = amountPaid;
	}

	public CustomerEntity getCustomerEntity() {
		return customerEntity;
	}

	public void setCustomerEntity(CustomerEntity customerEntity) {
		this.customerEntity = customerEntity;
	}

	@Override
	public String toString() {
		return "PaymentDetailsHistoryEntity [id=" + id + ", invoiceDetailsEntity=" + invoiceDetailsEntity + ", customerEntity="
				+ customerEntity + ", totalAmount=" + invoiceValue + ", amountPaid=" + amountReceived
				+ ", taxableValue=" + taxableValue
				+ ", cgst=" + cgst + ", igst=" + igst + ", dateOfReceipt=" + dateOfReceipt + "]";
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

	public Long getInvoiceValue() {
		return invoiceValue;
	}

	public void setInvoiceValue(Long invoiceValue) {
		this.invoiceValue = invoiceValue;
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

}
