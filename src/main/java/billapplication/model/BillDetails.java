package billapplication.model;

public class BillDetails {
	private int id;
	private String item_desc;
	private String sac;
	private Long basic_value;
	private int quantity;
	private int cgst;
	private int sgst;
	private int igst;
	private InvoiceDetails invoiceDetails;
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
	public Long getBasic_value() {
		return basic_value;
	}
	public void setBasic_value(Long basic_value) {
		this.basic_value = basic_value;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	private Customer customer;
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
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
	public InvoiceDetails getInvoiceDetails() {
		return invoiceDetails;
	}
	public void setInvoiceDetails(InvoiceDetails invoiceDetails) {
		this.invoiceDetails = invoiceDetails;
	}
	public void setId(int id) {
		this.id=id;
	}
	public int getId() {
		return id;
	}
}
