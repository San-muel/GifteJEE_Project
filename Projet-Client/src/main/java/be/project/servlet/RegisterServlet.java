package be.project.servlet;

import be.project.MODEL.User;
import be.project.service.*;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    
    private static final long serialVersionUID = 2892885722562269480L;
    
    // ÉTAPE MANQUANTE : Déclarer et instancier le service
    private final UserService userService = new UserService(); 

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        //
        request.getRequestDispatcher("/WEB-INF/Vues/Register/register.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String password = request.getParameter("password");

            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPsw(password); //

            // Appel au Service qui contient la logique métier
            boolean success = userService.register(newUser);

            if (success) {
            		response.sendRedirect(request.getContextPath() + "/auth?success=true");
            } else {
                request.setAttribute("error", "Échec de l'inscription.");
                request.getRequestDispatcher("/WEB-INF/Vues/Register/register.jsp").forward(request, response);
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/register?error=server_error");
        }
    }
}