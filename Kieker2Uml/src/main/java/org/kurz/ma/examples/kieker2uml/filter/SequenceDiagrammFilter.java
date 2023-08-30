package org.kurz.ma.examples.kieker2uml.filter;

import kieker.analysis.plugin.trace.AbstractMessageTraceProcessingFilter;
import kieker.model.repository.SystemModelRepository;
import kieker.model.system.model.MessageTrace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SequenceDiagrammFilter extends AbstractMessageTraceProcessingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceDiagrammFilter.class);

    /**
     * Creates a new instance of this class using the given parameters.
     *
     * @param repository model repository
     */
    public SequenceDiagrammFilter(final SystemModelRepository repository) {
        super(repository);
    }

    @Override
    protected void execute(final MessageTrace element) throws Exception {

    }
}
