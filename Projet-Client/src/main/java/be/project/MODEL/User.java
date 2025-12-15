package be.project.MODEL;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class User implements Serializable {

	private static final long serialVersionUID = 1339986681925033665L;
	private int id;
	private String username;
	private String email;
	private String psw; 
	private String token; 
	
	// Relations complexes (Les noms des champs internes restent comme avant)
	private Set<Contribution> contributions = new HashSet<>();
	private Set<Wishlist> WishlistPartager = new HashSet<>();
	private Set<Wishlist> WishlistCreer = new HashSet<>();
	private Set<SharedWishlist> InfoWishlist = new HashSet<>();
	
    public User() {}
    
    // ... (Constructeur inchangé) ...
    
    // --- Getters et Setters BASIQUES (Inchangés) ---

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPsw() { return psw; }
    public void setPsw(String psw) { this.psw = psw; }
    public Set<Contribution> getContributions() { return contributions; }
    public void setContributions(Set<Contribution> contributions) { this.contributions = contributions; }
    
    // --- Getters/Setters pour les RELATIONS (CORRIGÉS POUR LA JSP/EL) ---

    /**
     * Getter correct, utilisé par EL: ${user.createdWishlists}
     * Mappe sur le champ interne WishlistCreer.
     */
    public Set<Wishlist> getCreatedWishlists() {
        return WishlistCreer;
    }

    public void setCreatedWishlists(Set<Wishlist> createdWishlists) {
        this.WishlistCreer = createdWishlists;
    }
    
    /**
     * Ancien Getter (Laisser pour la compatibilité si le DAO API le demande, 
     * mais il est préférable de migrer le DAO vers getCreatedWishlists)
     */
    public Set<Wishlist> getWishlistCreer() {
        return WishlistCreer;
    }
    public void setWishlistCreer(Set<Wishlist> WishlistCreer) {
        this.WishlistCreer = WishlistCreer;
    }

    /**
     * Getter correct, utilisé par EL: ${user.sharedWishlists}
     * Mappe sur le champ interne WishlistPartager.
     */
    public Set<Wishlist> getSharedWishlists() {
        return WishlistPartager;
    }

    public void setSharedWishlists(Set<Wishlist> sharedWishlists) {
        this.WishlistPartager = sharedWishlists;
    }

    /**
     * Ancien Getter (Laisser pour la compatibilité)
     */
    public Set<Wishlist> getWishlistPartager() {
        return WishlistPartager;
    }
    public void setWishlistPartager(Set<Wishlist> WishlistPartager) {
        this.WishlistPartager = WishlistPartager;
    }

    /**
     * Getter correct, utilisé par EL: ${user.sharedWishlistInfos}
     * Mappe sur le champ interne InfoWishlist.
     */
    public Set<SharedWishlist> getSharedWishlistInfos() {
        return InfoWishlist;
    }

    public void setSharedWishlistInfos(Set<SharedWishlist> sharedWishlistInfos) {
        this.InfoWishlist = sharedWishlistInfos;
    }
    
    /**
     * Ancien Getter (Laisser pour la compatibilité)
     */
    public Set<SharedWishlist> getInfoWishlist() {
        return InfoWishlist;
    }
    public void setInfoWishlist(Set<SharedWishlist> InfoWishlist) {
        this.InfoWishlist = InfoWishlist;
    }
    
    // --- Méthodes Object (Inchangées) ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}