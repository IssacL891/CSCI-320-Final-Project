package com.CSCI32006.CLI;

import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
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
    @Setter
    @Getter
    private static JdbcTemplate jdbcTemplate;
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

    private @NotNull StringInput setupContext(boolean mask, String name, String defaultValue) {
        StringInput component = new StringInput(getTerminal(), name, defaultValue);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        if(mask) {
            component.setMaskCharacter('*');
        }
        return component;
    }

    @Command(command = "quit", alias = "exit", description = "Exit the shell", interactionMode = INTERACTIVE, group = "Built-In Commands")
    public void quit() throws SQLException {
        logout();
        throw new ExitRequest();
    }

    @Command(command = "logout", description = "Logs out of the database")
    void logout() throws SQLException {
        if(jdbcTemplate != null){
            Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection().close();
            dataSource.getConnection().close();
            session.disconnect();
        } else {
            getTerminal().writer().println("Not Logged in");
        }
    }
    @Command(command = "login", description = "Logs into the database")
    private void login() throws JSchException {
        StringInput.StringInputContext username =
                setupContext(false, "Enter Username: ", "username")
                        .run(StringInput.StringInputContext.empty());
        StringInput.StringInputContext password =
                setupContext(true, "Enter Password: ", "password")
                        .run(StringInput.StringInputContext.empty());
        var jsch = new JSch();
        session = jsch.getSession(username.getResultValue(), Config.HOST);
        session.setPassword(password.getResultValue());
        jsch.getHostKeyRepository().add(new HostKey(
                Config.HOST,
                3,
                Base64.getDecoder().decode(Config.PUBLICKEY.getBytes())
        ), null);
        session.setPortForwardingL(Config.LOCAL_PORT, Config.HOSTNAME, Config.HOST_PORT);
        session.connect();
        setDatabaseConn(username.getResultValue(), password.getResultValue());
    }
}
