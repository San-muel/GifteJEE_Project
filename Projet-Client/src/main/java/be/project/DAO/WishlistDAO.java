package be.project.DAO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.project.MODEL.Wishlist;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class WishlistDAO extends DAO<Wishlist> {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;

    public WishlistDAO() {
        super();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }
    
    /**
     * Correction : Ajout du paramètre notification pour envoyer le message personnalisé
     */
	public boolean share(int wishlistId, int targetUserId, String notification) {
	    try {
	        String baseUrl = ConfigLoad.API_BASE_URL;
	        if (baseUrl.endsWith("/")) baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
	        
	        String url = baseUrl + "/shared-wishlists";
	        
	        // --- PRINT DE DEBUG ---
	        System.out.println("[DEBUG CLIENT DAO] URL d'appel : " + url);
	        
	        Map<String, Object> data = new HashMap<>();
	        data.put("wishlistId", wishlistId);
	        data.put("targetUserId", targetUserId);
	        
	        String messageToSend = (notification != null && !notification.trim().isEmpty()) 
	                               ? notification 
	                               : "Tu as été invité à consulter ma liste !";
	        data.put("notification", messageToSend);
	
	        String json = objectMapper.writeValueAsString(data);
	
	        // --- PRINT DE DEBUG ---
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

    @Override public boolean create(Wishlist obj) { return false; }
    @Override public boolean delete(Wishlist obj) { return false; }
    @Override public boolean update(Wishlist obj) { return false; }
    @Override public Wishlist find(int id) { return null; }
    @Override 
    public List<Wishlist> findAll() {
        try {
            String baseUrl = ConfigLoad.API_BASE_URL;
            if (baseUrl.endsWith("/")) baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
            
            // L'URL vers ton endpoint GET
            String url = baseUrl + "/wishlists";
            
            System.out.println("[DEBUG CLIENT DAO] Récupération de toutes les wishlists sur : " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET() // Requête GET pour la lecture
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Désérialisation du JSON en Liste d'objets Wishlist
                return objectMapper.readValue(response.body(), 
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Wishlist.class));
            } else {
                System.err.println("[DEBUG CLIENT DAO] Erreur lors du findAll, Status : " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.err.println("[DEBUG CLIENT DAO] ERREUR findAll : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}