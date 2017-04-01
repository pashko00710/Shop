package me.uptop.mvpgoodpractice.data.network.res;

import com.squareup.moshi.Json;

import java.util.List;

import me.uptop.mvpgoodpractice.data.network.res.models.Comments;

public class ProductRes {
    @Json(name = "_id")
    private String id;
    private int remoteId;
    private String productName;
    private String imageUrl;
    private String description;
    private int price;
    private float raiting;
    private boolean active;
    public List<Comments> comments = null;

    public List<Comments> getComments() {
        return comments;
    }

    public String getId() {
        return id;
    }

    public int getRemoteId() {
        return remoteId;
    }

    public String getProductName() {
        return productName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public int getPrice() {
        return price;
    }

    public float getRaiting() {
        return raiting;
    }

    public boolean isActive() {
        return active;
    }
}
