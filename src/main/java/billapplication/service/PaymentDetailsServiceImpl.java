package billapplication.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import billapplication.dao.PaymentDetailsHistoryRepo;
import billapplication.dao.PaymentDetailsRepo;
import billapplication.entity.CustomerEntity;
import billapplication.entity.InvoiceDetailsEntity;
import billapplication.entity.PaymentDetailsEntity;
import billapplication.entity.PaymentDetailsHistoryEntity;
import billapplication.model.Customer;
import billapplication.model.InvoiceDetails;
import billapplication.model.PaymentDetails;
import billapplication.model.PaymentDetailsHistory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

@Service(value = "paymentDetailsService")
public class PaymentDetailsServiceImpl implements PaymentDetailsService {
	
	@Autowired
	private PaymentDetailsRepo paymentDetailsRepo;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	Environment environment;
	
	@Autowired
	private PaymentDetailsHistoryRepo paymentDetailsHistoryRepo;

	@Override
	public void saveInPaymentDetails(PaymentDetails paymentDetails) {
		PaymentDetailsEntity paymentDetailsEntity = paymentDetailsToPaymentDetailsEntity(paymentDetails);
		if(paymentDetailsEntity != null){
			paymentDetailsRepo.save(paymentDetailsEntity);
		}
	}
	
	@Override
	public void saveInPaymentDetailsHistory(PaymentDetailsHistory paymentDetailsHistory) {
		PaymentDetailsHistoryEntity paymentDetailsHistoryEntity = paymentDetailsHistoryToPaymentDetailsHistoryEntity(paymentDetailsHistory);
		paymentDetailsHistoryRepo.save(paymentDetailsHistoryEntity);
	}
	
	
	@Override
	public List<PaymentDetails> saveInPaymentDetailsByInvoiceNo(PaymentDetailsHistory paymentDetailsHistory) {
		Optional<PaymentDetailsEntity> paymentDetailsEntityOp = paymentDetailsRepo.findByInvoiceDetailsEntityInvoiceno(paymentDetailsHistory.getInvoiceDetails().getInvoiceno());
		PaymentDetailsEntity paymentDetailsEntity = null;
		if(paymentDetailsEntityOp.isPresent()){
			paymentDetailsEntity = paymentDetailsEntityOp.get();
		}
		paymentDetailsEntity.setAmountDue((paymentDetailsHistory.getInvoiceValue()!=null ? paymentDetailsHistory.getInvoiceValue():0)-(paymentDetailsHistory.getDiscount() !=null ? paymentDetailsHistory.getDiscount():0)-(paymentDetailsHistory.getTds() !=null ? paymentDetailsHistory.getTds():0));
		paymentDetailsEntity.setTotalAmountReceived((paymentDetailsEntity.getTotalAmountReceived() != null ? paymentDetailsEntity.getTotalAmountReceived():0) + (paymentDetailsHistory.getAmountReceived() != null ? paymentDetailsHistory.getAmountReceived():0));
		paymentDetailsEntity.setBalancePending((paymentDetailsEntity.getAmountDue() != null ? paymentDetailsEntity.getAmountDue():0) - (paymentDetailsEntity.getTotalAmountReceived() != null ? paymentDetailsEntity.getTotalAmountReceived():0));
		paymentDetailsRepo.save(paymentDetailsEntity);
		saveInPaymentDetailsHistory(paymentDetailsHistory);
		return getPaymentDetailsByCustomer(paymentDetailsHistory.getCustomer().getCid());
	}
	
	@Override
	public List<PaymentDetails> saveListOfPaymentDetailsByInvoiceNo(PaymentDetailsHistory[] paymentDetailsHistoryArray) {
		for (PaymentDetailsHistory paymentDetailsHistory : paymentDetailsHistoryArray) {
			if(paymentDetailsHistory.getAmountReceived() != null && paymentDetailsHistory.getAmountReceived() != 0) {
				Optional<PaymentDetailsEntity> paymentDetailsEntityOp = paymentDetailsRepo.findByInvoiceDetailsEntityInvoiceno(paymentDetailsHistory.getInvoiceDetails().getInvoiceno());
				PaymentDetailsEntity paymentDetailsEntity = null;
				if(paymentDetailsEntityOp.isPresent()){
					paymentDetailsEntity = paymentDetailsEntityOp.get();
				}
				paymentDetailsEntity.setAmountDue((paymentDetailsHistory.getInvoiceValue()!=null ? paymentDetailsHistory.getInvoiceValue():0)-(paymentDetailsHistory.getDiscount() !=null ? paymentDetailsHistory.getDiscount():0)-(paymentDetailsHistory.getTds() !=null ? paymentDetailsHistory.getTds():0));
				paymentDetailsEntity.setTotalAmountReceived((paymentDetailsEntity.getTotalAmountReceived() != null ? paymentDetailsEntity.getTotalAmountReceived():0) + (paymentDetailsHistory.getAmountReceived() != null ? paymentDetailsHistory.getAmountReceived():0));
				paymentDetailsEntity.setBalancePending((paymentDetailsEntity.getAmountDue() != null ? paymentDetailsEntity.getAmountDue():0) - (paymentDetailsEntity.getTotalAmountReceived() != null ? paymentDetailsEntity.getTotalAmountReceived():0));
				paymentDetailsRepo.save(paymentDetailsEntity);
				saveInPaymentDetailsHistory(paymentDetailsHistory);
			}
		}
		return getPaymentDetailsByCustomer(paymentDetailsHistoryArray[0].getCustomer().getCid());
	}
	
	@Override
	public List<PaymentDetails> getPaymentDetailsByCustomer(Integer customerId) {
		Query query = null;
		if(customerId == null || customerId == 0){
			query = entityManager.createQuery("Select pde from PaymentDetailsEntity pde");
		}
		else{
			query = entityManager.createQuery("Select pde from PaymentDetailsEntity pde where pde.customerEntity.cid="+customerId);
		}
		List<PaymentDetailsEntity> paymentDetailsEntityList = query.getResultList();
		List<PaymentDetails> paymentDetailsList = null;
		if(!CollectionUtils.isEmpty(paymentDetailsEntityList)){
			paymentDetailsList = paymentDetailsEntityList.stream().map(paymentDetailsEntity -> paymentDetailsEntityToPaymentDetails(paymentDetailsEntity)).collect(Collectors.toList());
		}
		return paymentDetailsList;
	}
	
	public Resource generateMasterReport() throws IOException {
		try {
		String filePath = environment.getProperty("MASTER_REPORT_FILE_LOCATION");
		String fileName = environment.getProperty("FILE_NAME");
		String fullFilePath = filePath + "/" + fileName;
		File file = new File(fullFilePath);
		XSSFWorkbook xssfWb = new XSSFWorkbook();
		XSSFSheet xssfSheet = xssfWb.createSheet("Master_Report");
		XSSFRow xssfRow = xssfSheet.createRow(0);
		xssfRow.createCell(0).setCellValue("S. No.");
		xssfRow.createCell(1).setCellValue("Invoice Number");
		xssfRow.createCell(2).setCellValue("Invoice Date");
		xssfRow.createCell(3).setCellValue("Client Name");
		xssfRow.createCell(4).setCellValue("Taxable Value");
		xssfRow.createCell(5).setCellValue("CGST");
		xssfRow.createCell(6).setCellValue("SGST");
		xssfRow.createCell(7).setCellValue("IGST");
		xssfRow.createCell(8).setCellValue("Invoice Value");
		xssfRow.createCell(9).setCellValue("Discount");
		xssfRow.createCell(10).setCellValue("TDS");
		xssfRow.createCell(11).setCellValue("Amount Due");
		xssfRow.createCell(12).setCellValue("Amount Received");
		xssfRow.createCell(13).setCellValue("Date Of Receipt");
		xssfRow.createCell(14).setCellValue("Balance Pending");
		List<PaymentDetails> paymentDetailsList = this.getPaymentDetailsByCustomer(0);
		if(!CollectionUtils.isEmpty(paymentDetailsList)) {
			int i=1;
			for (PaymentDetails paymentDetails : paymentDetailsList) {
				XSSFRow tempXssfRow = xssfSheet.createRow(i);
				tempXssfRow.createCell(0).setCellValue(paymentDetails.getInvoiceDetails().getInvoiceno());
				tempXssfRow.createCell(1).setCellValue(paymentDetails.getCustomer().getCname());
				
				i++;
			}
		}
		FileOutputStream fileOut = new FileOutputStream(file);
		xssfWb.write(fileOut);
	    fileOut.close();
	    xssfWb.close();
	    Path path = Paths.get(filePath).resolve(fileName);
	    Resource resource = new UrlResource(path.toUri());

        if (resource.exists() || resource.isReadable()) {
            return resource;
        } else {
            throw new RuntimeException("Could not read the file!");
        }
    } catch (MalformedURLException e) {
        throw new RuntimeException("Error: " + e.getMessage());
    }
	}
	
	public PaymentDetailsEntity paymentDetailsToPaymentDetailsEntity(PaymentDetails paymentDetails){
		PaymentDetailsEntity paymentDetailsEntity = new PaymentDetailsEntity();
		System.out.println(paymentDetails);
		
		return paymentDetailsEntity;
	}
	
	public PaymentDetails paymentDetailsEntityToPaymentDetails(PaymentDetailsEntity paymentDetailsEntity){
		PaymentDetails paymentDetails = new PaymentDetails();
		paymentDetails.setAmountDue(paymentDetailsEntity.getAmountDue());
		paymentDetails.setAmountReceived(paymentDetailsEntity.getTotalAmountReceived());
		paymentDetails.setBalancePending(paymentDetailsEntity.getBalancePending());
		paymentDetails.setCgst(paymentDetailsEntity.getCgst());
		paymentDetails.setSgst(paymentDetailsEntity.getSgst());
		paymentDetails.setIgst(paymentDetailsEntity.getIgst());
		paymentDetails.setDiscount(paymentDetailsEntity.getDiscount());
		paymentDetails.setTds(paymentDetailsEntity.getTds());
		paymentDetails.setTaxableValue(paymentDetailsEntity.getTaxableValue());
		InvoiceDetails invoiceDetails = new InvoiceDetails();
		invoiceDetails.setInvoiceno(paymentDetailsEntity.getInvoiceDetailsEntity().getInvoiceno());
		paymentDetails.setInvoiceDetails(invoiceDetails);
		Customer customer = new Customer();
		customer.setCname(paymentDetailsEntity.getCustomerEntity().getCname());
		paymentDetails.setCustomer(customer);
		return paymentDetails;
	}
	
	public PaymentDetailsHistoryEntity paymentDetailsHistoryToPaymentDetailsHistoryEntity(PaymentDetailsHistory paymentDetailsHistory){
		PaymentDetailsHistoryEntity paymentDetailsHistoryEntity = new PaymentDetailsHistoryEntity();
		InvoiceDetailsEntity invoiceDetailsEntity = entityManager.find(InvoiceDetailsEntity.class, paymentDetailsHistory.getInvoiceDetails().getInvoiceno());
		CustomerEntity customerEntity = entityManager.find(CustomerEntity.class, paymentDetailsHistory.getCustomer().getCid());
		paymentDetailsHistoryEntity.setInvoiceDetailsEntity(invoiceDetailsEntity);
		paymentDetailsHistoryEntity.setCustomerEntity(customerEntity);
		paymentDetailsHistoryEntity.setInvoiceValue(paymentDetailsHistory.getInvoiceValue());
		paymentDetailsHistoryEntity.setAmountDue(paymentDetailsHistory.getAmountDue());
		paymentDetailsHistoryEntity.setAmountReceived(paymentDetailsHistory.getAmountReceived());
		paymentDetailsHistoryEntity.setBalancePending(paymentDetailsHistory.getBalancePending());
		paymentDetailsHistoryEntity.setCgst(paymentDetailsHistory.getCgst());
		paymentDetailsHistoryEntity.setSgst(paymentDetailsHistory.getSgst());
		paymentDetailsHistoryEntity.setIgst(paymentDetailsHistory.getIgst());
		paymentDetailsHistoryEntity.setDateOfReceipt(paymentDetailsHistory.getDateOfReceipt());
		paymentDetailsHistoryEntity.setDiscount(paymentDetailsHistory.getDiscount());
		paymentDetailsHistoryEntity.setTds(paymentDetailsHistory.getTds());
		paymentDetailsHistoryEntity.setTaxableValue(paymentDetailsHistory.getTaxableValue());
		return paymentDetailsHistoryEntity;
	}

}
