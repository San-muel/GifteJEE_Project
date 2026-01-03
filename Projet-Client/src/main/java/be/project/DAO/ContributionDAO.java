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

    public List<Contribution> findAllByGiftId(int giftId) {
        System.out.println("[DEBUG-DAO] >>> Entrée findAllByGiftId pour ID cadeau : " + giftId);
    
        String baseUrl = ConfigLoad.API_BASE_URL;
        if (!baseUrl.endsWith("/")) baseUrl += "/";
        String url = baseUrl + "contributions/gift/" + giftId;
        System.out.println("[DEBUG-DAO] URL générée : " + url);
    
        List<Contribution> list = new ArrayList<>();
        
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();
    
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
    
            System.out.println("[DEBUG-DAO] Code statut HTTP reçu : " + response.statusCode());
    
            if (response.statusCode() == 200) {
                String rawBody = response.body();
                System.out.println("[DEBUG-DAO] Contenu JSON brut : " + rawBody);
    
                JSONArray array = new JSONArray(rawBody);
                System.out.println("[DEBUG-DAO] Nombre d'objets dans le tableau JSON : " + array.length());
    
                for (int i = 0; i < array.length(); i++) {
                    list.add(buildContribution(array.getJSONObject(i)));
                }
            } else {
                System.out.println("[DEBUG-DAO] ATTENTION : L'appel API a échoué (Status != 200).");
            }
        } catch (Exception e) {
            System.out.println("[DEBUG-DAO] ERREUR CRITIQUE lors de l'appel API : " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("[DEBUG-DAO] <<< Sortie findAllByGiftId. Taille liste retournée : " + list.size());
        return list;
    }

    public Optional<Contribution> createContribution(Contribution contrib, int giftId, User user) {
        String baseUrl = ConfigLoad.API_BASE_URL;
        if (!baseUrl.endsWith("/")) baseUrl += "/";
        String url = baseUrl + "contributions";
        
        System.out.println("DEBUG CLIENT DAO: Tentative envoi vers " + url);
        
        try {
            JSONObject json = new JSONObject();
            json.put("amount", contrib.getAmount());
            json.put("comment", contrib.getComment());
            json.put("giftId", giftId);       
            json.put("userId", user.getId()); 
            System.out.println("DEBUG CLIENT DAO: JSON envoyé -> " + json.toString());
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + user.getToken()) 
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            

            System.out.println("DEBUG CLIENT DAO: Réponse API -> Code: " + response.statusCode() + " | Body: " + response.body());


            if (response.statusCode() == 201 || response.statusCode() == 200) {
                JSONObject responseJson = new JSONObject(response.body());
                return Optional.of(buildContribution(responseJson));
            } else {
                System.err.println("ERREUR API Contribution: " + response.statusCode() + " - " + response.body());
            }
        } catch (Exception e) {
            System.err.println("EXCEPTION CLIENT DAO: " + e.getMessage());
            e.printStackTrace();
        }

        return Optional.empty();
    }

    private Contribution buildContribution(JSONObject json) {
        Contribution c = new Contribution();
        
        c.setId(json.optInt("id")); 
        c.setAmount(json.optDouble("amount"));
        
        if (!json.isNull("contributedAt")) {
            String dateStr = json.get("contributedAt").toString();
            
            dateStr = dateStr.replace("\"", "");

            try {
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

    @Override public Contribution find(int id) { return null; }
    @Override public List<Contribution> findAll() { return null; }
    @Override public boolean create(Contribution obj) { return false; }
    @Override public boolean delete(Contribution obj) { return false; }
    @Override public boolean update(Contribution obj) { return false; }
}