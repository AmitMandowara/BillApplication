package billapplication.entity;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="invoicedetails")
public class InvoiceDetailsEntity {
	@Id
	private String invoiceno;
	private LocalDate invoicedate;
	private int count;
	@Column(name="total_value")
	private int totalValue;
	@ManyToOne
	@JoinColumn(name="cid")
	private CustomerEntity customerEntity;
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
	public CustomerEntity getCustomerEntity() {
		return customerEntity;
	}
	public void setCustomerEntity(CustomerEntity customerEntity) {
		this.customerEntity = customerEntity;
	}
	@Override
	public String toString() {
		return "InvoiceDetailsEntity [invoiceno=" + invoiceno + ", invoicedate=" + invoicedate + ", count=" + count
				+ ", totalValue=" + totalValue + "]";
	}
}
