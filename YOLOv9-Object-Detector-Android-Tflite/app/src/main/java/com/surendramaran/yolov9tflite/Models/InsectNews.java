package org.tensorflow.lite.examples.detection.Models;

public class InsectNews {
    String title, description, image , date, newsId;

    public InsectNews() {
    }

    public InsectNews(String title, String description, String image, String date, String newsId) {
        this.title = title;
        this.description = description;
        this.image = image;
        this.date = date;
        this.newsId = newsId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNewsId() {
        return newsId;
    }

    public void setNewsId(String newsId) {
        this.newsId = newsId;
    }
}
