package com.emr.app;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hyperledger.fabric.Logger;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;

import com.owlike.genson.Genson;

@Contract(name = "ElectronicMedicalRecordsChainCode", info = @Info(title = "Electronic medical records contract", description = "Hold the electronic medical records of patients", version = "0.0.1-SNAPSHOT"))
@Default
public class ElectronicMedicalRecordsChainCode implements ContractInterface {
	
	private Logger log = Logger.getLogger(getClass());

	private final Genson genson = new Genson();

	private enum EmrErrors {
		PATIENT_RECORD_NOT_FOUND, 
		PATIENT_RECORD_ALREADY_EXIST,
		TEST_REPORT_ALREADY_EXIST,
		TEST_REPORT_NOT_FOUND
	}

	// Initialize ledger with initial electronic medical records data
	@Transaction
	public void initLedger(final Context ctx) {

	}

	/**
	 * Create patient medical records
	 * 
	 * @param ctx
	 * @param patientId
	 * @param name
	 * @param diagnosis
	 * @param treatment
	 * @param dateofVisit
	 * 
	 * @exception ChaincodeException
	 */

	@Transaction
	public void createMedicalRecord(final Context ctx, String patientId, String name, String diagnosis,
			String treatment, String dateofVisit) {
		ChaincodeStub stub = ctx.getStub();
		String patientRecord = stub.getStringState(patientId);

		if (StringUtils.isNotEmpty(patientRecord)) {
			String errorMessage = String.format("Patient id %s records is already exists", patientId);
			throw new ChaincodeException(errorMessage, EmrErrors.PATIENT_RECORD_ALREADY_EXIST.toString());
		}

		MedicalRecord record = new MedicalRecord(patientId, name, diagnosis, treatment, dateofVisit);
		stub.putStringState(patientId, genson.serialize(record));
		
		log.info(String.format("#### Patient id %s added", patientId));

	}
	
	/**
	 * Add test report for patient
	 * 
	 * @param ctx
	 * @param patientId
	 * @param reportId
	 * @param reportName
	 * @param description
	 * @param reportDate
	 * @param resportIssuer
	 * 
	 * @exception ChaincodeException
	 */
	
	@Transaction
	public void addTestReport(final Context ctx, String patientId, String reportId,  String reportName, String description, 
			String reportDate, String resportIssuer) {
		ChaincodeStub stub = ctx.getStub();
		String patientRecord = viewMedicalRecord(ctx, patientId);
		
		MedicalRecord medicalRecord = genson.deserialize(patientRecord, MedicalRecord.class);
		
		if(CollectionUtils.isNotEmpty(medicalRecord.getTestReports())) {
			Optional<TestReport> report = medicalRecord.getTestReports()
				.stream()
				.filter(testReport -> testReport.getReportId().equalsIgnoreCase(reportId))
				.findAny();
			if (report.isPresent())
				throw new ChaincodeException(String.format("Report id  %s alreay exist", reportId), 
						EmrErrors.TEST_REPORT_ALREADY_EXIST.toString());
				
		}
		
		TestReport testReport = new TestReport(reportId, patientId, reportName, description, reportDate, resportIssuer);
		
		medicalRecord.getTestReports().add(testReport);
		
		stub.putStringState(patientId, genson.serialize(medicalRecord));
		
		log.info(String.format("#### Report id %s added for patient id %s", reportId, patientId));
	}
	
	/**
	 * Add medicine for patient
	 * 
	 * @param ctx
	 * @param patientId
	 * @param medicineName
	 * @param medicineExpiry
	 * @param medicineTakenDate
	 * 
	 * @exception ChaincodeException
	 */
	@Transaction
	public void addMedicine(final Context ctx, String patientId, String medicineName,  String medicineExpiry, String medicineTakenDate) {
		ChaincodeStub stub = ctx.getStub();
		String patientRecord = viewMedicalRecord(ctx, patientId);
		
		MedicalRecord medicalRecord = genson.deserialize(patientRecord, MedicalRecord.class);
		
		if(StringUtils.isNotEmpty(medicineExpiry)) {
			LocalDate expiryDate = LocalDate.parse(medicineExpiry);
			if (expiryDate.equals(LocalDate.now())|| expiryDate.isBefore(LocalDate.now())) {
				throw new ChaincodeException(String.format("Medicine %s is expired on date %s", medicineName, medicineExpiry));
			}
		}
		
		Medicine medicine = new Medicine(patientId, medicineName, medicineExpiry, medicineTakenDate);
		
		medicalRecord.getMedicines().add(medicine);
		
		stub.putStringState(patientId, genson.serialize(medicalRecord));
		
		log.info(String.format("#### Medicine %s is taken by patient id %s", medicineName, patientId));
	}

	/**
	 * Update patient medical records
	 * 
	 * @param ctx
	 * @param patientId
	 * @param diagnosis
	 * @param treatment
	 * @param dateOfVisit
	 * 
	 * @exception ChaincodeException
	 */

	@Transaction
	public void updateMedicalRecord(final Context ctx, String patientId, String diagnosis, String treatment,
			String dateOfVisit) {
		ChaincodeStub stub = ctx.getStub();

		String patientRecord = stub.getStringState(patientId);
		if (StringUtils.isEmpty(patientRecord)) {
			String errorMessage = String.format("Patient id  %s does not exist", patientId);
			throw new ChaincodeException(errorMessage, EmrErrors.PATIENT_RECORD_NOT_FOUND.toString());
		}
		MedicalRecord medicalRecord = genson.deserialize(patientRecord, MedicalRecord.class);
		medicalRecord.setDateOfVisit(dateOfVisit);
		medicalRecord.setDiagnosis(diagnosis);
		medicalRecord.setTreatment(treatment);

		stub.putStringState(patientId, genson.serialize(medicalRecord));
		
		log.info(String.format("#### Patient id %s updated", patientId));

	}
	
	/**
	 * Update test report for patient
	 * 
	 * @param ctx
	 * @param patientId
	 * @param reportId
	 * @param reportName
	 * @param description
	 * @param reportDate
	 * @param resportIssuer
	 * 
	 * @exception ChaincodeException
	 */
	
	@Transaction
	public void updateTestReport(final Context ctx, String patientId, String reportId,  String reportName, String description, 
			String reportDate, String resportIssuer) {
		ChaincodeStub stub = ctx.getStub();

		String patientRecord = stub.getStringState(patientId);
		if (StringUtils.isEmpty(patientRecord)) {
			String errorMessage = String.format("Patient id  %s does not exist", patientId);
			throw new ChaincodeException(errorMessage, EmrErrors.PATIENT_RECORD_NOT_FOUND.toString());
		}
		MedicalRecord medicalRecord = genson.deserialize(patientRecord, MedicalRecord.class);
		
		if(CollectionUtils.isNotEmpty(medicalRecord.getTestReports())) {
			Optional<TestReport> report  = medicalRecord.getTestReports()
			.stream()
			.filter(testReport -> testReport.getReportId().equalsIgnoreCase(reportId))
			.findFirst();
			
			if(!report.isPresent()) {
				String errorMessage = String.format("Report id %s id does not exist for patient id %s", reportId, patientId);
				throw new ChaincodeException(errorMessage, EmrErrors.TEST_REPORT_NOT_FOUND.toString());
			}
			
			TestReport testReport = report.get();
			testReport.setReportName(reportName);
			testReport.setResportIssuer(resportIssuer);
			testReport.setDescription(description);
			testReport.setReportDate(reportDate);
			
	        medicalRecord.getTestReports().removeIf(existingReport -> reportId.equals(existingReport.getReportId()));
	        medicalRecord.getTestReports().add(testReport);
					
		} else {
			String errorMessage = String.format("Test report(s) is not uploaded for patient id", patientId);
			throw new ChaincodeException(errorMessage, EmrErrors.TEST_REPORT_NOT_FOUND.toString());
		}
		
	
		
		stub.putStringState(patientId, genson.serialize(medicalRecord));
		
		log.info(String.format("#### Report id %s for Patient id %s is updated", reportId, patientId));

	}

	/**
	 * Get patient medical record by patient id
	 * 
	 * @param ctx
	 * @param patientId
	 * @return
	 * 
	 * @exception ChaincodeException
	 */
	@Transaction
	public String viewMedicalRecord(final Context ctx, String patientId) {
		ChaincodeStub stub = ctx.getStub();
		String patientRecord = stub.getStringState(patientId);

		if (StringUtils.isEmpty(patientRecord)) {
			String errorMessage = String.format("Patient id  %s does not exist", patientId);
			throw new ChaincodeException(errorMessage, EmrErrors.PATIENT_RECORD_NOT_FOUND.toString());
		}

		return patientRecord;

	}

	/**
	 * Get patient test report
	 * 
	 * @param ctx
	 * @param patientId
	 * @return
	 * 
	 * @exception ChaincodeException
	 */
	@Transaction
	public String viewTestReports(final Context ctx, String patientId) {
		ChaincodeStub stub = ctx.getStub();
		String patientRecord = stub.getStringState(patientId);

		if (StringUtils.isEmpty(patientRecord)) {
			String errorMessage = String.format("Patient id  %s does not exist", patientId);
			throw new ChaincodeException(errorMessage, EmrErrors.PATIENT_RECORD_NOT_FOUND.toString());
		}

		MedicalRecord medicalRecord = genson.deserialize(patientRecord, MedicalRecord.class);
		
		String report  = genson.serialize(medicalRecord.getTestReports());
		
		return report;

	}
	
	/**
	 * Get medicine taken on specific date
	 * @param ctx
	 * @param patientId
	 * @param date
	 * @return
	 * 
	 * @exception ChaincodeException
	 */
	
	@Transaction
	public String viewMedicineTakenOnParticularDate(final Context ctx, String patientId, String date) {
		ChaincodeStub stub = ctx.getStub();
		String patientRecord = stub.getStringState(patientId);

		if (StringUtils.isEmpty(patientRecord)) {
			String errorMessage = String.format("Patient id  %s does not exist", patientId);
			throw new ChaincodeException(errorMessage, EmrErrors.PATIENT_RECORD_NOT_FOUND.toString());
		}

		MedicalRecord medicalRecord = genson.deserialize(patientRecord, MedicalRecord.class);
		List<Medicine> medicines = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(medicalRecord.getMedicines())) {
			medicines = medicalRecord.getMedicines().stream()
				.filter(medcine -> LocalDate.parse(medcine.getMedicineTakenDate()).equals(LocalDate.parse(date)))
				.collect(Collectors.toList());
			log.info(String.format("Total %s medicine taken on date %s", medicines.size(), date));
			
		}
		
		String medcines  = genson.serialize(medicines);
		
		return medcines;

	}
	
	/**
	 * Get medicines taken between dates
	 * @param ctx
	 * @param patientId
	 * @param startDate
	 * @param endDate
	 * @return
	 * 
	 * @exception ChaincodeException
	 */
	
	@Transaction
	public String viewMedicineTakenOnStartAndEndDate(final Context ctx, String patientId, String startDate, String endDate) {
		ChaincodeStub stub = ctx.getStub();
		String patientRecord = stub.getStringState(patientId);

		if (StringUtils.isEmpty(patientRecord)) {
			String errorMessage = String.format("Patient id  %s does not exist", patientId);
			throw new ChaincodeException(errorMessage, EmrErrors.PATIENT_RECORD_NOT_FOUND.toString());
		}

		MedicalRecord medicalRecord = genson.deserialize(patientRecord, MedicalRecord.class);
		List<Medicine> medicines = new ArrayList<>();
		if(CollectionUtils.isNotEmpty(medicalRecord.getMedicines())) {
			medicines = medicalRecord.getMedicines().stream()
				.filter(medicine -> 
					!LocalDate.parse(medicine.getMedicineTakenDate()).isBefore(LocalDate.parse(startDate)) && 
			        !LocalDate.parse(medicine.getMedicineTakenDate()).isAfter(LocalDate.parse(endDate))
				)
				.collect(Collectors.toList());
			log.info(String.format("Total %s medicine taken from date %s to date %s", medicines.size(), startDate, endDate));
			
		}
		
		String medcines  = genson.serialize(medicines);
		
		return medcines;

	}
	/**
	 * Get all medicines taken by patient
	 * @param ctx
	 * @param patientId
	 * @return
	 * 
	 * @exception ChaincodeException
	 */
	@Transaction
	public String getAllMedicine(final Context ctx, String patientId) {
		ChaincodeStub stub = ctx.getStub();
		String patientRecord = stub.getStringState(patientId);

		if (StringUtils.isEmpty(patientRecord)) {
			String errorMessage = String.format("Patient id  %s does not exist", patientId);
			throw new ChaincodeException(errorMessage, EmrErrors.PATIENT_RECORD_NOT_FOUND.toString());
		}

		MedicalRecord medicalRecord = genson.deserialize(patientRecord, MedicalRecord.class);
	
		String medcines  = genson.serialize(medicalRecord.getMedicines());
		
		return medcines;

	}

}
