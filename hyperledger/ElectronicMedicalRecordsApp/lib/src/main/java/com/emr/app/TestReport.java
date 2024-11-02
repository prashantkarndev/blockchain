package com.emr.app;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType
public class TestReport {
	
	@Property
	private String reportId;
	
	@Property
	private String patientId;
	
	@Property
	private String reportName;
	
	@Property
	private String description;
	
	@Property
	private String reportDate;
	
	@Property
	private String resportIssuer;
	
	
	public TestReport() {
		
	}


	public TestReport(@JsonProperty("reportId") String reportId, @JsonProperty("patientId") String patientId, 
			@JsonProperty("reportName") String reportName, 
			String description, String reportDate,String resportIssuer) {
		super();
		this.reportId = reportId;
		this.patientId = patientId;
		this.reportName = reportName;
		this.description = description;
		this.reportDate = reportDate;
		this.resportIssuer = resportIssuer;
	}


	public String getReportId() {
		return reportId;
	}


	public void setReportId(String reportId) {
		this.reportId = reportId;
	}


	public String getPatientId() {
		return patientId;
	}


	public void setPatientId(String patientId) {
		this.patientId = patientId;
	}


	public String getReportName() {
		return reportName;
	}


	public void setReportName(String reportName) {
		this.reportName = reportName;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public String getReportDate() {
		return reportDate;
	}


	public void setReportDate(String reportDate) {
		this.reportDate = reportDate;
	}


	public String getResportIssuer() {
		return resportIssuer;
	}


	public void setResportIssuer(String resportIssuer) {
		this.resportIssuer = resportIssuer;
	}
	

	
}
