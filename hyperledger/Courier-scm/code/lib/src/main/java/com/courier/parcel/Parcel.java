package com.courier.parcel;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType
public class Parcel {

	@Property
	private final String parcelId;

	@Property
	private final String sender;

	@Property
	private final String receiver;

	@Property
	private String status;

	public Parcel(@JsonProperty("parcelId") String parcelId, @JsonProperty("sender") String sender,
			@JsonProperty("receiver") String receiver, @JsonProperty("status") String status) {
		this.parcelId = parcelId;
		this.sender = sender;
		this.receiver = receiver;
		this.status = status;
	}

	public String getParcelId() {
		return parcelId;
	}

	public String getSender() {
		return sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@Override
    public String toString() {
        return String.format("{ \"parcelId\": \"%s\", \"sender\": \"%s\", \"receiver\": \"%s\", \"status\": \"%s\" }", 
            parcelId, sender, receiver, status);
    }
}
