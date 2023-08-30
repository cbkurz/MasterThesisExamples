package org.kurz.ma.examples.kieker2uml.teetime;

import kieker.analysis.architecture.trace.TraceEventRecords2ExecutionAndMessageTraceStage;
import kieker.analysis.architecture.trace.execution.ExecutionRecordTransformationStage;
import kieker.analysis.architecture.trace.flow.EventRecordTraceReconstructionStage;
import kieker.analysis.architecture.trace.reconstruction.TraceReconstructionStage;
import kieker.analysis.generic.DynamicEventDispatcher;
import kieker.analysis.generic.IEventMatcher;
import kieker.analysis.generic.ImplementsEventMatcher;
import kieker.analysis.util.debug.ListCollectionFilter;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.common.record.flow.trace.AbstractTraceEvent;
import kieker.model.repository.SystemModelRepository;
import kieker.model.system.model.AbstractTrace;
import kieker.tools.source.LogsReaderCompositeStage;
import org.kurz.ma.examples.kieker2uml.cli.CliParameters;
import org.kurz.ma.examples.kieker2uml.stage.PrintOutputStage;
import teetime.framework.Configuration;
import teetime.framework.InputPort;
import teetime.stage.basic.merger.Merger;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;

public class TeeTimeConfiguration extends Configuration {

    public TeeTimeConfiguration(final CliParameters parameters) {
        final SystemModelRepository systemModelRepository = new SystemModelRepository();

        final LogsReaderCompositeStage reader = new LogsReaderCompositeStage(parameters.getInputDirectories().stream().map(Path::toFile).toList(), false, null);

        final IEventMatcher<? extends AbstractTraceEvent> traceEventMatcher = new ImplementsEventMatcher<>(AbstractTraceEvent.class, null);
        final IEventMatcher<? extends OperationExecutionRecord> operationExecutionEventMatcher = new ImplementsEventMatcher<>(OperationExecutionRecord.class, null);

        final DynamicEventDispatcher dispatcher = new DynamicEventDispatcher(operationExecutionEventMatcher, true, false, false);

        dispatcher.registerOutput(traceEventMatcher);

        final ExecutionRecordTransformationStage executionRecordTransformationFilter = new ExecutionRecordTransformationStage(systemModelRepository);

        boolean ignoreInvalidTraces = true;
        long maxTraceDuration = Long.MAX_VALUE;

        final TraceReconstructionStage traceReconstructionFilter = new TraceReconstructionStage(systemModelRepository,
                TimeUnit.NANOSECONDS, ignoreInvalidTraces, maxTraceDuration);

        boolean repairEventBasedTraces = true;
        long maxTraceTimeout = Long.MAX_VALUE; // deactivate timeout and time input port

        final EventRecordTraceReconstructionStage eventRecordTraceReconstructionFilter = new EventRecordTraceReconstructionStage(TimeUnit.NANOSECONDS,
                repairEventBasedTraces, maxTraceDuration, maxTraceTimeout);

        final boolean enhanceJavaContructors = true;
        final boolean enhanceCallDetection = true;
        final boolean ignoreAssumedCalls = false;

        final TraceEventRecords2ExecutionAndMessageTraceStage ter2eamt = new TraceEventRecords2ExecutionAndMessageTraceStage(
                systemModelRepository,
                enhanceJavaContructors,
                enhanceCallDetection,
                ignoreAssumedCalls
        );

        final Merger<AbstractTrace> merger = new Merger<>();
        final InputPort<AbstractTrace> traceReconstructorInputPort = merger.getNewInputPort();
        final InputPort<AbstractTrace> ter2eamtExecutionInputPort = merger.getNewInputPort();
        final InputPort<AbstractTrace> ter2eamtMessageInputPort = merger.getNewInputPort();

        final int maxNumberOfEntries = 10000;
        // output
        final ListCollectionFilter<AbstractTrace> listCollectionStage = new ListCollectionFilter<>(maxNumberOfEntries,
                ListCollectionFilter.ListFullBehavior.DROP_OLDEST);

        final PrintOutputStage printOutputStage = new PrintOutputStage();


        // configuration
        this.connectPorts(reader.getOutputPort(), dispatcher.getInputPort());

        // OperationExecutionRecord
        this.connectPorts(operationExecutionEventMatcher.getOutputPort(), executionRecordTransformationFilter.getInputPort());
        this.connectPorts(executionRecordTransformationFilter.getOutputPort(), traceReconstructionFilter.getInputPort());
        this.connectPorts(traceReconstructionFilter.getExecutionTraceOutputPort(), traceReconstructorInputPort);

        // AbstractTraceEVent
        this.connectPorts(traceEventMatcher.getOutputPort(), eventRecordTraceReconstructionFilter.getTraceRecordsInputPort());
        this.connectPorts(eventRecordTraceReconstructionFilter.getValidTracesOutputPort(), ter2eamt.getInputPort());

        this.connectPorts(ter2eamt.getExecutionTraceOutputPort(), ter2eamtExecutionInputPort);
        this.connectPorts(ter2eamt.getMessageTraceOutputPort(), ter2eamtMessageInputPort);

        this.connectPorts(merger.getOutputPort(), listCollectionStage.getInputPort());
        this.connectPorts(listCollectionStage.getOutputPort(), printOutputStage.getInputPort());
    }

}
