package com.leetcode;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LongestChunkedPalindromeDecompositionTest {

    private final LongestChunkedPalindromeDecomposition solution =
            new LongestChunkedPalindromeDecomposition();

    @ParameterizedTest(name = "{0}")
    @MethodSource("decompositions")
    void findsLargestChunkCount(String description, String text, int expected) {
        assertEquals(expected, solution.longestDecomposition(text));
    }

    @Test
    void rollingHashProducesTheSameValueInBothDirections() {
        var forward = new RollingHash();
        var backward = new RollingHash();

        "abcdef".chars().forEach(character -> forward.append((char) character));
        new StringBuilder("abcdef").reverse().chars()
                .forEach(character -> backward.prepend((char) character));

        assertEquals(forward.value(), backward.value());
    }

    private static Stream<Arguments> decompositions() {
        return Stream.of(
                Arguments.of("first example", "ghiabcdefhelloadamhelloabcdefghi", 7),
                Arguments.of("single unmatched chunk", "merchant", 1),
                Arguments.of("third example", "antaprezatepzapreanta", 11),
                Arguments.of("single character", "a", 1),
                Arguments.of("every character is a chunk", "aaaa", 4),
                Arguments.of("two matching outer chunks and a center", "volvo", 3)
        );
    }
}