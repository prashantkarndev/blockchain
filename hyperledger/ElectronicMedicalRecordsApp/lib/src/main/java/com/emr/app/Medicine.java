package com.emr.app;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType
public class Medicine {
	
	@Property
	private String patientId;
	
	@Property
	private String medicineName;
	
	@Property
	private String medicineExpiry;
	
	@Property
	private String medicineTakenDate;
	
	public Medicine() {}

	public Medicine(@JsonProperty("patientId")String patientId, @JsonProperty("medicineName") String medicineName, String medicineExpiry, String medicineTakenDate) {
		super();
		this.patientId = patientId;
		this.medicineName = medicineName;
		this.medicineExpiry = medicineExpiry;
		this.medicineTakenDate = medicineTakenDate;
	}

	public String getPatientId() {
		return patientId;
	}

	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}

	public String getMedicineName() {
		return medicineName;
	}

	public void setMedicineName(String medicineName) {
		this.medicineName = medicineName;
	}

	public String getMedicineExpiry() {
		return medicineExpiry;
	}

	public void setMedicineExpiry(String medicineExpiry) {
		this.medicineExpiry = medicineExpiry;
	}

	public String getMedicineTakenDate() {
		return medicineTakenDate;
	}

	public void setMedicineTakenDate(String medicineTakenDate) {
		this.medicineTakenDate = medicineTakenDate;
	}
	
	

}
