package billapplication.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import billapplication.dao.BillApplicationDAO;
import billapplication.exception.CustomerNotFoundException;
import billapplication.model.BillDetails;
import billapplication.model.Customer;
import billapplication.model.InvoiceDetails;
import billapplication.model.LoginDetails;
@Service
@Transactional(isolation=Isolation.READ_UNCOMMITTED, rollbackFor= Exception.class)
public class BillApplicationServiceImpl implements BillApplicationService{
	@Autowired
	Environment environment;
	@Autowired
	BillApplicationDAO 	billApplicationDao;
	@Value("${CUSTOMER_NOT_FOUND}")
	String customerNotFound;
	@Override
	public String customerAdd(Customer customer) throws Exception {
		try{
			return billApplicationDao.customerAdd(customer);
		}
		catch(Exception e){
			throw new Exception(environment.getProperty("ADD_ERROR_MESSAGE"));
		}
	}
	@Override
	public ArrayList<Customer> getCustomerDetails() throws Exception {
		try{
			return billApplicationDao.getCustomerDetails();
		}
		catch(Exception e){
			throw new Exception(environment.getProperty("GET_CUSTOMER_ERROR"));
		}
	}
	@Override
	public String updateCustomerDetails(Customer customer) throws Exception {
		try{
			return billApplicationDao.updateCustomerDetails(customer);
		}
		catch(Exception e){
			throw new Exception(environment.getProperty("UPDATE_ERROR_MESSAGE"));
		}
	}
	@Override
	public String addBillDetails(BillDetails[] billDetails,boolean sign,LocalDate invoiceDate) throws Exception {
		try{
			return billApplicationDao.addBillDetails(billDetails,sign,invoiceDate);
		}
		catch(Exception e){
			throw e;
			//new Exception(environment.getProperty("ADDED_BILL_ERROR"));
		}
	}
	@Override
	public InvoiceDetails getInvoiceDetails() throws Exception {
		try{
			return billApplicationDao.getInvoiceDetails();
		}
		catch(Exception e){
			System.out.println(e);
			throw e;
			//new Exception(environment.getProperty("ADDED_BILL_ERROR"));
		}
	}
	@Override
	public String addInvoiceDetails(InvoiceDetails invoiceDetails) throws Exception {
		try{
			return billApplicationDao.addInvoiceDetails(invoiceDetails);
		}
		catch(Exception e){
			System.out.println(e);
			throw e;
			//new Exception(environment.getProperty("ADDED_BILL_ERROR"));
		}
	}
	@Override
	public String invoiceNoCheck(String invoiceNo) throws Exception{
		try{
			return billApplicationDao.invoiceNoCheck(invoiceNo);
		}
		catch(Exception e){
			throw e;
		}
	}
	@Override
	public LoginDetails getLoginDetails() throws Exception{
		try{
			return billApplicationDao.getLoginDetails();
		}
		catch(Exception e){
			throw e;
		}
	}
	@Override
	public List<InvoiceDetails> getAllInvoice(Integer customerId) throws CustomerNotFoundException{
		try{
			return billApplicationDao.getAllInvoice(customerId);
		}
		catch(Exception e){
			throw new CustomerNotFoundException(customerNotFound);
		}
	}
	public List<BillDetails> getBillDetailsByInvoiceNo(InvoiceDetails invoiceDetails) throws Exception{
		try{
			return billApplicationDao.getBillDetailsByInvoiceNo(invoiceDetails);
		}
		catch(Exception e){
			throw e;
		}
	}
}
