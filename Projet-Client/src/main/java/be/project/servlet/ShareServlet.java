package be.project.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.project.MODEL.SharedWishlist;
import be.project.MODEL.User;

/**
 * Servlet implementation class ShareServlet
 */
@WebServlet("/share")
public class ShareServlet extends HttpServlet {

	private static final long serialVersionUID = -1350736351886535373L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Logique Active Record : Le modèle fournit la liste
        List<User> members = User.fetchAllSystemUsers();
        
        // On récupère l'ID de la wishlist à partager passé par le bouton de la home
        String wId = request.getParameter("wishlistId");
        
        request.setAttribute("users", members);
        request.setAttribute("wishlistId", wId);
        request.getRequestDispatcher("/WEB-INF/Vues/Home/share.jsp").forward(request, response);
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    User currentUser = (User) request.getSession().getAttribute("user");
	    
	    // Sécurité de base
	    if (currentUser == null) {
	        response.sendError(HttpServletResponse.SC_FORBIDDEN);
	        return;
	    }

	    try {
	        int wishlistId = Integer.parseInt(request.getParameter("wishlistId"));
	        int targetUserId = Integer.parseInt(request.getParameter("targetUserId"));
	        String note = request.getParameter("notification");

	        boolean success = currentUser.shareMyWishlist(wishlistId, targetUserId, note);

	        if (success) {
	            response.sendRedirect(request.getContextPath() + "/dashboard?status=shared");
	        } else {
	            response.sendRedirect(request.getContextPath() + "/share?wishlistId=" + wishlistId + "&error=failed");
	        }
	    } catch (Exception e) {
	        response.sendRedirect(request.getContextPath() + "/displayingWG?error=exception");
	    }
	}
}
