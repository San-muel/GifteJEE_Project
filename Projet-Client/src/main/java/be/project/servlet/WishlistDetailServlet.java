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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String idParam = request.getParameter("id");
        if (idParam != null) {
        	String id = request.getParameter("id");
            System.out.println("[DETAIL] Clic sur la wishlist ID : " + id);
            // On utilise Active Record pour trouver la liste et ses cadeaux
            Wishlist wl = null;
            request.setAttribute("selectedWishlist", wl);
        }

        request.getRequestDispatcher("/WEB-INF/Vues/Home/wishlistDetail.jsp").forward(request, response);
    }
}