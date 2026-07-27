package com.example.portalnexus.utils;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class PhotoStorageHelper {
    private static final String TAG = "PhotoStorageHelper";
    private static final String PHOTOS_DIR = "employee_photos";

    public static String savePhotoLocally(Context context, String sourceUri, int employeeId) {
        if (sourceUri == null || sourceUri.isEmpty()) return null;

        File photosDir = new File(context.getFilesDir(), PHOTOS_DIR);
        if (!photosDir.exists()) {
            photosDir.mkdirs();
        }

        File destFile = new File(photosDir, "emp_" + employeeId + ".jpg");
        try {
            Uri uri = Uri.parse(sourceUri);
            File sourceFile = new File(uri.getPath());

            if (sourceFile.exists()) {
                copyFile(sourceFile, destFile);
                return destFile.getAbsolutePath();
            } else {
                Log.e(TAG, "Source file does not exist: " + sourceUri);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving photo locally: " + e.getMessage());
        }
        return null;
    }

    public static String getLocalPhotoPath(Context context, int employeeId) {
        File photosDir = new File(context.getFilesDir(), PHOTOS_DIR);
        File photoFile = new File(photosDir, "emp_" + employeeId + ".jpg");
        if (photoFile.exists()) {
            return photoFile.getAbsolutePath();
        }
        return null;
    }

    public static void deletePhotoLocally(Context context, int employeeId) {
        File photosDir = new File(context.getFilesDir(), PHOTOS_DIR);
        File photoFile = new File(photosDir, "emp_" + employeeId + ".jpg");
        if (photoFile.exists()) {
            photoFile.delete();
        }
    }

    private static void copyFile(File sourceFile, File destFile) throws IOException {
        try (FileChannel sourceChannel = new FileInputStream(sourceFile).getChannel();
             FileChannel destChannel = new FileOutputStream(destFile).getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
    }
}
