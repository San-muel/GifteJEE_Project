package be.project.MODEL;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import be.project.DAO.ContributionDAO;
import be.project.DAO.WishlistDAO;

public class Wishlist implements Serializable {

    private static final long serialVersionUID = -6934862286099117393L;
    private int id;
    private String title;
    private String occasion;         
    private LocalDate expirationDate;
    private Status status; 
    private Set<Gift> gifts = new HashSet<>();

    public Wishlist() {}
    
    public Wishlist(int id, String title, String occasion, LocalDate expirationDate, Status status) {
        this();
        this.id = id;
        this.title = title;
        this.occasion = occasion;
        this.expirationDate = expirationDate;
        this.status = status;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getOccasion() { return occasion; }
    public void setOccasion(String occasion) { this.occasion = occasion; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public Set<Gift> getGifts() { return gifts; }
    public void setGifts(Set<Gift> gifts) { this.gifts = gifts; }
    
    public static List<Wishlist> findAll() {
        WishlistDAO dao = new WishlistDAO();
        return dao.findAll();
    }

    public static Wishlist find(int id) {
        WishlistDAO dao = new WishlistDAO();
        return dao.find(id);
    }

    public static List<Wishlist> findActiveAndValid() {
        List<Wishlist> allWishlists = findAll();
        LocalDate today = LocalDate.now();

        return allWishlists.stream()
            .filter(w -> w.getStatus() == Status.ACTIVE)
            .filter(w -> w.getExpirationDate() == null || !w.getExpirationDate().isBefore(today))
            .collect(Collectors.toList());
    }

    public boolean toggleStatus(User user) {
        System.out.println("[METIER MODEL] Début de la bascule. Statut actuel : " + this.status);
        
        if (this.status == Status.ACTIVE) {
            this.status = Status.INACTIVE; 
            System.out.println("[METIER MODEL] Passage en INACTIVE.");
        } else {
            if (this.expirationDate != null && this.expirationDate.isAfter(java.time.LocalDate.now())) {
                this.status = Status.ACTIVE;
                System.out.println("[METIER MODEL] Date valide ("+this.expirationDate+"), passage en ACTIVE.");
            } else {
                this.status = Status.EXPIRED;
                System.out.println("[METIER MODEL] ECHEC : Date expirée ou nulle, statut mis à EXPIRED.");
            }
        }

        System.out.println("[ACTIVE RECORD] Envoi de la mise à jour vers le DAO...");
        WishlistDAO dao = new WishlistDAO();
        boolean success = dao.updateWishlist(this, user);
        
        System.out.println("[ACTIVE RECORD] Résultat de l'update API : " + (success ? "SUCCÈS" : "ÉCHEC"));
        return success;
    }
    
    public static Wishlist fromForm(String title, String occasion, String statusStr, String dateStr) {
        Wishlist w = new Wishlist();
        w.setTitle(title);
        w.setOccasion(occasion);

        if (statusStr != null && !statusStr.isEmpty()) {
            try {
                w.setStatus(Status.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                w.setStatus(Status.ACTIVE);
            }
        }

        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                w.setExpirationDate(LocalDate.parse(dateStr));
            } catch (Exception e) {
                w.setExpirationDate(null);
            }
        }
        return w;
    }
    
    public void loadAllGiftsContributions() {
        if (this.gifts == null || this.gifts.isEmpty()) return;

        ContributionDAO contributionDAO = new ContributionDAO();
        for (Gift gift : this.gifts) {
            List<Contribution> list = contributionDAO.findAllByGiftId(gift.getId());
            gift.setContributions(new HashSet<>(list));
        }
    }
    
    public List<Gift> getGiftsSortedByPriority() {
        if (this.gifts == null) return new ArrayList<>();
        
        List<Gift> sorted = new ArrayList<>(this.gifts);
        Collections.sort(sorted, (g1, g2) -> {
            Integer p1 = (g1.getPriority() != null) ? g1.getPriority() : 3;
            Integer p2 = (g2.getPriority() != null) ? g2.getPriority() : 3;
            return p1.compareTo(p2);
        });
        return sorted;
    }
    
    @Override
    public String toString() {
        return "Wishlist{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", occasion='" + occasion + '\'' +
                ", status='" + status + '\'' +
                ", nbGifts=" + gifts.size() +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wishlist wishlist = (Wishlist) o;
        return id == wishlist.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}