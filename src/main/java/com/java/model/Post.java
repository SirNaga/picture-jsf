package com.java.model;

/**
 * Model Class for Post screen
 */

import java.util.ArrayList;
import java.util.List;

public class Post {

    public String id;
    public String title;
    public String description;
    public String username;

    public String ImageId;
    public List<String> comments = new ArrayList<>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public String getImageId() {
        if (ImageId == null || ImageId.isBlank())
            return "65aee626fb7cfd511db055ee";
        return ImageId;
    }

    public void setImageId(String imageId) {
        ImageId = imageId;
    }

    @Override
    public String toString() {
        return "Post{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", username='" + username + '\'' +
                ", ImageId='" + ImageId + '\'' +
                ", comments=" + comments +
                '}';
    }
}