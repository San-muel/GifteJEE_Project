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

    // POST : /wishlists/{wishlistId}/gifts
    public Optional<Gift> createGift(Gift gift, int wishlistId, User user) {
        String url = ConfigLoad.API_BASE_URL + "wishlists/" + wishlistId + "/gifts";
        try {
            String jsonBody = objectMapper.writeValueAsString(gift);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + user.getToken()) 
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                return Optional.of(objectMapper.readValue(response.body(), Gift.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    // PUT : /wishlists/{wishlistId}/gifts/{giftId}
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
            return response.statusCode() == 204; // 204 No Content est attendu pour un PUT rÃ©ussi
        } catch (Exception e) {
            return false;
        }
    }

    // DELETE : /wishlists/{wishlistId}/gifts/{giftId}
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