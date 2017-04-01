package me.uptop.mvpgoodpractice.data.storage.dto;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

import me.uptop.mvpgoodpractice.data.network.res.ProductRes;
import me.uptop.mvpgoodpractice.data.network.res.models.Comments;
import me.uptop.mvpgoodpractice.data.storage.realm.ProductRealm;

public class ProductDto implements Parcelable {
    private int id;
    private String productName;
    private String imageUrl;
    private String description;
    private int price;
    private int count;
    private boolean favorite;
    private List<Comments> comments;

    public List<Comments> getComments() {
        return comments;
    }

    public void setComments(List<Comments> comments) {
        this.comments = comments;
    }

    public ProductDto() {
    }

    public ProductDto(int id, String productName, String imageUrl, String description, int price, int count, boolean favorite, List<Comments> comments) {
        this.id = id;
        this.productName = productName;
        this.imageUrl = imageUrl;
        this.description = description;
        this.price = price;
        this.count = count;
        this.favorite = favorite;
        this.comments = comments;
    }

    public ProductDto(ProductRealm productRealm) {
        this.productName = productRealm.getProductName();
        this.imageUrl = productRealm.getImageUrl();
        this.description = productRealm.getDescription();
        this.price = productRealm.getPrice();
        this.count = productRealm.getCount();
        this.favorite = productRealm.isFavorite();
    }

    public ProductDto(ProductRes productRes, ProductLocalInfo productLocalInfo) {
        this.id = productRes.getRemoteId();
        this.productName = productRes.getProductName();
        this.imageUrl = productRes.getImageUrl();
        this.description = productRes.getDescription();
        this.price = productRes.getPrice() ;
        this.count = productLocalInfo.getCount();
        this.favorite = productLocalInfo.isFavorite();
        this.comments = productRes.getComments();
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    //region  ============================= Parcelable ===================================
    protected ProductDto(Parcel in) {
        id = in.readInt();
        productName = in.readString();
        imageUrl = in.readString();
        description = in.readString();
        price = in.readInt();
        count = in.readInt();
        in.readList(comments, null);
    }

    public static final Creator<ProductDto> CREATOR = new Creator<ProductDto>() {
        @Override
        public ProductDto createFromParcel(Parcel in) {
            return new ProductDto(in);
        }

        @Override
        public ProductDto[] newArray(int size) {
            return new ProductDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(productName);
        dest.writeString(imageUrl);
        dest.writeString(description);
        dest.writeInt(price);
        dest.writeInt(count);
        dest.writeList(comments);
    }

    //endregion  ============================= Parcelable ===================================



    //region  ============================= Getters ===================================
    public int getId() {
        return id;
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
    public int getCount() {
        return count;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public int getRating() {
        return 3;
    }

    //endregion  ============================= Getters ===================================



    public void deleteProduct(ProductRes productRes) {
        count--;
    }

    public void addProduct() {
        count++;
    }


    public void updateOrInsert(ProductRes productRes) {

    }


    //region ============================== Builder ==============================

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private ProductDto mProductDto;

        private Builder() {
            mProductDto = new ProductDto();
        }

        public Builder id(int id) {
            mProductDto.id = id;
            return this;
        }

        public Builder productName(String productName) {
            mProductDto.productName = productName;
            return this;
        }

        public Builder imageUrl(String imageUrl) {
            mProductDto.imageUrl = imageUrl;
            return this;
        }

        public Builder description(String description) {
            mProductDto.description = description;
            return this;
        }

        public Builder price(int price) {
            mProductDto.price = price;
            return this;
        }

        public Builder count(int count) {
            mProductDto.count = count;
            return this;
        }

        public Builder favorite(boolean favorite) {
            mProductDto.favorite = favorite;
            return this;
        }

        public Builder comments(List<Comments> comments) {
            mProductDto.comments = comments;
            return this;
        }

        public ProductDto build() {
            return mProductDto;
        }
    }

    //endregion
}
