package com.pipeline.enums;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

/**
 * Enum for time-based filters for recent decisions
 */
public enum TimeFilter {
    LAST_24_HOURS("Last 24 Hours", 1, ChronoUnit.DAYS),
    LAST_7_DAYS("Last 7 Days", 7, ChronoUnit.DAYS),
    LAST_1_MONTH("Last 1 Month", 30, ChronoUnit.DAYS);

    private final String displayName;
    private final long amount;
    private final ChronoUnit unit;

    TimeFilter(String displayName, long amount, ChronoUnit unit) {
        this.displayName = displayName;
        this.amount = amount;
        this.unit = unit;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Get the start time for this filter (from now minus the time period)
     */
    public Instant getStartTime() {
        return Instant.now().minus(amount, unit);
    }

    /**
     * Parse string to TimeFilter enum
     */
    public static TimeFilter fromString(String filter) {
        try {
            return TimeFilter.valueOf(filter.toUpperCase());
        } catch (IllegalArgumentException e) {
            return LAST_7_DAYS; // Default
        }
    }
}
