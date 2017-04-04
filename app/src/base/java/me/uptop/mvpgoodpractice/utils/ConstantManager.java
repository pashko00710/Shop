package me.uptop.mvpgoodpractice.utils;

public class ConstantManager {
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    public static final String PATTERN_EMAIL = "^[a-zA-Z_0-9]{3,}@[a-zA-Z_0-9.]{2,}\\.[a-zA-Z0-9]{2,}$";
    public static final String PATTERN_PASSWORD = "^\\S{8,}$";
    public static final String BASKET_COUNT = "BUSKET_COUNT";


//    public static final String BASE_URL = "https://skba1.mgbeta.ru/api/v1/";
    public static final String BASE_URL = "https://private-8967d4-middleappskillbranch.apiary-mock.com";
    public static final String FB_BASE_URL = "https://graph.facebook.com/v2.8/";
    public static final String VK_BASE_URL = "https://api.vk.com/method/";
    public static final String TW_BASE_URL = "https://api.twitter.com/1.1/users/";
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
    public static final String FILE_PROVIDER_AUTHORITY = "me.uptop.mvpgoodpractice.fileprovider";
    public static final String LAST_MODIFIED_HEADER = "Last-Modified";
    public static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";


    public static String ADD_COMMENT_USERNAME = "";
    public static String ADD_COMMENT_AVATAR = "";

    public static final int MIN_CONSUMER_COUNT = 1;
    public static final int MAX_CONSUMER_COUNT = 3;
    public static final int LOAD_FACTOR = 3;
    public static final int KEEP_ALIVE = 120;
    public static final long INITIAL_BACK_OFF_IN_MS = 1000;
    public static final int UPDATE_DATA_INTERVAL = 30;
    public static final int RETRY_REQUEST_COUNT = 5;
    public static final int RETRY_REQUEST_BASE_DELAY = 500;
}

