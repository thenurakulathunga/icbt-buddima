package com.printxpress.app.core.storage;

/**
 * Immutable description of an artwork file: where it is, what it is called, its type and size.
 * <p>
 * Used both for a file freshly picked through the Storage Access Framework (the path is then a
 * content URI string) and for a copy stored in app-private storage.
 * </p>
 *
 * @since 1.0
 */
public final class DesignFile {

    /** Content URI string or absolute app-private path. */
    private final String location;
    /** Display name, e.g. logo.pdf. */
    private final String displayName;
    /** MIME type reported by the content provider. */
    private final String mimeType;
    /** Size in bytes. */
    private final long sizeBytes;

    /**
     * Creates a file description.
     *
     * @param location    content URI string or absolute path
     * @param displayName the file name to show
     * @param mimeType    the MIME type, may be null when unknown
     * @param sizeBytes   the size in bytes, or -1 when unknown
     */
    public DesignFile(String location, String displayName, String mimeType, long sizeBytes) {
        this.location = location;
        this.displayName = displayName;
        this.mimeType = mimeType;
        this.sizeBytes = sizeBytes;
    }

    /** @return the content URI string or absolute path */
    public String getLocation() {
        return location;
    }

    /** @return the display name */
    public String getDisplayName() {
        return displayName;
    }

    /** @return the MIME type, may be null */
    public String getMimeType() {
        return mimeType;
    }

    /** @return the size in bytes, or -1 when unknown */
    public long getSizeBytes() {
        return sizeBytes;
    }

    /** @return true when the file is a PDF */
    public boolean isPdf() {
        return "application/pdf".equals(mimeType);
    }

    /**
     * Returns a copy pointing at a new location.
     *
     * @param newLocation the new path
     * @return the relocated description
     */
    public DesignFile withLocation(String newLocation) {
        return new DesignFile(newLocation, displayName, mimeType, sizeBytes);
    }
}
