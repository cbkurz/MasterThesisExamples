package de.kurz.ma.dotToXml.dot;

import de.kurz.ma.dotToXml.dot.model.Edge;
import de.kurz.ma.dotToXml.dot.model.Graph;
import de.kurz.ma.dotToXml.dot.model.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

public class GraphFactory {

    public static final Pattern PATTERN_FROM_TO = Pattern.compile("(\\d+)->(\\d+)(\\[.*])?");
    public static final Pattern PATTERN_NODE = Pattern.compile("(\\d+)(\\[.*])?");
    final static public String NAMESPACE_CALL_TREE = "de.kurz.ma.model.CallTree";
    public static final Pattern PATTERN_ATTRIBUTES = Pattern.compile("\\[(.*)]");

    public static Graph createGraph(final Path inputPath) throws IOException {
        final Graph graph = new Graph();

        try (final BufferedReader br = new BufferedReader(new FileReader(inputPath.toFile()))){
            String line = br.readLine();
            if (!line.equals("digraph G {")) {
                throw new IllegalArgumentException("The file does not contain a digraph, please check the file for correctness.");
            }
            line = br.readLine();
            while(!isNull(line)) {
                if (isNode(line)) {
                    graph.addNode(createNode(line));
                }
                if (isEdge(line)) {
                    graph.addEdge(createEdge(graph.getNodes(), line));
                }

                line = br.readLine();
            }
        }

        return graph;
    }

    private static Edge createEdge(final Set<Node> nodes, final String line) {
        final Node from = getFrom(nodes, line);
        final Node to = getTo(nodes, line);
        final Map<String, String> attributes = getAttributes(line);
        return new Edge(attributes, from, to);
    }

    private static Node getFrom(final Set<Node> nodes, final String line) {
        final Matcher matcher = PATTERN_FROM_TO.matcher(line);
        if (!matcher.find()) {
            throw new IllegalArgumentException("From-To was not found successfully for line: " + line);
        }
        final Integer from = Integer.valueOf(matcher.group(1));
        return nodes.stream()
                .filter(n -> n.getNumber().equals(from))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find 'from' Node with number: " + from + "\nline: " + line));
    }

    private static Node getTo(final Set<Node> nodes, final String line) {
        final Matcher matcher = PATTERN_FROM_TO.matcher(line);
        if (!matcher.find()) {
            throw new IllegalArgumentException("From-To was not found successfully for line: " + line);
        }
        final Integer to = Integer.valueOf(matcher.group(2));
        return nodes.stream()
                .filter(n -> n.getNumber().equals(to))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Cannot find 'to' Node with number: " + to + "\nline: " + line));
    }

    private static boolean isEdge(final String line) {
        return line.matches("\\d+->\\d+(\\[.*])?");
    }

    private static Node createNode(final String line) {
        final Integer number = getNumberForNode(line);
        Map<String, String> attributes = getAttributes(line);
        return new Node(number, attributes);
    }

    private static Map<String, String> getAttributes(final String line) {
        final HashMap<String, String> m = new HashMap<>();
        final Matcher attributesMatcher = PATTERN_ATTRIBUTES.matcher(line);
        if (!attributesMatcher.find()) { // No attributes found.
            return m;
        }

        final String[] attributes = attributesMatcher.group(1).split(",");
        for (final String attribute : attributes) {
            final String[] split = attribute.split("=");
            final String key = split[0].trim();
            final String value = split[1].trim().replace("\"", "");
            m.put(key, value);
        }
        return m;
    }

    private static Integer getNumberForNode(final String line) {
        final Matcher matcher = PATTERN_NODE.matcher(line);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Node could not be found in line: " + line);
        }
        return Integer.valueOf(matcher.group(1));
    }

    private static boolean isNode(final String line) {
        return line.matches("\\d+\\[.*];");
    }
}
