package billapplication.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import billapplication.entity.PaymentDetailsHistoryEntity;

@Repository(value = "paymentDetailsHistoryRepo")
public interface PaymentDetailsHistoryRepo extends CrudRepository<PaymentDetailsHistoryEntity, Long>{

}
