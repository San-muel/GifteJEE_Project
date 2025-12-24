package be.project.servlet;

import be.project.DAO.ContributionDAO;
import be.project.MODEL.Contribution;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// CORRECTION DU MAPPING : 
// * signifie que tout ce qui suit /contribution/ sera mappé à ce Servlet.
@WebServlet("/contribution/*")
public class ContributionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ContributionDAO contributionDAO = new ContributionDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        System.out.println("DEBUG SERVLET: Entrée dans la méthode doGet du Servlet.");

        // 1. EXTRAIRE L'ID DU PATH
        String pathInfo = request.getPathInfo(); // Récupère la partie après /contribution, ex: /1
        
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "L'ID de contribution est requis dans l'URL (ex: /contribution/1).");
            return;
        }
        
        // pathInfo est /1, on retire le premier slash.
        String idParam = pathInfo.substring(1); 
        
        if (idParam.isEmpty()) {
             response.sendError(HttpServletResponse.SC_BAD_REQUEST, "L'ID de contribution est requis dans l'URL (ex: /contribution/1).");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);
            System.out.println("DEBUG SERVLET: ID à chercher : " + id);

            // 2. Contrôleur : Appel au DAO (qui appelle l'API)
            Contribution contribution = contributionDAO.find(id);
            // ... (le reste du code est correct)

            System.out.println("DEBUG SERVLET: DAO a retourné Contribution: " + (contribution != null ? contribution.getId() : "null"));

            // 3. Contrôleur : Préparation et envoi à la Vue
            if (contribution != null) {
                
                request.setAttribute("contribution", contribution);

                String jspPath = "/WEB-INF/Vues/contributionDetail.jsp";
                System.out.println("DEBUG SERVLET: Tentative de transfert vers la JSP: " + jspPath);

                // Transfert vers la JSP
                request.getRequestDispatcher(jspPath).forward(request, response);
                System.out.println("DEBUG SERVLET: Forward (Transfert) terminé (si aucune erreur n'est survenue).");

            } else {
                System.out.println("DEBUG SERVLET: Génération d'un 404 car la contribution est null.");
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Contribution introuvable avec l'ID : " + id);
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "L'ID doit être un nombre.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Une erreur interne est survenue : " + e.getMessage());
        }
    }
 // be.project.servlet.ContributionServlet

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 1. Récupérer les paramètres
            int giftId = Integer.parseInt(request.getParameter("giftId"));
            int wishlistId = Integer.parseInt(request.getParameter("wishlistId"));
            double amount = Double.parseDouble(request.getParameter("amount"));
            String comment = request.getParameter("comment");
            
            // 2. Récupérer l'OBJET User complet en session
            // Important : on a besoin de l'objet entier pour obtenir l'ID ET le Token Bearer
            be.project.MODEL.User currentUser = (be.project.MODEL.User) request.getSession().getAttribute("user");

            if (currentUser == null) {
                response.sendRedirect(request.getContextPath() + "/login"); // Sécurité
                return;
            }

            // 3. Préparer la contribution
            Contribution contrib = new Contribution();
            contrib.setAmount(amount);
            contrib.setComment(comment);
            // L'ID utilisateur est géré soit ici, soit dans le DAO via l'objet user
            contrib.setUserId(currentUser.getId()); 

            // 4. Appel Active Record (on passe giftId et l'user pour le token)
            Contribution result = contrib.create(giftId, currentUser); 

            // 5. Redirection selon le résultat
            if (result != null) {
                // Succès
                response.sendRedirect(request.getContextPath() + "/wishlistDetail?id=" + wishlistId + "&success=contrib");
            } else {
                // Échec (montant trop élevé ou erreur API)
                response.sendRedirect(request.getContextPath() + "/wishlistDetail?id=" + wishlistId + "&error=contrib_failed");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format de données invalide");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}