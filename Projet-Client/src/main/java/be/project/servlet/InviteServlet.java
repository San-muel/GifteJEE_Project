package be.project.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class InviteServlet
 */
@WebServlet("/invite")
public class InviteServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String wishlistId = request.getParameter("wishlistId");
        HttpSession session = request.getSession();

        if (wishlistId != null) {
            session.setAttribute("pendingWishlistId", wishlistId);
        }

        // On redirige vers l'inscription
        response.sendRedirect(request.getContextPath() + "/auth?action=register");
    }
}
