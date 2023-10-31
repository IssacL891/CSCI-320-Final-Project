package com.CSCI32006.CLI.Games;

import com.CSCI32006.CLI.Collections.Collection;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GameRowMapper implements RowMapper<Game> {
    @Override
    public Game mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Game(
                rs.getInt("gameid"),
                rs.getString("title"),
                rs.getString("esrb_rating"),
                rs.getString("developername"),
                rs.getString("publishername")
        );
    }
}
