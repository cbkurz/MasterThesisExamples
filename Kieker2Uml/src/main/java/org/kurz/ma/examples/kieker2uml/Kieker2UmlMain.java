package org.kurz.ma.examples.kieker2uml;

import com.beust.jcommander.JCommander;
import io.vavr.control.Try;
import kieker.common.configuration.Configuration;
import kieker.common.exception.ConfigurationException;
import kieker.monitoring.core.configuration.ConfigurationFactory;
import kieker.tools.common.AbstractService;
import kieker.tools.source.LogsReaderCompositeStage;
import org.kurz.ma.examples.kieker2uml.cli.CliParameters;
import org.kurz.ma.examples.kieker2uml.teetime.TeeTimeConfiguration;

import java.nio.file.Path;
import java.nio.file.Paths;

import static java.lang.Boolean.FALSE;

public class Kieker2UmlMain extends AbstractService<TeeTimeConfiguration, CliParameters> {

    // Depending on the input, you have to change the input path and the reader configuration (commented with ALTERNATIVE)
    private static final String INPUT_MONITORING_LOG_OER = "TeaStore/kieker-monitoring/teastore-auth/kieker-20230726-195044-42028674758-UTC--KIEKER";
    // ALTERNATIVE
    // private static final String INPUT_MONITORING_LOG_TE = "./input-data/trace-event-log/";

    public static final CliParameters PARAMETERS = new CliParameters();

    /**
     * This is a simple main class which does not need to be instantiated.
     */
    private Kieker2UmlMain(String[] args) {

    }

    /**
     * Configure and execute the TCP Kieker data collector.
     *
     * @param args
     *            arguments are ignored
     */
    public static void main(final String[] args) {
        final Kieker2UmlMain myMain = new Kieker2UmlMain(args);
        System.exit(myMain.run("Trace Analysis", "trace-analysis", args, PARAMETERS));
    }

    @Override
    protected TeeTimeConfiguration createTeetimeConfiguration() throws ConfigurationException {
        return new TeeTimeConfiguration(this.kiekerConfiguration);
    }

    @SuppressWarnings("DanglingJavadoc")
    @Override
    protected boolean checkParameters(final JCommander commander) throws ConfigurationException {
        /**
         * This method shall check the parameters to their correctness.
         * However, this is not necessary since this can be implemented directly into JCommander
         * For details check the {@link org.kurz.ma.examples.kieker2uml.cli.CliParameters} class.
         * </p>
         * the help parameter has to be read out and is handled by {@link kieker.tools.common.AbstractLegacyTool#run(String, String, String[], Object)} correctly.
        */

        this.help = Try.ofSupplier(PARAMETERS::isHelp).getOrElseGet((t) -> FALSE);
        return true;
    }

    @Override
    protected Path getConfigurationPath() {
        return null;
    }

    @Override
    protected kieker.common.configuration.Configuration readConfiguration() {
        Configuration configuration = ConfigurationFactory.createDefaultConfiguration();
        if (!Paths.get(INPUT_MONITORING_LOG_OER).toFile().exists()) {
            throw new RuntimeException("File does not exist: " + INPUT_MONITORING_LOG_OER);
        }
        configuration.setProperty(LogsReaderCompositeStage.LOG_DIRECTORIES, INPUT_MONITORING_LOG_OER);

        return configuration;
    }

    @Override
    protected boolean checkConfiguration(final Configuration configuration, final JCommander commander) {
        return true;
    }

    @Override
    protected void shutdownService() {
        // nothing special to shutdown
    }


}
