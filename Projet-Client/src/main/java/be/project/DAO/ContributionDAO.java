package be.project.DAO;

import be.project.MODEL.Contribution;
import be.project.MODEL.Gift; // Import pour le champ Gift si l'API le retourne
import org.json.JSONObject; 
// Retirer l'import de Jackson (si présent)
// import com.fasterxml.jackson.databind.ObjectMapper; 

import java.time.LocalDateTime;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

public class ContributionDAO extends DAO<Contribution> {

    private final HttpClient client = HttpClient.newHttpClient();
    // Retirer l'ObjectMapper si vous l'aviez laissé
    // private final ObjectMapper mapper = new ObjectMapper(); 

    public ContributionDAO() {
    }

    /**
     * Méthode d'aide pour construire un objet Contribution à partir d'un JSONObject.
     * @param json L'objet JSON à désérialiser.
     * @return L'objet Contribution hydraté.
     */
    private Contribution buildContribution(JSONObject json) {
        Contribution c = new Contribution();
        
        c.setId(json.getInt("id"));
        c.setAmount(json.getDouble("amount"));
        
        // Gérer le champ "contributedAt" (LocalDateTime)
        if (!json.isNull("contributedAt")) {
            String dateString = json.getString("contributedAt");
            // Conversion de la chaîne ISO en objet LocalDateTime
            c.setContributedAt(LocalDateTime.parse(dateString)); 
        }
        
        // Gérer le champ "comment"
        if (!json.isNull("comment")) {
            c.setComment(json.getString("comment"));
        }
        
        // REMARQUE: Les champs complexes (Gift, Users) ne sont PAS gérés ici 
        // car cela nécessiterait la désérialisation manuelle d'objets ou de tableaux imbriqués.
        // Si l'API retourne un JSON imbriqué, il faudra étendre cette logique.
        // Pour l'instant, c.setGift(null) et c.setUsers(new HashSet<>()) sont implicites.

        return c;
    }


    @Override
    public Contribution find(int id) {
        String baseUrl = ConfigLoad.API_BASE_URL; 
        
        if (baseUrl == null || baseUrl.isEmpty()) {
            System.err.println("ERREUR CRITIQUE DAO: L'URL de base de l'API est vide.");
            return null;
        }
        
        if (baseUrl.endsWith("/")) {
            baseUrl = baseUrl.substring(0, baseUrl.length() - 1);
        }
        
        String url = baseUrl + "/contributions/" + id;

        try {
            System.out.println("DEBUG DAO: Appel API avec l'URL : " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, 
                    HttpResponse.BodyHandlers.ofString());

            System.out.println("DEBUG DAO: Code de statut reçu de l'API : " + response.statusCode());

            if (response.statusCode() == 200) {
                
                // --- Désérialisation Manuelle ---
                String jsonBody = response.body();
                JSONObject json = new JSONObject(jsonBody);
                Contribution c = buildContribution(json);
                System.out.println("DEBUG DAO: Désérialisation manuelle réussie.");
                return c;
                
            } else if (response.statusCode() == 404) {
                System.out.println("Contribution avec id " + id + " non trouvée (404 de l'API).");
                return null;
            } else {
                System.out.println("Erreur HTTP inattendue : " + response.statusCode());
                return null;
            }

        } catch (Exception e) {
            System.err.println("DEBUG DAO: Erreur lors de la désérialisation manuelle ou de la requête.");
            e.printStackTrace();
            return null;
        }
    }

    // Le reste de la classe (findAll, create, delete, update)
    @Override
    public List<Contribution> findAll() {
        // Implementation findAll manuelle non incluse ici pour simplicité
        return List.of(); 
    }
    
	@Override
	public boolean create(Contribution obj) {
		return false;
	}

	@Override
	public boolean delete(Contribution obj) {
		return false;
	}

	@Override
	public boolean update(Contribution obj) {
		return false;
	}
}