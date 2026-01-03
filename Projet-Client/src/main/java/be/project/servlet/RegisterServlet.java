package be.project.servlet;

import be.project.MODEL.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String pendingId = request.getParameter("wishlistId");
        
        if (pendingId != null && !pendingId.isEmpty()) {
            request.setAttribute("pendingWishlistId", pendingId);
        }

        request.getRequestDispatcher("/WEB-INF/Vues/Register/register.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String username = request.getParameter("username");
        
        String pendingIdStr = request.getParameter("pendingWishlistId");
        
        HttpSession session = request.getSession();
        
        try {
            Integer pendingId = null;
            if (pendingIdStr != null && !pendingIdStr.isEmpty()) {
                pendingId = Integer.parseInt(pendingIdStr);
            }

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPsw(password);

            User userWithId = newUser.completeRegistration(password, pendingId);

            if (userWithId != null) {
                session.setAttribute("user", userWithId);
                
                String status = (pendingId != null) ? "welcome_shared" : "welcome";
                response.sendRedirect(request.getContextPath() + "/dashboard?status=" + status);
            } else {
                request.setAttribute("error", "Échec de l'inscription. Email déjà utilisé.");
                if (pendingId != null) request.setAttribute("pendingWishlistId", pendingId);
                doGet(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/register?error=server_error");
        }
    }
}