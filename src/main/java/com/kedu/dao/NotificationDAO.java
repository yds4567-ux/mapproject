package com.kedu.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kedu.dto.NotificationDTO;

@Repository
public class NotificationDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public int insert(String receiverId, String type, String targetType, int targetId, String message) {

		String sql = "insert into NOTIFICATION "
				+ "(NOTIFICATION_ID, RECEIVER_ID, TYPE, TARGET_TYPE, TARGET_ID, MESSAGE) "
				+ "values (NOTIFICATION_SEQ.nextval, ?, ?, ?, ?, ?)";

		return jdbcTemplate.update(sql, receiverId, type, targetType, targetId, message);
	}

	public List<NotificationDTO> findByReceiverId(String receiverId){
		
		String sql = "select * from NOTIFICATION "
				+ "where RECEIVER_ID = ? "
				+ "order by NOTIFICATION_ID desc";
		
		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			   NotificationDTO dto = new NotificationDTO();
			
			dto.setNotificationId(rs.getInt("NOTIFICATION_ID"));
			dto.setReceiverId(rs.getString("RECEIVER_ID"));
			dto.setType(rs.getString("TYPE"));
			dto.setTargetType(rs.getString("TARGET_TYPE"));
			
			int targetId = rs.getInt("TARGET_ID");
			dto.setTargetId(rs.wasNull() ?  null : targetId);
			
			dto.setMessage(rs.getString("MESSAGE"));
			dto.setReadYn(rs.getString("READ_YN"));
			dto.setRegdate(rs.getTimestamp("REGDATE"));
			
			return dto;		
		}, receiverId);
	}

	public int markAsRead(int notificationId, String receiverId) {
		String sql = "update NOTIFICATION "
				+ "set READ_YN = 'Y' "
				+ "where NOTIFICATION_ID = ? and RECEIVER_ID = ?";
		
		return jdbcTemplate.update(sql, notificationId, receiverId);
	}
	
}
