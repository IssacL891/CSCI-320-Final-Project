package com.CSCI32006.CLI.Platforms;

import com.CSCI32006.CLI.Games.Game;
import com.CSCI32006.CLI.Platforms.Platform;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class PlatformRowMapper implements RowMapper<Platform> {
    @Override
    public Platform mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Platform(
                rs.getInt("platformid"),
                rs.getString("name"),
                rs.getDate("releasedate"),
                rs.getDouble("price")
        );
    }
}