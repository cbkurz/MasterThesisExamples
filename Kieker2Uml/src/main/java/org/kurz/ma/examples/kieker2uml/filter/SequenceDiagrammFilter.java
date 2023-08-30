package org.kurz.ma.examples.kieker2uml.filter;

import kieker.analysis.plugin.trace.AbstractMessageTraceProcessingFilter;
import kieker.common.util.signature.Signature;
import kieker.model.repository.SystemModelRepository;
import kieker.model.system.model.AbstractMessage;
import kieker.model.system.model.AllocationComponent;
import kieker.model.system.model.AssemblyComponent;
import kieker.model.system.model.MessageTrace;
import kieker.model.system.model.SynchronousCallMessage;
import kieker.model.system.model.SynchronousReplyMessage;
import org.apache.commons.io.FileUtils;
import org.kurz.ma.examples.kieker2uml.uml.Lifeline;
import org.kurz.ma.examples.kieker2uml.uml.Lifeline.LifelineType;
import org.kurz.ma.examples.kieker2uml.uml.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

import static java.lang.String.format;
import static kieker.model.repository.AllocationRepository.ROOT_ALLOCATION_COMPONENT;

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
        LOGGER.debug("Successfully received MessageTrace: " + mt.getTraceId());


        final List<AbstractMessage> messages = mt.getSequenceAsVector();

        final Set<Lifeline> lifelines = getLifelines(messages);

        toFile("TraceId: " + mt.getTraceId());
        toFile(format("Total number of messages: %s", messages.size()));
//        toFile(format("Total elapsed time for Trace Id %s: %s", mt.getTraceId(), (mt.getEndTimestamp() - mt.getStartTimestamp()) / 1000.0));
        toFile(format("Lifelines found: %s", lifelines.size()));
        toFile(format("Outgoing Messages found in Lifelines: %s", lifelines.stream().mapToLong(l -> l.getMessagesOutgoing().size()).sum()));
        toFile(format("Incoming Messages found in Lifelines: %s", lifelines.stream().mapToLong(l -> l.getMessagesIncoming().size()).sum()));
        toFile("\n");
    }

    private Set<Lifeline> getLifelines(final List<AbstractMessage> messages) {
        final Set<Lifeline> lifelines = new TreeSet<>();

        final AllocationComponent root = ROOT_ALLOCATION_COMPONENT;
        final Lifeline actor = new Lifeline((long) root.getId(), LifelineType.ACTOR, root.getIdentifier());
        lifelines.add(actor);

        Lifeline lastReceiver = null;
        boolean first = true;
        for (final AbstractMessage message : messages) {
            final Lifeline senderLifeline = getLifeline(lifelines, message.getSendingExecution().getAllocationComponent().getAssemblyComponent());
            final Lifeline receiverLifeline = getLifeline(lifelines, message.getReceivingExecution().getAllocationComponent().getAssemblyComponent());

            if (first) {
                actor.messageOutgoing(senderLifeline, Message.MessageType.ASYNCHRONOUS, "'Entry'");
                first = false;
            }

            final Message.MessageType messageType = getMessageType(message, first);
            senderLifeline.messageOutgoing(receiverLifeline, messageType, getMessageLabel(message));
            lastReceiver = receiverLifeline;
        }
        if (Objects.nonNull(lastReceiver)) {
            lastReceiver.messageOutgoing(actor, Message.MessageType.ASYNCHRONOUS_REPLY, "Finished");
        }
        return lifelines;
    }

    private String  getMessageLabel(final AbstractMessage me) {
        final Signature sig = me.getReceivingExecution().getOperation().getSignature();
        final String params = String.join(", ", sig.getParamTypeList());
        return sig.getName() + '(' + params + ')';
    }

    private Message.MessageType getMessageType(final AbstractMessage message, final boolean first) {
        if (first) {
            return Message.MessageType.ASYNCHRONOUS;
        }
        if (message instanceof SynchronousCallMessage) {
            return Message.MessageType.SYNCHRONOUS;
        }
        if (message instanceof SynchronousReplyMessage) {
            return Message.MessageType.SYNCHRONOUS_REPLY;
        }
        throw new RuntimeException("Unexpected message type of: " + message.getClass());
    }

    private static Lifeline getLifeline(final Set<Lifeline> lifelines, final AssemblyComponent component) {
        final Lifeline lifeline = new Lifeline((long) component.getId(), LifelineType.OBJECT, component.getIdentifier());
        if (lifelines.contains(lifeline)) {
            return lifelines.stream().filter(l -> l.compareTo(lifeline) == 0).findFirst().get();
        }
        lifelines.add(lifeline);
        return lifeline;
    }

    private String assemblyComponentLabel(final AssemblyComponent component) {
        final String assemblyComponentName = component.getName();
        final String componentTypePackagePrefx = component.getType().getPackageName();
        final String componentTypeIdentifier = component.getType().getTypeName();

        final StringBuilder strBuild = new StringBuilder(assemblyComponentName).append(':');
        strBuild.append(componentTypePackagePrefx).append('.');
        strBuild.append(componentTypeIdentifier);
        return strBuild.toString();
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
