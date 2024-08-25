package billapplication.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import billapplication.exception.CustomerNotFoundException;
import billapplication.model.BillDetails;
import billapplication.model.Customer;
import billapplication.model.InvoiceDetails;
import billapplication.model.LoginDetails;

public interface BillApplicationService {
	public String customerAdd(Customer customer) throws Exception;
	public ArrayList<Customer> getCustomerDetails() throws Exception;
	public String updateCustomerDetails(Customer customer) throws Exception;
	public String addBillDetails(BillDetails[] billDetails,boolean sign,LocalDate invoiceDate) throws Exception;
	public InvoiceDetails getInvoiceDetails() throws Exception;
	public String addInvoiceDetails(InvoiceDetails invoiceDetails) throws Exception;
	public String invoiceNoCheck(String invoiceNo) throws Exception;
	public LoginDetails getLoginDetails() throws Exception;
	public List<InvoiceDetails> getAllInvoice(Integer customerId) throws CustomerNotFoundException;
	public List<BillDetails> getBillDetailsByInvoiceNo(InvoiceDetails invoiceDetails) throws Exception;
}
