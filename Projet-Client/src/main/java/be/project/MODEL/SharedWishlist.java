package be.project.MODEL;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import be.project.DAO.WishlistDAO;

public class SharedWishlist implements Serializable {

    private static final long serialVersionUID = -8206067521209338294L;
    private int id;
    private LocalDateTime sharedAt;  
    private String notification;       
    
    // On garde ton DAO existant
    private static final WishlistDAO wishlistDAO = new WishlistDAO();

    public SharedWishlist() {}

    public SharedWishlist(int id, LocalDateTime sharedAt, String notification) {
        this.id = id;
        this.sharedAt = sharedAt;
        this.notification = notification;
    }

    // --- GETTERS & SETTERS ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getSharedAt() { return sharedAt; }
    public void setSharedAt(LocalDateTime sharedAt) { this.sharedAt = sharedAt; }
    public String getNotification() { return notification; }
    public void setNotification(String notification) { this.notification = notification; }

    /**
     * Correction : Ajout du paramètre 'message' pour ne pas perdre la saisie utilisateur
     */
    public boolean shareWishlist(int wishlistId, int targetUserId, String message) {
        // On met à jour l'attribut de l'instance au cas où on en aurait besoin
        this.notification = message;
        
        // On transmet le message au DAO pour qu'il soit inséré en base de données
        // Assure-toi que wishlistDAO.share accepte (int, int, String)
        return wishlistDAO.share(wishlistId, targetUserId, message); 
    }
    
    // Overload pour garder la compatibilité si nécessaire
    public boolean shareWishlist(int wishlistId, int targetUserId) {
        return this.shareWishlist(wishlistId, targetUserId, null);
    }

    @Override
    public String toString() {
        return "SharedWishlist{id=" + id + ", sharedAt=" + sharedAt + ", notification='" + notification + "'}";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedWishlist that = (SharedWishlist) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}