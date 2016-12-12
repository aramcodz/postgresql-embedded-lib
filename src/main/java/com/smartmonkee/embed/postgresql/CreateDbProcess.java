package com.smartmonkee.embed.postgresql;

import de.flapdoodle.embed.process.config.IRuntimeConfig;
import de.flapdoodle.embed.process.distribution.Distribution;
import de.flapdoodle.embed.process.extract.IExtractedFileSet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.smartmonkee.embed.postgresql.config.PostgresConfig;

/**
 * createdb process
 * (helper to initialize the DB)
 */
class CreateDbProcess<E extends CreateDbExecutable> extends AbstractPGProcess<E, CreateDbProcess> {

    public CreateDbProcess(Distribution distribution, PostgresConfig config, IRuntimeConfig runtimeConfig, E executable) throws IOException {
        super(distribution, config, runtimeConfig, executable);
    }

    @Override
    protected List<String> getCommandLine(Distribution distribution, PostgresConfig config, IExtractedFileSet exe)
            throws IOException {
        List<String> ret = new ArrayList<>();
        ret.add(exe.executable().getAbsolutePath());
        ret.addAll(Arrays.asList(
                "-h", config.net().host(),
                "-p", String.valueOf(config.net().port())
        ));
        ret.add(config.storage().dbName());

        return ret;
    }
}