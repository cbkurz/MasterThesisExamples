package org.kurz.ma.examples.kieker2uml.teetime;

import kieker.analysis.architecture.recovery.OperationAndCallGeneratorStage;
import kieker.analysis.architecture.trace.execution.ExecutionRecordTransformationStage;
import kieker.analysis.architecture.trace.reconstruction.TraceReconstructionStage;
import kieker.analysis.generic.DynamicEventDispatcher;
import kieker.analysis.generic.IEventMatcher;
import kieker.analysis.generic.ImplementsEventMatcher;
import kieker.common.record.controlflow.OperationExecutionRecord;
import kieker.common.record.flow.IFlowRecord;
import kieker.model.repository.SystemModelRepository;
import kieker.tools.source.LogsReaderCompositeStage;
import org.kurz.ma.examples.kieker2uml.cli.CliParameters;
import org.kurz.ma.examples.kieker2uml.filter.SequenceDiagrammFilter;
import teetime.framework.Configuration;
import teetime.stage.basic.merger.Merger;

import java.nio.file.Path;
import java.util.concurrent.TimeUnit;


public class TeeTimeConfiguration extends Configuration {

    private SystemModelRepository systemModelRepository;
    private LogsReaderCompositeStage reader;
    private DynamicEventDispatcher dispatcher;
    private IEventMatcher<? extends OperationExecutionRecord> operationExecutionDispatcherOutput;
    private ImplementsEventMatcher<IFlowRecord> flowEventMatcher;
    private Merger<IFlowRecord> merger;
    private OperationAndCallGeneratorStage operationAndCallStage;
    private TraceReconstructionStage traceReconstructionStage;
    private ExecutionRecordTransformationStage executionRecordTransformationStage;

    private SequenceDiagrammFilter sequenceDiagramFilter;


    public TeeTimeConfiguration(final CliParameters parameters) {
        setup(parameters);

        this.connectPorts(reader.getOutputPort(), dispatcher.getInputPort());
        // configuration
        this.connectPorts(operationExecutionDispatcherOutput.getOutputPort(), this.executionRecordTransformationStage.getInputPort());
        this.connectPorts(this.executionRecordTransformationStage.getOutputPort(), this.traceReconstructionStage.getInputPort());
        this.connectPorts(this.traceReconstructionStage.getMessageTraceOutputPort(), sequenceDiagramFilter.getInputPort());

//        this.connectPorts(merger.getOutputPort(), operationAndCallStage.getInputPort());
//        this.connectPorts(operationAndCallStage.getOperationOutputPort(), typeModelAssemblerStage.getInputPort());
//        this.connectPorts(typeModelAssemblerStage.getOutputPort() , null); // TODO
    }

    private void setup(final CliParameters parameters) {
        this.systemModelRepository = new SystemModelRepository();
//        this.repository = ArchitectureModelManagementUtils.createModelRepository(
//                this.createRepositoryName(this.settings.getExperimentName(), this.settings.getModuleModes()));

        this.reader = new LogsReaderCompositeStage(parameters.getInputDirectories().stream().map(Path::toFile).toList(), false, null);

        this.dispatcher = new DynamicEventDispatcher(null, false, true, false);
        this.operationExecutionDispatcherOutput = new ImplementsEventMatcher<>(OperationExecutionRecord.class, null);
//        this.flowEventMatcher = new ImplementsEventMatcher<>(IFlowRecord.class, null);
        dispatcher.registerOutput(operationExecutionDispatcherOutput);
//        dispatcher.registerOutput(flowEventMatcher);

//        this.merger = new Merger<>();
//        merger.declareActive();

//        this.operationAndCallStage = new OperationAndCallGeneratorStage(true,
//                this.createProcessors(settings.getModuleModes(), settings, logger),
//                !settings.isKeepMetaDataOnCompletedTraces());

//        final ModelAssemblerStage<OperationEvent> typeModelAssemblerStage = new ModelAssemblerStage<>(
//                new OperationTypeModelAssembler(systemModelRepository.getModel(TypePackage.Literals.TYPE_MODEL),
//                        systemModelRepository.getModel(SourcePackage.Literals.SOURCE_MODEL),
//                        settings.getSourceLabel(),
//                        componentSignatureExtractor,
//                        operationSignatureExtractor)
//        );


        this.traceReconstructionStage = new TraceReconstructionStage(this.systemModelRepository, TimeUnit.MILLISECONDS, true, Long.MAX_VALUE);

        this.executionRecordTransformationStage = new ExecutionRecordTransformationStage(this.systemModelRepository);
        executionRecordTransformationStage.declareActive();

        this.sequenceDiagramFilter = new SequenceDiagrammFilter(this.systemModelRepository, parameters.getModelPath(), parameters.getUseCaseName() );
    }

}
