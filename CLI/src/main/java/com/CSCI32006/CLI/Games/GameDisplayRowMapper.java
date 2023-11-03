package com.CSCI32006.CLI.Games;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class GameDisplayRowMapper implements RowMapper<GameDisplay> {
    @Override
    public GameDisplay mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new GameDisplay(
                rs.getInt("gameid"),
                rs.getString("title"),
                rs.getString("esrb_rating"),
                rs.getString("developername"),
                rs.getString("publishername"),
                rs.getString("platformname"),
                rs.getDouble("avgHours"),
                rs.getDouble("avgMinutes"),
                rs.getDouble("avgRatings")
        );
    }
}