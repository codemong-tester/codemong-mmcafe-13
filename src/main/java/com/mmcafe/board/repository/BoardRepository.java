package com.mmcafe.board.repository;
import com.mmcafe.board.dto.BoardResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public class BoardRepository {
    private final JdbcTemplate jdbc;
    private final RowMapper<BoardResponse> boardMapper = (rs, rowNum) -> new BoardResponse(rs.getLong("id"), rs.getString("title"), rs.getString("content"), rs.getTimestamp("created_at").toLocalDateTime());
    public BoardRepository(JdbcTemplate jdbc) { this.jdbc = jdbc; }
    public BoardResponse save(String title, String content) {
        LocalDateTime now = LocalDateTime.now();
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            PreparedStatement ps = connection.prepareStatement("insert into boards(title, content, created_at) values (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setTimestamp(3, Timestamp.valueOf(now));
            return ps;
        }, keyHolder);
        return new BoardResponse(keyHolder.getKey().longValue(), title, content, now);
    }
    public Optional<BoardResponse> findById(long id) {
        return jdbc.query("select id, title, content, created_at from boards where id = ?", boardMapper, id).stream().findFirst();
    }

    public List<BoardResponse> findAll(int page, int size) {
        return jdbc.query("select id, title, content, created_at from boards order by created_at desc, id desc limit ? offset ?", boardMapper, size, page * size);
    }
    public long count() {
        Long count = jdbc.queryForObject("select count(*) from boards", Long.class);
        return count == null ? 0 : count;
    }



}
