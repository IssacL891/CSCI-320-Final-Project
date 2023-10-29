package com.CSCI32006.CLI.Collections;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CollectionRowMapper implements RowMapper<Collection> {
    @Override
    public Collection mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new Collection(
                rs.getInt("userid"),
                rs.getInt("collectionid"),
                rs.getString("collectionname")
        );
    }
}
