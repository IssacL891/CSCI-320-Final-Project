package com.CSCI32006.CLI;

import com.jcraft.jsch.HostKey;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.shell.component.StringInput;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import javax.sql.DataSource;
import java.util.Base64;

import static org.springframework.shell.command.invocation.InvocableShellMethod.log;

@ShellComponent
public class UserCommands extends AbstractShellComponent {

    private JdbcTemplate jdbcTemplate;
    private Session session;
    private void setJdbcTemplate(DataSource dataSource) {
        if(jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(dataSource);
        } else {
            log.error("Already instanced the JdbcTemplate");
        }
    }

    private DataSource dataSource(String username, String password){
        PGSimpleDataSource dataSource = new PGSimpleDataSource();
        dataSource.setURL(Config.URL);
        dataSource.setUser(username);
        dataSource.setPassword(password);
        dataSource.setDatabaseName(Config.databaseName);
        dataSource.setServerNames(new String[]{
                Config.hostname
        });
        return dataSource;
    }

    private StringInput setupContext(boolean mask, String name, String defaultValue) {
        StringInput component = new StringInput(getTerminal(), name, defaultValue);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        if(mask) {
            component.setMaskCharacter('*');
        }
        return component;
    }

    @ShellMethod(key = "logout", value = "cleans up port", group = "Components")
    private void cleanPort(){
        if(session.isConnected()) {
            try {
                session.delPortForwardingL(Config.hostname, 3307);
            } catch (JSchException e) {
                throw new RuntimeException(e);
            }
            session.disconnect();
            log.debug("Disconnecting from server.");
        } else {
            log.warn("Server already disconnected!");
        }
    }
    @ShellMethod(key = "login", value = "Logins the User", group = "Components")
    private void login() throws JSchException {
        StringInput.StringInputContext username =
                setupContext(false, "Enter Username: ", "username")
                        .run(StringInput.StringInputContext.empty());
        StringInput.StringInputContext password =
                setupContext(true, "Enter Password: ", "password")
                        .run(StringInput.StringInputContext.empty());
        var jsch = new JSch();
        session = jsch.getSession(username.getResultValue(), Config.SSHURL);
        session.setPassword(password.getResultValue());
        jsch.getHostKeyRepository().add(new HostKey(
                Config.SSHURL,
                3,
                Base64.getDecoder().decode(Config.publicKey.getBytes())
        ), null);
        session.setPortForwardingL(Config.localPort, Config.hostname, Config.port);
        session.connect();
        Thread cleanupHook = new Thread(() -> {
            if(session.isConnected()){
                try {
                    session.delPortForwardingL(Config.localPort);
                } catch (JSchException e) {
                    throw new RuntimeException(e);
                }
                session.disconnect();
                log.debug("Disconnecting from server.");
            }else {
                log.warn("Already Disconnected!");
            }
        });
        Runtime.getRuntime().addShutdownHook(cleanupHook);
        setJdbcTemplate(dataSource(username.getResultValue(), password.getResultValue()));
    }
    @ShellMethod(key = "getTables", value = "Logins the User", group = "Components")
    public String getGame(int value) {
        return jdbcTemplate.queryForObject("SELECT title FROM game WHERE gameid = "+ value + ";", String.class);
    }

}
