package be.project.servlet;

import be.project.DAO.ContributionDAO;
import be.project.DAO.WishlistDAO;
import be.project.MODEL.Contribution;
import be.project.MODEL.Gift;
import be.project.MODEL.Wishlist;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@WebServlet("/wishlistDetail")
public class WishlistDetailServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            String idParam = request.getParameter("id");
            if (idParam == null || idParam.isEmpty()) {
                response.sendRedirect(request.getContextPath() + "/home");
                return;
            }
            
            int id = Integer.parseInt(idParam);

            Wishlist wishlist = Wishlist.find(id); 

            if (wishlist == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Liste introuvable");
                return;
            }

            wishlist.loadAllGiftsContributions();

            request.setAttribute("selectedWishlist", wishlist);
            request.getRequestDispatcher("/WEB-INF/Vues/Home/wishlistDetail.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}