# Number Range Summarizer

Implements the `NumberRangeSummarizer` interface: takes a comma delimited list of
integers and produces a summarized string, grouping sequential numbers into ranges.

```
"1,3,6,7,8,12,13,14,15,21,22,23,24,31"  ->  "1, 3, 6-8, 12-15, 21-24, 31"
```

## Project layout

```
src/NumberRangeSummarizer.java        provided interface
src/NumberRangeSummarizerImpl.java    implementation (Java 8 streams)
tests/NumberRangeSummarizerImplTest.java   JUnit 5 unit tests
Makefile                              build and test with javac
```

## Running the tests

Requires a JDK (8 or newer) and `make`.

```
make test
```

The first run downloads the JUnit 5 console launcher into `lib/` using `curl`.
`make clean` removes the compiled output.

## Design and assumptions

- `collect` parses the input into a `List<Integer>` in the order given.
- `summarizeCollection` sorts the numbers, removes duplicates, then walks them once
  (O(n log n) overall, dominated by the sort) to build the ranges.
- Any run of two or more sequential numbers becomes a range (`1,2` -> `1-2`).
- Whitespace around tokens and empty tokens (e.g. `1,,2`) are ignored.
- A non-numeric token, or a number outside the `int` range, throws `IllegalArgumentException`.
- `null` input gives an empty collection / empty string; `null` elements are skipped.
- Negative numbers are supported (`-3,-2,-1` -> `-3--1`).
- Gaps are computed using `long` arithmetic, so `Integer.MIN_VALUE` and
  `Integer.MAX_VALUE` do not overflow.
