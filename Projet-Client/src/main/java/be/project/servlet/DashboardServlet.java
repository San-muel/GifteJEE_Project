package be.project.servlet;

import be.project.MODEL.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    // Plus besoin d'importer ou d'instancier ContributionDAO ici !

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // --- 1. SÉCURITÉ & CACHE (Rôle du Contrôleur) ---
        preventCaching(response);

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        if (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty()) {
            if (session != null) session.invalidate();
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }

        // --- 2. APPEL AU MODÈLE (Une ligne pour tout faire) ---
        try {
            // A. On dit à l'utilisateur de rafraîchir ses données partagées
            user.refreshSharedListsData();
            
            // B. On récupère les notifications calculées par le modèle
            List<String> notifications = user.generateDashboardNotifications();
            
            // C. On envoie à la vue
            request.setAttribute("notifications", notifications);
            
        } catch (Exception e) {
            e.printStackTrace(); // Log serveur
            // On ne plante pas l'appli pour une notif, on continue vers la vue
        }

        // --- 3. AFFICHAGE ---
        request.getRequestDispatcher("/WEB-INF/Vues/Home/displayingWG.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Méthode utilitaire privée pour alléger le doGet
     */
    private void preventCaching(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}