package be.project.servlet;

import be.project.MODEL.User;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/Vues/Register/register.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        try {
            User newUser = new User();
            newUser.setUsername(request.getParameter("username"));
            newUser.setEmail(request.getParameter("email"));
            newUser.setPsw(request.getParameter("password"));

            // Appel direct au modèle
            if (newUser.register()) {
                response.sendRedirect(request.getContextPath() + "/auth?success=true");
            } else {
                request.setAttribute("error", "Échec de l'inscription.");
                doGet(request, response);
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/register?error=server_error");
        }
    }
}