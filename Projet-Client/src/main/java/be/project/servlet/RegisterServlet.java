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
        request.getRequestDispatcher("/WEB-INF/Vues/Register/register.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        
        try {
            User newUser = new User();
            newUser.setUsername(request.getParameter("username"));
            newUser.setEmail(email);
            newUser.setPsw(password);

            // 1. Inscription : Création du compte via l'API
            if (newUser.register()) {
                
                // 2. Premier Login : Indispensable pour récupérer l'ID généré par la DB
                User userWithId = newUser.login(email, password); 
                
                if (userWithId != null) {
                    HttpSession session = request.getSession();
                    Object pendingIdObj = session.getAttribute("pendingWishlistId");
                    
                    if (pendingIdObj != null) {
                        try {
                            int wId = Integer.parseInt(pendingIdObj.toString());
                            
                            // 3. Liaison : On lie l'utilisateur à la wishlist dans la DB
                            boolean shared = userWithId.acceptPublicInvitation(wId);
                            
                            if (shared) {
                                System.out.println("[REGISTER] Liaison réussie. Rafraîchissement des données...");
                                
                                // 4. Deuxième Login : Pour que l'objet Java contienne la nouvelle wishlist 
                                // dans ses listes partagées (refresh local)
                                userWithId = userWithId.login(email, password); 
                                
                                session.setAttribute("user", userWithId);
                                session.removeAttribute("pendingWishlistId");
                                
                                response.sendRedirect(request.getContextPath() + "/dashboard?status=welcome_shared");
                                return;
                            }
                        } catch (NumberFormatException nfe) {
                            System.err.println("[REGISTER] Erreur format ID : " + pendingIdObj);
                        }
                    }
                    
                    // Si pas d'invitation, on connecte l'utilisateur tel quel
                    session.setAttribute("user", userWithId);
                }
                
                response.sendRedirect(request.getContextPath() + "/dashboard?success=true");
                
            } else {
                request.setAttribute("error", "Échec de l'inscription. L'email est peut-être déjà utilisé.");
                doGet(request, response);
            }
        } catch (Exception e) {
            System.err.println("[REGISTER] Erreur Serveur : " + e.getMessage());
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/register?error=server_error");
        }
    }
}