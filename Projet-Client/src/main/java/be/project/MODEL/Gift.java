package be.project.MODEL;

import be.project.DAO.GiftDAO;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // 1. Importation à ajouter

@JsonIgnoreProperties(ignoreUnknown = true)
public class Gift implements Serializable {

	private static final long serialVersionUID = 1638358617492812187L;
	private int id;
    private String name;
    private String description;
    private double price;
    private Integer priority;  
    private String photoUrl;
    private Set<Contribution> contributions = new HashSet<>();

    public Gift() {}
    
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

    // --- Getters / Setters ---
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

    @JsonIgnore
    public GiftStatus getStatus() {
        double total = contributions.stream().mapToDouble(Contribution::getAmount).sum();
        if (total >= price && total > 0) return GiftStatus.FUNDED;
        if (total > 0) return GiftStatus.PARTIALLY_FUNDED;
        return GiftStatus.AVAILABLE;
    }
}