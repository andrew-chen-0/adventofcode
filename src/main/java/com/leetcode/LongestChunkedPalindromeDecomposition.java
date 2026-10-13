package com.leetcode;

import java.util.Objects;

public class LongestChunkedPalindromeDecomposition {

    public int longestDecomposition(String text) {
        Objects.requireNonNull(text, "text must not be null");

        var leftHash = new RollingHash();
        var rightHash = new RollingHash();
        int chunks = 0;
        int candidateLength = 0;
        int leftStart = 0;
        int left = 0;
        int right = text.length() - 1;

        while (left < right) {
            leftHash.append(text.charAt(left));
            rightHash.prepend(text.charAt(right));
            candidateLength++;

            if (leftHash.value() == rightHash.value()
                    && text.regionMatches(leftStart, text, right, candidateLength)) {
                chunks += 2;
                candidateLength = 0;
                leftHash.reset();
                rightHash.reset();
                leftStart = left + 1;
            }

            left++;
            right--;
        }

        return chunks + (left == right || candidateLength > 0 ? 1 : 0);
    }
}