package numberrangesummarizer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Parses a comma delimited string of integers and summarizes them, collapsing
 * runs of sequential numbers into ranges.
 *
 * <p>Example: {@code "1,3,6,7,8,12,13,14,15,21,22,23,24,31"} becomes
 * {@code "1, 3, 6-8, 12-15, 21-24, 31"}.
 *
 * <p>Behaviour notes:
 * <ul>
 *   <li>Output is sorted ascending and duplicates are removed.</li>
 *   <li>Any run of two or more sequential numbers is written as a range.</li>
 *   <li>Whitespace around tokens and empty tokens are ignored.</li>
 *   <li>Non-numeric tokens cause an {@link IllegalArgumentException}.</li>
 *   <li>{@code null} input yields an empty result.</li>
 * </ul>
 */
public class NumberRangeSummarizerImpl implements NumberRangeSummarizer {

    private static final Pattern DELIMITER = Pattern.compile(",");

    @Override
    public Collection<Integer> collect(String input) {
        if (input == null) {
            return new ArrayList<>();
        }
        return Arrays.stream(DELIMITER.split(input))
                .map(String::trim)
                .filter(token -> !token.isEmpty())
                .map(NumberRangeSummarizerImpl::parse)
                .collect(Collectors.toList());
    }

    @Override
    public String summarizeCollection(Collection<Integer> input) {
        if (input == null) {
            return "";
        }
        TreeSet<Integer> sorted = input.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(TreeSet::new));

        List<String> parts = new ArrayList<>();
        Integer start = null;
        Integer previous = null;
        for (Integer current : sorted) {
            if (previous != null && (long) current - previous != 1) {
                parts.add(format(start, previous));
                start = current;
            } else if (start == null) {
                start = current;
            }
            previous = current;
        }
        if (start != null) {
            parts.add(format(start, previous));
        }
        return String.join(", ", parts);
    }

    private static String format(int start, int end) {
        return start == end ? String.valueOf(start) : start + "-" + end;
    }

    private static Integer parse(String token) {
        try {
            return Integer.valueOf(token);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number: '" + token + "'", e);
        }
    }
}
