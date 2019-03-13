package com.example.anotherwall;

import android.os.Parcel;
import android.os.Parcelable;

public class WallItem implements Parcelable {

    private String title;
    private String thumbnail;
    private String address;
    private String material;
    private String photographer;
    private String description;
    private String[] imgURLArray;

    public WallItem(Parcel in) {
        title = in.readString();
        thumbnail = in.readString();
        address = in.readString();
        material = in.readString();
        photographer = in.readString();
        description = in.readString();
        imgURLArray = in.createStringArray();
    }

    public static final Creator<WallItem> CREATOR = new Creator<WallItem>() {
        @Override
        public WallItem createFromParcel(Parcel in) {
            return new WallItem(in);
        }

        @Override
        public WallItem[] newArray(int size) {
            return new WallItem[size];
        }
    };

    // Overwrite constructor for overloading
    public WallItem() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getPhotographer() {
        return photographer;
    }

    public void setPhotographer(String photographer) {
        this.photographer = photographer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.thumbnail);
        dest.writeString(this.address);
        dest.writeString(this.material);
        dest.writeString(this.photographer);
        dest.writeString(this.description);
        dest.writeStringArray(this.imgURLArray);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String[] getImgURLArray() {
        return imgURLArray;
    }

    public void setImgURLArray(String[] imgURLArray) {
        this.imgURLArray = imgURLArray;
    }
}

