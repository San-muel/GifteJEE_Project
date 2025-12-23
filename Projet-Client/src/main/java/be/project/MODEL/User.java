package be.project.MODEL;

import be.project.DAO.SharedWishlistDAO;
import be.project.DAO.UserDAO;
import be.project.DAO.WishlistDAO;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class User implements Serializable {
    private static final long serialVersionUID = 1339986681925033665L;
    private int id;
    private String username;
    private String email;
    private String psw; 
    private String token; 
    
    
    private Set<Contribution> contributions = new HashSet<>();
    private Set<Wishlist> WishlistPartager = new HashSet<>();
    private Set<Wishlist> createdWishlists = new HashSet<>();
    private Set<SharedWishlist> InfoWishlist = new HashSet<>();

    // DAO pour la persistance REST
    private static final UserDAO userDAO = new UserDAO();
    

    public User() {}

    // --- Méthodes Active Record ---

    public User login(String email, String password) throws Exception {
        return userDAO.authenticate(email, password);
    }

    public boolean register() {
        if (this.email == null || this.email.isEmpty()) return false;
        return userDAO.create(this);
    }

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
        return createdWishlists;
    }

    public void setCreatedWishlists(Set<Wishlist> createdWishlists) {
        this.createdWishlists = createdWishlists;
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

    public void addGiftLocally(int wishlistId, Gift gift) {
        this.createdWishlists.stream()
            .filter(w -> w.getId() == wishlistId)
            .findFirst()
            .ifPresent(w -> w.getGifts().add(gift));
    }

    /**
     * Supprime un cadeau localement
     */
    public void removeGiftLocally(int wishlistId, int giftId) {
        this.createdWishlists.stream()
            .filter(w -> w.getId() == wishlistId)
            .findFirst()
            .ifPresent(w -> w.getGifts().removeIf(g -> g.getId() == giftId));
    }

    /**
     * Met à jour un cadeau localement
     */
    public void updateGiftLocally(int wishlistId, Gift updatedGift) {
        this.createdWishlists.stream()
            .filter(w -> w.getId() == wishlistId)
            .findFirst()
            .ifPresent(w -> {
                w.getGifts().removeIf(g -> g.getId() == updatedGift.getId());
                w.getGifts().add(updatedGift);
            });
    }
    
    public static List<User> fetchAllSystemUsers() {
        // Appelle userDAO.findAll() qui lui-même appelle l'API /users/all
        return userDAO.findAll(); 
    }
    
    public boolean acceptPublicInvitation(int wishlistId) {
        // 1. On instancie le DAO
        WishlistDAO wishlistDAO = new WishlistDAO();
        
        // 2. On appelle la méthode share que tu m'as montrée
        // 'this.id' est l'ID de l'utilisateur qui vient de s'inscrire
        // On passe un message spécifique pour le lien public
        return wishlistDAO.share(wishlistId, this.id, "J'ai rejoint ta liste via ton lien public !");
    }
    
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
    

    public void addWishlistLocally(Wishlist wishlist) {
        if (this.createdWishlists == null) {
            this.createdWishlists = new HashSet<>(); // ou ArrayList selon ton implémentation
        }
        this.createdWishlists.add(wishlist);
    }
}