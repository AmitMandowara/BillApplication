package billapplication.model;

import java.time.LocalDate;

public class InvoiceDetails {
	private int count;
	private String invoiceno;
	private LocalDate invoicedate;
	private int totalValue;
	private Customer customer;
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getInvoiceno() {
		return invoiceno;
	}
	public void setInvoiceno(String invoiceno) {
		this.invoiceno = invoiceno;
	}
	public LocalDate getInvoicedate() {
		return invoicedate;
	}
	public void setInvoicedate(LocalDate invoicedate) {
		this.invoicedate = invoicedate;
	}
	public int getTotalValue() {
		return totalValue;
	}
	public void setTotalValue(int totalValue) {
		this.totalValue = totalValue;
	}
	public Customer getCustomer() {
		return customer;
	}
	public void setCustomer(Customer customer) {
		this.customer = customer;
	}
	@Override
	public String toString() {
		return "InvoiceDetails [count=" + count + ", invoiceno=" + invoiceno + ", invoicedate=" + invoicedate
				+ ", totalValue=" + totalValue + ", customer=" + customer + "]";
	}
}
