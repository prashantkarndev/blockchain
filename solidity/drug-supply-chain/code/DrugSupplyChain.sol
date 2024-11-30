// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract DrugSupplyChain {

    enum Role { Manufacturer, Supplier, Distributor, Pharmacy, Consumer }

    struct Drug {
        uint id;
        string name;
        string batchNumber;
        address currentOwner;
        string status;
        string details;
        Transaction[] history;

    }

     struct Transaction {
        address from;
        address to;
        string status;
        uint256 timestamp;
    }


    struct Participant {
        address account;
        Role role;
        string name;
    }

    uint8 public drugCounter = 0;
    mapping(uint => Drug) public drugs;
    mapping(address => Participant) public participants;
    mapping(uint => address[]) public drugHistory; // Tracking ownership

    modifier onlyParticipant() {
        require(participants[msg.sender].account == msg.sender, "Not a registered participant");
        _;
    }

    event DrugRegistered(uint drugId, string name, address owner, string details);
    event StatusUpdated(uint drugId, string status, address updatedBy);
   event OwnershipTransferred(
        uint256 id,
        address from,
        address to,
        string status
    );

    constructor() {
        // Register the contract deployer as a manufacturer
        registerStakeHolder(msg.sender, Role.Manufacturer);
    }

    function registerStakeHolder(address _account, Role _role) public {
        string memory name; // Declare the name variable

    if (_role == Role.Manufacturer) {
        name = "Manufacturer";
    } else if (_role == Role.Supplier) {
        name = "Supplier";
    } else if (_role == Role.Distributor) {
        name = "Distributor";
    } else if (_role == Role.Pharmacy) {
        name = "Pharmacy";
    } else if (_role == Role.Consumer) {
        name = "Consumer";
    } else {
        revert("Invalid role provided");
    }

    // Correctly initialize and assign the Participant struct
    participants[_account] = Participant({
        account: _account,
        role: _role,
        name: name 
    });
    }

    function registerDrug(string memory _name, string memory _batchNumber,string memory _details) public onlyParticipant {
        require(participants[msg.sender].role == Role.Manufacturer, "Only manufacturers can register drugs");
        drugCounter++;
         // Create a new Drug struct
        Drug storage newDrug = drugs[drugCounter];
        newDrug.id = drugCounter;
        newDrug.name = _name;
        newDrug.batchNumber = _batchNumber;
        newDrug.currentOwner = msg.sender;
        newDrug.status = "Manufactured";
        newDrug.details = _details;

    // Add an initial transaction to the history
    newDrug.history.push(Transaction({
        from: address(0),
        to: msg.sender,
        status: "Manufactured",
        timestamp: block.timestamp
    }));
        drugHistory[drugCounter].push(msg.sender);
        emit DrugRegistered(drugCounter, _name, msg.sender, _details);
    }

    function updateStatus(uint _drugId, string memory _status) public onlyParticipant {
        require(drugs[_drugId].currentOwner == msg.sender, "Only current owner can update status");
        drugs[_drugId].status = _status;
        emit StatusUpdated(_drugId, _status, msg.sender);
    }

    function transferOwnership(uint _drugId, address _newOwner, string memory _newStatus) public onlyParticipant {
          Drug storage drug = drugs[_drugId];

        require(drugs[_drugId].currentOwner == msg.sender, "Only current owner can transfer ownership");
        // Record the transfer in history
        
        drug.history.push(
            Transaction({
                from: msg.sender,
                to: _newOwner,
                status: drug.status,
                timestamp: block.timestamp
            })
        );

        drug.currentOwner = _newOwner;

        
        drugHistory[_drugId].push(_newOwner);
        emit OwnershipTransferred(_drugId, msg.sender, _newOwner, _newStatus);
    }

    function getDrugHistory(uint _drugId) public view returns (address[] memory) {
        return drugHistory[_drugId];
    }
}
