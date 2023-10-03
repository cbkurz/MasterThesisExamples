package org.kurz.ma.examples.kieker2uml.filter;

import kieker.analysis.plugin.trace.AbstractMessageTraceProcessingFilter;
import kieker.common.util.signature.Signature;
import kieker.model.repository.SystemModelRepository;
import kieker.model.system.model.AbstractMessage;
import kieker.model.system.model.AssemblyComponent;
import kieker.model.system.model.MessageTrace;
import kieker.model.system.model.SynchronousCallMessage;
import kieker.model.system.model.SynchronousReplyMessage;
import org.apache.commons.io.FileUtils;
import org.eclipse.uml2.uml.Model;
import org.kurz.ma.examples.kieker2uml.model.Lifeline;
import org.kurz.ma.examples.kieker2uml.model.Lifeline.LifelineType;
import org.kurz.ma.examples.kieker2uml.model.Message;
import org.kurz.ma.examples.kieker2uml.xml.SimpleSequenceDiagramWriter;
import org.kurz.ma.examples.kieker2uml.xml.XmlSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.String.format;
import static org.kurz.ma.examples.kieker2uml.uml.Uml2Support.addInteractionToUseCase;
import static org.kurz.ma.examples.kieker2uml.uml.Uml2Support.loadModel;
import static org.kurz.ma.examples.kieker2uml.uml.Uml2Support.saveModel;

public class SequenceDiagrammFilter extends AbstractMessageTraceProcessingFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(SequenceDiagrammFilter.class);
    private final Path modelPath;
    private final String useCaseName;

    /**
     * @param repository model repository
     * @param modelPath  the path to the file to which the sequence diagramm is written.
     * @param modelPath
     */
    public SequenceDiagrammFilter(final SystemModelRepository repository, final Path modelPath, final String useCaseName) {
        super(repository);
        this.modelPath = modelPath;
        this.useCaseName = useCaseName;
    }

    @Override
    protected void execute(final MessageTrace mt) throws Exception {
        writeMessageTrace(mt);
        this.reportSuccess(mt.getTraceId());
    }

    private void writeMessageTrace(final MessageTrace mt) {
        LOGGER.debug("Successfully received MessageTrace: " + mt.getTraceId());


        final List<AbstractMessage> messages = mt.getSequenceAsVector();

        final Set<Lifeline> lifelines = getLifelines(messages);

        // UML
        final Model model = loadModel(modelPath);
        addInteractionToUseCase(model, mt, useCaseName);
        saveModel(model, modelPath);

        // simplified Model
        liflinesToXml(mt.getTraceId(), lifelines);


//        final Interaction interaction = (Interaction) model.getPackagedElements().get(0);
        // logging
        toFile("TraceId: " + mt.getTraceId());
        toFile(format("Total number of messages: %s", messages.size()));
        toFile(format("Total elapsed time for Trace Id %s: %s ms", mt.getTraceId(), (mt.getEndTimestamp() - mt.getStartTimestamp()) / 1_000_000.0));
        toFile(format("Lifelines found: %s", lifelines.size()));
        toFile(format("Outgoing Messages found in Lifelines: %s", lifelines.stream().mapToLong(l -> l.getMessagesOutgoing().size()).sum()));
        toFile(format("Incoming Messages found in Lifelines: %s", lifelines.stream().mapToLong(l -> l.getMessagesIncoming().size()).sum()));
//        toFile(format("UML - Lifelines found: %s", interaction.getLifelines()));
//        toFile(format("UML - Total number of Messages found: %s", interaction.getMessages().size()));
        toFile("\n");
    }


    private void liflinesToXml(final Long traceId, final Set<Lifeline> lifelines) {
        final Path pathToFile = Paths.get("output").resolve(String.format("sequencediagram-%s.xml", traceId.toString()));
        try {
            FileUtils.createParentDirectories(pathToFile.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        XmlSupport.writeXml(pathToFile, new SimpleSequenceDiagramWriter(lifelines));
    }

    private Set<Lifeline> getLifelines(final List<AbstractMessage> messages) {
        final Set<Lifeline> lifelines = new TreeSet<>();

        for (final AbstractMessage message : messages) {
            final Lifeline senderLifeline = getLifeline(lifelines, message.getSendingExecution().getAllocationComponent().getAssemblyComponent());
            final Lifeline receiverLifeline = getLifeline(lifelines, message.getReceivingExecution().getAllocationComponent().getAssemblyComponent());

            final Message.MessageType messageType = getMessageType(message);
            senderLifeline.messageOutgoing(receiverLifeline, messageType, getMessageLabel(message));
        }

        return lifelines;
    }

    private String getMessageLabel(final AbstractMessage me) {
        final Signature sig = me.getReceivingExecution().getOperation().getSignature();
        final String params = String.join(", ", sig.getParamTypeList());
        return sig.getName() + '(' + params + ')';
    }

    private Message.MessageType getMessageType(final AbstractMessage message) {
        if (message instanceof SynchronousCallMessage) {
            return Message.MessageType.SYNCHRONOUS;
        }
        if (message instanceof SynchronousReplyMessage) {
            return Message.MessageType.SYNCHRONOUS_REPLY;
        }
        throw new RuntimeException("Unexpected message type of: " + message.getClass());
    }

    private static Lifeline getLifeline(final Set<Lifeline> lifelines, final AssemblyComponent component) {
        final Lifeline lifeline = new Lifeline((long) component.getId(), LifelineType.OBJECT, assemblyComponentLabel(component)); // TODO: braucht man wirklich den Namen oder reicht auch .getIdentifier() ?
        if (lifelines.contains(lifeline)) {
            // A simple "return lifeline" is not possible the returned object has to be the original one and not the newly created one
            return lifelines.stream().filter(l -> l.compareTo(lifeline) == 0).findFirst().get();
        }
        lifelines.add(lifeline);
        return lifeline;
    }

    private static String assemblyComponentLabel(final AssemblyComponent component) {
        final String assemblyComponentName = component.getName();
        final String componentTypePackagePrefx = component.getType().getPackageName();
        final String componentTypeIdentifier = component.getType().getTypeName();

        final String strBuild = assemblyComponentName + ':' +
                componentTypePackagePrefx + '.' +
                componentTypeIdentifier;
        return strBuild;
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
