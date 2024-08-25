package billapplication.api;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import billapplication.model.BillDetails;
import billapplication.model.Customer;
import billapplication.model.InvoiceDetails;
import billapplication.model.LoginDetails;
import billapplication.service.BillApplicationService;
@CrossOrigin
@RestController
public class BillApplicationAPI {
	@Autowired
	BillApplicationService billApplicationService;
	@RequestMapping(value="/add",method=RequestMethod.POST)
	public ResponseEntity<String> addCustomer(@RequestBody Customer customer)
	{
		try{
			String msg=billApplicationService.customerAdd(customer);
			ResponseEntity<String> responseEntity=new ResponseEntity<String>(msg,HttpStatus.OK);
			return responseEntity;
		}
		catch(Exception e){
			ResponseStatusException rse=new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(),e);
			throw rse;
		}
	}
	@RequestMapping(value="/getCustomerDetails",method=RequestMethod.GET)
	public ResponseEntity<ArrayList<Customer>> getCustomerDetails(){
		try{
			ArrayList<Customer> customerArrayList=billApplicationService.getCustomerDetails();
			ResponseEntity<ArrayList<Customer>> responseEntity=new ResponseEntity<ArrayList<Customer>>(customerArrayList,HttpStatus.OK);
			return responseEntity;
		}
		catch(Exception e){
			ResponseStatusException rse=new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(),e);
			throw rse;
		}
	}
	@RequestMapping(value="/updateCustomerDetails",method=RequestMethod.POST)
	public ResponseEntity<String> updateCustomerDetails(@RequestBody Customer customer)
	{
		try{
			String msg=billApplicationService.updateCustomerDetails(customer);
			ResponseEntity<String> responseEntity=new ResponseEntity<String>(msg,HttpStatus.OK);
			return responseEntity;
		}
		catch(Exception e){
			ResponseStatusException rse=new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(),e);
			throw rse;
		}
	}
	@RequestMapping(value="/addBillDetails/{sign}/{invoiceDate}",method=RequestMethod.POST)
	public ResponseEntity<InputStreamResource> addBillDetails(@RequestBody BillDetails[] billDetails,@PathVariable boolean sign,@PathVariable String invoiceDate){
		try{
			FileSystemResource pdfFile;
			HttpHeaders headers = new HttpHeaders();
			if(!invoiceDate.equals("undefined")){
			String fileName=billApplicationService.addBillDetails(billDetails,sign,LocalDate.parse(invoiceDate));
			pdfFile = new FileSystemResource(fileName);
		    headers.setContentType(MediaType.parseMediaType("application/pdf"));
		    headers.add("Content-Disposition", "filename=" + fileName);
		    headers.set("FileName",fileName.split("/")[2] );
		    headers.setContentLength(pdfFile.contentLength());
			}
			else{
				String fileName=billApplicationService.addBillDetails(billDetails,sign,null);
				pdfFile = new FileSystemResource(fileName);
			    
			    headers.setContentType(MediaType.parseMediaType("application/pdf"));
			    headers.add("Content-Disposition", "filename=" + fileName);
			    headers.set("FileName",fileName.split("/")[2] );
			    headers.setContentLength(pdfFile.contentLength());
			}
			ResponseEntity<InputStreamResource> responseEntity=new ResponseEntity<InputStreamResource>( new InputStreamResource(pdfFile.getInputStream()), headers, HttpStatus.OK);
			return responseEntity;
		}
		catch(Exception e){
			System.out.println(e);
			e.printStackTrace();
			ResponseStatusException rse=new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(),e);
			throw rse;
		}
	}
	@RequestMapping(value="/getInvoiceDetails",method=RequestMethod.GET)
	public ResponseEntity<InvoiceDetails> getInvoiceDetails(){
		try{
			InvoiceDetails invoiceDetails=billApplicationService.getInvoiceDetails();
			ResponseEntity<InvoiceDetails> responseEntity=new ResponseEntity<InvoiceDetails>(invoiceDetails, HttpStatus.OK);
			return responseEntity;
		}
		catch(Exception e){
			System.out.println(e);
			ResponseStatusException rse=new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(),e);
			throw rse;
		}
	}
	@RequestMapping(value="/addInvoiceDetails",method=RequestMethod.POST)
	public ResponseEntity<String> addInvoiceDetails(@RequestBody InvoiceDetails invoiceDetails){
		try{
			String msg=billApplicationService.addInvoiceDetails(invoiceDetails);
			ResponseEntity<String> responseEntity=new ResponseEntity<String>(msg,HttpStatus.OK);
			return responseEntity;
		}
		catch(Exception e){
			ResponseStatusException rse=new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(),e);
			throw rse;
		}
	}
	@RequestMapping(value="/invoiceNoCheck",method=RequestMethod.POST)
	public ResponseEntity<String> invoiceNoCheck(@RequestBody InvoiceDetails invoiceDetails){
		try{
			String msg=billApplicationService.invoiceNoCheck(invoiceDetails.getInvoiceno());
			ResponseEntity<String> responseEntity=new ResponseEntity<String>(msg,HttpStatus.OK);
			return responseEntity;
		}
		catch(Exception e){
			ResponseStatusException rse=new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(),e);
			throw rse;
		}
	}
	@RequestMapping(value="/getLoginDetails",method=RequestMethod.GET)
	public ResponseEntity<LoginDetails> getLoginDetails(){
		try{
			LoginDetails lde=billApplicationService.getLoginDetails();
			ResponseEntity<LoginDetails> responseEntity=new ResponseEntity<LoginDetails>(lde,HttpStatus.OK);
			return responseEntity;
		}
		catch(Exception e){
			ResponseStatusException rse=new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(),e);
			throw rse;
		}
	}
	@RequestMapping(value="/getAllInvoice/{customerId}",method=RequestMethod.GET)
	public ResponseEntity<List<InvoiceDetails>> getAllInvoice(@PathVariable Integer customerId){
			List<InvoiceDetails> invoiceDetailsArray=billApplicationService.getAllInvoice(customerId);
			ResponseEntity<List<InvoiceDetails>> responseEntity=new ResponseEntity<List<InvoiceDetails>>(invoiceDetailsArray,HttpStatus.OK);
			return responseEntity;
	}
	@RequestMapping(value="/getBillDetailsByInvoiceNo",method=RequestMethod.POST)
	public ResponseEntity<List<BillDetails>> getBillDetailsByInvoiceNo(@RequestBody InvoiceDetails invoiceDetails){
		try{
			List<BillDetails> billDetailsArray=billApplicationService.getBillDetailsByInvoiceNo(invoiceDetails);
			ResponseEntity<List<BillDetails>> responseEntity=new ResponseEntity<List<BillDetails>>(billDetailsArray,HttpStatus.OK);
			return responseEntity;
		}
		catch(Exception e){
			ResponseStatusException rse=new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(),e);
			throw rse;
		}
	}
}
