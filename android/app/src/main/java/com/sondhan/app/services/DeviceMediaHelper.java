package com.sondhan.app.services;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates Content Provider interaction by querying the system
 * MediaStore ContentProvider (MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
 * via ContentResolver to retrieve shared device media.
 */
public class DeviceMediaHelper {
    private static final String TAG = "DeviceMediaHelper";

    public static class MediaItem {
        private final long id;
        private final Uri uri;
        private final String displayName;
        private final long size;

        public MediaItem(long id, Uri uri, String displayName, long size) {
            this.id = id;
            this.uri = uri;
            this.displayName = displayName;
            this.size = size;
        }

        public Uri getUri() { return uri; }
        public String getDisplayName() { return displayName; }
        public long getSize() { return size; }
    }

    /**
     * Queries the system MediaStore ContentProvider for recent device screenshots / images.
     */
    public static List<MediaItem> queryRecentImages(Context context, int limit) {
        List<MediaItem> items = new ArrayList<>();
        ContentResolver resolver = context.getContentResolver();

        Uri collection = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
                ? MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
                : MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.SIZE
        };

        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC LIMIT " + limit;

        try (Cursor cursor = resolver.query(collection, projection, null, null, sortOrder)) {
            if (cursor != null) {
                int idCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
                int nameCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
                int sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE);

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idCol);
                    String name = cursor.getString(nameCol);
                    long size = cursor.getLong(sizeCol);
                    Uri contentUri = ContentUris.withAppendedId(collection, id);
                    items.add(new MediaItem(id, contentUri, name, size));
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Content Provider query failed (may need permission): " + e.getMessage());
        }

        return items;
    }
}

