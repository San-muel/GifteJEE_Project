package be.project.servlet;

import be.project.MODEL.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 2781328286625384724L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Empêcher la mise en cache pour éviter le retour arrière après déconnexion
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        response.setHeader("Pragma", "no-cache"); // HTTP 1.0
        response.setDateHeader("Expires", 0); // Proxies

        HttpSession session = request.getSession(false); // On ne crée pas de session si elle n'existe pas

        // 1. Récupération de l'utilisateur
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // 2. VÉRIFICATION STRICTE
        // On vérifie si l'utilisateur est null OU si son username est vide/null
        boolean isInvalidUser = (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty());

        if (isInvalidUser) {
            System.err.println("[SECURITY] Tentative d'accès illégitime ou session corrompue.");
            
            if (session != null) {
                session.removeAttribute("user"); // On retire l'attribut spécifique
                session.invalidate();           // On détruit la session côté serveur
            }
            
            // Redirection vers la servlet d'authentification
            response.sendRedirect(request.getContextPath() + "/auth");
            return; // Sortie immédiate pour ne pas exécuter le forward
        }

        // 3. Tout est OK : Accès au Dashboard
        request.getRequestDispatcher("/WEB-INF/Vues/Home/displayingWG.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}