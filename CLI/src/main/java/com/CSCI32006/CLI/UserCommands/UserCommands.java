package com.CSCI32006.CLI.UserCommands;
import com.CSCI32006.CLI.Helper;
import com.CSCI32006.CLI.SetupDatabase;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.AbstractShellComponent;
import java.sql.Date;

@Command(group = "User Commands", interactionMode = InteractionMode.INTERACTIVE)
public class UserCommands extends AbstractShellComponent {

    private User user;
    private boolean checkUserExists(String username) {
        try {
            SetupDatabase.getJdbcTemplate().queryForObject(
                    "SELECT username FROM users where username = ?;", String.class, username);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    private String getPassword(String username) {
        return SetupDatabase.getJdbcTemplate().queryForObject(
                "SELECT password FROM users where username = ?;", String.class, username
        );
    }

    private void setUser(String username, String password) {
        var encoder = new BCryptPasswordEncoder(16);
        if(encoder.matches(password, getPassword(username))) {
            user = SetupDatabase.getJdbcTemplate().queryForObject(
                    "SELECT * FROM users where username = ?;", new UserRowMapper(), username
            );
            SetupDatabase.getJdbcTemplate().update(
                    "UPDATE users SET lastaccessdate = ? where username = ? AND password = ?;", new Date(System.currentTimeMillis()), username, password);
            getTerminal().writer().println("Successfully logged in!");
        } else {
            getTerminal().writer().println("Wrong password. Login failed.");
        }
    }
    private void setupNewUser(String username) {
        var encoder = new BCryptPasswordEncoder(16);
        getTerminal().writer().println("Creating a new User!");
        var password = Helper.setupContext(true, "Password: ", "password", getTerminal(), getResourceLoader(), getTemplateExecutor()).run(StringInput.StringInputContext.empty()).getResultValue();
        var email = Helper.setupContext(false, "Email: ", "abc123@example.com", getTerminal(), getResourceLoader(), getTemplateExecutor()).run(StringInput.StringInputContext.empty()).getResultValue();
        var firstName = Helper.setupContext(false, "firstName: ", "firstname", getTerminal(), getResourceLoader(), getTemplateExecutor()).run(StringInput.StringInputContext.empty()).getResultValue();
        var lastName = Helper.setupContext(false, "lastName: ", "lastname", getTerminal(), getResourceLoader(), getTemplateExecutor()).run(StringInput.StringInputContext.empty()).getResultValue();
        var dateOfCreation = new Date(System.currentTimeMillis());
        SetupDatabase.getJdbcTemplate().update(
                "INSERT INTO users (userid, username, password, email, firstname, lastname, dateofcreation, lastaccessdate, followers)" +
                        "VALUES ((SELECT MAX(userid) FROM users) + 1, ?, ?, ?, ?, ?, ?, ?, ?)", username, encoder.encode(password), email, firstName, lastName, dateOfCreation, dateOfCreation, 0
        );
        getTerminal().writer().println("User setup successful!");
        setUser(username, password);
    }

    @Command(command = "logout user", description = "logout to app")
    private void logout() {
        user = null;
        getTerminal().writer().println("Successfully logged out!");
    }
    @Command(command = "login user", description = "login to app")
    private void login() {
        getTerminal().writer().println("Logging in to the application");
        var username = Helper.setupContext(false, "Username: ", "username", getTerminal(), getResourceLoader(), getTemplateExecutor()).run(StringInput.StringInputContext.empty()).getResultValue();
        if(checkUserExists(username)) {
            getTerminal().writer().println("User Exists!");
            var password = Helper.setupContext(true, "Password: ", "password", getTerminal(), getResourceLoader(), getTemplateExecutor()).run(StringInput.StringInputContext.empty()).getResultValue();
            setUser(username, password);
        } else {
            setupNewUser(username);
        }
    }
}
