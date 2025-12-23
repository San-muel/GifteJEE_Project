package be.project.servlet;

import be.project.MODEL.Wishlist;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/wishlistDetail")
public class WishlistDetailServlet extends HttpServlet {
	private static final long serialVersionUID = 3020048919194754366L;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
	        throws ServletException, IOException {
	    
	    String idParam = request.getParameter("id");
	    
	    if (idParam != null && !idParam.isEmpty()) {
	        try {
	            int id = Integer.parseInt(idParam);
	            System.out.println("[DETAIL] Appel du modèle pour la wishlist ID : " + id);
	            
	            Wishlist wl = Wishlist.find(id);
	            
	            if (wl != null) {
	                request.setAttribute("selectedWishlist", wl);
	            } else {
	                request.setAttribute("error", "La liste demandée est introuvable.");
	            }
	            
	        } catch (NumberFormatException e) {
	            System.err.println("[DETAIL] Format d'ID invalide : " + idParam);
	        }
	    }

	    // Forward vers la JSP de détail
	    request.getRequestDispatcher("/WEB-INF/Vues/Home/wishlistDetail.jsp").forward(request, response);
	}
}