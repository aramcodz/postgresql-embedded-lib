package com.smartmonkee.embed.postgresql.config;

import de.flapdoodle.embed.process.distribution.IVersion;

import static com.smartmonkee.embed.postgresql.distribution.Version.Main.PRODUCTION;

import java.io.IOException;

import com.smartmonkee.embed.postgresql.Command;

/**
 * Configuration for postgres
 */
public class PostgresConfig extends AbstractPostgresConfig<PostgresConfig> {

    public PostgresConfig(AbstractPostgresConfig config, Command command) {
        super(config, command);
    }

    public PostgresConfig(AbstractPostgresConfig config) {
        super(config);
    }

    public PostgresConfig(IVersion version, String dbName) throws IOException {
        this(version, new Net(), new Storage(dbName), new Timeout());
    }

    public PostgresConfig(IVersion version, String host, int port, String dbName) throws IOException {
        this(version, new Net(host, port), new Storage(dbName), new Timeout());
    }

    public PostgresConfig(IVersion version, Net networt, Storage storage, Timeout timeout, Credentials cred, Command command) {
        super(version, networt, storage, timeout, cred, new SupportConfig(command));
    }

    public PostgresConfig(IVersion version, Net networt, Storage storage, Timeout timeout, Credentials cred) {
        this(version, networt, storage, timeout, cred, Command.Postgres);
    }

    public PostgresConfig(IVersion version, Net network, Storage storage, Timeout timeout) {
        super(version, network, storage, timeout);
    }

    public static PostgresConfig defaultWithDbName(String dbName, String user, String password) throws IOException {
        return new PostgresConfig(PRODUCTION, new Net(), new Storage(dbName), new Timeout(),
                new Credentials(user, password));
    }

    public static PostgresConfig defaultWithDbName(String dbName) throws IOException {
        return new PostgresConfig(PRODUCTION, dbName);
    }
}
