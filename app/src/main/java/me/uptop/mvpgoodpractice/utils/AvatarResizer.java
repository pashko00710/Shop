package me.uptop.mvpgoodpractice.utils;

import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class AvatarResizer {

    private Bitmap bitmap;

    public AvatarResizer(String encodeString) {
        bitmap = StringToBitMap(encodeString);
        bitmap = resizeAvatar(bitmap);
        bitmapToFile(bitmap);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void bitmapToFile(Bitmap bitmap) {
        OutputStream os = null;
        try {
            os = new FileOutputStream(String.valueOf(bitmap));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Bitmap resizeAvatar(Bitmap b) {
        byte[] imageAsBytes=null;

        b = BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length);
//        profileImage.setImageBitmap(Bitmap.createScaledBitmap(b, 120, 120, false));
        return b;
    }

    public Bitmap StringToBitMap(String encodedString) {
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public File saveBitmap(Bitmap bitmap, String path) {
        File file = null;
        if (bitmap != null) {
            file = new File(LocalStorageAvatar.getPath(MvpAuthApplication.getContext(), Uri.parse(path)));
            try {
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(path); //here is set your file path where you want to save or also here you can set file object directly

                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream); // bitmap is your Bitmap instance, if you want to compress it you can compress reduce percentage
                    // PNG is a lossless format, the compression factor (100) is ignored
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) {
                            outputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return file;
    }


}
