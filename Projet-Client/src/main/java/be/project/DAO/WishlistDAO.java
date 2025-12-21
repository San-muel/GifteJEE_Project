package be.project.DAO;

import be.project.MODEL.User;
import be.project.MODEL.Wishlist;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Nécessaire pour LocalDate
import com.fasterxml.jackson.databind.SerializationFeature;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class WishlistDAO extends DAO<Wishlist> {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;

    public WishlistDAO() {
        this.objectMapper = new ObjectMapper();
        // Configuration pour gérer LocalDate correctement
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    // POST : /wishlists
    // Crée une nouvelle wishlist pour l'utilisateur connecté
    public Optional<Wishlist> createWishlist(Wishlist wishlist, User user) {
        // Hypothèse : l'endpoint est à la racine "/wishlists" et l'API déduit l'utilisateur via le Token
        String url = ConfigLoad.API_BASE_URL + "wishlists"; 
        System.out.println("DEBUG URL APPELE: " + url);
        
        try {
            String jsonBody = objectMapper.writeValueAsString(wishlist);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + user.getToken())
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                // On retourne l'objet créé (avec son ID généré par la BDD)
                return Optional.of(objectMapper.readValue(response.body(), Wishlist.class));
            } else {
                System.err.println("API Error Create Wishlist: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // PUT : /wishlists/{id}
    public boolean updateWishlist(Wishlist wishlist, User user) {
        String url = ConfigLoad.API_BASE_URL + "wishlists/" + wishlist.getId();
        
        try {
            String jsonBody = objectMapper.writeValueAsString(wishlist);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + user.getToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // On accepte 200 OK ou 204 No Content comme succès
            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE : /wishlists/{id}
    public boolean deleteWishlist(int wishlistId, User user) {
        String url = ConfigLoad.API_BASE_URL + "wishlists/" + wishlistId;
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + user.getToken())
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            return response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Méthodes de l'interface DAO (Non utilisées car besoin du User/Token) ---
    
    @Override public boolean create(Wishlist obj) { return false; }
    @Override public boolean delete(Wishlist obj) { return false; }
    @Override public boolean update(Wishlist obj) { return false; }
    @Override public Wishlist find(int id) { return null; }
    @Override public java.util.List<Wishlist> findAll() { return null; }
}