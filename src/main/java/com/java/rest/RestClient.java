package com.java.rest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.java.model.Post;
import jakarta.servlet.http.Part;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

/**
 * Use MySQL Database for store data
 * <p>
 * User Singleton Design Pattern , allow only one object creation.
 */
public class RestClient {
    //local machine address
    private final static String API_HOST = "http://localhost:9090";
    private final static String POST = "/post";
    private final static String FILE = "/file";

    public List<Post> getAllPost() {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_HOST + POST))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return convertStringToList(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public Post getPost(String postId) {
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(API_HOST + POST + "/" + postId))
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return convertStringIntoPostObject(response.body());
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public Post sendPostRequest(Post post) {
        try {
            // Create a new HttpClient
            HttpClient httpClient = HttpClient.newHttpClient();

            // Build the request with the POST method and JSON content
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_HOST + POST + "/create"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(convertPostObjectIntoJsonString(post)))
                    .build();

            // Send the request and retrieve the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Return the response body
            return convertStringIntoPostObject(response.body());
        } catch (Exception e) {
            throw new RuntimeException("Error making POST request");
        }
    }

    public Post sendPutRequestForPost(Post post) {
        try {
            // Create a new HttpClient
            HttpClient httpClient = HttpClient.newHttpClient();

            // Build the request with the POST method and JSON content
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_HOST + POST + "/"+ post.getId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(convertPostObjectIntoJsonString(post)))
                    .build();

            // Send the request and retrieve the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Return the response body
            return convertStringIntoPostObject(response.body());
        } catch (Exception e) {
            throw new RuntimeException("Error making POST request");
        }
    }

    public Post sendPutRequest(Post post) {
        try {
            // Create a new HttpClient
            HttpClient httpClient = HttpClient.newHttpClient();

            // Build the request with the POST method and JSON content
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_HOST + POST + "/update/" + post.getId()))
                    .header("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(convertPostObjectIntoJsonString(post)))
                    .build();

            // Send the request and retrieve the response
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            // Return the response body
            return convertStringIntoPostObject(response.body());
        } catch (Exception e) {
            throw new RuntimeException("Error making POST request");
        }
    }

    public String uploadFile(Part file) {
        if (file != null) {
            try {
                HttpClient httpClient = HttpClient.newHttpClient();

                // Prepare the multipart request
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(API_HOST + FILE + "/upload"))
                        .header("Content-Type", "multipart/form-data; boundary=BoundaryString")
                        .POST(HttpRequest.BodyPublishers.ofByteArray(createMultipartRequest(file)))
                        .build();

                // Send the request and retrieve the response
                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                // Process the response
                int statusCode = response.statusCode();
                if (statusCode == HttpURLConnection.HTTP_OK) {
                    // File uploaded successfully
                    System.out.println("File uploaded successfully.");
                    Map body = new ObjectMapper().readValue(response.body(), Map.class);
                    return (String) body.getOrDefault("imageId", "65aee626fb7cfd511db055ee");
                } else {
                    // Handle error response
                    System.out.println("Failed to upload file. Response code: " + statusCode);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
                // Handle exception
            }
        } else {
            // No file selected
            System.out.println("No file selected.");
        }

        return null;
    }

    private List<Post> convertStringToList(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        CollectionType collectionType = objectMapper.getTypeFactory().constructCollectionType(List.class, Post.class);
        return objectMapper.readValue(json, collectionType);
    }

    private String convertPostObjectIntoJsonString(Post post) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(post);
    }

    private Post convertStringIntoPostObject(String body) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(body, Post.class);
    }

    private byte[] createMultipartRequest(Part file) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        String boundary = "BoundaryString";

        // Write the file part
        outputStream.write(("--" + boundary + "\r\n").getBytes());
        outputStream.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + file.getSubmittedFileName() + "\"\r\n").getBytes());
        outputStream.write(("Content-Type: " + file.getContentType() + "\r\n").getBytes());
        outputStream.write("\r\n".getBytes());

        InputStream inputStream = file.getInputStream();
        byte[] buffer = new byte[4096];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.write("\r\n".getBytes());

        // Write boundary end marker
        outputStream.write(("--" + boundary + "--\r\n").getBytes());

        return outputStream.toByteArray();
    }

}
