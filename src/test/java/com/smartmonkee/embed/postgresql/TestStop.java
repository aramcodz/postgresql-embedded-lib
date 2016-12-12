package com.smartmonkee.embed.postgresql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.smartmonkee.embed.postgresql.PostgresProcess;
import com.smartmonkee.embed.postgresql.PostgresStarter;
import com.smartmonkee.embed.postgresql.config.PostgresConfig;
import com.smartmonkee.embed.postgresql.distribution.Version;

/**
 * @author Ilya Sadykov
 */
public class TestStop {
    private static final Logger LOG = LoggerFactory.getLogger(TestStop.class);

    public static void main(String[] args) throws Exception {
        LOG.info("Starting postgres");
        PostgresProcess process = PostgresStarter.getDefaultInstance().prepare(new PostgresConfig(Version.Main.PRODUCTION,
                "test-db")).start();

        LOG.info("Stopping postgres");
        process.stop();
    }
}
