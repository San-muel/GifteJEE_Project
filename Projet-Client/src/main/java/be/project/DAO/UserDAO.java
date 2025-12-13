package be.project.DAO;

import be.project.MODEL.User;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class UserDAO extends DAO<User> {

    private final HttpClient client = HttpClient.newHttpClient();

    public UserDAO() {
        super();
    }
    
    // Méthode d'aide pour la désérialisation manuelle
    private User buildUser(JSONObject jsonResponse) {
        User user = new User();
        
        // Champs obligatoires
        user.setId(jsonResponse.getInt("id"));
        user.setEmail(jsonResponse.getString("email"));
        
        // Récupération du TOKEN JWT
        if (!jsonResponse.isNull("token")) {
            user.setToken(jsonResponse.getString("token"));
        }
        
        // Autres champs (nom d'utilisateur)
        if (!jsonResponse.isNull("username")) {
            user.setUsername(jsonResponse.getString("username"));
        }
        
        // NOTE: Les champs complexes (Wishlist, Contribution) ne sont pas hydratés ici.
        return user;
    }

    /**
     * Authentification RESTful : envoie email/psw, reçoit User+Token.
     * @param email Email de l'utilisateur.
     * @param psw Mot de passe de l'utilisateur.
     * @return L'objet User avec le token s'il est authentifié, sinon null.
     */
    public User authenticate(String email, String psw) {
        // Assurez-vous que ConfigLoad existe et contient API_BASE_URL
        String baseUrl = ConfigLoad.API_BASE_URL;
        if (baseUrl == null || baseUrl.isEmpty()) {
             System.err.println("ERREUR DAO: L'URL de base de l'API est vide.");
             return null;
        }
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        String url = baseUrl + "/auth/login"; // Endpoint RESTful d'authentification

        try {
            // Création du corps de la requête JSON (utilisation de "psw" comme dans votre modèle)
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            jsonBody.put("psw", psw); 

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json") // Nous envoyons du JSON
                    .header("Accept", "application/json")       // Nous attendons du JSON
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Succès : Désérialisation manuelle du JSON
                JSONObject jsonResponse = new JSONObject(response.body());
                return buildUser(jsonResponse);

            } else if (response.statusCode() == 401 || response.statusCode() == 403) {
                System.out.println("DEBUG AUTH DAO: Échec de l'authentification (identifiants invalides).");
                return null;
            } else {
                System.err.println("ERREUR AUTH DAO: Erreur HTTP inattendue : " + response.statusCode());
                return null;
            }

        } catch (Exception e) {
            System.err.println("ERREUR AUTH DAO: Échec de la communication ou du traitement JSON.");
            e.printStackTrace();
            return null;
        }
    }
    
    // --- Implémentations des méthodes abstraites ---

	@Override
	public boolean create(User obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(User obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(User obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public User find(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<User> findAll() {
		// TODO Auto-generated method stub
		return null;
	}
}