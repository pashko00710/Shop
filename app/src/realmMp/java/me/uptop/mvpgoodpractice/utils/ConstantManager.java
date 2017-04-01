package me.uptop.mvpgoodpractice.utils;

import me.uptop.mvpgoodpractice.BuildConfig;

public class ConstantManager {
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    public static final String PATTERN_EMAIL = "^[a-zA-Z_0-9]{3,}@[a-zA-Z_0-9.]{2,}\\.[a-zA-Z0-9]{2,}$";
    public static final String PATTERN_PASSWORD = "^\\S{8,}$";
    public static final String BASKET_COUNT = "BUSKET_COUNT";

    public static final String BASE_URL = "https://skba1.mgbeta.ru/api/v1/";
    public static final long MAX_CONNECTION_TIMEOUT = 5000;
    public static final long MAX_READ_TIMEOUT = 5000;
    public static final long MAX_WRITE_TIMEOUT = 5000;

//    public static final int PICK_PHOTO_FROM_GALLERY = 111;
//    public static final int PICK_PHOTO_FROM_CAMERA = 272;
//    public static final int REQUEST_READ_EXTERNAL_STORAGE = 333;
    public static final int MANAGE_DOCUMENTS = 333;


    public static final int PICK_PHOTO_FROM_GALLERY = 111;
    public static final int PICK_PHOTO_FROM_CAMERA = 222;
    public static final int REQUEST_READ_EXTERNAL_STORAGE = 333;


    public static String PHOTO_FILE_PREFIX = "IMG_";

    public static final int REQUEST_PERMISSION_CAMERA = 3000;
    public static final int REQUEST_PERMISSION_READ_EXTERNAL_STORAGE = 3001;

    public static final int REQUEST_PROFILE_PHOTO_PICKER = 1001;
    public static final int REQUEST_PROFILE_PHOTO_CAMERA = 1002;
    public static final String FILE_PROVIDER_AUTHORITY = BuildConfig.APPLICATION_ID +  ".fileprovider";
    public static final String LAST_MODIFIED_HEADER = "Last-Modified";
    public static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";


    public static String ADD_COMMENT_USERNAME = "";
    public static String ADD_COMMENT_AVATAR = "";

    public static final String REALM_USER = "pashko00710@yandex.ru";
    public static final String REALM_PASSWORD = "paragon1";
    public static final String REALM_AUTH_URL = "http://192.168.1.49:9080/auth";
    public static final String REALM_DB_URL = "realm://192.168.1.49:9080/~/default";

}
