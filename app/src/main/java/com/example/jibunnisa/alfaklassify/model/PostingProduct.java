package com.example.jibunnisa.alfaklassify.model;

/**
 * Created by Shamon on 5/2/2017.
 */

public class PostingProduct {

    private String productId;
    private String brandName;
    private String categoryName;
    private String typeName;
    private String modelName;
    private String locationName;
    private String condition;
    private String description;
    private String imageUrl;
    private String userId;
    private Double price;
    String  date;
    String  time;

    public PostingProduct(String productId, String brandName,String categoryName,String typeName,
                          String modelName, String locationName,String condition,String userId,
                          String description, String imageUrl,Double price, String date, String time) {

        this.productId = productId;
        this.brandName = brandName;
        this.categoryName = categoryName;
        this.typeName = typeName;
        this.modelName = modelName;
        this.locationName = locationName;
        this.condition = condition;
        this.description = description;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.price = price;
        this.date = date;
        this.time = time;

    }

    public PostingProduct() {


    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }
    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }
    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }



}
