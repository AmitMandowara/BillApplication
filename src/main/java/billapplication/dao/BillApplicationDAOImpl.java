package billapplication.dao;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Repository;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import billapplication.entity.AdminEntity;
import billapplication.entity.BankDetailsEntity;
import billapplication.entity.BillDetailsEntity;
import billapplication.entity.CustomerEntity;
import billapplication.entity.InvoiceDetailsEntity;
import billapplication.entity.LoginDetailsEntity;
import billapplication.entity.PaymentDetailsEntity;
import billapplication.model.BillDetails;
import billapplication.model.Customer;
import billapplication.model.InvoiceDetails;
import billapplication.model.LoginDetails;

@Repository(value = "billApplicationDao")
public class BillApplicationDAOImpl implements BillApplicationDAO {
	@Autowired
	Environment environment;
	@PersistenceContext
	EntityManager entityManager;
	@Autowired
	PaymentDetailsRepo paymentDetailsRepo;

	@Override
	public String customerAdd(Customer customer) {
		CustomerEntity customerEntity = new CustomerEntity();
		customerEntity.setCname(customer.getCname());
		customerEntity.setCaddress(customer.getCaddress());
		customerEntity.setCno(customer.getCno());
		customerEntity.setEmailid(customer.getMailId());
		customerEntity.setGstin(customer.getGstIn());
		customerEntity.setCtype(customer.getCtype());
		entityManager.persist(customerEntity);
		return environment.getProperty("ADD_SUCCESS_MESSAGE");
	}

	@Override
	public ArrayList<Customer> getCustomerDetails() {
		String sql = "select c from CustomerEntity c";
		Query query = entityManager.createQuery(sql);
		List<CustomerEntity> customerEntitiesList = query.getResultList();
		ArrayList<Customer> customersList = new ArrayList<>();
		for (CustomerEntity customerEntity : customerEntitiesList) {
			Customer customer = new Customer();
			customer.setCid(customerEntity.getCid());
			customer.setCname(customerEntity.getCname());
			customer.setCaddress(customerEntity.getCaddress());
			customer.setCno(customerEntity.getCno());
			customer.setGstIn(customerEntity.getGstin());
			customer.setMailId(customerEntity.getEmailid());
			customer.setCtype(customerEntity.getCtype());
			customersList.add(customer);
		}
		return customersList;
	}

	@Override
	public String updateCustomerDetails(Customer customer) {
		CustomerEntity customerEntity = entityManager.find(CustomerEntity.class, customer.getCid());
		customerEntity.setCname(customer.getCname());
		customerEntity.setCaddress(customer.getCaddress());
		customerEntity.setCno(customer.getCno());
		customerEntity.setEmailid(customer.getMailId());
		customerEntity.setGstin(customer.getGstIn());
		customerEntity.setCtype(customer.getCtype());
		return environment.getProperty("UPDATE_SUCCESS_MESSAGE");
	}

	@Override
	public InvoiceDetails getInvoiceDetails() throws Exception {
		Query query = entityManager.createQuery("Select ide from InvoiceDetailsEntity ide ORDER BY ide.count DESC");
		List<InvoiceDetailsEntity> invoiceDetailsEntityList = query.getResultList();
		InvoiceDetails invoiceDetails = new InvoiceDetails();
//		System.out.println(invoiceDetailsEntityList.get(0).getInvoiceno());
		if (!invoiceDetailsEntityList.isEmpty()) {
			String currentYear;
			String invoiceNo = invoiceDetailsEntityList.get(0).getInvoiceno();
			String[] invoiceNoArray = invoiceNo.split("/");
			invoiceNoArray[2] = Integer.toString(Integer.parseInt(invoiceNoArray[2]) + 1);
			if (LocalDate.now().getMonthValue() <= 3) {
				currentYear = Integer.toString(LocalDate.now().getYear()).substring(2);
			} else {
				currentYear = Integer.toString(LocalDate.now().getYear() + 1).substring(2);
			}
			int previousYear = Integer.parseInt(currentYear) - 1;
			invoiceDetails.setInvoiceno(
					String.join("/", invoiceNoArray[0], previousYear + "-" + currentYear, invoiceNoArray[2]));
			invoiceDetails.setInvoicedate(LocalDate.now());
			invoiceDetails.setCount(invoiceDetailsEntityList.get(0).getCount() + 1);
		} else {
//			DateFormat df = new SimpleDateFormat("yy");
			String currentYear;
			if (LocalDate.now().getMonthValue() <= 3) {
				currentYear = Integer.toString(LocalDate.now().getYear()).substring(2);
			} else {
				currentYear = Integer.toString(LocalDate.now().getYear() + 1).substring(2);
			}
//			String currentYear=df.format(Calendar.getInstance().getTime());
			int previousYear = Integer.parseInt(currentYear) - 1;
			invoiceDetails.setInvoiceno("SB/" + previousYear + "-" + currentYear + "/000");
			invoiceDetails.setInvoicedate(LocalDate.now());
			invoiceDetails.setCount(1);
		}
		return invoiceDetails;
	}

	@Override
	public String addBillDetails(BillDetails[] billDetails, boolean sign, LocalDate invoiceDate) throws Exception {
		AdminEntity adminEntity = entityManager.find(AdminEntity.class, 1);
		BankDetailsEntity bankDetailsEntity = entityManager.find(BankDetailsEntity.class, 1);
		CustomerEntity customerEntity = null;
		InvoiceDetailsEntity invoiceDetailsEntity = null;
		Long sum_taxable_value = 0L;
		Long igst = 0L;
		Long cgst_sgst = 0L;
		Long sum_igst = 0L;
		Long sum_cgst_sgst = 0L;
		Long sum_cgst_sgst_forsgst = 0L;
		Long cgst_sgst_forsgst = 0L;
		Long taxable_value;
		Long total = 0L;
		Long Invoice_Value = 0L;
		float x = 0f;
		String pdfPath = environment.getProperty("PDF_FILE_LOCATION");
		String signPath = environment.getProperty("SIGN_PATH");
		String caLogoPath = environment.getProperty("LOGO_PATH");
		List<Integer> billIdList = new ArrayList<>();
		String imageFile = caLogoPath;
		ImageData data = ImageDataFactory.create(imageFile);
		Image img = new Image(data);
		String signImage = signPath;
		ImageData data1 = ImageDataFactory.create(signImage);
		Image signImg = new Image(data1);
		PdfDocument pdfDocument = new PdfDocument(new PdfWriter(
				pdfPath + " " + billDetails[0].getInvoiceDetails().getInvoiceno().split("/")[2] + ".pdf"));
		Document layoutDocument = new Document(pdfDocument);
		layoutDocument = layoutDocument.setFontSize(8f);
		layoutDocument.setMargins(5, 5, 0, 5);
		layoutDocument.add(img.setHeight(50).setWidth(100).setHorizontalAlignment(HorizontalAlignment.CENTER));
		layoutDocument.add(new Paragraph("S BAHETI & ASSOCIATES").setBold().setFontSize(20)
				.setFont(PdfFontFactory.createFont(FontConstants.COURIER_OBLIQUE))
				.setFontColor(Color.convertRgbToCmyk(new DeviceRgb(41, 73, 43)))
				.setTextAlignment(TextAlignment.CENTER));
		layoutDocument.add(new Paragraph("TAX INVOICE").setFontColor(Color.WHITE)
				.setBackgroundColor(new DeviceRgb(0, 51, 102)).setTextAlignment(TextAlignment.CENTER));
		Table initialTable = new Table(UnitValue.createPercentArray(new float[] { 50, 15, 35 }));
		initialTable.addCell(new Cell().add("Supplier Details").setBold().setCharacterSpacing(x).setUnderline()
				.setBorder(Border.NO_BORDER));
		initialTable.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
		initialTable.addCell(new Cell().add("Customer Details").setTextAlignment(TextAlignment.LEFT).setBold()
				.setCharacterSpacing(x).setUnderline().setBorder(Border.NO_BORDER).setPadding(0));
		layoutDocument.add(initialTable);
		Table table1 = new Table(UnitValue.createPercentArray(new float[] { 10, 40, 15, 10, 25 }));
		table1.addCell(new Cell().add("Name: ").setBold().setCharacterSpacing(x).setBorder(Border.NO_BORDER)
				.setCharacterSpacing(x));
		table1.addCell(getCell(adminEntity.getName()));
		table1.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
		table1.addCell(new Cell().add("Name: ").setBold().setTextAlignment(TextAlignment.LEFT)
				.setBorder(Border.NO_BORDER).setCharacterSpacing(x));
		table1.addCell(getCell(billDetails[0].getCustomer().getCname()));
		table1.addCell(new Cell().add("GSTIN: ").setBold().setBorder(Border.NO_BORDER).setCharacterSpacing(x));
		table1.addCell(getCell(adminEntity.getGstIn()));
		table1.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
		table1.addCell(new Cell().add("GSTIN: ").setTextAlignment(TextAlignment.LEFT).setBold()
				.setBorder(Border.NO_BORDER).setCharacterSpacing(x));
		table1.addCell(getCell(billDetails[0].getCustomer().getGstIn()));
		table1.addCell(new Cell().add("Address: ").setBold().setBorder(Border.NO_BORDER).setCharacterSpacing(x));
		table1.addCell(getCell(adminEntity.getAddress()));
		table1.addCell(new Cell().add("").setBorder(Border.NO_BORDER));
		table1.addCell(new Cell().add("Address: ").setTextAlignment(TextAlignment.LEFT).setBold()
				.setBorder(Border.NO_BORDER).setCharacterSpacing(x));
		table1.addCell(getCell(billDetails[0].getCustomer().getCaddress()));
		layoutDocument.add(table1);
		layoutDocument.add(new Paragraph("\n"));
		Table invoiceTable = new Table(UnitValue.createPercentArray(new float[] { 11f, 29f, 50f, 10f }));
		invoiceTable.addCell(new Cell().add("Invoice No.: ").setBold().setCharacterSpacing(x)
				.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
		invoiceTable.addCell(new Cell().add(billDetails[0].getInvoiceDetails().getInvoiceno())
				.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
		invoiceTable.addCell(new Cell().add("Invoice Date: ").setBold().setCharacterSpacing(x)
				.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.RIGHT));
		if (invoiceDate == null) {
			invoiceTable.addCell(new Cell()
					.add(DateTimeFormatter.ofPattern("dd/MM/yyyy")
							.format(billDetails[0].getInvoiceDetails().getInvoicedate()))
					.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
		} else {
			invoiceTable.addCell(new Cell().add(DateTimeFormatter.ofPattern("dd/MM/yyyy").format(invoiceDate))
					.setBorder(Border.NO_BORDER).setTextAlignment(TextAlignment.LEFT));
		}
		invoiceTable.setHorizontalAlignment(HorizontalAlignment.CENTER);
		layoutDocument.add(invoiceTable);
		layoutDocument.add(new Paragraph("\n"));
		float[] tableColumnArray;
		if (billDetails[0].getCustomer().getCtype().equals("CGST/SGST")) {
			if (billDetails[0].getCgst() != 0 && billDetails[0].getSgst() != 0) {
				tableColumnArray = new float[] { 5, 15, 7, 12.33f, 10.66f, 10.66f, 7.66f, 7.66f, 7.66f, 7.66f, 8.66f };
			} else {
				tableColumnArray = new float[] { 5, 15, 7, 12.33f, 10.66f, 10.66f, 8.66f };
			}
		} else {
			if (billDetails[0].getIgst() != 0) {
				tableColumnArray = new float[] { 5, 20, 7, 12.33f, 10.66f, 9.66f, 11.75f, 11.75f, 11.75f };
			} else {
				tableColumnArray = new float[] { 5, 20, 7, 12.33f, 10.66f, 9.66f, 11.75f };
			}
		}
		Table table = new Table(UnitValue.createPercentArray(tableColumnArray));
		table.addCell(new Cell(2, 1).add("S.No.").setBold().setCharacterSpacing(x)
				.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
		table.addCell(new Cell(2, 1).add("Item Description").setBold().setCharacterSpacing(x)
				.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
		table.addCell(new Cell(2, 1).add("SAC").setBold().setCharacterSpacing(x)
				.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
		table.addCell(new Cell(2, 1).add("Basic Value").setBold().setCharacterSpacing(x)
				.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
		table.addCell(new Cell(2, 1).add("Quantity").setBold().setCharacterSpacing(x)
				.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
		table.addCell(new Cell(2, 1).add("Taxable Value").setBold().setCharacterSpacing(x)
				.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
		if (billDetails[0].getCustomer().getCtype().equals("CGST/SGST")) {
			if (billDetails[0].getCgst() != 0 && billDetails[0].getSgst() != 0) {
				table.addCell(new Cell(1, 2).add("CGST").setCharacterSpacing(x).setTextAlignment(TextAlignment.CENTER)
						.setBold().setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(new Cell(1, 2).add("SGST").setCharacterSpacing(x).setTextAlignment(TextAlignment.CENTER)
						.setBold().setBorder(new SolidBorder(Color.GRAY, 0.5f)));
			}
		} else if (billDetails[0].getIgst() != 0) {
			table.addCell(new Cell(1, 2).add("IGST").setCharacterSpacing(x).setTextAlignment(TextAlignment.CENTER)
					.setBold().setBorder(new SolidBorder(Color.GRAY, 0.5f)));
		}
		table.addCell(new Cell(2, 1).add("Total").setCharacterSpacing(x).setBold()
				.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
		if (billDetails[0].getCustomer().getCtype().equals("CGST/SGST")) {
			if (billDetails[0].getCgst() != 0 && billDetails[0].getSgst() != 0) {
				table.addCell(new Cell().add("Rate").setCharacterSpacing(x).setBold()
						.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(new Cell().add("Amt.").setCharacterSpacing(x).setBold()
						.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(new Cell().add("Rate").setCharacterSpacing(x).setBold()
						.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(new Cell().add("Amt.").setCharacterSpacing(x).setBold()
						.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
			}
		} else if (billDetails[0].getIgst() != 0) {
			table.addCell(new Cell().add("Rate").setCharacterSpacing(x).setBold()
					.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
			table.addCell(new Cell().add("Amt.").setCharacterSpacing(x).setBold()
					.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
		}
		for (int i = 0; i < billDetails.length; i++) {
			if (billDetails[i].getId() != 0) {
				billIdList.add(billDetails[i].getId());
			}
		}
		Query queryForbillId = entityManager
				.createQuery("select bde from BillDetailsEntity bde where bde.invoiceDetailsEntity.invoiceno='"
						+ billDetails[0].getInvoiceDetails().getInvoiceno() + "'");
		List<BillDetailsEntity> billEntitiesDatabaseList = queryForbillId.getResultList();
		for (BillDetailsEntity tempBillEntity : billEntitiesDatabaseList) {
			if (!billIdList.contains(tempBillEntity.getId())) {
				tempBillEntity.setInvoiceDetailsEntity(null);
				tempBillEntity.setCustomerEntity(null);
				entityManager.remove(tempBillEntity);
			}
		}
		for (int i = 0; i < billDetails.length; i++) {
			if (billDetails[i].getId() == 0) {
				BillDetailsEntity billDetailsEntity = new BillDetailsEntity();
				billDetailsEntity.setItem_desc(billDetails[i].getItem_desc());
				billDetailsEntity.setSac(adminEntity.getSac());
				billDetailsEntity.setBasic_value(billDetails[i].getBasic_value());
				billDetailsEntity.setQuantity(billDetails[i].getQuantity());
				billDetailsEntity.setCgst(billDetails[i].getCgst());
				billDetailsEntity.setSgst(billDetails[i].getSgst());
				billDetailsEntity.setIgst(billDetails[i].getIgst());
				taxable_value = billDetails[i].getBasic_value() * billDetails[i].getQuantity();
				customerEntity = entityManager.find(CustomerEntity.class, billDetails[i].getCustomer().getCid());
				billDetailsEntity.setCustomerEntity(customerEntity);
				invoiceDetailsEntity = entityManager.find(InvoiceDetailsEntity.class,
						billDetails[i].getInvoiceDetails().getInvoiceno());
				if (customerEntity.getCtype().equals("CGST/SGST")) {
					cgst_sgst = (long) Math.ceil(taxable_value * billDetailsEntity.getCgst() / 100.0);
					cgst_sgst_forsgst = (long) Math.ceil(taxable_value * billDetailsEntity.getSgst() / 100.0);
					total = taxable_value + cgst_sgst + cgst_sgst_forsgst;
					Invoice_Value += total;
				} else {
					igst = (long) Math.ceil(taxable_value * billDetailsEntity.getIgst() / 100.0);
					total = taxable_value + igst;
					Invoice_Value += total;
				}
				invoiceDetailsEntity.setTotalValue(Invoice_Value.intValue());
				billDetailsEntity.setInvoiceDetailsEntity(invoiceDetailsEntity);
				entityManager.persist(billDetailsEntity);
				table.addCell(new Cell().add(Integer.toString(i + 1)).setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(
						new Cell().add(billDetailsEntity.getItem_desc()).setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(new Cell().add(billDetailsEntity.getSac()).setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(
						new Cell().add(BillApplicationDAOImpl.format(Long.toString(billDetailsEntity.getBasic_value())))
								.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(new Cell().add(Long.toString(billDetailsEntity.getQuantity()))
						.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(new Cell().add(BillApplicationDAOImpl.format(Long.toString(taxable_value)))
						.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				if (customerEntity.getCtype().equals("CGST/SGST")) {
					if (billDetails[i].getCgst() != 0 && billDetails[i].getSgst() != 0) {
						table.addCell(new Cell().add(Integer.toString(billDetailsEntity.getCgst()) + "%")
								.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
						table.addCell(new Cell().add(BillApplicationDAOImpl.format(cgst_sgst.toString()))
								.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
						table.addCell(new Cell().add(Integer.toString(billDetailsEntity.getSgst()) + "%")
								.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
						table.addCell(new Cell().add(BillApplicationDAOImpl.format(cgst_sgst_forsgst.toString()))
								.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
					}
				} else {
					if (billDetails[i].getIgst() != 0) {
						table.addCell(new Cell().add(Integer.toString(billDetailsEntity.getIgst()) + "%")
								.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
						table.addCell(new Cell().add(BillApplicationDAOImpl.format(igst.toString()))
								.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
					}
				}
				table.addCell(new Cell().add(BillApplicationDAOImpl.format(total.toString()))
						.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				sum_taxable_value += taxable_value;
				sum_igst += igst;
				sum_cgst_sgst += cgst_sgst;
				sum_cgst_sgst_forsgst += cgst_sgst_forsgst;
			} else {
				BillDetailsEntity billDetailsEntity = entityManager.find(BillDetailsEntity.class,
						billDetails[i].getId());
				billDetailsEntity.setItem_desc(billDetails[i].getItem_desc());
				billDetailsEntity.setSac(adminEntity.getSac());
				billDetailsEntity.setBasic_value(billDetails[i].getBasic_value());
				billDetailsEntity.setQuantity(billDetails[i].getQuantity());
				billDetailsEntity.setCgst(billDetails[i].getCgst());
				billDetailsEntity.setSgst(billDetails[i].getSgst());
				billDetailsEntity.setIgst(billDetails[i].getIgst());
				taxable_value = billDetails[i].getBasic_value() * billDetails[i].getQuantity();
				customerEntity = entityManager.find(CustomerEntity.class, billDetails[i].getCustomer().getCid());
				billDetailsEntity.setCustomerEntity(customerEntity);
				invoiceDetailsEntity = entityManager.find(InvoiceDetailsEntity.class,
						billDetails[i].getInvoiceDetails().getInvoiceno());
				invoiceDetailsEntity.setInvoicedate(invoiceDate);
				if (customerEntity.getCtype().equals("CGST/SGST")) {
					cgst_sgst = (long) Math.ceil(taxable_value * billDetailsEntity.getCgst() / 100.0);
					cgst_sgst_forsgst = (long) Math.ceil(taxable_value * billDetailsEntity.getSgst() / 100.0);
					total = taxable_value + cgst_sgst + cgst_sgst_forsgst;
					Invoice_Value += total;
				} else {
					igst = (long) Math.ceil(taxable_value * billDetailsEntity.getIgst() / 100.0);
					total = taxable_value + igst;
					Invoice_Value += total;
				}
				invoiceDetailsEntity.setTotalValue(Invoice_Value.intValue());
				billDetailsEntity.setInvoiceDetailsEntity(invoiceDetailsEntity);
				entityManager.persist(billDetailsEntity);
				table.addCell(new Cell().add(Integer.toString(i + 1)).setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(
						new Cell().add(billDetailsEntity.getItem_desc()).setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(new Cell().add(billDetailsEntity.getSac()).setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(
						new Cell().add(BillApplicationDAOImpl.format(Long.toString(billDetailsEntity.getBasic_value())))
								.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(new Cell().add(Long.toString(billDetailsEntity.getQuantity()))
						.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(new Cell().add(BillApplicationDAOImpl.format(Long.toString(taxable_value)))
						.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				if (customerEntity.getCtype().equals("CGST/SGST")) {
					if (billDetails[0].getCgst() != 0 && billDetails[0].getSgst() != 0) {
						table.addCell(new Cell().add(Integer.toString(billDetailsEntity.getCgst()) + "%")
								.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
						table.addCell(new Cell().add(BillApplicationDAOImpl.format(cgst_sgst.toString()))
								.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
						table.addCell(new Cell().add(Integer.toString(billDetailsEntity.getSgst()) + "%")
								.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
						table.addCell(new Cell().add(BillApplicationDAOImpl.format(cgst_sgst_forsgst.toString()))
								.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
					}
				} else if (billDetails[0].getIgst() != 0) {
					table.addCell(new Cell().add(Integer.toString(billDetailsEntity.getIgst()) + "%")
							.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
					table.addCell(new Cell().add(BillApplicationDAOImpl.format(igst.toString()))
							.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				}
				table.addCell(new Cell().add(BillApplicationDAOImpl.format(total.toString()))
						.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				sum_taxable_value += taxable_value;
				sum_igst += igst;
				sum_cgst_sgst += cgst_sgst;
				sum_cgst_sgst_forsgst += cgst_sgst_forsgst;
			}

		}

		table.addCell(new Cell(1, 1).add("").setBorder(new SolidBorder(Color.GRAY, 0.5f)));
		table.addCell(new Cell(1, 4).add("SubTotal:").setBold().setCharacterSpacing(x)
				.setHorizontalAlignment(HorizontalAlignment.CENTER).setBorder(new SolidBorder(Color.GRAY, 0.5f)));
		table.addCell(new Cell(1, 1).add(BillApplicationDAOImpl.format(Long.toString(sum_taxable_value)))
				.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
		if (customerEntity.getCtype().equals("CGST/SGST")) {
			if (billDetails[0].getCgst() != 0 && billDetails[0].getSgst() != 0) {
				table.addCell(new Cell(1, 1).add("-").setBold().setHorizontalAlignment(HorizontalAlignment.CENTER)
						.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(new Cell(1, 1).add(BillApplicationDAOImpl.format(Long.toString(sum_cgst_sgst))).setBold()
						.setCharacterSpacing(x).setHorizontalAlignment(HorizontalAlignment.CENTER)
						.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(new Cell(1, 1).add("-").setBold().setHorizontalAlignment(HorizontalAlignment.CENTER)
						.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
				table.addCell(new Cell(1, 1).add(BillApplicationDAOImpl.format(Long.toString(sum_cgst_sgst_forsgst)))
						.setBold().setCharacterSpacing(x).setHorizontalAlignment(HorizontalAlignment.CENTER)
						.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
			}
		} else if (billDetails[0].getIgst() != 0) {
			table.addCell(new Cell(1, 1).add("-").setBorder(new SolidBorder(Color.GRAY, 0.5f)).setBold()
					.setCharacterSpacing(x));
			table.addCell(new Cell(1, 1).add(BillApplicationDAOImpl.format(Long.toString(sum_igst)))
					.setBorder(new SolidBorder(Color.GRAY, 0.5f)).setBold().setCharacterSpacing(x));
		}
		table.addCell(new Cell(1, 1).add("-").setBorder(new SolidBorder(Color.GRAY, 0.5f)));
		if (customerEntity.getCtype().equals("CGST/SGST")) {
			if (billDetails[0].getCgst() != 0 && billDetails[0].getSgst() != 0) {
				table.addCell(new Cell(1, 10).add("Total invoice value (in figure)").setCharacterSpacing(x).setBold()
						.setTextAlignment(TextAlignment.RIGHT).setBorder(new SolidBorder(Color.GRAY, 0.5f)));
			} else {
				table.addCell(new Cell(1, 6).add("Total invoice value (in figure)").setCharacterSpacing(x).setBold()
						.setTextAlignment(TextAlignment.RIGHT).setBorder(new SolidBorder(Color.GRAY, 0.5f)));
			}
		} else {
			if (billDetails[0].getIgst() != 0) {
				table.addCell(new Cell(1, 8).add("Total invoice value (in figure)").setBold().setCharacterSpacing(x)
						.setTextAlignment(TextAlignment.RIGHT).setBorder(new SolidBorder(Color.GRAY, 0.5f)));
			} else {
				table.addCell(new Cell(1, 6).add("Total invoice value (in figure)").setCharacterSpacing(x).setBold()
						.setTextAlignment(TextAlignment.RIGHT).setBorder(new SolidBorder(Color.GRAY, 0.5f)));
			}
		}
		table.addCell(new Cell(1, 1).add("Rs." + BillApplicationDAOImpl.format(Long.toString(Invoice_Value)))
				.setBorder(new SolidBorder(Color.GRAY, 0.5f)));
		layoutDocument.add(table);
		layoutDocument.add(new Paragraph("\n"));
		Table totalInvoiceTable = new Table(2);
		totalInvoiceTable.addCell(new Cell().add("Total invoice value (in Words)").setCharacterSpacing(x)
				.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setBold());
		totalInvoiceTable.addCell(new Cell()
				.add("Rupees " + NumberToWordConverter.convert(Integer.parseInt(Long.toString(Invoice_Value))))
				.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
		totalInvoiceTable.addCell(new Cell().add("Amount of tax subject to reverse charges").setCharacterSpacing(x)
				.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER).setBold());
		Table nestedTable = new Table(3);
		nestedTable.addCell(new Cell().add("-"));
		nestedTable.addCell(new Cell().add("-"));
		nestedTable.addCell(new Cell().add("-"));
		totalInvoiceTable
				.addCell(new Cell().add(nestedTable).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
		layoutDocument.add(totalInvoiceTable.setMargins(0.5f, 0.5f, 20f, 0.5f));
		layoutDocument.add(
				new Paragraph("\n Bank Details").setCharacterSpacing(x).setTextAlignment(TextAlignment.LEFT).setBold());
		Table lastTable = new Table(2);
		Table bankDetailsTable = new Table(UnitValue.createPercentArray(new float[] { 30, 70 }));
		bankDetailsTable.setHorizontalAlignment(HorizontalAlignment.LEFT);
		bankDetailsTable.setWidth(300);
		bankDetailsTable.addCell(new Cell().add("Bank Name ").setBorder(Border.NO_BORDER));
		bankDetailsTable.addCell(new Cell().add(bankDetailsEntity.getBankname()).setTextAlignment(TextAlignment.LEFT)
				.setBorder(Border.NO_BORDER));
		bankDetailsTable.addCell(new Cell().add("Account Name ").setBorder(Border.NO_BORDER));
		bankDetailsTable.addCell(new Cell().add(bankDetailsEntity.getAccountname()).setTextAlignment(TextAlignment.LEFT)
				.setBorder(Border.NO_BORDER));
		bankDetailsTable.addCell(new Cell().add("Account Number ").setBorder(Border.NO_BORDER));
		bankDetailsTable.addCell(new Cell().add(bankDetailsEntity.getAccountnumber())
				.setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
		bankDetailsTable.addCell(new Cell().add("Account Type ").setBorder(Border.NO_BORDER));
		bankDetailsTable.addCell(new Cell().add(bankDetailsEntity.getAccounttype()).setTextAlignment(TextAlignment.LEFT)
				.setBorder(Border.NO_BORDER));
		bankDetailsTable.addCell(new Cell().add("IFSC Code ").setBorder(Border.NO_BORDER));
		bankDetailsTable.addCell(new Cell().add(bankDetailsEntity.getIfsccode()).setTextAlignment(TextAlignment.LEFT)
				.setBorder(Border.NO_BORDER));
		lastTable.addCell(
				new Cell().add(bankDetailsTable).setTextAlignment(TextAlignment.LEFT).setBorder(Border.NO_BORDER));
		if (sign) {
			lastTable.addCell(new Cell()
					.add(new Paragraph("Thanking You,").setCharacterSpacing(x).setMarginRight(42).setBold()).add("\n\n")
					.add(new Paragraph("For S Baheti & Associates").setCharacterSpacing(x).setBold())
					.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
		} else {
			lastTable.addCell(
					new Cell().add(new Paragraph("Thanking You,").setCharacterSpacing(x).setMarginRight(40).setBold())
							.setBorder(Border.NO_BORDER)
							.add(signImg.setWidth(100).setHeight(30).setHorizontalAlignment(HorizontalAlignment.RIGHT))
							.add(new Paragraph("For S Baheti & Associates").setCharacterSpacing(x).setBold())
							.setTextAlignment(TextAlignment.RIGHT).setBorder(Border.NO_BORDER));
		}
		layoutDocument.add(lastTable);
		layoutDocument.add(new Paragraph("\n"));
		layoutDocument.add(new Paragraph("Payment due within 7 days of Invoice date").setCharacterSpacing(x).setBold());
		pdfDocument.addEventHandler(PdfDocumentEvent.END_PAGE, new TextFooterEventHandler(layoutDocument, pdfDocument));
		layoutDocument.close();
		
		Optional<PaymentDetailsEntity> paymentDetailsEntityOp = paymentDetailsRepo.findByInvoiceDetailsEntityInvoiceno(invoiceDetailsEntity.getInvoiceno());
		PaymentDetailsEntity paymentDetailsEntity = null;
		if(paymentDetailsEntityOp.isPresent()) {
			paymentDetailsEntity = paymentDetailsEntityOp.get();
		}else {
			paymentDetailsEntity = new PaymentDetailsEntity();
		}
		paymentDetailsEntity.setInvoiceDetailsEntity(invoiceDetailsEntity);
		paymentDetailsEntity.setCustomerEntity(customerEntity);
		paymentDetailsEntity.setInvoiceValue(Invoice_Value);
		paymentDetailsEntity.setAmountDue(Invoice_Value);
		paymentDetailsEntity.setCgst(sum_cgst_sgst);
		paymentDetailsEntity.setSgst(sum_cgst_sgst_forsgst);
		paymentDetailsEntity.setIgst(sum_igst);
		paymentDetailsEntity.setTaxableValue(sum_taxable_value);
		paymentDetailsRepo.save(paymentDetailsEntity);
		return pdfPath + " " + billDetails[0].getInvoiceDetails().getInvoiceno().split("/")[2] + ".pdf";
	}

	public Cell getCell(String text) {
		Cell cell = new Cell().add(text);
		cell.setTextAlignment(TextAlignment.LEFT);
		cell.setBorder(Border.NO_BORDER);
		return cell;
	}

	private static class TextFooterEventHandler implements IEventHandler {
		protected Document doc;
		protected PdfDocument pdfDocument;

		public TextFooterEventHandler(Document doc, PdfDocument pdfDocument) {
			this.doc = doc;
			this.pdfDocument = pdfDocument;
		}

		@Override
		public void handleEvent(Event currentEvent) {
			PdfDocumentEvent docEvent = (PdfDocumentEvent) currentEvent;
			Rectangle pageSize = docEvent.getPage().getPageSize();
			PdfFont font = null;
			try {
				font = PdfFontFactory.createFont(FontConstants.HELVETICA_OBLIQUE);
			} catch (IOException e) {
				System.err.println(e.getMessage());
			}

			float coordX = ((pageSize.getLeft() + doc.getLeftMargin()) + (pageSize.getRight() - doc.getRightMargin()))
					/ 2;
			float footerY = doc.getBottomMargin() + 2;

			PdfCanvas pdfCanvas = new PdfCanvas(docEvent.getPage());
			Canvas canvas = new Canvas(pdfCanvas, pdfDocument, pageSize);
			canvas.setFont(font).setFontSize(8)
					.showTextAligned("S Baheti & Associates \n ", pageSize.getLeft() + doc.getLeftMargin(), footerY,
							TextAlignment.LEFT)
					.showTextAligned(
							"Office No -104, Royal Tranquil, Konkane Chowk, Rahatani, Pune-411017 \n www.casbaheti.com \t | \t Email:ca.sbaheti@gmail.com",
							coordX, footerY, TextAlignment.CENTER)
					.showTextAligned("Contact : 8956760409 \n ", pageSize.getRight() - doc.getRightMargin(), footerY,
							TextAlignment.RIGHT);
			canvas.close();
		}
	}

	public static String format(String num) {
		String temp = "";
		while (num.substring(0, 1).equals("0") && num.length() > 1) {
			num = num.substring(1);
		}
		if (num.length() <= 3) {
			return num;

		} else if (num.length() % 2 != 0) {
			temp = num.substring(0, 2);
			num = num.substring(2);
		} else {
			temp = num.substring(0, 1);
			num = num.substring(1);
		}
		while (num.length() > 3) {
			temp += "," + num.substring(0, 2);
			num = num.substring(2);

		}
		return temp + "," + num;
	}

	@Override
	public String addInvoiceDetails(InvoiceDetails invoiceDetails) throws Exception {
		InvoiceDetailsEntity ide = new InvoiceDetailsEntity();
		ide.setInvoiceno(invoiceDetails.getInvoiceno());
		ide.setInvoicedate(invoiceDetails.getInvoicedate());
		ide.setCount(invoiceDetails.getCount());
		ide.setTotalValue(invoiceDetails.getTotalValue());
		CustomerEntity customerEntity = entityManager.find(CustomerEntity.class, invoiceDetails.getCustomer().getCid());
		ide.setCustomerEntity(customerEntity);
		entityManager.persist(ide);
		return "Invoice Details added Successfully";
	}

	@Override
	public String invoiceNoCheck(String invoiceNo) throws Exception {
		String[] invoiceNoArray = invoiceNo.split("/");
		if (Integer.parseInt(invoiceNoArray[2]) == 0) {
			throw new Exception("Invoice No can't be zero");
		}
		if (invoiceNoArray[2].length() < 3) {
			invoiceNoArray[2] = String.format("%03d", Integer.parseInt(invoiceNoArray[2]));
			invoiceNo = invoiceNoArray[0] + "/" + invoiceNoArray[1] + "/" + invoiceNoArray[2];
		}
		InvoiceDetailsEntity ide = entityManager.find(InvoiceDetailsEntity.class, invoiceNo);
		if (ide != null) {
			throw new Exception("Invoice No already exists");
		} else {
			return "Not Found";
		}
	}

	@Override
	public List<InvoiceDetails> getAllInvoice(Integer customerId) {
		Query query = null;
		if (customerId == 0) {
			query = entityManager.createQuery("Select ide from InvoiceDetailsEntity ide");
		} else {
			query = entityManager
					.createQuery("Select ide from InvoiceDetailsEntity ide where ide.customerEntity.cid=" + customerId);
		}
		List<InvoiceDetailsEntity> invoiceDetailsEntitiesArray = query.getResultList();
		List<InvoiceDetails> invoiceDetailsArray = new ArrayList<>();
		for (InvoiceDetailsEntity invoiceDetailsEntity : invoiceDetailsEntitiesArray) {
			InvoiceDetails invoiceDetails = new InvoiceDetails();
			invoiceDetails.setInvoiceno(invoiceDetailsEntity.getInvoiceno());
			invoiceDetails.setInvoicedate(invoiceDetailsEntity.getInvoicedate());
			invoiceDetails.setTotalValue(invoiceDetailsEntity.getTotalValue());
			Customer customer = new Customer();
			customer.setCname(invoiceDetailsEntity.getCustomerEntity() != null
					? invoiceDetailsEntity.getCustomerEntity().getCname()
					: "");
			invoiceDetails.setCustomer(customer);
			invoiceDetailsArray.add(invoiceDetails);
		}
		return invoiceDetailsArray;
	}

	@Override
	public LoginDetails getLoginDetails() throws Exception {
		LoginDetailsEntity lde = entityManager.find(LoginDetailsEntity.class, 1);
		LoginDetails loginDetails = new LoginDetails();
		if (lde != null) {
			loginDetails.setUserName(lde.getUserName());
			loginDetails.setPassword(lde.getPassword());
		} else {
			throw new Exception("No credentials");
		}
		return loginDetails;
	}

	public List<BillDetails> getBillDetailsByInvoiceNo(InvoiceDetails invoiceDetails) throws Exception {
		Query query = entityManager
				.createQuery("Select bde from BillDetailsEntity bde where bde.invoiceDetailsEntity.invoiceno='"
						+ invoiceDetails.getInvoiceno() + "'");
		List<BillDetailsEntity> billDetailsEntitiesList = query.getResultList();
		List<BillDetails> billDetailsList = new ArrayList<>();
		for (BillDetailsEntity billDetailsEntity : billDetailsEntitiesList) {
			BillDetails billDetails = new BillDetails();
			billDetails.setId(billDetailsEntity.getId());
			billDetails.setBasic_value(billDetailsEntity.getBasic_value());
			billDetails.setSac(billDetailsEntity.getSac());
			billDetails.setItem_desc(billDetailsEntity.getItem_desc());
			billDetails.setQuantity(billDetailsEntity.getQuantity());
			billDetails.setCgst(billDetailsEntity.getCgst());
			billDetails.setSgst(billDetailsEntity.getSgst());
			billDetails.setIgst(billDetailsEntity.getIgst());
			Customer customer = new Customer();
			customer.setCid(billDetailsEntity.getCustomerEntity().getCid());
			customer.setCaddress(billDetailsEntity.getCustomerEntity().getCaddress());
			customer.setCname(billDetailsEntity.getCustomerEntity().getCname());
			customer.setCno(billDetailsEntity.getCustomerEntity().getCno());
			customer.setCtype(billDetailsEntity.getCustomerEntity().getCtype());
			customer.setGstIn(billDetailsEntity.getCustomerEntity().getGstin());
			customer.setMailId(billDetailsEntity.getCustomerEntity().getEmailid());
			billDetails.setCustomer(customer);
			billDetails.setInvoiceDetails(invoiceDetails);
			billDetailsList.add(billDetails);
		}
		return billDetailsList;
	}
}

class NumberToWordConverter {
	public static final String[] units = { "", "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine",
			"Ten", "Eleven", "Twelve", "Thirteen", "Fourteen", "Fifteen", "Sixteen", "Seventeen", "Eighteen",
			"Nineteen" };

	public static final String[] tens = { "", "", "Twenty", "Thirty", "Forty", "Fifty", "Sixty", "Seventy", "Eighty",
			"Ninety" };

	public static String convert(final int n) {
		if (n < 0) {
			return "Minus " + convert(-n);
		}
		if (n < 20) {
			return units[n];
		}
		if (n < 100) {
			return tens[n / 10] + ((n % 10 != 0) ? " " : "") + units[n % 10];
		}
		if (n < 1000) {
			return units[n / 100] + " Hundred" + ((n % 100 != 0) ? " " : "") + convert(n % 100);
		}
		if (n < 100000) {
			return convert(n / 1000) + " Thousand" + ((n % 10000 != 0) ? " " : "") + convert(n % 1000);
		}
		if (n < 10000000) {
			return convert(n / 100000) + " Lakh" + ((n % 100000 != 0) ? " " : "") + convert(n % 100000);
		}

		return convert(n / 10000000) + " Crore" + ((n % 10000000 != 0) ? " " : "") + convert(n % 10000000);
	}
}
