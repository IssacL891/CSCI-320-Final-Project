package com.CSCI32006.CLI.Collections;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CollectionDisplayRowMapper implements RowMapper<CollectionDisplay> {
    @Override
    public CollectionDisplay mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new CollectionDisplay(
                rs.getInt("userid"),
                rs.getInt("collectionid"),
                rs.getString("collectionname"),
                rs.getInt("countOfGames"),
                rs.getInt("hours"),
                rs.getInt("minutes")
        );
    }
}
