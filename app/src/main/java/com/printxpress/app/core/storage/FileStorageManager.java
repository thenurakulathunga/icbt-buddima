package com.printxpress.app.core.storage;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.OpenableColumns;

import androidx.annotation.WorkerThread;
import androidx.core.content.FileProvider;

import com.printxpress.app.core.common.Constants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;
import java.util.UUID;

/**
 * Copies files picked through the Storage Access Framework into app-private storage.
 * <p>
 * No storage permission is needed (NFR-6): the picker grants temporary read access to one URI,
 * the bytes are copied into {@code files/designs/}, and only the private path is saved in SQLite
 * (docs/01 assumption 4). All methods do I/O and must run on {@code AppExecutors.diskIO()}.
 * </p>
 *
 * @since 1.0
 */
public class FileStorageManager {

    /** Copy buffer size. */
    private static final int BUFFER = 16 * 1024;
    /** PNG quality hint (ignored by the lossless encoder). */
    private static final int PNG_QUALITY = 100;
    /** Authority suffix of the FileProvider declared in the manifest. */
    private static final String FILE_PROVIDER_SUFFIX = ".fileprovider";

    /** Application context. */
    private final Context context;

    /**
     * Creates the manager.
     *
     * @param context any context; the application context is retained
     */
    public FileStorageManager(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Reads the name, MIME type and size of a picked URI without copying it.
     *
     * @param uri the content URI
     * @return the file description
     */
    @WorkerThread
    public DesignFile describe(Uri uri) {
        ContentResolver resolver = context.getContentResolver();
        String name = uri.getLastPathSegment();
        long size = -1;
        try (Cursor cursor = resolver.query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                int sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE);
                if (nameIndex >= 0 && !cursor.isNull(nameIndex)) {
                    name = cursor.getString(nameIndex);
                }
                if (sizeIndex >= 0 && !cursor.isNull(sizeIndex)) {
                    size = cursor.getLong(sizeIndex);
                }
            }
        } catch (SecurityException | IllegalArgumentException e) {
            size = -1;
        }
        return new DesignFile(uri.toString(), name, resolver.getType(uri), size);
    }

    /**
     * Copies a picked file into the designs folder.
     *
     * @param source the picked file description (location is a content URI)
     * @return the description of the private copy
     * @throws IOException when reading or writing fails
     */
    @WorkerThread
    public DesignFile copyToDesigns(DesignFile source) throws IOException {
        return copyInto(Uri.parse(source.getLocation()), source, Constants.DESIGNS_DIR);
    }

    /**
     * Copies a picked image into the avatars folder.
     *
     * @param uri the picked image URI
     * @return the absolute path of the copy
     * @throws IOException when reading or writing fails
     */
    @WorkerThread
    public String copyAvatar(Uri uri) throws IOException {
        return copyInto(uri, describe(uri), Constants.AVATARS_DIR).getLocation();
    }

    /**
     * Deletes a file previously stored by this manager; paths outside app storage are ignored.
     *
     * @param path the absolute path
     * @return true when a file was deleted
     */
    @WorkerThread
    public boolean delete(String path) {
        if (path == null || !path.startsWith(context.getFilesDir().getAbsolutePath())) {
            return false;
        }
        File file = new File(path);
        return file.exists() && file.delete();
    }

    /**
     * Deletes every stored design and avatar, used when the account is deleted (FR-1.10).
     */
    @WorkerThread
    public void deleteAll() {
        deleteRecursively(new File(context.getFilesDir(), Constants.DESIGNS_DIR));
        deleteRecursively(new File(context.getFilesDir(), Constants.AVATARS_DIR));
    }

    /**
     * Writes a PNG into the cache folder exposed by the FileProvider and returns a shareable URI
     * (FR-5.5). An existing file with the same name is replaced.
     *
     * @param bitmap   the image
     * @param fileName the file name, e.g. {@code bleed_template.png}
     * @return the content URI
     * @throws IOException when the file cannot be written
     */
    @WorkerThread
    public Uri writeShareImage(Bitmap bitmap, String fileName) throws IOException {
        File dir = new File(context.getCacheDir(), Constants.SHARE_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException(dir.getAbsolutePath());
        }
        File file = new File(dir, fileName);
        try (OutputStream out = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, PNG_QUALITY, out);
        }
        return FileProvider.getUriForFile(context, context.getPackageName() + FILE_PROVIDER_SUFFIX, file);
    }

    /**
     * Returns a private directory, creating it when missing.
     *
     * @param name folder name under the files directory
     * @return the directory
     */
    public File directory(String name) {
        File dir = new File(context.getFilesDir(), name);
        if (!dir.exists() && !dir.mkdirs()) {
            return context.getFilesDir();
        }
        return dir;
    }

    /**
     * Streams a URI into a new file in the given folder.
     *
     * @param uri    the source URI
     * @param source the source description (for name and type)
     * @param folder the target folder name
     * @return the description of the copy
     * @throws IOException when reading or writing fails
     */
    private DesignFile copyInto(Uri uri, DesignFile source, String folder) throws IOException {
        File target = new File(directory(folder), UUID.randomUUID() + extensionOf(source));
        long written = 0;
        try (InputStream in = context.getContentResolver().openInputStream(uri);
             OutputStream out = new FileOutputStream(target)) {
            if (in == null) {
                throw new IOException("Cannot open input");
            }
            byte[] buffer = new byte[BUFFER];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                written += read;
            }
        } catch (IOException | SecurityException e) {
            if (target.exists() && !target.delete()) {
                target.deleteOnExit();
            }
            throw e instanceof IOException ? (IOException) e : new IOException(e);
        }
        return new DesignFile(target.getAbsolutePath(), source.getDisplayName(), source.getMimeType(), written);
    }

    /**
     * Picks a file extension from the MIME type.
     *
     * @param file the file description
     * @return the extension including the dot, or an empty string
     */
    private static String extensionOf(DesignFile file) {
        String mime = file.getMimeType() == null ? "" : file.getMimeType().toLowerCase(Locale.US);
        switch (mime) {
            case Constants.MIME_PDF:
                return ".pdf";
            case Constants.MIME_PNG:
                return ".png";
            case Constants.MIME_JPEG:
                return ".jpg";
            default:
                return "";
        }
    }

    /**
     * Deletes a file or directory tree.
     *
     * @param file the root
     */
    private static void deleteRecursively(File file) {
        File[] children = file.listFiles();
        if (children != null) {
            for (File child : children) {
                deleteRecursively(child);
            }
        }
        if (file.exists() && !file.delete()) {
            file.deleteOnExit();
        }
    }
}
