package it.unical.webdevelop.backafterglow.dao;

import it.unical.webdevelop.backafterglow.dto.OtpCodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Repository
public class OtpCodeDao {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int insert(OtpCodeDTO otpCodeDTO) {
        String sql = "INSERT INTO otp_codes (username, otp_code, expiration_time, used, created_at) " +
                "VALUES (?, ?, ?, ?, ?)";

        return jdbcTemplate.update(sql,
                otpCodeDTO.getUsername(),
                otpCodeDTO.getOtpCode(),
                Timestamp.valueOf(otpCodeDTO.getExpirationTime()),
                otpCodeDTO.isUsed(),
                Timestamp.valueOf(otpCodeDTO.getCreatedAt())
        );
    }

    public OtpCodeDTO findValidOtp(String username, String otpCode) {
        String sql = "SELECT * FROM otp_codes " +
                "WHERE username = ? AND otp_code = ? AND used = false " +
                "AND expiration_time > ?";

        try {
            return jdbcTemplate.queryForObject(sql,
                    new Object[]{username, otpCode, Timestamp.valueOf(LocalDateTime.now())},
                    this::mapRowToDTO
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int updateUsedStatus(Long id, boolean used) {
        String sql = "UPDATE otp_codes SET used = ? WHERE id = ?";
        return jdbcTemplate.update(sql, used, id);
    }

    public int deleteExpired() {
        String sql = "DELETE FROM otp_codes WHERE expiration_time < ?";
        return jdbcTemplate.update(sql, Timestamp.valueOf(LocalDateTime.now()));
    }

    private OtpCodeDTO mapRowToDTO(ResultSet rs, int rowNum) throws SQLException {
        OtpCodeDTO dto = new OtpCodeDTO();
        dto.setId(rs.getLong("id"));
        dto.setUsername(rs.getString("username"));
        dto.setOtpCode(rs.getString("otp_code"));
        dto.setExpirationTime(rs.getTimestamp("expiration_time").toLocalDateTime());
        dto.setUsed(rs.getBoolean("used"));
        dto.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        return dto;
    }
}
