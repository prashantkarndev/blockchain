package com.courier.parcel;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.contract.annotation.Info;


import com.owlike.genson.Genson;

@Contract(
        name = "Courier",
        info = @Info(
                title = "Courier parcel contract",
                description = "courier parcel chaincode example",
                version = "0.0.1-SNAPSHOT"))

@Default
public class CourierChaincode implements ContractInterface {

    private final Genson genson = new Genson();
    
    private enum CourierParcelErrors {
    	PARCEL_NOT_FOUND, PARCEL_ALREADY_EXISTS
	}

    // Initialize ledger with initial parcel data
    @Transaction
    public void initLedger(final Context ctx) {
        
    }

    // Add a new parcel
    /** 
     * 
     * Add new parcel
     * throws exception if parcel already exist
     * @exception ChaincodeException
     * 
     * @param ctx
     * @param parcelId
     * @param sender
     * @param receiver
     * @param status
     */
    @Transaction
    public void addParcel(final Context ctx, String parcelId, String sender, String receiver, String status) {
        ChaincodeStub stub = ctx.getStub();
        String parcelState = stub.getStringState(parcelId);

		if (!parcelState.isEmpty()) {
			String errorMessage = String.format("Parcel %s already exists", parcelId);
			System.out.println(errorMessage);
			throw new ChaincodeException(errorMessage, CourierParcelErrors.PARCEL_ALREADY_EXISTS.toString());
		}
        Parcel parcel = new Parcel(parcelId, sender, receiver, status);
        stub.putStringState(parcelId, genson.serialize(parcel));
    }

    // Update the parcel status
    /**
     * Update the parcel status
     * @exception ChaincodeException
     * 
     * @param ctx
     * @param parcelId
     * @param newStatus
     */
    @Transaction
    public void updateParcelStatus(final Context ctx, String parcelId, String newStatus) {
        ChaincodeStub stub = ctx.getStub();

        String parcelData = stub.getStringState(parcelId);
        if (parcelData == null || parcelData.isEmpty()) {
        	String errorMessage = String.format("Parcel %s does not exist", parcelId);
        	throw new ChaincodeException(errorMessage, CourierParcelErrors.PARCEL_NOT_FOUND.toString());
        }

        Parcel parcel = genson.deserialize(parcelData, Parcel.class);
        parcel.setStatus(newStatus);

        stub.putStringState(parcelId, genson.serialize(parcel));
    }

    // Retrieve a parcel
    /**
     * Query a parcel
     * @exception ChaincodeException
     * 
     * @param ctx
     * @param parcelId
     * @return
     */
    @Transaction
    public String queryParcel(final Context ctx, String parcelId) {
        ChaincodeStub stub = ctx.getStub();
        String parcelData = stub.getStringState(parcelId);
        
        if (parcelData == null || parcelData.isEmpty()) {
        	String errorMessage = String.format("Parcel %s does not exist", parcelId);
        	throw new ChaincodeException(errorMessage, CourierParcelErrors.PARCEL_NOT_FOUND.toString());
        }

        return parcelData;
    }
    
    
}
