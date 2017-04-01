package me.uptop.mvpgoodpractice.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.uptop.mvpgoodpractice.R;
import me.uptop.mvpgoodpractice.ui.activities.RootActivity;

import static me.uptop.mvpgoodpractice.utils.ConstantManager.PICK_PHOTO_FROM_CAMERA;
import static me.uptop.mvpgoodpractice.utils.ConstantManager.PICK_PHOTO_FROM_GALLERY;

public class AvatarHelper {

    static Intent intent;
    private static final String TAG = AvatarHelper.class.getSimpleName();

    public static void checkReadExternalStoragePermission(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            checkPermissionsCamera(activity);
            checkPermissionsWriteExternalStorage(activity);
            checkPermissionsReadExternalStorage(activity);
            checkPermissionsManageDocuments(activity);
        }
    }

    private static void checkPermissionsReadExternalStorage(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 255);
        }
    }

    private static void checkPermissionsWriteExternalStorage(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 255);
        }
    }

    private static void checkPermissionsManageDocuments(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.MANAGE_DOCUMENTS) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.MANAGE_DOCUMENTS}, 255);
        }
    }

    private static void checkPermissionsCamera(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 255);
        }
    }

    public static void pickAvatarFromGallery(Activity activity) {

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent.setType("image/*");
            activity.startActivityForResult(intent, PICK_PHOTO_FROM_GALLERY);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            activity.startActivityForResult(intent, PICK_PHOTO_FROM_GALLERY);
        }
    }

    public static void pickAvatarFromCamera(RootActivity activity) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            File avatarFile = null;
            try {
                avatarFile = AvatarHelper.createImageFile(activity);
            } catch (IOException e) {
                Log.e(TAG, "Create avatar file Error");
                activity.showMessage(activity.getString(R.string.error_message));
                e.printStackTrace();
            }
            if (avatarFile != null) {
                Uri uri = FileProvider.getUriForFile(activity,
                        "me.uptop.mvpgoodpractice.fileprovider",
                        avatarFile);
//                activity.setAvatarURI(uri);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                activity.startActivityForResult(intent, PICK_PHOTO_FROM_CAMERA);
            }
        } else {
            Log.e(TAG, "resolve Camera Activity Error");
            activity.showMessage(activity.getString(R.string.error_message));
        }
    }

    private static File createImageFile(Context context) throws IOException {
        // Create an image file name
        String timeStamp =
                new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
    }
}
