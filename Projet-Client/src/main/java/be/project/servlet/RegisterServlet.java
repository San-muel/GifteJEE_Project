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
        
        // 1. On récupère l'ID depuis l'URL (ex: /register?wishlistId=12)
        String pendingId = request.getParameter("wishlistId");
        
        // 2. Si un ID est présent, on l'envoie à la JSP pour qu'elle le mette dans le formulaire
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
        
        // 3. On récupère l'ID depuis le champ caché du formulaire (POST)
        String pendingIdStr = request.getParameter("pendingWishlistId");
        
        HttpSession session = request.getSession();
        
        try {
            Integer pendingId = null;
            if (pendingIdStr != null && !pendingIdStr.isEmpty()) {
                pendingId = Integer.parseInt(pendingIdStr);
            }

            // Appel au modèle
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPsw(password);

            // Ta méthode existante qui gère l'inscription + le partage
            User userWithId = newUser.completeRegistration(password, pendingId);

            if (userWithId != null) {
                session.setAttribute("user", userWithId);
                
                String status = (pendingId != null) ? "welcome_shared" : "welcome";
                response.sendRedirect(request.getContextPath() + "/dashboard?status=" + status);
            } else {
                request.setAttribute("error", "Échec de l'inscription. Email déjà utilisé.");
                // Important : Si ça rate, on renvoie l'ID à la vue pour ne pas le perdre !
                if (pendingId != null) request.setAttribute("pendingWishlistId", pendingId);
                doGet(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/register?error=server_error");
        }
    }
}