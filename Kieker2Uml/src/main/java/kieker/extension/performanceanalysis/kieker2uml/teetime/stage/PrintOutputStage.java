package kieker.extension.performanceanalysis.kieker2uml.teetime.stage;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import teetime.framework.AbstractConsumerStage;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * This Class is a simple class for debugging and will write the output either to the file "output.txt" or the standard output.
 * The default is to write to the file.
 * Both variants can be enacted.
 * It is not possible to
 */
public class PrintOutputStage extends AbstractConsumerStage<Object> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintOutputStage.class);
    private final boolean toFile;
    private final boolean toSout;
    private final boolean toLogging;

    /**
     * This constructor will enable writing to the file output, standard output is not enabled.
     */
    public PrintOutputStage() {
        this.toFile = true;
        this.toSout = false;
        this.toLogging = false;
    }

    /**
     * At least one parameter must be true, if all are false the default is enforced.
     * Default is to write the output to the file "output.txt".
     * @param toFile - bool value to write objects to "output.txt".
     * @param toSout - bool value to write objects to the standard output.
     * @param toLogging - bool value to write objects to the logger.
     */
    public PrintOutputStage(final boolean toFile, final boolean toSout, final boolean toLogging) {
        if (!toFile && !toSout && !toLogging) { // enforcing default
            this.toFile = true;
        } else {
            this.toFile = toFile;
        }
        this.toSout = toSout;
        this.toLogging = toLogging;
    }

    protected void execute(Object element) throws Exception {
        toFile(element);
        toSout(element);
        toLogging(element);
    }

    private void toLogging(final Object element) {
        if (!this.toLogging) {
            return;
        }
        LOGGER.info(getElementAsString(element));
    }

    private void toSout(final Object element) {
        if (!this.toSout) {
            return;
        }
        System.out.println(getElementAsString(element));
    }

    private static String getElementAsString(final Object element) {
        return String.format("Another Object\n(%s) %s", element.getClass(), element.toString());
    }

    private void toFile(final Object element) {
        if (!this.toFile) {
            return;
        }
        try {
            final File file = FileUtils.getFile("output.txt");
            FileUtils.write(file, getElementAsString(element), StandardCharsets.UTF_8, true);
        } catch (Exception e) {
            LOGGER.error("An Expection occured while writing to file 'output.txt' with element: " + element, e);
        }
    }

}
