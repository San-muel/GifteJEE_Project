package be.project.service;

import be.project.DAO.GiftDAO;
import be.project.DAO.UserDAO; // Pour rafraîchir l'utilisateur
import be.project.MODEL.Gift;
import be.project.MODEL.User;

import java.util.Optional;

public class GiftService {

    private final GiftDAO giftDAO = new GiftDAO();
    private final UserDAO userDAO = new UserDAO();

    /**
     * Ajoute un cadeau via la couche DAO et tente de rafraîchir l'objet User en session.
     * @param gift Le cadeau à ajouter (doit contenir l'objet Wishlist avec l'ID).
     * @param user L'utilisateur actuel de la session.
     * @return L'objet Gift créé par l'API (avec ID), ou Optional.empty().
     */
    public Optional<Gift> addGift(Gift gift, User user) {
        
        Optional<Gift> createdGift = giftDAO.createGift(gift, user);

        // LOGIQUE CRUCIALE : Si le cadeau est créé, nous devons rafraîchir 
        // l'objet User dans la session pour que la page "home" ou "wishlist" 
        // affiche la nouvelle donnée sans latence (car la liste des cadeaux est 
        // intégrée dans l'objet User hydraté lors de l'authentification).
        
        if (createdGift.isPresent()) {
            System.out.println("SERVICE LOGIC: Cadeau ajouté. Tentative de rafraîchissement de l'utilisateur.");
            
            // Appel au UserDAO pour recharger l'utilisateur complet depuis l'API.
            // (Nous supposons qu'il existe une méthode findById dans votre UserDAO)
            // User refreshedUser = userDAO.find(user.getId()); 
            // C'est souvent plus simple de demander à l'API un endpoint de rafraîchissement.
            
            // Pour l'exemple, nous allons faire un appel simple d'authentification 
            // si l'API renvoie l'utilisateur complet à chaque login.
            // ATTENTION : Cette ligne n'est pas optimale si l'API ne retourne 
            // pas le token si on utilise find(id). 
            // La meilleure approche est soit un endpoint dédié pour l'utilisateur, 
            // soit re-faire l'appel au login (mais l'API doit accepter le re-login)
            
            // Piste alternative et recommandée : Ajouter une méthode findUserWithToken(token) au UserDAO
            // pour recharger l'utilisateur complet par son token (sans email/psw).
            
            // *** Simplification pour la démo (Assurez-vous que votre UserDAO a find(id)) ***
            // User refreshedUser = userDAO.find(user.getId()); 
            
            // L'approche la plus simple et sécurisée est de forcer le client à 
            // rafraîchir l'utilisateur via une redirection vers un point de 
            // rafraîchissement qui va faire findUserWithToken(user.getToken()).
            
            // Nous allons laisser la Servlet gérer la redirection vers /home 
            // et vous laisser décider comment HomeServlet rafraîchira la session User.
        }
        
        return createdGift;
    }
    public boolean modifyGift(Gift gift, User user) {
        
        boolean success = giftDAO.updateGift(gift, user); // <-- Appel à la nouvelle méthode du DAO

        // LOGIQUE CRUCIALE : Si la modification est réussie, nous devons rafraîchir 
        // l'objet User dans la session.
        if (success) {
            System.out.println("SERVICE LOGIC: Cadeau modifié. Tentative de rafraîchissement de l'utilisateur.");
            // RAPPEL : La logique de rafraîchissement de l'utilisateur doit être implémentée 
            // soit ici, soit via une redirection vers une servlet qui s'en charge.
        }
        
        return success;
    }
    
	public boolean deleteGift(int giftId, User user)
	{
	        
        System.out.println("SERVICE LOGIC: Tentative de suppression Gift ID " + giftId);
        
        // 1. Appel au DAO
        boolean success = giftDAO.deleteGift(giftId, user); 

        // 2. Logique de rafraîchissement (post-suppression)
        if (success) {
            System.out.println("SERVICE LOGIC: Cadeau supprimé. Tentative de rafraîchissement de l'utilisateur.");
            // RAPPEL : La logique de rafraîchissement de l'utilisateur doit être implémentée.
        } else {
             System.out.println("SERVICE LOGIC: Suppression échouée (non autorisé ou cadeau introuvable).");
        }
        
        return success;
    }
}