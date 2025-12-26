package be.project.MODEL;

import be.project.DAO.GiftDAO;
import java.io.Serializable;
import java.util.ArrayList; // Changé HashSet en ArrayList
import java.util.List;      // Changé Set en List
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Gift implements Serializable {

    private static final long serialVersionUID = 1638358617492812187L;
    private int id;
    private String name;
    private String description;
    private double price;
    private Integer priority;
    private String photoUrl;
    
    // On utilise une List pour garder l'ordre et faciliter le mapping JSON
    private List<Contribution> contributions = new ArrayList<>();

    public Gift() {}
    
    // --- METHODES ACTIVE RECORD ---

    public boolean save(int wishlistId, User user, GiftDAO giftDAO) {
        Optional<Gift> result = giftDAO.createGift(this, wishlistId, user);
        if (result.isPresent()) {
            this.id = result.get().getId();
            return true;
        }
        return false;
    }

    public boolean update(int wishlistId, User user, GiftDAO giftDAO) {
        return giftDAO.updateGift(this, wishlistId, user);
    }

    public boolean delete(int wishlistId, User user, GiftDAO giftDAO) {
        return giftDAO.deleteGift(this.id, wishlistId, user);
    }

    // --- GETTERS / SETTERS ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }
    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    // --- GESTION DES CONTRIBUTIONS ---

    public List<Contribution> getContributions() {
        return contributions;
    }

    public void setContributions(List<Contribution> contributions) {
        this.contributions = contributions;
    }

    // --- CALCULS AUTOMATIQUES (Appelés par la JSP) ---

    /**
     * Calcule le total déjà récolté.
     * La JSP l'appelle via ${gift.collectedAmount}
     */
    public double getCollectedAmount() {
        if (contributions == null || contributions.isEmpty()) {
            return 0.0;
        }
        // Somme des montants
        return contributions.stream()
                            .mapToDouble(Contribution::getAmount)
                            .sum();
    }

    /**
     * Calcule le reste à payer.
     * La JSP l'appelle via ${gift.remainingAmount}
     */
    public double getRemainingAmount() {
        double remaining = this.price - getCollectedAmount();
        return Math.max(0, remaining); // Empêche les nombres négatifs
    }

    @JsonIgnore
    public GiftStatus getStatus() {
        double total = getCollectedAmount(); // On réutilise la méthode de calcul
        if (total >= price && total > 0) return GiftStatus.FUNDED;
        if (total > 0) return GiftStatus.PARTIALLY_FUNDED;
        return GiftStatus.AVAILABLE;
    }
}