package org.kurz.ma.examples.kieker2uml.filter;

import kieker.analysis.plugin.trace.AbstractMessageTraceProcessingFilter;
import kieker.model.repository.SystemModelRepository;
import kieker.model.system.model.MessageTrace;
import org.apache.commons.io.FileUtils;
import org.eclipse.uml2.uml.Model;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import static java.lang.String.format;
import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlModel.addStaticView;
import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlModel.addBehaviour;
import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlUtil.loadModel;
import static org.kurz.ma.examples.kieker2uml.uml.Kieker2UmlUtil.saveModel;

public class SequenceDiagrammFilter extends AbstractMessageTraceProcessingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceDiagrammFilter.class);
    private final Path modelPath;
    private final String useCaseName;

    /**
     * @param repository model repository
     * @param modelPath  the path to the file to which the sequence diagramm is written.
     * @param useCaseName the name of the UML use case to which the interaction shall be added.
     */
    public SequenceDiagrammFilter(final SystemModelRepository repository, final Path modelPath, final String useCaseName) {
        super(repository);
        this.modelPath = modelPath;
        this.useCaseName = useCaseName;
    }

    @Override
    protected void execute(final MessageTrace mt) throws Exception {
        createUmlModel(mt);
        this.reportSuccess(mt.getTraceId());
    }

    private void createUmlModel(final MessageTrace mt) {
        LOGGER.debug("Successfully received MessageTrace: " + mt.getTraceId());

        // UML
        final Model model = loadModel(modelPath);
        addBehaviour(model, mt, useCaseName);
        addStaticView(model, mt);
        saveModel(model, modelPath);


        // logging
        toFile("TraceId: " + mt.getTraceId());
        toFile(format("Total number of messages: %s", mt.getSequenceAsVector().size()));
        toFile(format("Total elapsed time for Trace Id %s: %s ms", mt.getTraceId(), (mt.getEndTimestamp() - mt.getStartTimestamp()) / 1_000_000.0));
        toFile("\n");
    }



    private void toFile(final String data) {
        try {
            final File file = FileUtils.getFile("output.txt");
            FileUtils.write(file, "\n" + data, StandardCharsets.UTF_8, true);
        } catch (Exception e) {
            LOGGER.error("An Expection occured while writing to file 'output.txt' with element: " + data, e);
        }
    }
}
