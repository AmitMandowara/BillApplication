package billapplication.service;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;

import billapplication.model.PaymentDetails;
import billapplication.model.PaymentDetailsHistory;

public interface PaymentDetailsService {
	
	void saveInPaymentDetails(PaymentDetails paymentDetails);
	
	void saveInPaymentDetailsHistory(PaymentDetailsHistory paymentDetailsHistory);
	
	List<PaymentDetails> saveInPaymentDetailsByInvoiceNo(PaymentDetailsHistory paymentDetailsHistory);
	
	List<PaymentDetails> getPaymentDetailsByCustomer(Integer customerId);
	
	List<PaymentDetails> saveListOfPaymentDetailsByInvoiceNo(PaymentDetailsHistory[] paymentDetailsHistoryArray);
	
    Resource generateMasterReport() throws IOException;
}
