package be.project.servlet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Récupérer la session existante oui
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            System.out.println("[LOGOUT] Suppression de la session de l'utilisateur.");
            // 2. Détruire la session (supprime TOUT ce qui est dedans)
            session.invalidate(); 
        }

        // 3. Rediriger vers l'accueil (qui sera maintenant en mode non-connecté)
        response.sendRedirect(request.getContextPath() + "/home");
    }
}