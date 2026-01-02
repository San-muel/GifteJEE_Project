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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        User currentUser = (User) request.getSession().getAttribute("user");
        String idParam = request.getParameter("wishlistId");

        if (currentUser != null && idParam != null) {
            try {
                int wishlistId = Integer.parseInt(idParam);
                Wishlist wl = Wishlist.find(wishlistId);
                
                if (wl != null) {
                    // 1. Exécuter la modification via le modèle
                    boolean hasChanged = wl.toggleStatus(currentUser);
                    
                    // 2. Synchronisation de la session via le modèle User
                    if (hasChanged) {
                        currentUser.updateWishlistStatusLocally(wishlistId, wl.getStatus());
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("ID de wishlist invalide");
            }
        }
        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}