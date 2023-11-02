package com.CSCI32006.CLI.Users;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRowMapper implements RowMapper<User> {
    @Override
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new User(
                rs.getInt("userid"),
                rs.getString("username"),
                rs.getString("password"),
                rs.getString("email"),
                rs.getString("firstname"),
                rs.getString("lastname"),
                rs.getDate("dateofcreation"),
                rs.getDate("lastaccessdate")
                );
    }
}
