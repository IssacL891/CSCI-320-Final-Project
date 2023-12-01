package com.CSCI32006.CLI;

import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import io.gorse.gorse4j.Gorse;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.ExitRequest;
import org.springframework.shell.command.annotation.Command;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.commands.Quit;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Objects;
import static org.springframework.shell.context.InteractionMode.INTERACTIVE;
//noinspection JSUnusedGlobalSymbols
@Command(group = "Setup Database Commands")
@Getter
public class SetupDatabase extends AbstractShellComponent implements Quit.Command {

    private Session session;
    private PGSimpleDataSource dataSource;
    @Getter
    private static JdbcTemplate jdbcTemplate;

    @Getter
    private static Gorse client;
    private void setDatabaseConn(String username, String password){
        dataSource = new PGSimpleDataSource();
        dataSource.setURL(Config.URL);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setDatabaseName(Config.DATABASE_NAME);
        dataSource.setServerNames(new String[]{
                Config.HOSTNAME
        });
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Command(command = "quit", alias = "exit", description = "Exit the shell", interactionMode = INTERACTIVE, group = "Built-In Commands")
    public void quit() throws SQLException {
        logout();
        throw new ExitRequest();
    }

    @Command(command = "logout database", description = "Logs out of the database")
    void logout() throws SQLException {
        if(jdbcTemplate != null){
            Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection().close();
            dataSource.getConnection().close();
            session.disconnect();
            getTerminal().writer().println("Successfully disconnected!");
        } else {
            getTerminal().writer().println("Not Logged in");
        }
    }
    @Command(command = "login database", description = "Logs into the database")
    private void login() throws JSchException {
        client = new Gorse("http://0.0.0.0:8088", "");
        var username =
                Helper.getContextValue(false, "Enter Username: ", "username", getTerminal(), getResourceLoader(), getTemplateExecutor());
        var password =
                Helper.getContextValue(true, "Enter Password: ", "password", getTerminal(), getResourceLoader(), getTemplateExecutor());
        var jsch = new JSch();
        session = jsch.getSession(username, Config.HOST);
        session.setPassword(password);
        jsch.getHostKeyRepository().add(new HostKey(
                Config.HOST,
                3,
                Base64.getDecoder().decode(Config.PUBLICKEY.getBytes())
        ), null);
        session.setPortForwardingL(Config.LOCAL_PORT, Config.HOSTNAME, Config.HOST_PORT);
        session.connect();
        setDatabaseConn(username, password);
    }
}
