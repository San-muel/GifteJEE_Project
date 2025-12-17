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

    /**
     * Nettoie l'URL de base pour éviter les doubles slashes
     */
    private String getCleanBaseUrl() {
        String base = ConfigLoad.API_BASE_URL;
        if (base != null && base.endsWith("/")) {
            return base.substring(0, base.length() - 1);
        }
        return base;
    }

    /**
     * Authentification RESTful : GET /users?email=...&psw=...
     */
    public User authenticate(String email, String psw) {
        try {
            String url = getCleanBaseUrl() + "/users" 
                    + "?email=" + URLEncoder.encode(email, StandardCharsets.UTF_8)
                    + "&psw=" + URLEncoder.encode(psw, StandardCharsets.UTF_8);

            // --- DEBUG LOG ---
            System.out.println("\n[DAO CLIENT - AUTHENTICATE]");
            System.out.println(">> URL d'appel : " + url);
            System.out.println(">> Méthode     : GET");
            // ------------------

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("<< Réponse API : Status " + response.statusCode());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), User.class);
            } else {
                System.err.println("<< Échec Authentification : " + response.body());
                return null;
            }
        } catch (Exception e) {
            System.err.println("ERREUR AUTH DAO: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Inscription RESTful : POST /users
     */
    @Override
    public boolean create(User newUser) {
        try {
            String url = getCleanBaseUrl() + "/users"; 
            String jsonBody = objectMapper.writeValueAsString(newUser);

            // --- DEBUG LOG ---
            System.out.println("\n[DAO CLIENT - CREATE]");
            System.out.println(">> URL d'appel : " + url);
            System.out.println(">> Méthode     : POST");
            System.out.println(">> Corps JSON  : " + jsonBody);
            // ------------------

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("<< Réponse API : Status " + response.statusCode());
            
            if (response.statusCode() != 201 && response.statusCode() != 200) {
                System.err.println("<< Détails Erreur : " + response.body());
            }

            return (response.statusCode() == 201 || response.statusCode() == 200);

        } catch (Exception e) {
            System.err.println("ERREUR CREATE DAO: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- Méthodes héritées non implémentées ---
    @Override public boolean delete(User obj) { return false; }
    @Override public boolean update(User obj) { return false; }
    @Override public User find(int id) { return null; }
    @Override public List<User> findAll() { return null; }
}