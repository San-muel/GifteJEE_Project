package be.project.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import be.project.MODEL.Wishlist;

/**
 * Servlet implementation class ToggleStatusServlet
 */
@WebServlet("/wishlist/toggleStatus")
public class ToggleStatusServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        int wishlistId = Integer.parseInt(request.getParameter("wishlistId"));
        /*WishlistDAO dao = new WishlistDAO();*/
        Wishlist wl = null;

        if (wl != null) {
            // Logique de bascule
            if ("ACTIVE".equals(wl.getStatus())) {
                wl.setStatus("PRIVATE"); // ou "INACTIVE" selon votre nomenclature
            } else {
                // On ne réactive que si la date n'est pas passée
                if (wl.getExpirationDate().isAfter(java.time.LocalDate.now())) {
                    wl.setStatus("ACTIVE");
                }
            }
          //  dao.update(wl);  Assurez-vous d'avoir une méthode update dans votre DAO
        }

        response.sendRedirect(request.getContextPath() + "/dashboard");
    }
}
