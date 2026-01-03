package be.project.DAO;

import be.project.MODEL.Gift;
import be.project.MODEL.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class GiftDAO extends DAO<Gift> { 

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Optional<Gift> createGift(Gift gift, int wishlistId, User user) {
        String url = ConfigLoad.API_BASE_URL + "wishlists/" + wishlistId + "/gifts";
        System.out.println("[CLIENT DAO] Envoi POST vers : " + url);
        System.out.println("[CLIENT DAO] Token utilisé : " + user.getToken());
        
        try {
            String jsonBody = objectMapper.writeValueAsString(gift);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + user.getToken()) 
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("[CLIENT DAO] STATUS CODE RECU : " + response.statusCode());
            System.out.println("[CLIENT DAO] CORPS RECU : " + response.body());

            if (response.statusCode() == 201) {
                return Optional.of(objectMapper.readValue(response.body(), Gift.class));
            } else {
                System.err.println("[CLIENT DAO] Echec. Status: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("[CLIENT DAO] EXCEPTION : " + e.getMessage());
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public boolean updateGift(Gift gift, int wishlistId, User user) {
        String url = ConfigLoad.API_BASE_URL + "wishlists/" + wishlistId + "/gifts/" + gift.getId();
        try {
            String jsonBody = objectMapper.writeValueAsString(gift);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + user.getToken()) 
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204; 
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean updatePriority(Gift gift, int wishlistId, User user) {
        String url = ConfigLoad.API_BASE_URL + "wishlists/" + wishlistId + "/gifts/" + gift.getId() + "/priority";
        
        try {
            String jsonBody = "{\"priority\": " + gift.getPriority() + "}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + user.getToken()) 
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            System.err.println("[CLIENT DAO] Erreur REST Priority: " + e.getMessage());
            return false;
        }
    }

    public boolean deleteGift(int giftId, int wishlistId, User user) {
        String url = ConfigLoad.API_BASE_URL + "wishlists/" + wishlistId + "/gifts/" + giftId;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + user.getToken()) 
                    .DELETE()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 204;
        } catch (Exception e) {
            return false;
        }
    }

    @Override public boolean create(Gift obj) { return false; }
    @Override public boolean delete(Gift obj) { return false; }
    @Override public boolean update(Gift obj) { return false; }
    @Override public Gift find(int id) { return null; }
    @Override public java.util.List<Gift> findAll() { return null; }
}