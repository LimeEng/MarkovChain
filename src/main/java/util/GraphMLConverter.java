package util;

import java.io.File;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.io.ComponentNameProvider;
import org.jgrapht.io.ExportException;
import org.jgrapht.io.GraphMLExporter;

import markov.MarkovChain;
import markov.ProbabilityMapping;
import markov.TokenSequence;

public class GraphMLConverter {

    /**
     * 
     * Highly experimental and prone to breaking. Only really works with tiny
     * Markov chains. More of an educational tool than anything else.
     * 
     * The exported file can be read by other programs to visualize the graph
     * that the Markov chain is built out of.
     * 
     * .graphml extension.
     * 
     * @param chain
     *            the markov chain to be converted
     * @param file
     *            what file the results should be written to
     * @throws ExportException
     *             if something goes wrong when exporting to file
     */
    public static <T> void convertToGraphML(MarkovChain<T> chain, File file) throws ExportException {
        Map<TokenSequence<T>, ProbabilityMapping<T>> mapping = chain.getMatrix();

        DefaultDirectedWeightedGraph<TokenSequence<T>, DefaultWeightedEdge> g = new DefaultDirectedWeightedGraph<>(
                DefaultWeightedEdge.class);

        mapping.keySet()
                .stream()
                .forEach(g::addVertex);

        for (TokenSequence<T> seq : mapping.keySet()) {
            Map<T, Long> map = mapping.get(seq)
                    .getMapping();
            for (Entry<T, Long> entry : map.entrySet()) {
                TokenSequence<T> next = seq.getNext(entry.getKey());
                DefaultWeightedEdge edge = g.addEdge(seq, next);
                g.setEdgeWeight(edge, entry.getValue());
            }
        }

        ComponentNameProvider<TokenSequence<T>> vertexIdProvider = e -> e.getTokens()
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" "));

        GraphMLExporter<TokenSequence<T>, DefaultWeightedEdge> exporter = new GraphMLExporter<>();
        exporter.setVertexIDProvider(vertexIdProvider);
        exporter.setVertexLabelProvider(vertexIdProvider);
        exporter.setExportEdgeWeights(true);
        exporter.exportGraph(g, file);
    }
}
