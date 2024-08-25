package billapplication.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name="billdetails")
public class BillDetailsEntity {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column(name="item_desc")
	private String item_desc;
	@Column(name="sac")
	private String sac;
	@Column(name="basic_value")
	private Long basic_value;
	@Column(name="quantity")
	private int quantity;
	@Column(name="cgst")
	private int cgst;
	@Column(name="sgst")
	private int sgst;
	@Column(name="igst")
	private int igst;
	@ManyToOne(cascade=CascadeType.ALL,fetch=FetchType.LAZY)
	@JoinColumn(name="cid")
	private CustomerEntity customerEntity;
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name="invoiceno")
	private InvoiceDetailsEntity invoiceDetailsEntity;
	public String getItem_desc() {
		return item_desc;
	}
	public void setItem_desc(String item_desc) {
		this.item_desc = item_desc;
	}
	public String getSac() {
		return sac;
	}
	public void setSac(String sac) {
		this.sac = sac;
	}
	public Long getBasic_value() {
		return basic_value;
	}
	public void setBasic_value(Long basic_value) {
		this.basic_value = basic_value;
	}
	public CustomerEntity getCustomerEntity() {
		return customerEntity;
	}
	public void setCustomerEntity(CustomerEntity customerEntity) {
		this.customerEntity = customerEntity;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getCgst() {
		return cgst;
	}
	public void setCgst(int cgst) {
		this.cgst = cgst;
	}
	public int getSgst() {
		return sgst;
	}
	public void setSgst(int sgst) {
		this.sgst = sgst;
	}
	public int getIgst() {
		return igst;
	}
	public void setIgst(int igst) {
		this.igst = igst;
	}
	public InvoiceDetailsEntity getInvoiceDetailsEntity() {
		return invoiceDetailsEntity;
	}
	public void setInvoiceDetailsEntity(InvoiceDetailsEntity invoiceDetailsEntity) {
		this.invoiceDetailsEntity = invoiceDetailsEntity;
	}
	public int getId() {
		return id;
	}
}
