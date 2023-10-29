package com.CSCI32006.CLI.UserCommands.Users;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;

//noinspection JSUnusedGlobalSymbols
@AllArgsConstructor
@Setter
@Getter
@ToString
public class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String firstName;
    private String lastName;
    private Date dateOfCreation;
    private Date lastAccessDate;
    private int followers;
}
