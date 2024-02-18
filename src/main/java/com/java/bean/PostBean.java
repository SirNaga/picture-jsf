package com.java.bean;

import com.java.rest.RestClient;
import com.java.model.Post;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.util.List;
import java.util.Map;


@Named
@RequestScoped
public class PostBean {

    private Post post = null;
    private String comment = null;
    RestClient restClient = null;
    public PostBean() {
        this.post = new Post();
        this.restClient = new RestClient();
    }

    public List<Post> getAllPost() {
        System.out.println("find All Post record : ");
        return this.restClient.getAllPost();
    }

    public String createPost() throws ServletException, IOException {
        System.out.println("create new Post");
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        Part uploadedFile = (Part) request.getPart("file");
        String uploadFile = restClient.uploadFile(uploadedFile);
        String navigationResult = null;
        if (uploadedFile != null) {
            this.post.ImageId = uploadFile;
            Post p = this.restClient.sendPostRequest(post);

            if (p != null && p.getId() != null) {
                System.out.println("save Post record : " + p);
            } else {
                System.out.println("Post record is not save: " + p);
            }

        } else {
            System.out.println("file is not upload");
        }


        return "posts.xhtml?faces-redirect=true";
    }

    /* Method Used To Update Student Record In Database */
    public String updatePost(Post post) {
        addComment(post);
        Post p = this.restClient.sendPutRequestForPost(post);
        System.out.println("update Post record : " + post);
        return "/posts.xhtml?faces-redirect=true";
    }

    public String updatePostComments(Post post) {
        addComment(post);
        Post p = this.restClient.sendPutRequest(post);
        System.out.println("update Post record : " + post);
        return "/posts.xhtml?faces-redirect=true";
    }


    private void addComment(Post post) {
        if (comment != null && !comment.isBlank()) {
            post.getComments().add(comment);
        }
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
