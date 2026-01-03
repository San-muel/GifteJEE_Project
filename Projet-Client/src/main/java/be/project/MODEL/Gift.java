package be.project.MODEL;

import be.project.DAO.GiftDAO;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
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
    private String siteUrl;
    private Set<Contribution> contributions = new HashSet<>();

    public Gift() {}
    
    public void calculateNewPriority(String direction) {
        int current = (this.priority == null) ? 0 : this.priority;
        if ("UP".equals(direction)) {
            this.priority = Math.max(1, current - 1);
        } else {
            this.priority = current + 1;
        }
    }

    public boolean save(int wishlistId, User user, GiftDAO giftDAO) {
        Optional<Gift> result = giftDAO.createGift(this, wishlistId, user);
        if (result.isPresent()) {
            this.id = result.get().getId();
            return true;
        }
        return false;
    }
    
    public boolean updatePriority(int wishlistId, User user, GiftDAO giftDAO) {
        return giftDAO.updatePriority(this, wishlistId, user);
    }

    public boolean update(int wishlistId, User user, GiftDAO giftDAO) {
        return giftDAO.updateGift(this, wishlistId, user);
    }

    public boolean delete(int wishlistId, User user, GiftDAO giftDAO) {
        return giftDAO.deleteGift(this.id, wishlistId, user);
    }

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
    public String getSiteUrl() {return siteUrl;}
    public void setSiteUrl(String siteUrl) {this.siteUrl = siteUrl;}
    public Set<Contribution> getContributions() { return contributions; }
    public void setContributions(Set<Contribution> contributions) { this.contributions = contributions; }

    @JsonIgnore
    public GiftStatus getStatus() {
        double total = getCollectedAmount();
        if (total >= price && total > 0) return GiftStatus.FUNDED;
        if (total > 0) return GiftStatus.PARTIALLY_FUNDED;
        return GiftStatus.AVAILABLE;
    }
    @JsonIgnore
    public double getCollectedAmount() {
        if (contributions == null || contributions.isEmpty()) return 0.0;
        return contributions.stream().mapToDouble(Contribution::getAmount).sum();
    }
    @JsonIgnore
    public double getRemainingAmount() {
        return Math.max(0, this.price - getCollectedAmount());
    }
    @JsonIgnore
    public boolean isReadOnly() {
        return getCollectedAmount() > 0;
    }
}