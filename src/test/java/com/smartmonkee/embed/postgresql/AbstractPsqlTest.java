package com.smartmonkee.embed.postgresql;

import org.junit.After;
import org.junit.Before;

import com.smartmonkee.embed.postgresql.PostgresExecutable;
import com.smartmonkee.embed.postgresql.PostgresProcess;
import com.smartmonkee.embed.postgresql.PostgresStarter;
import com.smartmonkee.embed.postgresql.config.AbstractPostgresConfig;
import com.smartmonkee.embed.postgresql.config.PostgresConfig;

import java.sql.Connection;
import java.sql.DriverManager;

import static com.smartmonkee.embed.postgresql.distribution.Version.Main.PRODUCTION;
import static com.smartmonkee.embed.postgresql.util.SocketUtil.findFreePort;
import static java.lang.String.format;

/**
 * @author Ilya Sadykov
 */
public abstract class AbstractPsqlTest {
    protected PostgresProcess process;
    protected Connection conn;

    @Before
    public void setUp() throws Exception {
        PostgresStarter<PostgresExecutable, PostgresProcess> runtime = PostgresStarter.getDefaultInstance();
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

}
