package util;

import java.io.PrintStream;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import markov.MarkovChain;
import markov.ProbabilityMapping;
import markov.TokenSequence;

public class MatrixPrinter {

    public static <T> void printAsMatrix(MarkovChain<T> chain, PrintStream out) {
        Map<TokenSequence<T>, ProbabilityMapping<T>> matrix = chain.getMatrix();

        int maxTokenLength = matrix.keySet()
                .stream()
                .map(TokenSequence::getTokens)
                .flatMap(List::stream)
                .map(String::valueOf)
                .mapToInt(String::length)
                .max()
                .getAsInt();

        int maxValueLength = matrix.values()
                .stream()
                .map(ProbabilityMapping::getMapping)
                .map(Map::values)
                .flatMap(Collection::stream)
                .map(String::valueOf)
                .mapToInt(String::length)
                .max()
                .getAsInt();

        int maxLength = Math.max(maxTokenLength, maxValueLength);

        List<Entry<TokenSequence<T>, ProbabilityMapping<T>>> table = matrix.entrySet()
                .stream()
                .collect(Collectors.toList());

        List<TokenSequence<T>> rows = table.stream()
                .map(Entry::getKey)
                .collect(Collectors.toList());

        List<T> cols = table.stream()
                .map(Entry::getValue)
                .map(ProbabilityMapping::getMapping)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .map(Entry::getKey)
                .distinct() // Should this be here?
                .collect(Collectors.toList());

        int rowHorizontalSpace = 2;
        int colHorizontalSpace = 4;
        int order = chain.getOrder();
        
        String horizontalDivider = "-";
        String verticalDivider = "|";

        StringBuilder sb = new StringBuilder();

        int whitespaceAtBeginLength = rowHorizontalSpace + maxTokenLength * order + order;
        sb.append(leftPad(" ", whitespaceAtBeginLength));
        sb.append(verticalDivider);

        int lengthOfHorizontalDivider = whitespaceAtBeginLength + verticalDivider.length();
        int length = maxLength + colHorizontalSpace;
        for (int i = 0; i < cols.size(); i++) {
            String value = cols.get(i)
                    .toString();
            sb.append(leftPad(value, length));
            lengthOfHorizontalDivider += length;
        }
        out.println(sb.toString());
        sb.setLength(0);
        sb.append(replicate(horizontalDivider, lengthOfHorizontalDivider));
        out.println(sb.toString());
        sb.setLength(0);
        for (int i = 0; i < rows.size(); i++) {
            TokenSequence<T> row = rows.get(i);
            String tokens = row.getTokens()
                    .stream()
                    .map(String::valueOf)
                    .collect(Collectors.joining(" "));

            sb.append(rightPad(tokens, whitespaceAtBeginLength));
            sb.append(verticalDivider);
            for (int k = 0; k < cols.size(); k++) {
                T col = cols.get(k);
                Long number = matrix.get(row)
                        .getMapping()
                        .get(col);
                String value = "0";
                if (number != null) {
                    value = number.toString();
                }
                sb.append(leftPad(value, length));
            }
            out.println(sb.toString());
            sb.setLength(0);
        }
    }

    private static String leftPad(String text, int outputLength) {
        int toPad = outputLength - text.length();
        if (toPad < 1) {
            return text;
        }
        return replicate(" ", toPad) + text;
    }

    private static String rightPad(String text, int outputLength) {
        int toPad = outputLength - text.length();
        if (toPad < 1) {
            return text;
        }
        return text + replicate(" ", toPad);
    }

    private static String replicate(String text, int times) {
        return String.join("", Collections.nCopies(times, text));
    }
}
