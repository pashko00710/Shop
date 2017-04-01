package me.uptop.mvpgoodpractice.data.network.res;

import android.util.Log;

import com.google.gson.internal.bind.util.ISO8601Utils;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import me.uptop.mvpgoodpractice.data.network.res.models.Comments;

public class CommentJsonAdapter {
    private static final String TAG = CommentJsonAdapter.class.getSimpleName();

    @FromJson
    Comments commentResFromJson(CommentJson commentJson) {
//        Date date;
//        if(commentJson.commentDate != null) {
//            date = parseToDate(commentJson.commentDate);
//        } else {
//            date = new Date();
//        }

        return new Comments(
                commentJson.id,
                commentJson.avatar,
                commentJson.userName,
                commentJson.rating,
                commentJson.commentDate != null ? parseToDate(commentJson.commentDate) : new Date(),
                commentJson.comment,
                commentJson.active);
    }

    @ToJson
    CommentJson commentResToJson(Comments commentRes) {
        //DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy, HH:mm:ss", Locale.ENGLISH);
        DateFormat dateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        CommentJson commentJson = new CommentJson();
        commentJson.id = commentRes.getId();
        commentJson.avatar = commentRes.getAvatar();
        commentJson.userName = commentRes.getUserName();
        commentJson.rating =  commentRes.getRating();
        commentJson.commentDate = dateFormat.format(commentRes.getCommentDate()); //Wed, 15 Nov 2016 04:58:08 GMT
        commentJson.comment = commentRes.getComment();
        commentJson.active = commentRes.isActive();

        return commentJson;
    }

    private Date parseToDate(String date) {
        try {
            return ISO8601Utils.parse(date, new ParsePosition(0));
        } catch (ParseException e) {
            e.printStackTrace();
            Log.e(TAG, "Date parsing error");
        }
        return null;
    }
}
