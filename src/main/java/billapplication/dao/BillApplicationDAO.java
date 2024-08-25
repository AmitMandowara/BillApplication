package billapplication.dao;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import billapplication.model.BillDetails;
import billapplication.model.Customer;
import billapplication.model.InvoiceDetails;
import billapplication.model.LoginDetails;

public interface BillApplicationDAO {
	public String customerAdd(Customer customer);
	public ArrayList<Customer> getCustomerDetails();
	public String updateCustomerDetails(Customer customer);
	public String addBillDetails(BillDetails[] billDetails, boolean sign,LocalDate invoiceDate) throws Exception;
	public InvoiceDetails getInvoiceDetails() throws Exception;
	public String addInvoiceDetails(InvoiceDetails invoiceDetails) throws Exception;
	public String invoiceNoCheck(String invoiceNo) throws Exception;
	public LoginDetails getLoginDetails() throws Exception;
	public List<InvoiceDetails> getAllInvoice(Integer customerId);
	public List<BillDetails> getBillDetailsByInvoiceNo(InvoiceDetails invoiceDetails) throws Exception;
}
