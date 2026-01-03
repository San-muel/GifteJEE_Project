package be.project.MODEL;

import be.project.DAO.UserDAO;
import be.project.DAO.WishlistDAO;
import java.io.Serializable;
import java.util.ArrayList;
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

    private static final UserDAO userDAO = new UserDAO();

    public User() {}

    public static User login(String email, String password) throws Exception {
        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("Veuillez remplir tous les champs.");
        }

        User foundUser = userDAO.authenticate(email, password);
        
        if (foundUser == null) {
            throw new IllegalArgumentException("Email ou mot de passe invalide.");
        }
        
        return foundUser;
    }

    public boolean register() {
        if (this.email == null || this.email.isEmpty()) return false;
        return userDAO.create(this);
    }
    
    public User completeRegistration(String password, Integer pendingWishlistId) throws Exception {
        if (!this.register()) return null; 
        User loggedUser = this.login(this.email, password);
        if (loggedUser != null && pendingWishlistId != null) {
            boolean shared = loggedUser.acceptPublicInvitation(pendingWishlistId);
            if (shared) loggedUser.refresh(); 
        }
        return loggedUser;
    }

    public void syncContributionAddition(Contribution c) {
        if (this.contributions == null) {
            this.contributions = new HashSet<>();
        }
        this.contributions.add(c);
        System.out.println("[USER MEMORY] Contribution ajoutée à la session utilisateur.");
    }
    
    public boolean createWishlist(String title, String occasion, String statusStr, String dateStr) {
        Wishlist toCreate = Wishlist.fromForm(title, occasion, statusStr, dateStr);
        WishlistDAO dao = new WishlistDAO();
        java.util.Optional<Wishlist> created = dao.createWishlist(toCreate, this);
        if (created.isPresent()) {
            this.addWishlistLocally(created.get());
            return true;
        }
        return false;
    }

    public boolean deleteWishlist(int wishlistId) {
        WishlistDAO dao = new WishlistDAO();
        boolean success = dao.deleteWishlist(wishlistId, this);
        if (success) {
            this.createdWishlists.removeIf(w -> w.getId() == wishlistId);
        }
        return success;
    }

    public boolean updateWishlist(int id, String title, String occasion, String statusStr, String dateStr) {
        Wishlist toUpdate = Wishlist.fromForm(title, occasion, statusStr, dateStr);
        toUpdate.setId(id);
        WishlistDAO dao = new WishlistDAO();
        boolean success = dao.updateWishlist(toUpdate, this);
        if (success) {
            this.createdWishlists.removeIf(w -> w.getId() == id);
            this.createdWishlists.add(toUpdate);
        }
        return success;
    }
    
    public void updateWishlistStatusLocally(int wishlistId, Status newStatus) {
        if (this.createdWishlists == null) return;

        for (Wishlist w : this.createdWishlists) {
            if (w.getId() == wishlistId) {
                w.setStatus(newStatus);
                System.out.println("[USER MEMORY] Statut mis à jour localement pour la liste " + wishlistId);
                break; 
            }
        }
    }

    public boolean reactivateWishlist(int wishlistId, String newDateStr) {
        Wishlist target = null;
        if (this.createdWishlists != null) {
            for (Wishlist w : this.createdWishlists) {
                if (w.getId() == wishlistId) {
                    target = w;
                    break;
                }
            }
        }
        if (target == null) return false;
        return this.updateWishlist(wishlistId, target.getTitle(), target.getOccasion(), "ACTIVE", newDateStr);
    }

    public boolean shareMyWishlist(int wishlistId, int targetUserId, String note) {
        boolean ownsList = this.createdWishlists.stream().anyMatch(w -> w.getId() == wishlistId);
        if (!ownsList) return false;
        SharedWishlist shareAction = new SharedWishlist();
        return shareAction.shareWishlist(wishlistId, targetUserId, note, String.valueOf(this.getId()));
    }
    
    public boolean acceptPublicInvitation(int wishlistId) {
        WishlistDAO wishlistDAO = new WishlistDAO();
        return wishlistDAO.share(
            wishlistId, 
            this.id, 
            "J'ai rejoint ta liste via ton lien public !", 
            this.token
        );
    }

    public void syncGiftAddition(int wishlistId, Gift newGift) {
        if (this.createdWishlists == null) return;
        this.createdWishlists.stream()
            .filter(wl -> wl.getId() == wishlistId)
            .findFirst()
            .ifPresent(wl -> wl.getGifts().add(newGift));
    }

    public void syncGiftRemoval(int wishlistId, int giftId) {
        if (this.createdWishlists == null) return;
        this.createdWishlists.stream()
            .filter(wl -> wl.getId() == wishlistId)
            .findFirst()
            .ifPresent(wl -> wl.getGifts().removeIf(g -> g.getId() == giftId));
    }

    public void syncGiftUpdate(int wishlistId, Gift updatedGift) {
        if (this.createdWishlists == null) return;
        this.createdWishlists.stream()
            .filter(wl -> wl.getId() == wishlistId)
            .findFirst()
            .ifPresent(wl -> {
                for (Gift g : wl.getGifts()) {
                    if (g.getId() == updatedGift.getId()) {
                        g.setName(updatedGift.getName());
                        g.setPrice(updatedGift.getPrice());
                        g.setSiteUrl(updatedGift.getSiteUrl());
                        g.setPhotoUrl(updatedGift.getPhotoUrl());
                        g.setDescription(updatedGift.getDescription());
                        if(updatedGift.getPriority() != null) g.setPriority(updatedGift.getPriority());
                        break;
                    }
                }
            });
    }

    public void addWishlistLocally(Wishlist wishlist) {
        if (this.createdWishlists == null) this.createdWishlists = new HashSet<>();
        this.createdWishlists.add(wishlist);
    }
    public void refreshSharedListsData() {
        if (this.WishlistPartager == null) return;

        for (Wishlist wl : this.WishlistPartager) {
            if (wl.getStatus() == Status.ACTIVE) {
                wl.loadAllGiftsContributions(); 
            }
        }
    }

    public List<String> generateDashboardNotifications() {
        List<String> notifications = new ArrayList<>();

        if (this.WishlistPartager == null) return notifications;

        for (Wishlist wl : this.WishlistPartager) {
            if (wl.getStatus() == Status.ACTIVE && wl.getGifts() != null) {
                
                for (Gift gift : wl.getGifts()) {
                    double collected = gift.getCollectedAmount();
                    
                    if (collected > 0) {
                        String statusEmoji = (gift.getRemainingAmount() <= 0.01) ? "✅" : "💸";
                        
                        String msg = String.format(
                            "%s Le cadeau <strong>%s</strong> (Liste : <em>%s</em>) a reçu des contributions (%s€ récoltés) !", 
                            statusEmoji, 
                            gift.getName(), 
                            wl.getTitle(),
                            String.format("%.2f", collected)
                        );
                        notifications.add(msg);
                    }
                }
            }
        }
        return notifications;
    }
    
    public void refresh() {
        User freshData = userDAO.find(this.id); 
        if (freshData != null) {
            this.username = freshData.getUsername();
            this.email = freshData.getEmail();
            this.createdWishlists = freshData.getCreatedWishlists();
            this.WishlistPartager = freshData.getSharedWishlists();
            this.InfoWishlist = freshData.getSharedWishlistInfos();
        }
    }
    
    public static List<User> fetchAllSystemUsers() {
        return userDAO.findAll(); 
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
    public Set<Wishlist> getCreatedWishlists() { return createdWishlists; }
    public void setCreatedWishlists(Set<Wishlist> createdWishlists) { this.createdWishlists = createdWishlists; }
    public Set<Wishlist> getSharedWishlists() { return WishlistPartager; }
    public void setSharedWishlists(Set<Wishlist> sharedWishlists) { this.WishlistPartager = sharedWishlists; }
    public Set<SharedWishlist> getSharedWishlistInfos() { return InfoWishlist; }
    public void setSharedWishlistInfos(Set<SharedWishlist> sharedWishlistInfos) { this.InfoWishlist = sharedWishlistInfos; }
    public Set<SharedWishlist> getInfoWishlist() { return InfoWishlist; } 
    public void setInfoWishlist(Set<SharedWishlist> InfoWishlist) { this.InfoWishlist = InfoWishlist; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id;
    }
    @Override
    public int hashCode() { return Objects.hash(id); }
}