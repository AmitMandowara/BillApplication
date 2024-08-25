package billapplication.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import billapplication.entity.CustomerEntity;
import billapplication.entity.InvoiceDetailsEntity;
import billapplication.entity.PaymentDetailsEntity;

@Repository(value = "paymentDetailsRepo")
public interface PaymentDetailsRepo extends CrudRepository<PaymentDetailsEntity, Integer>{
	
	Optional<PaymentDetailsEntity> findByInvoiceDetailsEntity(InvoiceDetailsEntity invoiceDetailsEntity);
	
	List<PaymentDetailsEntity> findByCustomerEntity(CustomerEntity customerEntity);

	Optional<PaymentDetailsEntity> findByInvoiceDetailsEntityInvoiceno(String invoiceNo);
	
	Optional<PaymentDetailsEntity> findTop1ByInvoiceDetailsEntityInvoicenoOrderByIdDesc(String invoiceNo);
}
