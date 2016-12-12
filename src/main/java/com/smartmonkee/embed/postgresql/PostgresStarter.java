package com.smartmonkee.embed.postgresql;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.config.io.ProcessOutput;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;
import de.flapdoodle.embed.process.io.LoggingOutputStreamProcessor;
import de.flapdoodle.embed.process.runtime.Starter;

import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.smartmonkee.embed.postgresql.config.PostgresConfig;
import com.smartmonkee.embed.postgresql.config.RuntimeConfigBuilder;
import com.smartmonkee.embed.postgresql.ext.LogWatchStreamProcessor;

import static java.util.Collections.singletonList;


/**
 * Starter for every pg process
 */
public class PostgresStarter<E extends AbstractPGExecutable<PostgresConfig, P>, P extends AbstractPGProcess<E, P>>
        extends Starter<PostgresConfig, E, P> {
    final Class<E> execClass;

    public PostgresStarter(final Class<E> execClass, final IRuntimeConfig runtimeConfig) {
        super(runtimeConfig);
        this.execClass = execClass;
    }

    public static PostgresStarter<PostgresExecutable, PostgresProcess> getInstance(IRuntimeConfig config) {
        return new PostgresStarter(PostgresExecutable.class, config);
    }

    public static PostgresStarter<PostgresExecutable, PostgresProcess> getDefaultInstance() {
        return getInstance(runtimeConfig(Command.Postgres));
    }

    public static IRuntimeConfig runtimeConfig(Command cmd) {
        LogWatchStreamProcessor logWatch = new LogWatchStreamProcessor(
                "started", new HashSet<>(singletonList("failed")),
                new LoggingOutputStreamProcessor(Logger.getLogger("postgres"), Level.ALL));
        return new RuntimeConfigBuilder()
                .defaults(cmd)
                .processOutput(new ProcessOutput(logWatch, logWatch, logWatch)).build();
    }

    public static <E extends AbstractPGExecutable<PostgresConfig, P>, P extends AbstractPGProcess<E, P>>
    PostgresStarter<E, P> getCommand(Command command, IRuntimeConfig config) {
        return new PostgresStarter(command.executableClass(), config);
    }

    public static <E extends AbstractPGExecutable<PostgresConfig, P>, P extends AbstractPGProcess<E, P>>
    PostgresStarter<E, P> getCommand(Command command) {
        return getCommand(command, runtimeConfig(command));
    }

    @Override
    protected E newExecutable(PostgresConfig config, Distribution distribution,
                              IRuntimeConfig runtime, IExtractedFileSet exe) {
        try {
            Constructor<E> c = execClass.getConstructor(
                    Distribution.class, PostgresConfig.class,
                    IRuntimeConfig.class, IExtractedFileSet.class
            );
            return c.newInstance(distribution, config, runtime, exe);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize the executable", e);
        }
    }
}
