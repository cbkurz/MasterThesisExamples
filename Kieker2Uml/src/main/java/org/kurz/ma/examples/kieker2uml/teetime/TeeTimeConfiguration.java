package org.kurz.ma.examples.kieker2uml.teetime;

import kieker.analysis.architecture.trace.TraceEventRecords2ExecutionAndMessageTraceStage;
import kieker.analysis.architecture.trace.execution.ExecutionRecordTransformationStage;
import kieker.analysis.architecture.trace.flow.EventRecordTraceReconstructionStage;
import kieker.analysis.architecture.trace.reconstruction.TraceReconstructionStage;
import kieker.analysis.generic.DynamicEventDispatcher;
import kieker.analysis.generic.IEventMatcher;
import kieker.analysis.generic.ImplementsEventMatcher;
import kieker.analysis.util.debug.ListCollectionFilter;
import kieker.common.exception.ConfigurationException;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.common.record.flow.trace.AbstractTraceEvent;
import kieker.model.repository.SystemModelRepository;
import kieker.model.system.model.AbstractTrace;
import kieker.tools.source.LogsReaderCompositeStage;
import org.kurz.ma.examples.kieker2uml.PrintOutputStage;
import teetime.framework.Configuration;
import teetime.framework.InputPort;
import teetime.stage.basic.merger.Merger;

import java.util.concurrent.TimeUnit;

public class TeeTimeConfiguration extends Configuration {

    /**
     * Configure analysis.
     *
     * @param configuration
     *            configuration for the collector
     * @throws ConfigurationException
     *             on configuration error
     */
    public TeeTimeConfiguration(final kieker.common.configuration.Configuration configuration)
            throws ConfigurationException {
        SystemModelRepository systemModelRepository = new SystemModelRepository();

        LogsReaderCompositeStage reader = new LogsReaderCompositeStage(configuration);

        IEventMatcher<? extends AbstractTraceEvent> traceEventMatcher = new ImplementsEventMatcher<>(AbstractTraceEvent.class, null);
        IEventMatcher<? extends OperationExecutionRecord> operationExecutionEventMatcher = new ImplementsEventMatcher<>(OperationExecutionRecord.class, null);

        DynamicEventDispatcher dispatcher = new DynamicEventDispatcher(operationExecutionEventMatcher, true, false, false);

        dispatcher.registerOutput(traceEventMatcher);

        ExecutionRecordTransformationStage executionRecordTransformationFilter = new ExecutionRecordTransformationStage(systemModelRepository);

        boolean ignoreInvalidTraces = true;
        long maxTraceDuration = Long.MAX_VALUE;

        TraceReconstructionStage traceReconstructionFilter = new TraceReconstructionStage(systemModelRepository,
                TimeUnit.NANOSECONDS, ignoreInvalidTraces, maxTraceDuration);

        boolean repairEventBasedTraces = true;
        long maxTraceTimeout = Long.MAX_VALUE; // deactivate timeout and time input port

        EventRecordTraceReconstructionStage eventRecordTraceReconstructionFilter = new EventRecordTraceReconstructionStage(TimeUnit.NANOSECONDS,
                repairEventBasedTraces, maxTraceDuration, maxTraceTimeout);

        boolean enhanceJavaContructors = true;
        boolean enhanceCallDetection = true;
        boolean ignoreAssumedCalls = false;

        TraceEventRecords2ExecutionAndMessageTraceStage ter2eamt = new TraceEventRecords2ExecutionAndMessageTraceStage(systemModelRepository,
                enhanceJavaContructors, enhanceCallDetection, ignoreAssumedCalls);

        Merger<AbstractTrace> merger = new Merger<>();
        InputPort<AbstractTrace> traceReconstructorInputPort = merger.getNewInputPort();
        InputPort<AbstractTrace> ter2eamtExecutionInputPort = merger.getNewInputPort();
        InputPort<AbstractTrace> ter2eamtMessageInputPort = merger.getNewInputPort();

        int maxNumberOfEntries = 10000;
        // output
        ListCollectionFilter<AbstractTrace> listCollectionStage = new ListCollectionFilter<AbstractTrace>(maxNumberOfEntries,
                ListCollectionFilter.ListFullBehavior.DROP_OLDEST);

        PrintOutputStage printOutputStage = new PrintOutputStage();


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
