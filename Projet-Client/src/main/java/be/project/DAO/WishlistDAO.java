package be.project.DAO;

import be.project.MODEL.User;
import be.project.MODEL.Wishlist;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    
    // --- METHODES DE PARTAGE (Venant de HEAD) ---

    /**
     * Permet de partager une wishlist avec un autre utilisateur
     */
	public boolean share(int wishlistId, int targetUserId, String notification) {
	    try {
	        String baseUrl = ConfigLoad.API_BASE_URL;
	        if (baseUrl.endsWith("/")) baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
	        
	        String url = baseUrl + "/shared-wishlists";
	        
	        System.out.println("[DEBUG CLIENT DAO] URL d'appel : " + url);
	        
	        Map<String, Object> data = new HashMap<>();
	        data.put("wishlistId", wishlistId);
	        data.put("targetUserId", targetUserId);
	        
	        String messageToSend = (notification != null && !notification.trim().isEmpty()) 
	                               ? notification 
	                               : "Tu as été invité à consulter ma liste !";
	        data.put("notification", messageToSend);
	
	        String json = objectMapper.writeValueAsString(data);
	
	        System.out.println("[DEBUG CLIENT DAO] JSON envoyé : " + json);
	
	        HttpRequest request = HttpRequest.newBuilder()
	                .uri(URI.create(url))
	                .header("Content-Type", "application/json")
	                .POST(HttpRequest.BodyPublishers.ofString(json))
	                .build();
	
	        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
	        
	        System.out.println("[DEBUG CLIENT DAO] Réponse reçue : " + response.statusCode());
	        return response.statusCode() == 201 || response.statusCode() == 200; 
	    } catch (Exception e) {
	        System.err.println("[DEBUG CLIENT DAO] ERREUR : " + e.getMessage());
	        return false;
	    }
	}

    // Garder l'ancienne signature pour la compatibilité si nécessaire
    public boolean share(int wishlistId, int targetUserId) {
        return this.share(wishlistId, targetUserId, null);
    }

    // --- METHODES DE GESTION WISHLIST (Venant de AddWishlist) ---

    // POST : /wishlists
    public Optional<Wishlist> createWishlist(Wishlist wishlist, User user) {
        // Attention au slash final dans l'URL
        String baseUrl = ConfigLoad.API_BASE_URL;
        if (!baseUrl.endsWith("/")) baseUrl += "/";
        String url = baseUrl + "wishlists"; 
        
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
        String baseUrl = ConfigLoad.API_BASE_URL;
        if (!baseUrl.endsWith("/")) baseUrl += "/";
        String url = baseUrl + "wishlists/" + wishlist.getId();
        
        try {
            String jsonBody = objectMapper.writeValueAsString(wishlist);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + user.getToken())
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            return response.statusCode() == 200 || response.statusCode() == 204;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // DELETE : /wishlists/{id}
    public boolean deleteWishlist(int wishlistId, User user) {
        String baseUrl = ConfigLoad.API_BASE_URL;
        if (!baseUrl.endsWith("/")) baseUrl += "/";
        String url = baseUrl + "wishlists/" + wishlistId;
        
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

    // --- IMPLEMENTATION ABSTRAITES ---

    @Override 
    public List<Wishlist> findAll() {
        try {
            String baseUrl = ConfigLoad.API_BASE_URL;
            if (baseUrl.endsWith("/")) baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            
            // IMPORTANT : On pointe vers /wishlists/all qui est publique
            String url = baseUrl + "/wishlists/all";
            
            System.out.println("[DEBUG CLIENT DAO] Récupération publique sur : " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    // PAS DE HEADER AUTHORIZATION ICI
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return objectMapper.readValue(response.body(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Wishlist.class));
            } else {
                System.err.println("[DEBUG CLIENT DAO] Erreur findAll, Status : " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.err.println("[DEBUG CLIENT DAO] ERREUR findAll : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Ces méthodes ne sont pas utilisées car on passe par les méthodes spécifiques avec User
    @Override public boolean create(Wishlist obj) { return false; }
    @Override public boolean delete(Wishlist obj) { return false; }
    @Override public boolean update(Wishlist obj) { return false; }
    @Override public Wishlist find(int id) { return null; }
}