// SPDX-License-Identifier: MIT
pragma solidity ^0.8.0;

contract InsuranceContract {
    
    address public insurer;
    
    struct Policy {
        address policyHolder;
        uint premium;
        uint coverageAmount;
        uint policyDuration;
        uint startTime;
        bool isActive;
        bool isClaimed;
    }

     struct Claim {
        uint256 amount;
        bool approved;
    }

    
    mapping(address => Policy) public policies;
    mapping(address => Claim) public claims;

    constructor() {
        insurer = msg.sender;
    }

    event PolicyIssued(address  policyHolder, uint coverageAmount, uint policyDuration);
    event PremiumPaid(address  policyHolder, uint amount);
    event ClaimSubmitted(address  policyHolder, uint amount, string reason);
    event ClaimApproved(address  policyHolder, uint amount);
    event ClaimPaid(address  policyHolder, uint amount);

    modifier onlyInsurer() {
        require(msg.sender == insurer, "Only insurer can call this function");
        _;
    }

    modifier onlyActivePolicy(address _policyHolder) {
        require(policies[_policyHolder].isActive, "Policy is not active");
        _;
    }

    

    // Function to issue new policy
    function issuePolicy(address _policyHolder, uint _premium, uint _coverageAmount, uint _policyDuration) external onlyInsurer {
        require(policies[_policyHolder].policyHolder == address(0), "Policy already exists for this address");

        policies[_policyHolder] = Policy({
            policyHolder: _policyHolder,
            premium: _premium,
            coverageAmount: _coverageAmount,
            policyDuration: _policyDuration,
            startTime: block.timestamp,
            isActive: true,
            isClaimed: false
        });

        emit PolicyIssued(_policyHolder, _coverageAmount, _policyDuration);
    }

    // Function to pay premium
    function payPremium() external payable onlyActivePolicy(msg.sender) {
        Policy storage policy = policies[msg.sender];
        
        require(policy.isActive, "Policy inactive");
        require(msg.value == policy.premium, "Incorrect premium amount");
        
        require(block.timestamp < policy.startTime + policy.policyDuration, "Policy expired");

        emit PremiumPaid(msg.sender, msg.value);
    }

   

    // Function to submit a claim
    function submitClaim(uint _claimAmount, string memory _reason) external onlyActivePolicy(msg.sender) {
        Policy storage policy = policies[msg.sender];
        require(!policy.isClaimed, "Claim already submitted for this policy");
        require(_claimAmount <= policy.coverageAmount, "Claim exceeds coverage");

        claims[msg.sender] = Claim(_claimAmount, false);
        emit ClaimSubmitted(msg.sender, _claimAmount, _reason);
    }

    // Function to approve a claim
    function approveClaim(address _policyHolder) external onlyInsurer onlyActivePolicy(_policyHolder) {
        Policy storage policy = policies[_policyHolder];
        Claim storage claim = claims[_policyHolder];
        require(claim.amount > 0, "No claim to approve");
       
        policy.isClaimed = true;
        claim.approved = true;

        emit ClaimApproved(_policyHolder, claim.amount);
    }

    // Function to pay out a claim
    function payClaim(address payable _policyHolder) external onlyInsurer {
        require(claims[_policyHolder].approved, "Claim not approved");
        _policyHolder.transfer(claims[_policyHolder].amount);
        emit ClaimPaid(_policyHolder, claims[_policyHolder].amount);
    }

    
}
