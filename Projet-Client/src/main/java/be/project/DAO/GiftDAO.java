package be.project.DAO;

import be.project.MODEL.Gift;
import be.project.MODEL.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject; 

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

public class GiftDAO extends DAO<Gift> { 

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper; 

    public GiftDAO() {
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Crée un nouveau cadeau via l'API.
     * URL attendue : http://localhost:11265/Projet-API/api/gifts?wishlistId=X
     */
    public Optional<Gift> createGift(Gift gift, User user) {
        String token = user.getToken();
        if (token == null || token.isEmpty() || gift.getwishlist() == null) {
            System.err.println("ERREUR GIFT DAO: Token ou Wishlist manquante.");
            return Optional.empty();
        }

        // Construction de l'URL avec le paramètre wishlistId (car plus de DTO)
        String url = ConfigLoad.API_BASE_URL + "gifts?wishlistId=" + gift.getwishlist().getId();

        try {
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", gift.getName());
            jsonBody.put("description", gift.getDescription());
            jsonBody.put("price", gift.getPrice());
            jsonBody.put("priority", gift.getPriority());
            jsonBody.put("photoUrl", gift.getPhotoUrl());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + token) 
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                return Optional.of(objectMapper.readValue(response.body(), Gift.class));
            } else {
                System.err.println("ERREUR GIFT DAO (POST): " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    /**
     * Modifie un cadeau.
     * URL attendue : http://localhost:11265/Projet-API/api/gifts/{id}?wishlistId=X
     */
    public boolean updateGift(Gift gift, User user) {
        String token = user.getToken();
        if (token == null || gift.getwishlist() == null) return false;

        String url = ConfigLoad.API_BASE_URL + "gifts/" + gift.getId() + "?wishlistId=" + gift.getwishlist().getId();

        try {
            JSONObject jsonBody = new JSONObject();
            // On envoie l'objet Gift sans le wishlistId dans le body
            jsonBody.put("id", gift.getId());
            jsonBody.put("name", gift.getName());
            jsonBody.put("description", gift.getDescription());
            jsonBody.put("price", gift.getPrice());
            jsonBody.put("priority", gift.getPriority());
            jsonBody.put("photoUrl", gift.getPhotoUrl());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + token) 
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200 || response.statusCode() == 204;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Supprime un cadeau.
     * URL attendue : http://localhost:11265/Projet-API/api/gifts/{id}
     */
    public boolean deleteGift(int giftId, User user) {
        String token = user.getToken();
        String url = ConfigLoad.API_BASE_URL + "gifts/" + giftId;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + token) 
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Méthodes héritées de DAO<Gift> (à implémenter si nécessaire) ---
    @Override public boolean create(Gift obj) { return false; }
    @Override public boolean delete(Gift obj) { return deleteGift(obj.getId(), null); }
    @Override public boolean update(Gift obj) { return false; }
    @Override public Gift find(int id) { return null; }
    @Override public List<Gift> findAll() { return null; }
}