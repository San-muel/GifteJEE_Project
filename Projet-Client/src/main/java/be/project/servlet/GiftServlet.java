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
import javax.servlet.http.HttpSession;

@WebServlet("/gift/*") 
public class GiftServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private final GiftService giftService = new GiftService();

    public GiftServlet() {
        super();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.sendRedirect(request.getContextPath() + "/home");
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getPathInfo(); 
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
             response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous devez être connecté.");
             return;
        }
        
        if ("/update".equals(path)) path = "/modify";

        if ("/add".equals(path)) {
            handleAddGift(request, response, user);
        } else if ("/modify".equals(path)) { 
            handleModifyGift(request, response, user);
        } else if ("/delete".equals(path)) {
            handleDeleteGift(request, response, user);
        } else {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Action non reconnue.");
        }
    }
    
    /**
     * AJOUT : Crée le cadeau et l'ajoute à l'objet User en session.
     */
    private void handleAddGift(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {

        try {
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            double price = Double.parseDouble(request.getParameter("price"));
            int wishlistId = Integer.parseInt(request.getParameter("wishlistId"));
            String priorityStr = request.getParameter("priority");
            Integer priority = (priorityStr != null && !priorityStr.isEmpty()) ? Integer.parseInt(priorityStr) : null;
            String photoUrl = request.getParameter("photoUrl");

            Gift newGift = new Gift();
            newGift.setName(name);
            newGift.setDescription(description);
            newGift.setPrice(price);
            newGift.setPriority(priority);
            newGift.setPhotoUrl(photoUrl);
            
            Wishlist associatedWishlist = new Wishlist();
            associatedWishlist.setId(wishlistId); 
            newGift.setwishlist(associatedWishlist); 

            Optional<Gift> createdGift = giftService.addGift(newGift, user);
            
            if (createdGift.isPresent()) {
                // MISE À JOUR SESSION : Ajouter le cadeau à la bonne wishlist locale
                for (Wishlist w : user.getCreatedWishlists()) { // Vérifiez si c'est getWishlists() ou getwishlists()
                    if (w.getId() == wishlistId) {
                        w.getGifts().add(createdGift.get());
                        break;
                    }
                }
                response.sendRedirect(request.getContextPath() + "/home?status=gift_added");
            } else {
                response.sendRedirect(request.getContextPath() + "/home?error=gift_failed"); 
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/home?error=invalid_data");
        }
    }
    
    /**
     * MODIFICATION : Modifie le cadeau sur l'API et met à jour l'objet User en session.
     */
    private void handleModifyGift(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {

        try {
            int giftId = Integer.parseInt(request.getParameter("giftId"));
            String name = request.getParameter("name");
            String description = request.getParameter("description");
            double price = Double.parseDouble(request.getParameter("price"));
            int wishlistId = Integer.parseInt(request.getParameter("wishlistId"));
            String priorityStr = request.getParameter("priority");
            Integer priority = (priorityStr != null && !priorityStr.isEmpty()) ? Integer.parseInt(priorityStr) : null;
            String photoUrl = request.getParameter("photoUrl");

            Gift modifiedGift = new Gift();
            modifiedGift.setId(giftId);
            modifiedGift.setName(name);
            modifiedGift.setDescription(description);
            modifiedGift.setPrice(price);
            modifiedGift.setPriority(priority);
            modifiedGift.setPhotoUrl(photoUrl);
            
            Wishlist associatedWishlist = new Wishlist();
            associatedWishlist.setId(wishlistId); 
            modifiedGift.setwishlist(associatedWishlist); 

            boolean success = giftService.modifyGift(modifiedGift, user);
            
            if (success) {
                // MISE À JOUR SESSION : Modifier les valeurs du cadeau localement
                for (Wishlist w : user.getCreatedWishlists()) {
                    if (w.getId() == wishlistId) {
                        for (Gift g : w.getGifts()) {
                            if (g.getId() == giftId) {
                                g.setName(name);
                                g.setDescription(description);
                                g.setPrice(price);
                                g.setPriority(priority);
                                g.setPhotoUrl(photoUrl);
                                break;
                            }
                        }
                    }
                }
                response.sendRedirect(request.getContextPath() + "/home?status=gift_modified");
            } else {
                response.sendRedirect(request.getContextPath() + "/home?error=gift_modify_failed"); 
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/home?error=modify_error");
        }
    }
    
    /**
     * SUPPRESSION : Supprime sur l'API et retire le cadeau de l'objet User en session.
     */
    private void handleDeleteGift(HttpServletRequest request, HttpServletResponse response, User user) 
            throws ServletException, IOException {
        
        try {
            int giftId = Integer.parseInt(request.getParameter("giftId"));
            boolean success = giftService.deleteGift(giftId, user);
            
            if (success) {
                // MISE À JOUR SESSION : Retirer le cadeau de la liste locale
                for (Wishlist w : user.getCreatedWishlists()) {
                    w.getGifts().removeIf(g -> g.getId() == giftId);
                }
                response.sendRedirect(request.getContextPath() + "/home?status=gift_deleted");
            } else {
                response.sendRedirect(request.getContextPath() + "/home?error=gift_delete_failed"); 
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/home?error=delete_error");
        }
    }
}