package be.project.servlet;

import be.project.MODEL.User; // Assurez-vous d'importer la classe User correcte
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        HttpSession session = request.getSession(false); // Récupère la session existante, ne la crée pas.

        // 1. Vérification de l'authentification (Guard)
        // Si la session n'existe pas ou l'utilisateur n'est pas en session
        if (session == null || session.getAttribute("user") == null) {
            
            System.out.println("DEBUG HOME SERVLET: Accès non autorisé ou session expirée. Redirection vers la connexion.");
            
            // Redirection vers la page de connexion
            response.sendRedirect(request.getContextPath() + "/auth");
            return; 
        }

        // L'utilisateur est connecté. On peut récupérer ses données.
        User user = (User) session.getAttribute("user");
        
        System.out.println("DEBUG HOME SERVLET: Affichage de la page d'accueil pour " + user.getUsername());

        // 2. Logique métier (Chargement des relations) - Optionnel ici, mais mieux si fait dans le DAO
        // Si la page d'accueil a besoin des listes de souhaits créées/partagées, 
        // c'est ici qu'on appelle la méthode loadUserRelations si elle n'a pas été appelée à l'auth.
        // userDAO.loadUserRelations(user); 
        
        // 3. Renvoi vers la Vue (JSP)
        // La JSP pourra accéder à l'objet User via ${sessionScope.user}
        String jspPath = "/WEB-INF/Vues/Home/home.jsp";
        
        // Transférer la requête à la JSP (Forward)
        request.getRequestDispatcher(jspPath).forward(request, response);
    }
}