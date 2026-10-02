package com.kedu.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.kedu.dto.PartyApplicationDTO;
import com.kedu.dto.PartyDTO;

@Repository
public class PartyDAO {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private NotificationDAO notificationDAO;

	public List<PartyDTO> findAll(int offset) {
		String sql = "SELECT * FROM ( " + "  SELECT t.*, ROWNUM rn FROM ( "
				+ "    SELECT p.PARTY_ID, p.STORE_ID, p.TITLE, p.MEET_DATE, s.STORE_NAME, "
				+ "           REGEXP_SUBSTR(s.ADDRESS, '^[^ ]+시 [^ ]+구') AS address " + "    FROM PARTY p "
				+ "    LEFT JOIN GOOD_STORE s ON p.STORE_ID = s.STORE_ID " + "    ORDER BY p.PARTY_ID DESC "
				+ "  ) t WHERE ROWNUM <= ? " + ") WHERE rn > ?";

		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			PartyDTO party = new PartyDTO();
			party.setPartyId(rs.getInt("PARTY_ID"));
			party.setStoreId(rs.getInt("STORE_ID"));
			party.setTitle(rs.getString("TITLE"));
			party.setMeetDate(rs.getTimestamp("MEET_DATE"));
			party.setStoreName(rs.getString("STORE_NAME"));
			party.setAddress(rs.getString("ADDRESS"));
			return party;
		}, offset + 9, offset);
	}

	public PartyDTO findById(int partyId) {
		String sql = "select p.*, s.STORE_NAME, s.ADDRESS " + "from PARTY p "
				+ "left join GOOD_STORE s on p.STORE_ID = s.STORE_ID " + "where p.PARTY_ID = ?";

		List<PartyDTO> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
			PartyDTO party = new PartyDTO();
			party.setPartyId(rs.getInt("PARTY_ID"));
			party.setHostId(rs.getString("HOST_ID"));
			party.setStoreId(rs.getInt("STORE_ID"));
			party.setContents(rs.getString("CONTENTS"));
			party.setTitle(rs.getString("TITLE"));
			party.setMeetDate(rs.getTimestamp("MEET_DATE"));
			party.setStoreName(rs.getString("STORE_NAME"));
			party.setAddress(rs.getString("ADDRESS"));
			party.setJoinType(rs.getString("JOIN_TYPE"));
			party.setMinPeople(rs.getInt("MIN_PEOPLE"));
			party.setMaxPeople(rs.getInt("MAX_PEOPLE"));
			party.setGenderRule(rs.getString("GENDER_RULE"));
			int minAge = rs.getInt("MIN_AGE");
			party.setMinAge(rs.wasNull() ? null : minAge);

			int maxAge = rs.getInt("MAX_AGE");
			party.setMaxAge(rs.wasNull() ? null : maxAge);

			party.setQuestion(rs.getString("QUESTION"));
			party.setRegdate(rs.getTimestamp("REGDATE"));

			return party;
		}, partyId);

		return results.isEmpty() ? null : results.get(0);
	}

	public int countMembers(int partyId) {
		String sql = "select count(*) from PARTY_MEMBER where PARTY_ID = ?";
		return jdbcTemplate.queryForObject(sql, Integer.class, partyId);
	}

	public List<String> findMemberIds(int partyId) {
		String sql = "select MEMBER_ID FROM PARTY_MEMBER " + "where PARTY_ID = ? order by JOIN_DATE";
		return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("MEMBER_ID"), partyId);
	}

	public int getNextPartyId() {
		String sql = "select PARTY_SEQ.nextval from dual";
		return jdbcTemplate.queryForObject(sql, Integer.class);
	}

	public int insert(PartyDTO partyDTO) {
		String sql = "insert into PARTY "
				+ "(PARTY_ID, HOST_ID, STORE_ID, TITLE, CONTENTS, MEET_DATE, JOIN_TYPE, MIN_PEOPLE, MAX_PEOPLE, GENDER_RULE, MIN_AGE, MAX_AGE, QUESTION) "
				+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

		return jdbcTemplate.update(sql, partyDTO.getPartyId(), partyDTO.getHostId(), partyDTO.getStoreId(),
				partyDTO.getTitle(), partyDTO.getContents(), partyDTO.getMeetDate(), partyDTO.getJoinType(),
				partyDTO.getMinPeople(), partyDTO.getMaxPeople(), partyDTO.getGenderRule(), partyDTO.getMinAge(),
				partyDTO.getMaxAge(), partyDTO.getQuestion());
	}

	public int insertMember(int partyId, String memberId) {
		String sql = "insert into PARTY_MEMBER " + "(PARTY_MEMBER_ID, PARTY_ID, MEMBER_ID) "
				+ "values (PARTY_MEMBER_SEQ.nextval, ?, ?)";

		return jdbcTemplate.update(sql, partyId, memberId);
	}

	@Transactional
	public int createParty(PartyDTO partyDTO) {
		int partyId = getNextPartyId();
		partyDTO.setPartyId(partyId);

		insert(partyDTO);
		insertMember(partyId, partyDTO.getHostId());

		return partyId;
	}

	public boolean isMember(int partyId, String memberId) {
		String sql = "select count(*) from PARTY_MEMBER " + "where PARTY_ID = ? and MEMBER_ID = ?";

		int count = jdbcTemplate.queryForObject(sql, Integer.class, partyId, memberId);

		return count > 0;
	}

	public boolean hasPendingApplication(int partyId, String applicantId) {
		String sql = "select count(*) from PARTY_APPLICATION " + "where PARTY_ID = ? and APPLICANT_ID = ? "
				+ "and STATUS = 'PENDING'";

		int count = jdbcTemplate.queryForObject(sql, Integer.class, partyId, applicantId);

		return count > 0;
	}

	public int insertApplication(int partyId, String applicantId, String answer, String status) {

		String sql = "insert into PARTY_APPLICATION " + "(APPLICATION_ID, PARTY_ID, APPLICANT_ID, ANSWER, STATUS) "
				+ "values (PARTY_APPLICATION_SEQ.nextval, ?, ?, ?, ?)";

		return jdbcTemplate.update(sql, partyId, applicantId, answer, status);

	}

	@Transactional
	public void apply(int partyId, String applicantId, String answer) {
		String sql = "select PARTY_ID from PARTY where PARTY_ID = ? for update";
		List<Integer> ids = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("PARTY_ID"), partyId);

		if (ids.isEmpty()) {
			throw new IllegalArgumentException("존재하지 않는 모임입니다.");
		}

		PartyDTO dto = findById(partyId);

		if (!dto.getMeetDate().toLocalDateTime().isAfter(LocalDateTime.now())) {
			throw new IllegalArgumentException("이미 시작했거나 지난 모임에는 신청할 수 없습니다.");
		}

		if (isMember(partyId, applicantId) || hasPendingApplication(partyId, applicantId)) {
			throw new IllegalArgumentException("이미 참여했거나 신청 대기 중인 모임입니다.");
		}

		if (countMembers(partyId) >= dto.getMaxPeople()) {
			throw new IllegalArgumentException("모집 인원이 마감되었습니다.");
		}

		if ("FCFS".equals(dto.getJoinType())) {
			insertApplication(partyId, applicantId, answer, "APPROVED");
			insertMember(partyId, applicantId);
			
			notificationDAO.insert(
				dto.getHostId(), 
				"PARTY_JOIN",
				"PARTY", 
				partyId, 
				dto.getTitle() + " 모임에 새로운 멤버가 참여했습니다.");
			
		} else if ("APPROVAL".equals(dto.getJoinType())) {
			insertApplication(partyId, applicantId, answer, "PENDING");

			notificationDAO.insert(dto.getHostId(), "PARTY_APPLICATION", "PARTY", partyId,
					dto.getTitle() + " 모임에 새로운 참여 신청이 있습니다.");
		} else {
			throw new IllegalArgumentException("참여 방식이 올바르지 않습니다.");
		}

	}

	public List<PartyApplicationDTO> findPendingApplications(int partyId) {
		String sql = "select * from PARTY_APPLICATION " + "where PARTY_ID = ? and STATUS = 'PENDING' "
				+ "order by APPLY_DATE, APPLICATION_ID";

		return jdbcTemplate.query(sql, (rs, rowNum) -> {
			PartyApplicationDTO padto = new PartyApplicationDTO();

			padto.setApplicationId(rs.getInt("APPLICATION_ID"));
			padto.setPartyId(rs.getInt("PARTY_ID"));
			padto.setApplicantId(rs.getString("APPLICANT_ID"));
			padto.setAnswer(rs.getString("ANSWER"));
			padto.setStatus(rs.getString("STATUS"));
			padto.setApplyDate(rs.getTimestamp("APPLY_DATE"));

			return padto;
		}, partyId);
	}

	public PartyApplicationDTO findApplicationById(int applicationId) {
		String sql = "select * from PARTY_APPLICATION where APPLICATION_ID = ?";

		List<PartyApplicationDTO> results = jdbcTemplate.query(sql, (rs, rowNum) -> {
			PartyApplicationDTO padto = new PartyApplicationDTO();

			padto.setApplicationId(rs.getInt("APPLICATION_ID"));
			padto.setPartyId(rs.getInt("PARTY_ID"));
			padto.setApplicantId(rs.getString("APPLICANT_ID"));
			padto.setAnswer(rs.getString("ANSWER"));
			padto.setStatus(rs.getString("STATUS"));
			padto.setApplyDate(rs.getTimestamp("APPLY_DATE"));

			return padto;
		}, applicationId);
		return results.isEmpty() ? null : results.get(0);
	}

	public int updateApplicationStatus(int applicationId, String status) {
		String sql = "update PARTY_APPLICATION set STATUS = ? " + "where APPLICATION_ID = ? and STATUS = 'PENDING'";

		return jdbcTemplate.update(sql, status, applicationId);
	}

	private void lockParty(int partyId) {
		String sql = "select PARTY_ID from PARTY where PARTY_ID = ? for update";

		List<Integer> ids = jdbcTemplate.query(sql, (rs, rowNum) -> rs.getInt("PARTY_ID"), partyId);

		if (ids.isEmpty()) {
			throw new IllegalArgumentException("존재하지 않는 모임입니다.");
		}
	}

	@Transactional
	public void approve(int partyId, int applicationId, String hostId) {
		lockParty(partyId);

		PartyDTO dto = findById(partyId);
		PartyApplicationDTO padto = findApplicationById(applicationId);

		if (!hostId.equals(dto.getHostId())) {
			throw new IllegalArgumentException("모임장만 승인할 수 있습니다.");
		}
		if (padto == null || padto.getPartyId() != partyId) {
			throw new IllegalArgumentException("해당 모임의 신청이 아닙니다.");
		}

		if (!"PENDING".equals(padto.getStatus())) {
			throw new IllegalArgumentException("이미 처리한 신청입니다.");
		}
		if (countMembers(partyId) >= dto.getMaxPeople()) {
			throw new IllegalArgumentException("모집 인원이 마감되었습니다.");
		}

		if (isMember(partyId, padto.getApplicantId())) {
			throw new IllegalArgumentException("이미 참여 중인 회원입니다.");
		}

		int count = updateApplicationStatus(applicationId, "APPROVED");

		if (count != 1) {
			throw new IllegalArgumentException("이미 처리한 신청입니다.");
		}
		insertMember(partyId, padto.getApplicantId());
		
		notificationDAO.insert(
		padto.getApplicantId(),
		"PARTY_APPROVED",
		"PARTY",
		partyId,
		dto.getTitle() + " 모임 참여 신청이 승인되었습니다.");
	}

	@Transactional
	public void reject(int partyId, int applicationId, String hostId) {
		lockParty(partyId);

		PartyDTO dto = findById(partyId);
		PartyApplicationDTO padto = findApplicationById(applicationId);

		if (!hostId.equals(dto.getHostId())) {
			throw new IllegalArgumentException("해당 모임의 신청이 아닙니다.");
		}

		if (padto == null || padto.getPartyId() != partyId) {
			throw new IllegalArgumentException("해다 모임의 신청이 아닙니다.");
		}

		int count = updateApplicationStatus(applicationId, "REJECTED");

		if (count != 1) {
			throw new IllegalArgumentException("이미 처리한 신청입니다.");
		}
		
		notificationDAO.insert(
				padto.getApplicantId(), 
				"PARTY_REJECTED",
				"PARTY", 
				partyId, 
				dto.getTitle() + " 모임 참여 신청이 거절되었습니다.");
	}

	public List<String> findMemberNames(int partyId) {
		String sql = "select m.USERNAME " + "from PARTY_MEMBER pm " + "join MEMBER m on pm.MEMBER_ID = m.MEMBER_ID "
				+ "where pm.PARTY_ID = ? " + "order by pm.JOIN_DATE, pm.PARTY_MEMBER_ID";

		return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getString("USERNAME"), partyId);
	}
}
