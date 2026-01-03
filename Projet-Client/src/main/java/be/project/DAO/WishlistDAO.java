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
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    public boolean share(int wishlistId, int targetUserId, String notification, String token) {
        try {
            System.out.println("DEBUG DAO SHARE - Token reçu : " + token);
            
            String baseUrl = ConfigLoad.API_BASE_URL;
            if (baseUrl.endsWith("/")) baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            String url = baseUrl + "/shared-wishlists";
            
            Map<String, Object> data = new HashMap<>();
            data.put("wishlistId", wishlistId);
            data.put("targetUserId", targetUserId);
            
            String messageToSend = (notification != null && !notification.trim().isEmpty()) 
                                   ? notification 
                                   : "Tu as été invité à consulter ma liste !";
            data.put("notification", messageToSend);

            String json = objectMapper.writeValueAsString(data);

            String authHeader = (token != null) ? "Bearer " + token : "";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", authHeader)
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("[DEBUG DAO] Réponse partage : " + response.statusCode());
            return response.statusCode() == 201 || response.statusCode() == 200; 

        } catch (Exception e) {
            System.err.println("[DEBUG DAO] ERREUR PARTAGE : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

	public boolean share(int wishlistId, int targetUserId, String token) {
	    return this.share(wishlistId, targetUserId, null, token);
	}

    public Optional<Wishlist> createWishlist(Wishlist wishlist, User user) {
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

    public boolean updateWishlist(Wishlist wishlist, User user) {
        String baseUrl = ConfigLoad.API_BASE_URL;
        if (!baseUrl.endsWith("/")) baseUrl += "/";
        String url = baseUrl + "wishlists/" + wishlist.getId();
        System.out.println("DEBUG URL APPELE: " + url);
        
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

    @Override 
    public List<Wishlist> findAll() {
        try {
            String baseUrl = ConfigLoad.API_BASE_URL;
            if (baseUrl.endsWith("/")) baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            
            String url = baseUrl + "/wishlists?filter=public";
            
            System.out.println("[DEBUG CLIENT DAO] URL Appelée pour findAll : " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonBrut = response.body();
                return objectMapper.readValue(jsonBrut, 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Wishlist.class));
            } else {
                System.err.println("[DEBUG CLIENT DAO] Erreur findAll, Status : " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override public boolean create(Wishlist obj) { return false; }
    @Override public boolean delete(Wishlist obj) { return false; }
    @Override public boolean update(Wishlist obj) { return false; }

    @Override 
    public Wishlist find(int id) {
        try {
            String baseUrl = ConfigLoad.API_BASE_URL;
            if (baseUrl.endsWith("/")) baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            
            String url = baseUrl + "/wishlists/" + id;
            
            System.out.println("[DEBUG CLIENT DAO] Recherche de la wishlist ID " + id + " sur : " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String jsonRaw = response.body();
                
                System.out.println("[DEBUG CLIENT DAO] JSON BRUT REÇU : " + jsonRaw);
                
                Wishlist wishlist = objectMapper.readValue(jsonRaw, Wishlist.class);
                
                if (wishlist != null) {
                    System.out.println("[DEBUG CLIENT DAO] Objet Wishlist créé : " + wishlist.getTitle());
                    if (wishlist.getGifts() != null) {
                        System.out.println("[DEBUG CLIENT DAO] Nombre de cadeaux dans l'objet : " + wishlist.getGifts().size());
                        wishlist.getGifts().forEach(g -> 
                            System.out.println("   -> Cadeau détecté : " + g.getName() + " (ID: " + g.getId() + ")")
                        );
                    } else {
                        System.out.println("[DEBUG CLIENT DAO] ATTENTION : La liste 'gifts' est NULL après désérialisation !");
                    }
                }
                
                return wishlist;
            } else {
                System.err.println("[DEBUG CLIENT DAO] Erreur findById, Status : " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.err.println("[DEBUG CLIENT DAO] ERREUR CRITIQUE : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}