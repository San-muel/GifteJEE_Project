package be.project.servlet;

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

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/Vues/authentification/login.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String email = request.getParameter("email");
        String password = request.getParameter("psw");
        
        if (email == null || email.isEmpty() || password == null || password.isEmpty()) {
            request.setAttribute("errorMessage", "Veuillez remplir tous les champs.");
            doGet(request, response);
            return;
        }

        try {
            // Appel direct au modèle
            User userTemplate = new User();
            User authenticatedUser = userTemplate.login(email, password);
            
            if (authenticatedUser != null) {
                request.getSession().setAttribute("user", authenticatedUser);
                response.sendRedirect(request.getContextPath() + "/home");
            } else {
                request.setAttribute("errorMessage", "Email ou mot de passe invalide.");
                doGet(request, response);
            }
        } catch (Exception e) {
            request.setAttribute("errorMessage", "Erreur technique : " + e.getMessage());
            doGet(request, response);
        }
    }
}