package be.project.DAO;

import be.project.MODEL.User;
// Importation de Jackson pour la désérialisation
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Pour LocalDate

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

// On garde org.json pour construire la requête, car c'est simple,
// mais on utilise Jackson pour le corps de la réponse complexe.
import org.json.JSONObject; 

public class UserDAO extends DAO<User> {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper; // Déclaration de l'ObjectMapper

    public UserDAO() {
        super();
        this.objectMapper = new ObjectMapper();
        // IMPORTANT : Le Client a BESOIN de la même configuration de date que l'API pour lire.
        this.objectMapper.registerModule(new JavaTimeModule()); 
        // Note: Désactiver la fonction si elle ne trouve pas toutes les propriétés JSON pour être plus tolérant
        // this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    
    // Suppression de la méthode buildUser, on utilise Jackson

    /**
     * Authentification RESTful : envoie email/psw, reçoit User+Token (COMPLET).
     * @param email Email de l'utilisateur.
     * @param psw Mot de passe de l'utilisateur.
     * @return L'objet User HYDRATÉ (complet) avec le token s'il est authentifié, sinon null.
     */
    public User authenticate(String email, String psw) {
        String baseUrl = ConfigLoad.API_BASE_URL;
        // ... (Vérifications de baseUrl inchangées) ...
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        String url = baseUrl + "/auth/login";

        try {
            // Création du corps de la requête JSON (inchangé)
            JSONObject jsonBody = new JSONObject();
            jsonBody.put("email", email);
            jsonBody.put("psw", psw); 

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            if (response.statusCode() == 200) {
                // Succès : Désérialisation FACILE et COMPLÈTE via Jackson
                
                // C'est l'étape qui va mapper TOUS les champs (y compris Wishlists)
                User user = objectMapper.readValue(responseBody, User.class);
                return user;

            } else if (response.statusCode() == 401 || response.statusCode() == 403) {
                System.out.println("DEBUG AUTH DAO: Échec de l'authentification (identifiants invalides).");
                return null;
            } else {
                System.err.println("ERREUR AUTH DAO: Erreur HTTP inattendue : " + response.statusCode());
                System.err.println("Corps de la réponse : " + responseBody);
                return null;
            }

        } catch (Exception e) {
            System.err.println("ERREUR AUTH DAO: Échec de la communication ou du traitement Jackson.");
            e.printStackTrace();
            return null;
        }
    }
   
   
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