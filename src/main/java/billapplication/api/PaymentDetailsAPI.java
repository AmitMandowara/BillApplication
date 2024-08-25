package billapplication.api;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import billapplication.model.PaymentDetails;
import billapplication.model.PaymentDetailsHistory;
import billapplication.service.PaymentDetailsService;

@CrossOrigin
@RestController
public class PaymentDetailsAPI {
	
	@Autowired
	private PaymentDetailsService paymentDetailsService;
	
	@GetMapping(value = "/getPaymentDetailsByCustomer/{customerId}")
	public ResponseEntity<List<PaymentDetails>> getPaymentDetailsByCustomer(@PathVariable("customerId") Integer customerId){
		try{
			List<PaymentDetails> paymentDetailsList = paymentDetailsService.getPaymentDetailsByCustomer(customerId);
			return new ResponseEntity<List<PaymentDetails>>(paymentDetailsList, HttpStatus.OK);
		}catch(Exception e){
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		}
	}
	
	@PostMapping(value = "/saveInPaymentDetailsByInvoiceNo")
	public ResponseEntity<List<PaymentDetails>> saveInPaymentDetailsByInvoiceNo(@RequestBody PaymentDetailsHistory paymentDetails){
		try{
			List<PaymentDetails> paymentDetailsList = paymentDetailsService.saveInPaymentDetailsByInvoiceNo(paymentDetails);
			return new ResponseEntity<List<PaymentDetails>>(paymentDetailsList, HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		}
	}
	
	@PostMapping(value = "/saveListOfPaymentDetailsByInvoiceNo")
	public ResponseEntity<List<PaymentDetails>> saveListOfPaymentDetailsByInvoiceNo(@RequestBody PaymentDetailsHistory[] paymentDetailsArray){
		try{
			List<PaymentDetails> paymentDetailsList = paymentDetailsService.saveListOfPaymentDetailsByInvoiceNo(paymentDetailsArray);
			return new ResponseEntity<List<PaymentDetails>>(paymentDetailsList, HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		}
	}
	
	@GetMapping(value = "/downloadMasterFile")
	@ResponseBody
	public ResponseEntity<Resource> downloadFile() {
		try {
			Resource file = paymentDetailsService.generateMasterReport();
			Path path = file.getFile()
                    .toPath();

			return ResponseEntity.ok()
                         .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(path))
                         .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFilename() + "\"")
                         .body(file);
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
