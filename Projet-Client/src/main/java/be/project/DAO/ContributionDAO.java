package be.project.DAO;

import be.project.MODEL.Contribution;
import be.project.MODEL.User;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;

public class ContributionDAO extends DAO<Contribution> {

    private final HttpClient client = HttpClient.newHttpClient();

    public ContributionDAO() {}

    /**
     * Crée une contribution via l'API.
     * URL : /gifts/{giftId}/contributions
     */
    public Optional<Contribution> createContribution(Contribution contrib, int giftId, User user) {
        String url = ConfigLoad.API_BASE_URL + "gifts/" + giftId + "/contributions";
        
        try {
            // Construction manuelle du JSON avec org.json
            JSONObject json = new JSONObject();
            json.put("amount", contrib.getAmount());
            json.put("comment", contrib.getComment());
            json.put("userId", user.getId()); // On envoie l'ID de l'user connecté

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + user.getToken()) 
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                JSONObject responseJson = new JSONObject(response.body());
                return Optional.of(buildContribution(responseJson));
            } else {
                System.err.println("API Error " + response.statusCode() + ": " + response.body());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Contribution buildContribution(JSONObject json) {
        Contribution c = new Contribution();
        c.setId(json.getInt("id"));
        c.setAmount(json.getDouble("amount"));
        
        if (!json.isNull("contributedAt")) {
            c.setContributedAt(LocalDateTime.parse(json.getString("contributedAt"))); 
        }
        if (!json.isNull("comment")) {
            c.setComment(json.getString("comment"));
        }
        return c;
    }

    @Override
    public Contribution find(int id) {
        String url = ConfigLoad.API_BASE_URL + "contributions/" + id;
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return buildContribution(new JSONObject(response.body()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- Méthodes non utilisées dans ce flux ---
    @Override public List<Contribution> findAll() { return List.of(); }
    @Override public boolean create(Contribution obj) { return false; }
    @Override public boolean delete(Contribution obj) { return false; }
    @Override public boolean update(Contribution obj) { return false; }
}