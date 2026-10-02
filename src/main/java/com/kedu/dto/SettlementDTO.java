package com.kedu.dto;

public class SettlementDTO {

	// 정산 전체 정보
	private int settlementId;
	private int partyId;
	private String settlementType;
	private long totalAmount;
	
	//멤버별 정산 정보
	private int detailId;
	private String memberId;
	private String memberName;
	private long Amount;
	
	public SettlementDTO() {}

	public SettlementDTO(int settlementId, int partyId, String settlementType, long totalAmount, int detailId,
			String memberId, String memberName, long amount) {
		super();
		this.settlementId = settlementId;
		this.partyId = partyId;
		this.settlementType = settlementType;
		this.totalAmount = totalAmount;
		this.detailId = detailId;
		this.memberId = memberId;
		this.memberName = memberName;
		Amount = amount;
	}

	public int getSettlementId() {
		return settlementId;
	}

	public void setSettlementId(int settlementId) {
		this.settlementId = settlementId;
	}

	public int getPartyId() {
		return partyId;
	}

	public void setPartyId(int partyId) {
		this.partyId = partyId;
	}

	public String getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(String settlementType) {
		this.settlementType = settlementType;
	}

	public long getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(long totalAmount) {
		this.totalAmount = totalAmount;
	}

	public int getDetailId() {
		return detailId;
	}

	public void setDetailId(int detailId) {
		this.detailId = detailId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public String getMemberName() {
		return memberName;
	}

	public void setMemberName(String memberName) {
		this.memberName = memberName;
	}

	public long getAmount() {
		return Amount;
	}

	public void setAmount(long amount) {
		Amount = amount;
	}
	
}
	
	