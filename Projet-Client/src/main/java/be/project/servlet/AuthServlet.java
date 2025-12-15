package be.project.servlet;

import be.project.MODEL.User;
import be.project.service.UserService; // Import du Service Layer
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // CORRECTION : Le contrôleur appelle la couche Service, et non le DAO.
    private final UserService userService = new UserService(); 

    // --- 1. GESTION DE L'AFFICHAGE DU FORMULAIRE (GET) ---
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
            // Logique du Contrôleur : Définir l'erreur et afficher la vue
            request.setAttribute("errorMessage", "Veuillez entrer votre email et mot de passe.");
            request.setAttribute("email", email); 
            doGet(request, response); 
            return;
        }

        try {
            System.out.println("DEBUG AUTH SERVLET: Tentative d'authentification pour : " + email);
            
            // 2b. Appel au SERVICE pour gérer l'authentification (Délégation de la logique métier/DAO)
            User authenticatedUser = userService.authenticateUser(email, password);
            
            if (authenticatedUser != null) {
                // Succès : Logique du Contrôleur : Gérer la session et la redirection
                System.out.println("DEBUG AUTH SERVLET: Authentification réussie. Redirection.");
                
                request.getSession().setAttribute("user", authenticatedUser);
                
                // Redirection vers la HomeServlet (pattern Post-Redirect-Get)
                response.sendRedirect(request.getContextPath() + "/home");
                
            } else {
                // Échec : Logique du Contrôleur : Définir l'erreur et afficher la vue de login
                System.out.println("DEBUG AUTH SERVLET: Échec de l'authentification (Email/Mdp invalide).");
                
                request.setAttribute("errorMessage", "Email ou mot de passe invalide.");
                request.setAttribute("email", email); 
                doGet(request, response); 
            }
            
        } catch (Exception e) {
            // Erreur de communication ou autre erreur technique
            e.printStackTrace();
            request.setAttribute("errorMessage", "Une erreur interne est survenue lors de la communication.");
            request.setAttribute("email", email);
            doGet(request, response);
        }
    }
}