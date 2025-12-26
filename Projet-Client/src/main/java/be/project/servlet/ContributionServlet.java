package be.project.servlet;

import be.project.DAO.ContributionDAO;
import be.project.MODEL.Contribution;
import be.project.MODEL.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// Mapping:
// GET  /contribution/{id}  -> Affiche le détail (via doGet)
// POST /contribution/add   -> Ajoute une contribution (via doPost)
@WebServlet("/contribution/*")
public class ContributionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ContributionDAO contributionDAO = new ContributionDAO();

    /**
     * Gère l'affichage d'une contribution spécifique via son ID dans l'URL.
     * Ex: /contribution/5
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        System.out.println("DEBUG SERVLET: Entrée dans la méthode doGet.");

        // 1. EXTRAIRE L'ID DU PATH
        String pathInfo = request.getPathInfo(); // ex: "/5"
        
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "L'ID est requis (ex: /contribution/1).");
            return;
        }
        
        // On retire le slash initial
        String idParam = pathInfo.substring(1); 
        
        try {
            int id = Integer.parseInt(idParam);
            System.out.println("DEBUG SERVLET: ID à chercher : " + id);

            // 2. APPEL AU DAO (qui contacte l'API)
            Contribution contribution = contributionDAO.find(id);

            System.out.println("DEBUG SERVLET: Résultat DAO: " + (contribution != null ? "Trouvé" : "Null"));

            // 3. TRANSFERT VERS LA VUE
            if (contribution != null) {
                request.setAttribute("contribution", contribution);
                
                // Assurez-vous que ce fichier JSP existe bien
                String jspPath = "/WEB-INF/Vues/contributionDetail.jsp"; 
                request.getRequestDispatcher(jspPath).forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Contribution introuvable (ID: " + id + ")");
            }

        } catch (NumberFormatException e) {
            // Si l'utilisateur met /contribution/toto au lieu d'un nombre
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "L'ID doit être un nombre valide.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur interne: " + e.getMessage());
        }
    }

    /**
     * Gère la création d'une contribution via le formulaire HTML.
     * Ex: <form action=".../contribution/add" method="POST">
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        System.out.println("DEBUG SERVLET: Entrée dans la méthode doPost.");

        // Encodage pour gérer les accents dans les commentaires
        request.setCharacterEncoding("UTF-8");
        
        try {
            // 1. VERIFICATION DE LA SESSION (Utilisateur connecté ?)
            User currentUser = (User) request.getSession().getAttribute("user");

            if (currentUser == null) {
                // Si pas connecté, redirection vers login
                response.sendRedirect(request.getContextPath() + "/login"); 
                return;
            }

            // 2. RECUPERATION DES PARAMETRES DU FORMULAIRE
            String giftIdStr = request.getParameter("giftId");
            String wishlistIdStr = request.getParameter("wishlistId");
            String amountStr = request.getParameter("amount");
            String comment = request.getParameter("comment");

            // Vérification basique des champs obligatoires
            if (giftIdStr == null || amountStr == null || wishlistIdStr == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Paramètres manquants.");
                return;
            }

            int giftId = Integer.parseInt(giftIdStr);
            int wishlistId = Integer.parseInt(wishlistIdStr);
            double amount = Double.parseDouble(amountStr);

            System.out.println("DEBUG SERVLET: Création contribution - GiftID: " + giftId + ", Montant: " + amount);

            // 3. CREATION DE L'OBJET METIER
            Contribution contrib = new Contribution();
            contrib.setAmount(amount);
            contrib.setComment(comment);
            contrib.setUserId(currentUser.getId()); 

            // 4. APPEL ACTIVE RECORD (Via Modèle -> DAO -> API)
            // On passe giftId et l'user (pour récupérer le Token Bearer)
            Contribution result = contrib.create(giftId, currentUser); 

            // 5. REDIRECTION SELON LE RESULTAT
            if (result != null) {
                System.out.println("DEBUG SERVLET: Succès ! Redirection vers la liste.");
                // Succès : retour à la liste avec message vert
                response.sendRedirect(request.getContextPath() + "/wishlistDetail?id=" + wishlistId + "&success=contrib");
            } else {
                System.err.println("DEBUG SERVLET: Echec lors de la création.");
                // Echec : retour à la liste avec message rouge
                response.sendRedirect(request.getContextPath() + "/wishlistDetail?id=" + wishlistId + "&error=contrib_failed");
            }

        } catch (NumberFormatException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Format de nombre invalide.");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erreur serveur lors de l'ajout.");
        }
    }
}