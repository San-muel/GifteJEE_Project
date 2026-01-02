package be.project.servlet;

import be.project.MODEL.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/wishlist/*")
public class WishlistServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String path = request.getPathInfo();
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        // Récupération des paramètres
        String title = request.getParameter("title");
        String occasion = request.getParameter("occasion");
        String statusStr = request.getParameter("status");
        String dateStr = request.getParameter("expirationDate");
        String newDateStr = request.getParameter("newDate"); 

        boolean success = false;
        String actionStatus = "";

        try {
            if ("/create".equals(path)) {
                success = user.createWishlist(title, occasion, statusStr, dateStr);
                actionStatus = "wishlist_created";

            } else if ("/update".equals(path)) {
                int wId = Integer.parseInt(request.getParameter("wishlistId"));
                success = user.updateWishlist(wId, title, occasion, statusStr, dateStr);
                actionStatus = "wishlist_updated";

            } else if ("/delete".equals(path)) {
                int wId = Integer.parseInt(request.getParameter("wishlistId"));
                success = user.deleteWishlist(wId);
                actionStatus = "wishlist_deleted";

            } else if ("/updateDate".equals(path)) {
                int wId = Integer.parseInt(request.getParameter("wishlistId"));
                success = user.reactivateWishlist(wId, newDateStr);
                actionStatus = "wishlist_reactivated";
            }

            // --- C'EST ICI QU'ON CHANGE ---
            // On redirige vers la Servlet Dashboard pour qu'elle recalcule les notifs
            String targetUrl = request.getContextPath() + "/dashboard"; 
            
            if (success) {
                targetUrl += "?status=" + actionStatus;
            } else {
                targetUrl += "?error=action_failed";
            }
            
            response.sendRedirect(targetUrl);
            // -----------------------------

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/dashboard?error=exception");
        }
    }
}