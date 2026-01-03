package be.project.DAO;

import be.project.MODEL.User; 
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class UserDAO extends DAO<User> {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;

    public UserDAO() {
        super();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule()); 
    }

    private String getCleanBaseUrl() {
        String base = ConfigLoad.API_BASE_URL;
        if (base != null && base.endsWith("/")) {
            return base.substring(0, base.length() - 1);
        }
        return base;
    }

    public User authenticate(String email, String psw) {
        try {
            String url = getCleanBaseUrl() + "/users/login";

            java.util.Map<String, String> creds = new java.util.HashMap<>();
            creds.put("email", email);
            creds.put("psw", psw); 
            
            String jsonBody = objectMapper.writeValueAsString(creds);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json") 
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody)) 
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), User.class);
            } else {
                System.out.println("DAO DEBUG: Login échoué code " + response.statusCode());
            }
            return null;
        } catch (Exception e) {
            System.err.println("ERREUR REST UserDAO.authenticate: " + e.getMessage());
            return null;
        }
    }

    @Override
    public List<User> findAll() {
        try {
            String url = getCleanBaseUrl() + "/users";
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, User.class));
            }
        } catch (Exception e) {
            System.err.println("ERREUR REST UserDAO.findAll: " + e.getMessage());
        }
        return List.of();
    }

    @Override
    public boolean create(User newUser) {
        try {
            String url = getCleanBaseUrl() + "/users"; 
            
            java.util.Map<String, String> registerData = new java.util.HashMap<>();
            registerData.put("username", newUser.getUsername());
            registerData.put("email", newUser.getEmail());
            registerData.put("psw", newUser.getPsw());

            String jsonBody = objectMapper.writeValueAsString(registerData);

            System.out.println("[WEB-DAO] Envoi manuel de : " + jsonBody);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return (response.statusCode() == 201 || response.statusCode() == 200);
        } catch (Exception e) {
            System.err.println("[WEB-DAO] Erreur : " + e.getMessage());
            return false;
        }
    }
    @Override public boolean delete(User obj) { return false; }
    @Override public boolean update(User obj) { return false; }
    @Override 
    public User find(int id) {
        try {
            String url = getCleanBaseUrl() + "/users/" + id;
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), User.class);
            }
        } catch (Exception e) {
            System.err.println("ERREUR REST UserDAO.find: " + e.getMessage());
        }
        return null;
    }
}