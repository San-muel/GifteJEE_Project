package be.project.DAO;

import be.project.MODEL.User; // Vérifie la casse (MODEL ou model) // Assure-toi de l'import correct de ta config
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

    /**
     * Authentification : Appelle maintenant /users/login
     */
    public User authenticate(String email, String psw) {
        try {
            // si je fais pas du non RESTful ici 
        		// je pourrais pas differencier login et get all users
            String url = getCleanBaseUrl() + "/users/login" 
                    + "?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                    + "&psw=" + URLEncoder.encode(psw, StandardCharsets.UTF_8);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), User.class);
            } else if (response.statusCode() == 401) {
                System.out.println("DAO DEBUG: Identifiants incorrects (401)");
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
            // Reste sur /users (GET global)
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
            // Reste sur /users (POST)
            String url = getCleanBaseUrl() + "/users"; 
            
            // On envoie directement l'objet newUser, Jackson s'occupe du JSON
            String jsonBody = objectMapper.writeValueAsString(newUser);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return (response.statusCode() == 201 || response.statusCode() == 200);
        } catch (Exception e) {
            System.err.println("ERREUR REST UserDAO.create: " + e.getMessage());
            return false;
        }
    }

    @Override public boolean delete(User obj) { return false; }
    @Override public boolean update(User obj) { return false; }
    @Override public User find(int id) { return null; }
}