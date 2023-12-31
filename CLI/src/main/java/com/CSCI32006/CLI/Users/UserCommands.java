package com.CSCI32006.CLI.Users;

import com.CSCI32006.CLI.Games.Game;
import com.CSCI32006.CLI.Games.GameRowMapper;
import com.CSCI32006.CLI.Helper;
import com.CSCI32006.CLI.SetupDatabase;
import lombok.Getter;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.command.annotation.Option;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.context.InteractionMode;
import org.springframework.shell.standard.AbstractShellComponent;

import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Command(group = "User Commands", interactionMode = InteractionMode.INTERACTIVE)
public class UserCommands extends AbstractShellComponent {

    @Getter
    private static User user;
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
                    "UPDATE users SET lastaccessdate = current_date where username = ?;", username);
            getTerminal().writer().println("Successfully logged in!");
        } else {
            getTerminal().writer().println("Wrong password. Login failed.");
        }
    }
    private void setupNewUser(String username) {
        var encoder = new BCryptPasswordEncoder(16);
        getTerminal().writer().println("Creating a new User!");
        var password = Helper.getContextValue(true, "Password: ", "password", getTerminal(), getResourceLoader(), getTemplateExecutor());
        var email = Helper.getContextValue(false, "Email: ", "abc123@example.com", getTerminal(), getResourceLoader(), getTemplateExecutor());
        var firstName = Helper.getContextValue(false, "firstName: ", "firstname", getTerminal(), getResourceLoader(), getTemplateExecutor());
        var lastName = Helper.getContextValue(false, "lastName: ", "lastname", getTerminal(), getResourceLoader(), getTemplateExecutor());
        var dateOfCreation = new Date(System.currentTimeMillis());
        SetupDatabase.getJdbcTemplate().update(
                "INSERT INTO users (userid, username, password, email, firstname, lastname, dateofcreation, lastaccessdate)" +
                        "VALUES ((SELECT MAX(userid) FROM users) + 1, ?, ?, ?, ?, ?, ?, ?)", username, encoder.encode(password), email, firstName, lastName, dateOfCreation, dateOfCreation
        );
        getTerminal().writer().println("User setup successful!");
        setUser(username, password);
    }

    @Command(command = "follow user", description = "follows a user")
    private void follow() {
        var list = SetupDatabase.getJdbcTemplate().query(
                "SELECT * FROM users WHERE NOT userid = ? AND userid NOT in (SELECT useridown FROM followers WHERE useridfollow = ?);", new UserRowMapper(), UserCommands.getUser().getUserId(), UserCommands.getUser().getUserId()
        );
        var t = list.stream().map((user -> SelectorItem.of(user.getEmail(), String.valueOf(user.getUserId())))).collect(Collectors.toList());
        t.add(SelectorItem.of("cancel", "-1"));
        SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                t, "Select a follower to follow", null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                .run(SingleItemSelector.SingleItemSelectorContext.empty());
        var x = Integer.parseInt(context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
        if(x == -1) return;
        SetupDatabase.getJdbcTemplate().update(
                "INSERT INTO followers (useridown, useridfollow) values (?, ?)", x, UserCommands.getUser().getUserId());
        var u = SetupDatabase.getJdbcTemplate().queryForObject(
                "SELECT username FROM users WHERE userid = ?", String.class, x);
        getTerminal().writer().println("Sucessfully followed " + u);
    }

    @Command(command = "unfollow user", description = "unfollows a user")
    private void unfollow() {
        var list = SetupDatabase.getJdbcTemplate().query(
                "SELECT * FROM users JOIN followers f on users.userid = f.useridown WHERE useridfollow = ?", new UserRowMapper(), UserCommands.getUser().getUserId()
        );
        var t = list.stream().map((user -> SelectorItem.of(user.getEmail(), String.valueOf(user.getUserId())))).collect(Collectors.toList());
        t.add(SelectorItem.of("cancel", "-1"));
        SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                t, "Select a follower to unfollow", null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                .run(SingleItemSelector.SingleItemSelectorContext.empty());
        var x = Integer.parseInt(context.getResultItem().flatMap(si -> Optional.ofNullable(si.getItem())).get());
        if(x == -1) return;
        SetupDatabase.getJdbcTemplate().update(
                "DELETE FROM followers WHERE useridown = ? AND useridfollow = ?", x, UserCommands.getUser().getUserId());
        var u = SetupDatabase.getJdbcTemplate().queryForObject(
                "SELECT username FROM users WHERE userid = ?", String.class, x);
        getTerminal().writer().println("Sucessfully unfollowed " + u);
    }

    @Command(command = "logout user", description = "logout to app")
    private void logout() {
        user = null;
        getTerminal().writer().println("Successfully logged out!");
    }
    @Command(command = "login user", description = "login to app")
    private void login() throws IOException {
        getTerminal().writer().println("Logging in to the application");
        var username = Helper.getContextValue(false, "Username: ", "username", getTerminal(), getResourceLoader(), getTemplateExecutor());
        if(checkUserExists(username)) {
            getTerminal().writer().println("User Exists!");
            var password = Helper.getContextValue(true, "Password: ", "password", getTerminal(), getResourceLoader(), getTemplateExecutor());
            setUser(username, password);
        } else {
            setupNewUser(username);
        }
        SetupDatabase.getClient().insertUser(new io.gorse.gorse4j.User(Integer.toString(user.getUserId()), List.of(user.getEmail())));
    }

    private List<Game> getTop(Date startdate, Date enddate, int limit) {
        return SetupDatabase.getJdbcTemplate().query("SELECT g.gameid, title, esrb_rating, developername, publishername\n" +
                "FROM user_star_game usg JOIN p320_06.game g on usg.gameid = g.gameid\n" +
                "GROUP BY g.gameid, g.gameid, title, esrb_rating, developername, publishername " +
                        "order by (count(starrating) * avg(starrating) + get_x_percentile_rating(0.25, ?, ?) * get_avg_rating(?, ?)) / (count(starrating) + get_avg_rating(?, ?)) DESC LIMIT ?;",
                new GameRowMapper(), startdate, enddate, startdate, enddate, startdate, enddate, limit);
    }
    @Command(command = "stats", description = "user stats")
    private void getStats() {
        getTerminal().writer().println("Stats:");
        int collections = SetupDatabase.getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM collection WHERE userid = ?", Integer.class, user.getUserId());
        getTerminal().writer().println("Collections: " + collections);
        int followers = SetupDatabase.getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM followers WHERE  useridown = ?", Integer.class, user.getUserId());
        getTerminal().writer().println("Followers: " + followers);
        int following = SetupDatabase.getJdbcTemplate().queryForObject("SELECT COUNT(*) FROM followers WHERE  useridfollow = ?", Integer.class, user.getUserId());
        getTerminal().writer().println("Following: " + following);
        var t = getTop(null, null, 10).stream().map((game -> SelectorItem.of(game.toString(), String.valueOf(game.getGameId())))).collect(Collectors.toList());
        t.add(SelectorItem.of("Exit", "-1"));
        SingleItemSelector<String, SelectorItem<String>> component = new SingleItemSelector<>(getTerminal(),
                t, "My Top 10 Games: ", null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        SingleItemSelector.SingleItemSelectorContext<String, SelectorItem<String>> context = component
                .run(SingleItemSelector.SingleItemSelectorContext.empty());
    }
}
