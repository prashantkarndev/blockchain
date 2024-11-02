package com.emr.app;

import java.util.ArrayList;
import java.util.List;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType
public class MedicalRecord {
	@Property
	private String patientId;

	@Property
	private String name;

	@Property
	private String diagnosis;

	@Property
	private String treatment;

	@Property
	private String dateOfVisit;
	
	@Property
	private List<TestReport> testReports = new ArrayList<>();
	
	@Property
	private List<Medicine> medicines = new ArrayList<>();
	
	public MedicalRecord() {}

	public MedicalRecord(@JsonProperty("patientId") String patientId, @JsonProperty("name") String name,
			String diagnosis, String treatment, String dateOfVisit) {
		super();
		this.patientId = patientId;
		this.name = name;
		this.diagnosis = diagnosis;
		this.treatment = treatment;
		this.dateOfVisit = dateOfVisit;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	public void setTreatment(String treatment) {
		this.treatment = treatment;
	}

	public void setDateOfVisit(String dateOfVisit) {
		this.dateOfVisit = dateOfVisit;
	}

	public String getPatientId() {
		return patientId;
	}

	public String getName() {
		return name;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public String getTreatment() {
		return treatment;
	}

	public String getDateOfVisit() {
		return dateOfVisit;
	}
	
	

	public List<TestReport> getTestReports() {
		return testReports;
	}

	public void setTestReports(List<TestReport> testReports) {
		this.testReports = testReports;
	}
	
	

	public List<Medicine> getMedicines() {
		return medicines;
	}

	public void setMedicines(List<Medicine> medicines) {
		this.medicines = medicines;
	}

	@Override
	public String toString() {
		return String.format(
				"MedicalRecord [patientId=%s, name=%s, diagnosis=%s, treatment=%s, dateOfVisit=%s, testReports=%s, medicines=%s]",
				patientId, name, diagnosis, treatment, dateOfVisit, testReports, medicines);
	}

	

	

}
