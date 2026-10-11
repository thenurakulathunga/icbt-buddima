package com.printxpress.app.data.model.enums;

/**
 * Subject of a print guideline, stored in {@code guidelines.topic} (FR-5.1).
 *
 * @since 1.0
 */
public enum GuidelineTopic {
    /** Accepted file formats. */
    FILE_FORMAT,
    /** Resolution and DPI. */
    RESOLUTION,
    /** CMYK versus RGB colour. */
    COLOUR,
    /** Bleed and safe margins. */
    BLEED,
    /** Fonts and outlining. */
    FONTS;

    /**
     * Parses a stored value.
     *
     * @param value the column text
     * @return the topic, or {@link #FILE_FORMAT} for unknown text
     */
    public static GuidelineTopic from(String value) {
        try {
            return value == null ? FILE_FORMAT : valueOf(value);
        } catch (IllegalArgumentException e) {
            return FILE_FORMAT;
        }
    }
}
