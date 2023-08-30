package org.kurz.ma.examples.kieker2uml.teetime;

import com.beust.jcommander.JCommander;
import io.vavr.control.Try;
import kieker.common.configuration.Configuration;
import kieker.common.exception.ConfigurationException;
import kieker.tools.common.AbstractService;
import org.kurz.ma.examples.kieker2uml.cli.CliParameters;

import java.nio.file.Path;

import static java.lang.Boolean.FALSE;

public class Kieker2UmlTeeTimeService extends AbstractService<TeeTimeConfiguration, CliParameters> {

    private final CliParameters cliParameters = new CliParameters();
    private Kieker2UmlTeeTimeService() {
    }

    public static Kieker2UmlTeeTimeService getInstance() {
        return new Kieker2UmlTeeTimeService();
    }

    @Override
    protected TeeTimeConfiguration createTeetimeConfiguration() throws ConfigurationException {
        return new TeeTimeConfiguration(this.cliParameters);
    }

    /**
     * This method shall check the parameters to their correctness.
     * However, this is not necessary since this can be implemented directly into JCommander
     * For details check the {@link org.kurz.ma.examples.kieker2uml.cli.CliParameters} class.
     * </p>
     * the help parameter has to be read out and is handled by {@link kieker.tools.common.AbstractLegacyTool#run(String, String, String[], Object)} correctly.
     */
    @Override
    protected boolean checkParameters(final JCommander commander) throws ConfigurationException {
        this.help = Try.ofSupplier(cliParameters::isHelp).getOrElseGet((t) -> FALSE);
        return true;
    }

    @Override
    protected Path getConfigurationPath() {
        return null;
    }

    @Override
    protected boolean checkConfiguration(final Configuration configuration, final JCommander commander) {
        return true;
    }

    @Override
    protected void shutdownService() {
        // nothing special to shutdown
    }

    public CliParameters getParameters() {
        return cliParameters;
    }
}
