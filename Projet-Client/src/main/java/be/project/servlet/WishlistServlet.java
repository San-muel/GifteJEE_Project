package be.project.servlet;

import be.project.DAO.WishlistDAO;
import be.project.MODEL.Status;
import be.project.MODEL.User;
import be.project.MODEL.Wishlist;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/wishlist/*")
public class WishlistServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    
    // Instanciation du DAO
    private final WishlistDAO wishlistDAO = new WishlistDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Encodage pour les accents
        request.setCharacterEncoding("UTF-8");
        
        String path = request.getPathInfo();
        User user = (User) request.getSession().getAttribute("user");

        // 1. Sécurité : Si pas connecté, erreur 403
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        try {
            // --- ACTION : CRÉATION ---
            if ("/create".equals(path)) {
                System.out.println("SERVLET: Demande de création de wishlist...");
                
                // Extraction des données du formulaire
                Wishlist wishlistToCreate = extract(request);
                
                // Appel API via DAO
                Optional<Wishlist> createdOpt = wishlistDAO.createWishlist(wishlistToCreate, user);

                if (createdOpt.isPresent()) {
                    // Succès : on récupère l'objet complet (avec ID) retourné par l'API
                    Wishlist newWishlist = createdOpt.get();
                    
                    // Mise à jour locale de la session (pour affichage immédiat sans rechargement BDD)
                    user.addWishlistLocally(newWishlist);
                    
                    System.out.println("SERVLET: Wishlist créée avec succès ID=" + newWishlist.getId());
                    response.sendRedirect(request.getContextPath() + "/home?status=wishlist_created");
                } else {
                    System.err.println("SERVLET: Echec création DAO");
                    response.sendRedirect(request.getContextPath() + "/home?error=failed_to_create");
                }

            // --- ACTION : MODIFICATION ---
            } else if ("/update".equals(path)) {
                System.out.println("SERVLET: Demande de modification...");
                
                Wishlist wishlistToUpdate = extract(request);
                // L'ID est généralement passé en hidden dans le formulaire
                int wId = Integer.parseInt(request.getParameter("wishlistId"));
                wishlistToUpdate.setId(wId);

                boolean success = wishlistDAO.updateWishlist(wishlistToUpdate, user);
                
                if (success) {
                    // Mise à jour locale (Il faudra une méthode updateWishlistLocally dans User)
                    // user.updateWishlistLocally(wishlistToUpdate); 
                    response.sendRedirect(request.getContextPath() + "/home?status=wishlist_updated");
                } else {
                    response.sendRedirect(request.getContextPath() + "/home?error=failed_to_update");
                }

            // --- ACTION : SUPPRESSION ---
            } else if ("/delete".equals(path)) {
                System.out.println("SERVLET: Demande de suppression...");
                
                int wId = Integer.parseInt(request.getParameter("wishlistId"));
                
                boolean success = wishlistDAO.deleteWishlist(wId, user);
                
                if (success) {
                    // Suppression locale (Il faudra une méthode removeWishlistLocally dans User)
                    // user.removeWishlistLocally(wId);
                    response.sendRedirect(request.getContextPath() + "/home?status=wishlist_deleted");
                } else {
                    response.sendRedirect(request.getContextPath() + "/home?error=failed_to_delete");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            // En cas de crash, retour à l'accueil avec erreur générique
            response.sendRedirect(request.getContextPath() + "/home?error=exception");
        }
    }

    /**
     * Méthode utilitaire pour extraire les données du formulaire
     * et construire un objet Wishlist.
     */
    private Wishlist extract(HttpServletRequest req) {
        Wishlist w = new Wishlist();

        w.setTitle(req.getParameter("title"));
        w.setOccasion(req.getParameter("occasion"));

        // Conversion String -> enum Status
        String statusStr = req.getParameter("status");
        if (statusStr != null && !statusStr.isEmpty()) {
            try {
                w.setStatus(Status.valueOf(statusStr.toUpperCase()));
            } catch (IllegalArgumentException e) {
                System.err.println("Status invalide : " + statusStr);
                w.setStatus(null); // ou une valeur par défaut
            }
        }

        // Gestion de la date (String -> LocalDate)
        String dateStr = req.getParameter("expirationDate");
        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                w.setExpirationDate(LocalDate.parse(dateStr));
            } catch (DateTimeParseException e) {
                System.err.println("Erreur format date : " + dateStr);
                w.setExpirationDate(null);
            }
        }

        return w;
    }

}