package voxspell_data;

/**
 * A simple enum used in a few classes to present the outcome of words. Prevents
 * Strings from having to be used, allows for switch to be used in SessionStats.
 * 3 Values representing
 * MASTERED - Correct on first go
 * FAULTED - Correct on second attempt, counted as incorrect as part of overall accuracy
 * FAILED - Incorrect after two attempts, also counted as incorrest as part of overall accuracy
 */
public enum WordStatus {
    MASTERED, FAULTED, FAILED
}