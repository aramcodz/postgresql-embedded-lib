package com.smartmonkee.embed.postgresql;

import com.smartmonkee.embed.postgresql.config.PostgresConfig;

public enum Command {
    Postgres("postgres", PostgresExecutable.class),
    InitDb("initdb", InitDbExecutable.class),
    CreateDb("createdb", CreateDbExecutable.class),
    PgCtl("pg_ctl", PgCtlExecutable.class),
    Psql("psql", PsqlExecutable.class),
    PgDump("pg_dump", PsqlExecutable.class),
    Createuser("createuser", PsqlExecutable.class),
    ;

    private final String commandName;
    private final Class<? extends AbstractPGExecutable<PostgresConfig, ? extends AbstractPGProcess>> executableClass;

    Command(String commandName,
            Class<? extends AbstractPGExecutable<PostgresConfig, ? extends AbstractPGProcess>>
                    executableClass) {
        this.commandName = commandName;
        this.executableClass = executableClass;
    }

    public <E extends AbstractPGExecutable<PostgresConfig, P>, P extends AbstractPGProcess> Class<E> executableClass() {
        return (Class<E>) this.executableClass;
    }

    public String commandName() {
        return this.commandName;
    }
}