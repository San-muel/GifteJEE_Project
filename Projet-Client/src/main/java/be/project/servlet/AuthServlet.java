package be.project.servlet;

import be.project.DAO.UserDAO;
import be.project.MODEL.User;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // IMPORTANT : Ce UserDAO est le DAO du CLIENT qui appelle l'API RESTful.
    private final UserDAO userDAO = new UserDAO();

    // --- 1. GESTION DE L'AFFICHAGE DU FORMULAIRE (GET) ---
    // Cette méthode est utilisée pour l'affichage initial OU en cas d'erreur
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("DEBUG AUTH SERVLET: Affichage du formulaire de connexion.");

        // Chemin sécurisé vers la Vue (JSP)
        String jspPath = "/WEB-INF/Vues/authentification/login.jsp";
        
        // Transférer la requête à la JSP (Forward)
        request.getRequestDispatcher(jspPath).forward(request, response);
    }

    // --- 2. GESTION DE LA SOUMISSION (POST) ---
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("psw");
        
        // 2a. Vérification des champs vides
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Veuillez entrer votre email et mot de passe.");
            request.setAttribute("email", email); 
            doGet(request, response); // Réutilise doGet pour faire le forward vers la JSP avec l'erreur
            return;
        }

        try {
            System.out.println("DEBUG AUTH SERVLET: Tentative d'authentification pour : " + email);
            
            // 2b. Appel au DAO du Client pour contacter l'API RESTful
            User authenticatedUser = userDAO.authenticate(email, password);
            
            if (authenticatedUser != null) {
                // Succès : Stockage de l'objet User (qui contient le token) dans la session
                System.out.println("DEBUG AUTH SERVLET: Authentification réussie. Token stocké. Redirection.");
                
                request.getSession().setAttribute("user", authenticatedUser);
                
                // Redirection (Redirection HTTP 302) vers une URL du Contrôleur
                response.sendRedirect(request.getContextPath() + "/home"); 
                
            } else {
                // Échec : L'API a renvoyé 401/403
                System.out.println("DEBUG AUTH SERVLET: Échec de l'authentification.");
                
                request.setAttribute("errorMessage", "Email ou mot de passe invalide.");
                request.setAttribute("email", email); 
                doGet(request, response); // Réutilise doGet pour faire le forward vers la JSP avec l'erreur
            }
            
        } catch (Exception e) {
            // Erreur de communication (API injoignable, JSON invalide, etc.)
            e.printStackTrace();
            request.setAttribute("errorMessage", "Une erreur interne est survenue lors de la communication.");
            request.setAttribute("email", email);
            doGet(request, response);
        }
    }
}