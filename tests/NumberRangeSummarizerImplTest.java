package numberrangesummarizer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;

class NumberRangeSummarizerImplTest {

    private final NumberRangeSummarizer summarizer = new NumberRangeSummarizerImpl();

    private String summarize(String input) {
        return summarizer.summarizeCollection(summarizer.collect(input));
    }

    @Test
    void collectParsesCommaDelimitedNumbers() {
        assertEquals(Arrays.asList(1, 3, 6, 7), summarizer.collect("1,3,6,7"));
    }

    @Test
    void collectIgnoresWhitespaceAndEmptyTokens() {
        assertEquals(Arrays.asList(1, 2, 3), summarizer.collect(" 1 , 2,, 3 ,"));
    }

    @Test
    void collectHandlesNegativeNumbers() {
        assertEquals(Arrays.asList(-2, -1, 0), summarizer.collect("-2,-1,0"));
    }

    @Test
    void collectReturnsEmptyForNullOrBlank() {
        assertTrue(summarizer.collect(null).isEmpty());
        assertTrue(summarizer.collect("").isEmpty());
        assertTrue(summarizer.collect("  ").isEmpty());
    }

    @Test
    void collectRejectsNonNumericToken() {
        IllegalArgumentException e =
                assertThrows(IllegalArgumentException.class, () -> summarizer.collect("1,a,3"));
        assertTrue(e.getMessage().contains("'a'"));
    }

    @Test
    void collectRejectsNumbersOutsideIntRange() {
        assertThrows(IllegalArgumentException.class, () -> summarizer.collect("99999999999"));
    }

    @Test
    void summarizesSampleFromSpecification() {
        assertEquals("1, 3, 6-8, 12-15, 21-24, 31",
                summarize("1,3,6,7,8,12,13,14,15,21,22,23,24,31"));
    }

    @Test
    void summarizesSingleNumber() {
        assertEquals("5", summarize("5"));
    }

    @Test
    void summarizesNoSequentialNumbers() {
        assertEquals("1, 3, 5", summarize("1,3,5"));
    }

    @Test
    void summarizesEntireInputAsOneRange() {
        assertEquals("1-5", summarize("1,2,3,4,5"));
    }

    @Test
    void summarizesPairAsRange() {
        assertEquals("1-2, 5", summarize("1,2,5"));
    }

    @Test
    void sortsUnorderedInput() {
        assertEquals("1-3, 9", summarize("9,3,1,2"));
    }

    @Test
    void removesDuplicates() {
        assertEquals("1-3", summarize("1,1,2,2,3,3"));
    }

    @Test
    void summarizesNegativeRanges() {
        assertEquals("-3--1, 2", summarize("-3,-2,-1,2"));
    }

    @Test
    void summarizesEmptyAndNullCollections() {
        assertEquals("", summarizer.summarizeCollection(Collections.<Integer>emptyList()));
        assertEquals("", summarizer.summarizeCollection(null));
    }

    @Test
    void skipsNullElements() {
        assertEquals("1-2", summarizer.summarizeCollection(Arrays.asList(1, null, 2)));
    }

    @Test
    void handlesIntegerBoundariesWithoutOverflow() {
        assertEquals("-2147483648, 2147483647",
                summarizer.summarizeCollection(Arrays.asList(Integer.MAX_VALUE, Integer.MIN_VALUE)));
        assertEquals("2147483646-2147483647",
                summarizer.summarizeCollection(Arrays.asList(Integer.MAX_VALUE, Integer.MAX_VALUE - 1)));
    }
}
