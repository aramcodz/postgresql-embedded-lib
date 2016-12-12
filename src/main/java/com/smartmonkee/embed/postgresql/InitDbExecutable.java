package com.smartmonkee.embed.postgresql;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;

import java.io.IOException;

import com.smartmonkee.embed.postgresql.config.PostgresConfig;

/**
 * initdb executor
 * (helper to initialize the DB)
 */
class InitDbExecutable extends AbstractPGExecutable<PostgresConfig, InitDbProcess> {

    public InitDbExecutable(Distribution distribution, PostgresConfig config, IRuntimeConfig runtimeConfig, IExtractedFileSet exe) {
        super(distribution, config, runtimeConfig, exe);
    }

    @Override
    protected InitDbProcess start(Distribution distribution, PostgresConfig config, IRuntimeConfig runtime)
            throws IOException {
        return new InitDbProcess<>(distribution, config, runtime, this);
    }


    @Override
    public synchronized void stop() {
        // We don't want to cleanup after this particular single invocation
    }
}