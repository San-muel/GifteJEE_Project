package be.project.service;

import be.project.DAO.GiftDAO;
import be.project.MODEL.Gift;
import be.project.MODEL.User;
import be.project.MODEL.Wishlist;
import java.util.Optional;

public class GiftService {
    private final GiftDAO giftDAO = new GiftDAO();

    /**
     * Ajoute un cadeau et met à jour l'utilisateur en session localement.
     */
    public Optional<Gift> addGift(Gift gift, int wishlistId, User user) {
        // Préparation de l'objet pour le DAO
        Wishlist wl = new Wishlist();
        wl.setId(wishlistId);
        gift.setwishlist(wl);

        Optional<Gift> createdGift = giftDAO.createGift(gift, user);

        // Business Logic : Mise à jour de la session si succès
        if (createdGift.isPresent()) {
            for (Wishlist w : user.getCreatedWishlists()) {
                if (w.getId() == wishlistId) {
                    w.getGifts().add(createdGift.get());
                    break;
                }
            }
        }
        return createdGift;
    }

    /**
     * Modifie un cadeau et synchronise l'objet User.
     */
    public boolean modifyGift(Gift gift, int wishlistId, User user) {
        Wishlist wl = new Wishlist();
        wl.setId(wishlistId);
        gift.setwishlist(wl);

        boolean success = giftDAO.updateGift(gift, user);

        if (success) {
            for (Wishlist w : user.getCreatedWishlists()) {
                if (w.getId() == wishlistId) {
                    for (Gift g : w.getGifts()) {
                        if (g.getId() == gift.getId()) {
                            g.setName(gift.getName());
                            g.setDescription(gift.getDescription());
                            g.setPrice(gift.getPrice());
                            g.setPriority(gift.getPriority());
                            g.setPhotoUrl(gift.getPhotoUrl());
                            break;
                        }
                    }
                }
            }
        }
        return success;
    }

    /**
     * Supprime un cadeau et le retire de la session.
     */
    public boolean deleteGift(int giftId, User user) {
        boolean success = giftDAO.deleteGift(giftId, user);

        if (success) {
            for (Wishlist w : user.getCreatedWishlists()) {
                w.getGifts().removeIf(g -> g.getId() == giftId);
            }
        }
        return success;
    }
}