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
    private final GiftService giftService = new GiftService();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String path = request.getPathInfo(); 
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        if (user == null) {
             response.sendError(HttpServletResponse.SC_FORBIDDEN, "Vous devez être connecté.");
             return;
        }
        
        // Routage des actions
        try {
            if ("/add".equals(path)) {
                handleAddGift(request, response, user);
            } else if ("/modify".equals(path) || "/update".equals(path)) { 
                handleModifyGift(request, response, user);
            } else if ("/delete".equals(path)) {
                handleDeleteGift(request, response, user);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Action non reconnue.");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/home?error=server_error");
        }
    }
    
    private void handleAddGift(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        Gift gift = extractGiftFromRequest(request);
        int wishlistId = Integer.parseInt(request.getParameter("wishlistId"));
        
        Optional<Gift> created = giftService.addGift(gift, wishlistId, user);
        
        if (created.isPresent()) {
            response.sendRedirect(request.getContextPath() + "/home?status=gift_added");
        } else {
            response.sendRedirect(request.getContextPath() + "/home?error=add_failed");
        }
    }

    private void handleModifyGift(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        Gift gift = extractGiftFromRequest(request);
        gift.setId(Integer.parseInt(request.getParameter("giftId")));
        int wishlistId = Integer.parseInt(request.getParameter("wishlistId"));

        boolean success = giftService.modifyGift(gift, wishlistId, user);
        
        if (success) {
            response.sendRedirect(request.getContextPath() + "/home?status=gift_modified");
        } else {
            response.sendRedirect(request.getContextPath() + "/home?error=modify_failed");
        }
    }

    private void handleDeleteGift(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {
        int giftId = Integer.parseInt(request.getParameter("giftId"));
        boolean success = giftService.deleteGift(giftId, user);
        
        if (success) {
            response.sendRedirect(request.getContextPath() + "/home?status=gift_deleted");
        } else {
            response.sendRedirect(request.getContextPath() + "/home?error=delete_failed");
        }
    }

    // Méthode utilitaire pour éviter la répétition
    private Gift extractGiftFromRequest(HttpServletRequest request) {
        Gift gift = new Gift();
        gift.setName(request.getParameter("name"));
        gift.setDescription(request.getParameter("description"));
        gift.setPrice(Double.parseDouble(request.getParameter("price")));
        gift.setPhotoUrl(request.getParameter("photoUrl"));
        
        String prio = request.getParameter("priority");
        gift.setPriority((prio != null && !prio.isEmpty()) ? Integer.parseInt(prio) : null);
        return gift;
    }
}