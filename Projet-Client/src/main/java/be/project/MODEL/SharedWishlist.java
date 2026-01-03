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
    
    private static final WishlistDAO wishlistDAO = new WishlistDAO();

    public SharedWishlist() {}

    public SharedWishlist(int id, LocalDateTime sharedAt, String notification) {
        this.id = id;
        this.sharedAt = sharedAt;
        this.notification = notification;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getSharedAt() { return sharedAt; }
    public void setSharedAt(LocalDateTime sharedAt) { this.sharedAt = sharedAt; }
    public String getNotification() { return notification; }
    public void setNotification(String notification) { this.notification = notification; }

    public boolean shareWishlist(int wishlistId, int targetUserId, String note, String token) {
        this.notification = note;
        return wishlistDAO.share(wishlistId, targetUserId, note, token); 
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