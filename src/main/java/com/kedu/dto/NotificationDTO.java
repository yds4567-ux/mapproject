package com.kedu.dto;

import java.sql.Timestamp;

public class NotificationDTO {

	private int notificationId;
	private String receiverId;
	private String type;
	private String targetType;
	private int targetId;
	private String message;
	private String readYn;
	private Timestamp regdate;
	
	public NotificationDTO() {}
	
	public NotificationDTO(int notificationId, String receiverId, String type, String targetType, int targetId,
			String message, String readYn, Timestamp regdate) {
		super();
		this.notificationId = notificationId;
		this.receiverId = receiverId;
		this.type = type;
		this.targetType = targetType;
		this.targetId = targetId;
		this.message = message;
		this.readYn = readYn;
		this.regdate = regdate;
	}

	public int getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(int notificationId) {
		this.notificationId = notificationId;
	}

	public String getReceiverId() {
		return receiverId;
	}

	public void setReceiverId(String receiverId) {
		this.receiverId = receiverId;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTargetType() {
		return targetType;
	}

	public void setTargetType(String targetType) {
		this.targetType = targetType;
	}

	public int getTargetId() {
		return targetId;
	}

	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getReadYn() {
		return readYn;
	}

	public void setReadYn(String readYn) {
		this.readYn = readYn;
	}

	public Timestamp getRegdate() {
		return regdate;
	}

	public void setRegdate(Timestamp regdate) {
		this.regdate = regdate;
	}
	
	
	
	 
}
