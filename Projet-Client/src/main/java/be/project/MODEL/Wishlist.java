package be.project.MODEL;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

// On garde les deux imports ici
import be.project.DAO.WishlistDAO;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Wishlist implements Serializable {

	private static final long serialVersionUID = -6934862286099117393L;
	private int id;
    private String title;
    private String occasion;         
    private LocalDate expirationDate;
    private Status status; 
    private Set<Gift> gifts = new HashSet<>();
    public Wishlist() {}
    
    public Wishlist(int id, String title, String occasion, LocalDate expirationDate,
    			Status status) {
		this();
		this.id = id;
		this.title = title;
		this.occasion = occasion;
		this.expirationDate = expirationDate;
		this.status = status;
	}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOccasion() {
        return occasion;
    }

    public void setOccasion(String occasion) {
        this.occasion = occasion;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<Gift> getGifts() {
        return gifts;
    }

    public void setGifts(Set<Gift> gifts) {
        this.gifts = gifts;
    }
    
    // Cette méthode utilise WishlistDAO, d'où l'importance de l'import
    public static List<Wishlist> findAll() {
        WishlistDAO dao = new WishlistDAO();
        return dao.findAll();
    }
 // Dans be.project.MODEL.Wishlist.java

 // Dans be.project.MODEL.Wishlist.java (Côté Client)

    public boolean toggleStatus(User user) {
        System.out.println("[METIER MODEL] Début de la bascule. Statut actuel : " + this.status);
        
        // Comparaison correcte avec l'Enum
        if (this.status == Status.ACTIVE) {
            // Si c'est ACTIVE, on passe en INACTIVE (ou PRIVATE selon ton Enum)
            this.status = Status.INACTIVE; 
            System.out.println("[METIER MODEL] Passage en INACTIVE.");
        } else {
            // Si c'est INACTIVE ou EXPIRED, on vérifie la date pour réactiver
            if (this.expirationDate != null && this.expirationDate.isAfter(java.time.LocalDate.now())) {
                this.status = Status.ACTIVE;
                System.out.println("[METIER MODEL] Date valide ("+this.expirationDate+"), passage en ACTIVE.");
            } else {
                // Si la date est passée, on peut forcer le statut EXPIRED
                this.status = Status.EXPIRED;
                System.out.println("[METIER MODEL] ECHEC : Date expirée ou nulle, statut mis à EXPIRED.");
            }
        }

        // Persistance vers l'API
        System.out.println("[ACTIVE RECORD] Envoi de la mise à jour vers le DAO...");
        WishlistDAO dao = new WishlistDAO();
        boolean success = dao.updateWishlist(this, user);
        
        System.out.println("[ACTIVE RECORD] Résultat de l'update API : " + (success ? "SUCCÈS" : "ÉCHEC"));
        return success;
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
    public static Wishlist find(int id) {
        WishlistDAO dao = new WishlistDAO();
        return dao.find(id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}