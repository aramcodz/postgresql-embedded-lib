package com.smartmonkee.embed.postgresql;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.io.progress.LoggingProgressListener;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.smartmonkee.embed.postgresql.Command;
import com.smartmonkee.embed.postgresql.PostgresExecutable;
import com.smartmonkee.embed.postgresql.PostgresProcess;
import com.smartmonkee.embed.postgresql.PostgresStarter;
import com.smartmonkee.embed.postgresql.config.AbstractPostgresConfig;
import com.smartmonkee.embed.postgresql.config.DownloadConfigBuilder;
import com.smartmonkee.embed.postgresql.config.PostgresConfig;
import com.smartmonkee.embed.postgresql.config.RuntimeConfigBuilder;
import com.smartmonkee.embed.postgresql.ext.ArtifactStoreBuilder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import static com.smartmonkee.embed.postgresql.distribution.Version.Main.PRODUCTION;
import static com.smartmonkee.embed.postgresql.util.SocketUtil.findFreePort;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TestPostgresStarter {

    private static final Logger logger = Logger.getLogger(TestPostgresStarter.class.getName());

    private PostgresProcess process;
    private Connection conn;
    private final TestHandler testHandler = new TestHandler();

    @Before
    public void setUp() throws Exception {
        logger.setLevel(Level.INFO);
        logger.addHandler(testHandler);
        // turns off the default functionality of unzipping on every run.
        IRuntimeConfig runtimeConfig = new RuntimeConfigBuilder()
                .defaults(Command.Postgres)
                .artifactStore(new ArtifactStoreBuilder()
                        .defaults(Command.Postgres)
                        .download(new DownloadConfigBuilder()
                                .defaultsForCommand(Command.Postgres)
                                .progressListener(new LoggingProgressListener(logger, Level.ALL))
                                .build()))

                .build();

        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getInstance(runtimeConfig);
        final PostgresConfig config = new PostgresConfig(PRODUCTION, new AbstractPostgresConfig.Net(
                "localhost", findFreePort()
        ), new AbstractPostgresConfig.Storage("test"), new AbstractPostgresConfig.Timeout(),
                new AbstractPostgresConfig.Credentials("user", "password"));

        PostgresExecutable exec = runtime.prepare(config);
        process = exec.start();
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

        // verify no logs
        assertThat(testHandler.RECORDS.size(), is(0));
    }

    private static class TestHandler extends Handler {

        public final List<LogRecord> RECORDS = new ArrayList<>();

        @Override
        public void publish(LogRecord record) {
            RECORDS.add(record);
        }

        @Override
        public void flush() {

        }

        @Override
        public void close() throws SecurityException {

        }
    }

}

