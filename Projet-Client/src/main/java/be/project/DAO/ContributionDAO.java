package be.project.DAO;

import be.project.MODEL.Contribution;
import be.project.MODEL.User;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContributionDAO extends DAO<Contribution> {

    private final HttpClient client = HttpClient.newHttpClient();

    public ContributionDAO() {}

    /**
     * Récupère toutes les contributions d'un cadeau depuis l'API.
     */
    public List<Contribution> findAllByGiftId(int giftId) {
        String baseUrl = ConfigLoad.API_BASE_URL;
        if (!baseUrl.endsWith("/")) baseUrl += "/";
        
        // Appel de la route API : /contributions/gift/{id}
        String url = baseUrl + "contributions/gift/" + giftId;

        List<Contribution> list = new ArrayList<>();
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                // Parsing du tableau JSON reçu
                JSONArray array = new JSONArray(response.body());
                for (int i = 0; i < array.length(); i++) {
                    list.add(buildContribution(array.getJSONObject(i)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    // ... Ta méthode createContribution (EXISTANTE, ne change pas) ...
    public Optional<Contribution> createContribution(Contribution contrib, int giftId, User user) {
        // ... (URL setup) ...
        
        try {
            JSONObject json = new JSONObject();
            // ATTENTION AUX NOMS DES CLÉS (Doivent matcher les attributs Java côté Serveur)
            json.put("amount", contrib.getAmount());
            json.put("comment", contrib.getComment());
            
            // C'EST ICI QUE CA SE JOUE :
            json.put("giftId", giftId);  // Important
            json.put("userId", user.getId()); // Important
            
            // Debug du JSON envoyé
            System.out.println("DEBUG CLIENT DAO: Envoi JSON -> " + json.toString());

            HttpRequest request = HttpRequest.newBuilder()
                    // ... (headers) ...
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            // AJOUTE CE LOG :
            System.out.println("DEBUG CLIENT DAO: Réponse API -> Code: " + response.statusCode() + " | Body: " + response.body());

            // ... (reste du code)
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    // Méthode utilitaire de mapping
    private Contribution buildContribution(JSONObject json) {
        Contribution c = new Contribution();
        
        // Utilise optInt/optDouble pour éviter les crashs si la valeur manque
        c.setId(json.optInt("id")); 
        c.setAmount(json.optDouble("amount"));
        
        // Gestion sécurisée de la date
        if (!json.isNull("contributedAt")) {
            // On récupère l'objet, on le transforme en String, et on parse
            // Cela protège contre le cas où ce n'est pas strictement un objet String JSON
            String dateStr = json.get("contributedAt").toString();
            
            // On nettoie les guillemets éventuels si toString() en ajoute
            dateStr = dateStr.replace("\"", "");

            try {
                // Si le serveur envoie un tableau [2025,12...], le parse va échouer ici
                // Mais grâce à l'étape 1 (Serveur), dateStr sera bien "2025-12-..."
                c.setContributedAt(LocalDateTime.parse(dateStr)); 
            } catch (Exception e) {
                System.err.println("Erreur parsing date contribution: " + dateStr);
            }
        }
        
        if (!json.isNull("comment")) {
            c.setComment(json.optString("comment"));
        }
        
        return c;
    }

    // Méthodes abstraites non utilisées ici
    @Override public Contribution find(int id) { return null; }
    @Override public List<Contribution> findAll() { return null; }
    @Override public boolean create(Contribution obj) { return false; }
    @Override public boolean delete(Contribution obj) { return false; }
    @Override public boolean update(Contribution obj) { return false; }
}