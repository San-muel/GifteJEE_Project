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
	private String psw; // Mot de passe (utilisé uniquement pour le login)
	private String token; // Token d'authentification JWT
	
	// Relations complexes (non utilisées pour l'authentification, mais complètent le modèle)
	private Set<Contribution> contributions = new HashSet<>();
	private Set<Wishlist> WishlistPartager = new HashSet<>();
	private Set<Wishlist> WishlistCreer = new HashSet<>();
	private Set<SharedWishlist> InfoWishlist = new HashSet<>();
	
    public User() {}
    
    public User(int id, String username, String email, String psw) {
        this();
        this.id = id;
        this.username = username;
        this.email = email;
        this.psw = psw;
    }
    
    // --- Getters et Setters ---

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    // Le token est stocké dans l'objet après le login
    public void setToken(String token) {
        this.token = token;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPsw() {
        return psw;
    }

    public void setPsw(String psw) {
        this.psw = psw;
    }

    public Set<Contribution> getContributions() {
        return contributions;
    }

    public void setContributions(Set<Contribution> contributions) {
        this.contributions = contributions;
    }

    public Set<Wishlist> getWishlistCreer() {
        return WishlistCreer;
    }

    public void setWishlistCreer(Set<Wishlist> WishlistCreer) {
        this.WishlistCreer = WishlistCreer;
    }

    public Set<Wishlist> getWishlistPartager() {
        return WishlistPartager;
    }

    public void setWishlistPartager(Set<Wishlist> WishlistPartager) {
        this.WishlistPartager = WishlistPartager;
    }

    public Set<SharedWishlist> getInfoWishlist() {
        return InfoWishlist;
    }

    public void setInfoWishlist(Set<SharedWishlist> InfoWishlist) {
        this.InfoWishlist = InfoWishlist;
    }
    
    // --- Méthodes Object ---

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o; // OK
        return id == user.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}