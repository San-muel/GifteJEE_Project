package be.project.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.project.MODEL.User;
import be.project.MODEL.Wishlist;

/**
 * Servlet implementation class ToggleStatusServlet
 */
@WebServlet("/wishlist/toggleStatus")
public class ToggleStatusServlet extends HttpServlet {
    
	// Dans be.project.servlet.ToggleStatusServlet.java

	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    
	    User currentUser = (User) request.getSession().getAttribute("user");
	    String idParam = request.getParameter("wishlistId");

	    if (currentUser != null && idParam != null) {
	        int wishlistId = Integer.parseInt(idParam);
	        Wishlist wl = Wishlist.find(wishlistId);
	        
	        if (wl != null) {
	            // 1. Exécuter la modification
	            boolean hasChanged = wl.toggleStatus(currentUser);
	            
	            // 2. Si succès, synchroniser la SESSION
	            if (hasChanged) {
	                System.out.println("[SERVLET] Synchronisation de la session utilisateur...");
	                
	                // On cherche la wishlist dans la liste du User et on met à jour son statut
	                for (Wishlist userWl : currentUser.getCreatedWishlists()) {
	                    if (userWl.getId() == wishlistId) {
	                        userWl.setStatus(wl.getStatus()); // On synchronise le nouveau statut
	                        System.out.println("[SERVLET] Statut mis à jour dans la session pour l'ID " + wishlistId);
	                        break;
	                    }
	                }
	            }
	        }
	    }
	    response.sendRedirect(request.getContextPath() + "/dashboard");
	}
}
