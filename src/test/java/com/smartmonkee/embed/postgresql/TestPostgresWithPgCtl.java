package com.smartmonkee.embed.postgresql;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.smartmonkee.embed.postgresql.Command;
import com.smartmonkee.embed.postgresql.PostgresExecutable;
import com.smartmonkee.embed.postgresql.PostgresProcess;
import com.smartmonkee.embed.postgresql.PostgresStarter;
import com.smartmonkee.embed.postgresql.config.AbstractPostgresConfig;
import com.smartmonkee.embed.postgresql.config.PostgresConfig;
import com.smartmonkee.embed.postgresql.config.RuntimeConfigBuilder;
import com.smartmonkee.embed.postgresql.distribution.Version;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import static com.smartmonkee.embed.postgresql.util.SocketUtil.findFreePort;
import static java.lang.String.format;
import static java.lang.Thread.sleep;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TestPostgresWithPgCtl {

    private PostgresProcess process;
    private Connection conn;

    @Before
    public void setUp() throws Exception {
        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getInstance(
                new RuntimeConfigBuilder().defaults(Command.PgCtl).build());
        final PostgresConfig config = new PostgresConfig(Version.V9_4_4, new AbstractPostgresConfig.Net(
                "localhost", findFreePort()
        ), new AbstractPostgresConfig.Storage("test"), new AbstractPostgresConfig.Timeout(),
                new AbstractPostgresConfig.Credentials("user", "password"), Command.PgCtl);
        PostgresExecutable exec = runtime.prepare(config);
        process = exec.start();
        sleep(2000);
        String url = format("jdbc:postgresql://%s:%s/%s?user=%s&password=%s",
                config.net().host(),
                config.net().port(),
                config.storage().dbName(),
                config.credentials().username(),
                config.credentials().password()
        );
        conn = DriverManager.getConnection(url);
    }

    @After
    public void tearDown() throws Exception {
        conn.close();
        process.stop();
    }

    @Test
    public void testPostgres() throws Exception {
        assertThat(conn, not(nullValue()));
        assertThat(conn.createStatement().execute("CREATE TABLE films (code char(5));"), is(false));
        assertThat(conn.createStatement().execute("INSERT INTO films VALUES ('movie');"), is(false));
        final Statement statement = conn.createStatement();
        assertThat(statement.execute("SELECT * FROM films;"), is(true));
        assertThat(statement.getResultSet().next(), is(true));
        assertThat(statement.getResultSet().getString("code"), is("movie"));
    }

}
