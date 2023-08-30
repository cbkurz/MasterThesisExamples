package org.kurz.ma.examples.kieker2uml.filter;

import kieker.analysis.plugin.trace.AbstractMessageTraceProcessingFilter;
import kieker.model.repository.SystemModelRepository;
import kieker.model.system.model.MessageTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class SequenceDiagrammFilter extends AbstractMessageTraceProcessingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceDiagrammFilter.class);
    private final Path outputPath;

    /**
     * @param repository model repository
     * @param outputPath the path to the file to which the sequence diagramm is written.
     */
    public SequenceDiagrammFilter(final SystemModelRepository repository, final Path outputPath) {
        super(repository);
        this.outputPath = outputPath;
    }

    @Override
    protected void execute(final MessageTrace mt) throws Exception {
        writeMessageTrace(mt);
        this.reportSuccess(mt.getTraceId());
    }

    private void writeMessageTrace(final MessageTrace mt) {
        System.out.println("successfully received MessageTrace: " + mt);
    }
}
