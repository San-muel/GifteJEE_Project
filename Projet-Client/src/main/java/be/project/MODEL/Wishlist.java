package be.project.MODEL;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import be.project.DAO.WishlistDAO;

public class Wishlist implements Serializable {

	private static final long serialVersionUID = -6934862286099117393L;
	private int id;
    private String title;
    private String occasion;         
    private LocalDate expirationDate;
    private String status; 
    private Set<Gift> gifts = new HashSet<>();
    
    public Wishlist() {}
    
    public Wishlist(int id, String title, String occasion, LocalDate expirationDate,
            String status) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Set<Gift> getGifts() {
        return gifts;
    }

    public void setGifts(Set<Gift> gifts) {
        this.gifts = gifts;
    }
    
    public static List<Wishlist> findAll() {
        // Le modèle appelle le DAO Client
        WishlistDAO dao = new WishlistDAO();
        return dao.findAll();
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
