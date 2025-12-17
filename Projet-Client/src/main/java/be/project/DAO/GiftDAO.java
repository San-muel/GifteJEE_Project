package be.project.DAO;

import be.project.MODEL.Gift;
import be.project.MODEL.User;

// Assurez-vous d'avoir tous ces imports, basés sur votre UserDAO
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject; 

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional; // Utile si vous utilisez find

// Le GiftDAO pourrait hériter de DAO<Gift> ou être autonome s'il n'implémente que les méthodes nécessaires
public class GiftDAO  extends DAO<Gift>  { 

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper; 
    private final UserDAO userDAO; // Nécessaire pour rafraîchir l'utilisateur

    public GiftDAO() {
        this.objectMapper = new ObjectMapper();
        // Configuration de l'ObjectMapper si nécessaire (dates, etc.)
        this.userDAO = new UserDAO(); // On suppose que vous en avez besoin pour rafraîchir la session
    }

    /**
     * Crée un nouveau cadeau via l'API RESTful.
     * Le token de l'utilisateur est utilisé pour l'autorisation.
     * @param gift Le cadeau à créer (avec son ID Wishlist intégré ou passé séparément).
     * @param user L'utilisateur authentifié (pour récupérer le token).
     * @return L'objet Gift créé par l'API (avec l'ID généré) ou Optional.empty() en cas d'échec.
     */
    public Optional<Gift> createGift(Gift gift, User user) {
        String baseUrl = ConfigLoad.API_BASE_URL;
        String url = "http://localhost:11265/Projet-API/api/gifts"; // Supposons que l'endpoint POST soit /api/gifts

        // 1. Récupération du token pour l'en-tête d'autorisation
        String token = user.getToken();
        if (token == null || token.isEmpty()) {
            System.err.println("ERREUR GIFT DAO: Token manquant pour l'autorisation.");
            return Optional.empty();
        }
        
        try {
            // 2. Préparation du corps de la requête JSON
            // Pour l'ajout d'un cadeau, nous devons envoyer les données du cadeau PLUS l'ID de la wishlist
            // On peut soit construire le JSON manuellement, soit utiliser Jackson pour sérialiser l'objet.
            
            // Si l'API attend un JSON comme ceci : { "name": "...", "price": 50, "wishlistId": 1 }
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("name", gift.getName());
            jsonBody.put("description", gift.getDescription());
            jsonBody.put("price", gift.getPrice());
            jsonBody.put("priority", gift.getPriority());
            jsonBody.put("photoUrl", gift.getPhotoUrl());
            // C'est crucial : l'API doit savoir à quelle liste attacher le cadeau
            if (gift.getwishlist() != null) {
                jsonBody.put("wishlistId", gift.getwishlist().getId());
            } else {
                 System.err.println("ERREUR GIFT DAO: ID Wishlist manquant dans l'objet Gift.");
                 return Optional.empty();
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    // Utilisation du token pour l'autorisation
                    .header("Authorization", "Bearer " + token) 
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (response.statusCode() == 201) { // 201 Created est le code standard pour une création réussie
                // Succès : Désérialisation du Gift créé par l'API (qui contient l'ID généré)
                Gift createdGift = objectMapper.readValue(responseBody, Gift.class);
                System.out.println("DEBUG GIFT DAO: Cadeau créé avec succès. ID: " + createdGift.getId());
                return Optional.of(createdGift);

            } else if (response.statusCode() == 401) {
                System.err.println("ERREUR GIFT DAO: Non autorisé. Token invalide ou expiré.");
            } else {
                System.err.println("ERREUR GIFT DAO: Erreur HTTP inattendue : " + response.statusCode());
                System.err.println("Corps de la réponse : " + responseBody);
            }
            
        } catch (Exception e) {
            System.err.println("ERREUR GIFT DAO: Échec de la communication ou du traitement.");
            e.printStackTrace();
        }
        return Optional.empty();
    }
    
    public boolean updateGift(Gift gift, User user) {
        // L'endpoint PUT doit cibler une ressource spécifique: /api/gifts/{id}
        String baseUrl = ConfigLoad.API_BASE_URL;
        String url = "http://localhost:11265/Projet-API/api/gifts/" + gift.getId(); // <-- Cible l'ID du cadeau

        // 1. Vérifications initiales
        String token = user.getToken();
        if (token == null || token.isEmpty()) {
            System.err.println("ERREUR GIFT DAO (UPDATE): Token manquant pour l'autorisation.");
            return false;
        }
        if (gift.getId() <= 0) {
            System.err.println("ERREUR GIFT DAO (UPDATE): ID du cadeau manquant ou invalide.");
            return false;
        }

        try {
            // 2. Préparation du corps de la requête JSON (Doit être complet pour un PUT)
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("id", gift.getId()); // Inclus pour être sûr
            jsonBody.put("name", gift.getName());
            jsonBody.put("description", gift.getDescription());
            jsonBody.put("price", gift.getPrice());
            jsonBody.put("priority", gift.getPriority());
            jsonBody.put("photoUrl", gift.getPhotoUrl());
            
            // C'est crucial : l'API doit savoir à quelle liste il appartient
            if (gift.getwishlist() != null) {
                jsonBody.put("wishlistId", gift.getwishlist().getId());
            } else {
                 System.err.println("ERREUR GIFT DAO (UPDATE): ID Wishlist manquant dans l'objet Gift.");
                 return false;
            }

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + token) 
                    .PUT(HttpRequest.BodyPublishers.ofString(jsonBody.toString())) // <-- UTILISATION DE PUT
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) { // 200 OK est le code standard pour une mise à jour réussie
                System.out.println("DEBUG GIFT DAO (UPDATE): Cadeau ID " + gift.getId() + " modifié avec succès.");
                return true;

            } else if (response.statusCode() == 401) {
                System.err.println("ERREUR GIFT DAO (UPDATE): Non autorisé. Token invalide ou expiré.");
            } else if (response.statusCode() == 404) {
                System.err.println("ERREUR GIFT DAO (UPDATE): Cadeau non trouvé.");
            } else {
                System.err.println("ERREUR GIFT DAO (UPDATE): Erreur HTTP inattendue : " + response.statusCode());
                System.err.println("Corps de la réponse : " + response.body());
            }
            
        } catch (Exception e) {
            System.err.println("ERREUR GIFT DAO (UPDATE): Échec de la communication ou du traitement.");
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean deleteGift(int giftId, User user) {
        // L'endpoint DELETE doit cibler une ressource spécifique: /api/gifts/{id}
        String baseUrl = ConfigLoad.API_BASE_URL;
        String url = "http://localhost:11265/Projet-API/api/gifts/" + giftId; // <-- Cible l'ID du cadeau

        // 1. Vérifications initiales
        String token = user.getToken();
        if (token == null || token.isEmpty()) {
            System.err.println("ERREUR GIFT DAO (DELETE): Token manquant pour l'autorisation.");
            return false;
        }
        if (giftId <= 0) {
            System.err.println("ERREUR GIFT DAO (DELETE): ID du cadeau manquant ou invalide.");
            return false;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .header("Authorization", "Bearer " + token) 
                    .DELETE() // <-- UTILISATION DE DELETE
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 204 No Content est le code standard pour DELETE si la réponse est vide
            if (response.statusCode() == 204) { 
                System.out.println("DEBUG GIFT DAO (DELETE): Cadeau ID " + giftId + " supprimé avec succès.");
                return true;

            } else if (response.statusCode() == 401) {
                System.err.println("ERREUR GIFT DAO (DELETE): Non autorisé. Token invalide ou expiré.");
            } else if (response.statusCode() == 403) {
                System.err.println("ERREUR GIFT DAO (DELETE): Non autorisé à supprimer ce cadeau.");
            } else if (response.statusCode() == 404) {
                System.err.println("ERREUR GIFT DAO (DELETE): Cadeau non trouvé.");
            } else {
                System.err.println("ERREUR GIFT DAO (DELETE): Erreur HTTP inattendue : " + response.statusCode());
                System.err.println("Corps de la réponse : " + response.body());
            }
            
        } catch (Exception e) {
            System.err.println("ERREUR GIFT DAO (DELETE): Échec de la communication ou du traitement.");
            e.printStackTrace();
        }
        return false;
    }
    
	@Override
	public boolean create(Gift obj) {
		// TODO Auto-generated method stub
		return false;
	}

    // Vous ajouterez ici les méthodes update, delete, findById etc.
	@Override
	public boolean delete(Gift obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Gift obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Gift find(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Gift> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

}