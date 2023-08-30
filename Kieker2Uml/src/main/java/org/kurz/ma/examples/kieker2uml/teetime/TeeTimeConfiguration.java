package org.kurz.ma.examples.kieker2uml.teetime;

import kieker.analysis.architecture.trace.execution.ExecutionRecordTransformationStage;
import kieker.analysis.architecture.trace.reconstruction.TraceReconstructionStage;
import kieker.analysis.generic.DynamicEventDispatcher;
import kieker.analysis.generic.IEventMatcher;
import kieker.analysis.generic.ImplementsEventMatcher;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.model.repository.SystemModelRepository;
import kieker.tools.source.LogsReaderCompositeStage;
import org.kurz.ma.examples.kieker2uml.cli.CliParameters;
import org.kurz.ma.examples.kieker2uml.filter.SequenceDiagrammFilter;
import teetime.framework.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;


public class TeeTimeConfiguration extends Configuration {

    private LogsReaderCompositeStage reader;
    private SystemModelRepository systemModelRepository;
    private TraceReconstructionStage traceReconstructionStage;
    private ExecutionRecordTransformationStage executionRecordTransformationStage;
    private DynamicEventDispatcher dispatcher;
    private IEventMatcher<? extends OperationExecutionRecord> operationExecutionDispatcherOutput;
    private SequenceDiagrammFilter sequenceDiagramFilter;

    public TeeTimeConfiguration(final CliParameters parameters) {
        setup(parameters);

        this.connectPorts(reader.getOutputPort(), dispatcher.getInputPort());
        // configuration
        this.connectPorts(operationExecutionDispatcherOutput.getOutputPort(), this.executionRecordTransformationStage.getInputPort());
        this.connectPorts(this.executionRecordTransformationStage.getOutputPort(), this.traceReconstructionStage.getInputPort());
        this.connectPorts(this.traceReconstructionStage.getMessageTraceOutputPort(), sequenceDiagramFilter.getInputPort());


    }

    private void setup(final CliParameters parameters) {
        this.systemModelRepository = new SystemModelRepository();

        this.reader = new LogsReaderCompositeStage(parameters.getInputDirectories().stream().map(Path::toFile).toList(), false, null);

        this.dispatcher = new DynamicEventDispatcher(null, false, true, false);
        this.operationExecutionDispatcherOutput = new ImplementsEventMatcher<>(OperationExecutionRecord.class, null);
        dispatcher.registerOutput(operationExecutionDispatcherOutput);

        this.traceReconstructionStage = new TraceReconstructionStage(this.systemModelRepository, TimeUnit.MILLISECONDS,true, Long.MAX_VALUE);

        this.executionRecordTransformationStage = new ExecutionRecordTransformationStage(this.systemModelRepository);
        executionRecordTransformationStage.declareActive();

        this.sequenceDiagramFilter = new SequenceDiagrammFilter(this.systemModelRepository, Paths.get("output.uml"));
    }

}