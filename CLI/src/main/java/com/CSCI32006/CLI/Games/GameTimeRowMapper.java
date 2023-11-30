package com.CSCI32006.CLI.Games;

import com.CSCI32006.CLI.Collections.Collection;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GameTimeRowMapper implements RowMapper<GameTime> {
    @Override
    public GameTime mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new GameTime(
                rs.getString("title"),
                rs.getInt("time_played")
        );
    }
}
