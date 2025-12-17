package be.project.servlet;

import be.project.MODEL.Gift;
import be.project.MODEL.User;
import be.project.MODEL.Wishlist;
import be.project.service.GiftService; 
import java.io.IOException;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/gift/*") 
public class GiftServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
    private final GiftService giftService = new GiftService();

    public GiftServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
	    String path = request.getPathInfo(); 
        User user = (User) request.getSession().getAttribute("user");

        // 1. Vérification de l'authentification
        if (user == null) {
             response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous devez être connecté pour effectuer cette action.");
             return;
        }
        
        // Normalisation du chemin si le développeur utilise /update ou /modify indifféremment
        if ("/update".equals(path)) {
            path = "/modify";
        }

	    if ("/add".equals(path)) {
            handleAddGift(request, response, user);
	    } else if ("/modify".equals(path)) { 
            handleModifyGift(request, response, user);
        } else if ("/delete".equals(path)) { // AJOUT de la suppression
            handleDeleteGift(request, response, user);
        } else {
            // Cette erreur était la cause du 404/Action non reconnue
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Action non reconnue.");
        }
	}
    
    /**
     * Logique de création d'un cadeau (POST /gift/add).
     */
    private void handleAddGift(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {

        // --- 1. Récupérer et valider les paramètres ---
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String priorityStr = request.getParameter("priority");
        String photoUrl = request.getParameter("photoUrl");
        String wishlistIdStr = request.getParameter("wishlistId"); 

        if (name == null || name.isEmpty() || priceStr == null || priceStr.isEmpty() || wishlistIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/home?error=invalid_fields"); 
            return;
        }

        try {
            double price = Double.parseDouble(priceStr);
            int wishlistId = Integer.parseInt(wishlistIdStr);
            Integer priority = (priorityStr != null && !priorityStr.isEmpty()) ? Integer.parseInt(priorityStr) : null;

            // --- 2. Créer l'objet Gift (Modèle) ---
            Gift newGift = new Gift();
            newGift.setName(name);
            newGift.setDescription(description);
            newGift.setPrice(price);
            newGift.setPriority(priority);
            newGift.setPhotoUrl(photoUrl);
            
            Wishlist associatedWishlist = new Wishlist();
            associatedWishlist.setId(wishlistId); 
            newGift.setwishlist(associatedWishlist); 

            // --- 3. Appeler le Service (Logique métier/DAO) ---
            Optional<Gift> createdGift = giftService.addGift(newGift, user);
            
            if (createdGift.isPresent()) {
                response.sendRedirect(request.getContextPath() + "/home?status=gift_added");
            } else {
                response.sendRedirect(request.getContextPath() + "/home?error=gift_failed"); 
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home?error=number_format"); 
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/home?error=unknown");
        }
    }
    
    /**
     * Logique de modification d'un cadeau (POST /gift/modify ou /gift/update).
     * RAPPEL: L'ID du cadeau est obligatoire ici.
     */
    private void handleModifyGift(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {

        // --- 1. Récupérer et valider les paramètres (y compris l'ID) ---
        String idStr = request.getParameter("giftId"); // L'ID du cadeau est obligatoire
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String priorityStr = request.getParameter("priority");
        String photoUrl = request.getParameter("photoUrl");
        String wishlistIdStr = request.getParameter("wishlistId"); 

        if (idStr == null || idStr.isEmpty() || name == null || name.isEmpty() || priceStr == null || priceStr.isEmpty() || wishlistIdStr == null) {
            response.sendRedirect(request.getContextPath() + "/home?error=invalid_modify_fields"); 
            return;
        }

        try {
        	int giftId = Integer.parseInt(idStr);
            double price = Double.parseDouble(priceStr);
            int wishlistId = Integer.parseInt(wishlistIdStr);
            Integer priority = (priorityStr != null && !priorityStr.isEmpty()) ? Integer.parseInt(priorityStr) : null;

            // 🚨 AJOUT DES LOGS DE DÉBOGAGE ICI 🚨
            System.out.println("SERVLET DEBUG: TENTATIVE DE MODIFICATION DE CADEAU");
            System.out.println("SERVLET DEBUG: Paramètre lu - giftId (ID Cadeau): " + idStr + " (int: " + giftId + ")");
            System.out.println("SERVLET DEBUG: Paramètre lu - wishlistId (ID Liste): " + wishlistIdStr + " (int: " + wishlistId + ")");
            System.out.println("SERVLET DEBUG: Paramètre lu - name: " + name);
            System.out.println("SERVLET DEBUG: Utilisateur en session - ID: " + user.getId());
            // ------------------------------------
            
            // --- 2. Créer l'objet Gift (Modèle) à modifier ---
            Gift modifiedGift = new Gift();
            modifiedGift.setId(giftId); // ESSENTIEL
            modifiedGift.setName(name);
            modifiedGift.setDescription(description);
            modifiedGift.setPrice(price);
            modifiedGift.setPriority(priority);
            modifiedGift.setPhotoUrl(photoUrl);
            
            Wishlist associatedWishlist = new Wishlist();
            associatedWishlist.setId(wishlistId); 
            modifiedGift.setwishlist(associatedWishlist); 

            // --- 3. Appeler le Service (Logique métier/DAO) ---
            boolean success = giftService.modifyGift(modifiedGift, user);
            
            if (success) {
                response.sendRedirect(request.getContextPath() + "/home?status=gift_modified");
            } else {
                response.sendRedirect(request.getContextPath() + "/home?error=gift_modify_failed"); 
            }

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home?error=modify_number_format"); 
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/home?error=modify_unknown");
        }
    }
    
    /**
     * Logique de suppression d'un cadeau (POST /gift/delete).
     */
    private void handleDeleteGift(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        
        // --- 1. Récupérer l'ID du cadeau à supprimer ---
        String idStr = request.getParameter("giftId"); 

        if (idStr == null || idStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/home?error=delete_id_missing");
            return;
        }
        
        try {
            int giftId = Integer.parseInt(idStr);
            
            // --- 2. Appeler le Service (Logique métier/DAO) ---
            System.out.println("SERVLET DEBUG: Tentative de suppression du cadeau ID: " + giftId);
            boolean success = giftService.deleteGift(giftId, user); // <-- Appel au service
            
            if (success) {
                System.out.println("SERVLET DEBUG: Suppression réussie. Redirection.");
                response.sendRedirect(request.getContextPath() + "/home?status=gift_deleted");
            } else {
                System.out.println("SERVLET DEBUG: Suppression échouée.");
                response.sendRedirect(request.getContextPath() + "/home?error=gift_delete_failed"); 
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/home?error=delete_invalid_id"); 
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/home?error=delete_unknown");
        }
    }
    
}