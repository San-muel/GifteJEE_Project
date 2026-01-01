package be.project.servlet;

import be.project.DAO.ContributionDAO;
import be.project.MODEL.Contribution;
import be.project.MODEL.Gift;
import be.project.MODEL.User;
import be.project.MODEL.Wishlist;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // Instanciation du DAO nécessaire pour récupérer les contributions
    private final ContributionDAO contributionDAO = new ContributionDAO();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // --- 1. GESTION DU CACHE ET DE LA SESSION ---
        
        // Empêcher la mise en cache (évite le retour arrière après déconnexion)
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        HttpSession session = request.getSession(false);
        User user = (session != null) ? (User) session.getAttribute("user") : null;

        // Vérification stricte de l'utilisateur
        boolean isInvalidUser = (user == null || user.getUsername() == null || user.getUsername().trim().isEmpty());

        if (isInvalidUser) {
            if (session != null) {
                session.removeAttribute("user");
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }

        // --- 2. LOGIQUE MÉTIER : MISE À JOUR & NOTIFICATIONS ---

        // Liste pour stocker les messages de notification
        List<String> notifications = new ArrayList<>();

        try {
            // On parcourt les listes partagées avec l'utilisateur
            if (user.getSharedWishlists() != null) {
                for (Wishlist wl : user.getSharedWishlists()) {
                    
                    // On ne traite que les listes ACTIVES qui contiennent des cadeaux
                    if ("ACTIVE".equals(wl.getStatus()) && wl.getGifts() != null) {
                        for (Gift gift : wl.getGifts()) {
                            
                            // A. CORRECTION BUG AFFICHAGE :
                            // On va chercher en base de données les contributions à jour pour ce cadeau
                            List<Contribution> listContribs = contributionDAO.findAllByGiftId(gift.getId());
                            
                            // On met à jour l'objet Gift en mémoire (pour que la barre de progression fonctionne)
                            gift.setContributions(new HashSet<>(listContribs));

                            // B. NOUVELLE FONCTIONNALITÉ : NOTIFICATIONS
                            // On vérifie si de l'argent a été récolté sur ce cadeau
                            double collected = gift.getCollectedAmount();
                            
                            if (collected > 0) {
                                // On détermine l'icône selon si le cadeau est fini ou non
                                String statusEmoji = (gift.getRemainingAmount() <= 0.01) ? "✅" : "💸";
                                
                                // Construction du message HTML pour la notification
                                String msg = String.format(
                                    "%s Le cadeau <strong>%s</strong> (Liste : <em>%s</em>) a reçu des contributions (%s€ récoltés) !", 
                                    statusEmoji, 
                                    gift.getName(), 
                                    wl.getTitle(),
                                    String.format("%.2f", collected) // Formatage propre du montant
                                );
                                
                                notifications.add(msg);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des contributions dashboard : " + e.getMessage());
        }

        // --- 3. ENVOI À LA VUE ---
        
        // On passe la liste des notifications à la JSP
        request.setAttribute("notifications", notifications);

        // Redirection vers la JSP du dashboard
        request.getRequestDispatcher("/WEB-INF/Vues/Home/displayingWG.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}