package be.project.servlet;

import be.project.MODEL.Gift;
import be.project.MODEL.User;
import be.project.MODEL.Wishlist; // IMPORTANT : Import nécessaire
import be.project.DAO.GiftDAO; 
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/gift/*") 
public class GiftServlet extends HttpServlet {
    
    private static final long serialVersionUID = -2219147150738050882L;
    private final GiftDAO giftDAO = new GiftDAO();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getPathInfo(); 
        
        // On récupère l'utilisateur DE LA SESSION (c'est lui qu'on va modifier en direct)
        User user = (User) request.getSession().getAttribute("user");

        System.out.println("[DEBUG SERVLET] Reçu requête POST sur : " + path);

        if (user == null) { 
            response.sendError(HttpServletResponse.SC_FORBIDDEN); 
            return; 
        }
        
        try {
            // --- 1. GESTION DES PRIORITÉS ---
            if ("/priority".equals(path)) {
                int gId = Integer.parseInt(request.getParameter("giftId"));
                int wId = Integer.parseInt(request.getParameter("wishlistId"));
                int currentPrio = Integer.parseInt(request.getParameter("currentPriority"));
                String direction = request.getParameter("direction");

                // Calcul nouvelle priorité
                int newPrio = "UP".equals(direction) ? Math.max(1, currentPrio - 1) : currentPrio + 1;

                if (newPrio != currentPrio) {
                    Gift giftToUpdate = new Gift();
                    giftToUpdate.setId(gId);
                    giftToUpdate.setPriority(newPrio);
                    
                    // A. Mise à jour BDD
                    boolean success = giftToUpdate.updatePriority(wId, user, giftDAO);
                    
                    // B. Mise à jour SESSION (Mémoire)
                    if (success) {
                        System.out.println("[SESSION-UPDATE] Mise à jour priorité en mémoire.");
                        updateGiftInMemory(user, wId, gId, null, newPrio);
                    }
                }
                response.sendRedirect(request.getContextPath() + "/dashboard?status=priority_ok");
                return;
            }

            // --- 2. AJOUT CADEAU ---
            if ("/add".equals(path)) {
                Gift gift = extract(request);
                int wId = Integer.parseInt(request.getParameter("wishlistId"));
                
                // A. Mise à jour BDD
                boolean success = gift.save(wId, user, giftDAO);
                
                // B. Mise à jour SESSION (Mémoire)
                if (success) {
                    System.out.println("[SESSION-UPDATE] Ajout du cadeau en mémoire.");
                    addGiftInMemory(user, wId, gift);
                    response.sendRedirect(request.getContextPath() + "/dashboard?status=added");
                } else {
                    response.sendRedirect(request.getContextPath() + "/dashboard?error=failed_to_save");
                }

            // --- 3. UPDATE CADEAU ---
            } else if ("/update".equals(path)) {
                Gift gift = extract(request); 
                int gId = Integer.parseInt(request.getParameter("giftId"));
                gift.setId(gId);
                int wId = Integer.parseInt(request.getParameter("wishlistId"));
                
                // A. Mise à jour BDD
                boolean success = gift.update(wId, user, giftDAO);
                
                // B. Mise à jour SESSION (Mémoire)
                if (success) {
                    System.out.println("[SESSION-UPDATE] Modification du cadeau en mémoire.");
                    updateGiftInMemory(user, wId, gId, gift, null);
                }
                response.sendRedirect(request.getContextPath() + "/dashboard?status=updated");

            // --- 4. DELETE CADEAU ---
            } else if ("/delete".equals(path)) {
                int gId = Integer.parseInt(request.getParameter("giftId"));
                int wId = Integer.parseInt(request.getParameter("wishlistId"));
                
                Gift gift = new Gift();
                gift.setId(gId);
                
                // A. Mise à jour BDD
                boolean success = gift.delete(wId, user, giftDAO);
                
                // B. Mise à jour SESSION (Mémoire)
                if (success) {
                    System.out.println("[SESSION-UPDATE] Suppression du cadeau en mémoire.");
                    removeGiftFromMemory(user, wId, gId);
                }
                response.sendRedirect(request.getContextPath() + "/dashboard?status=deleted");
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/dashboard?error=1");
        }
    }

    // ====================================================================
    //              MÉTHODES DE MISE À JOUR DE LA MÉMOIRE (SESSION)
    // ====================================================================

    // 1. Ajouter un cadeau dans la liste en session
    private void addGiftInMemory(User user, int wishlistId, Gift newGift) {
        if (user.getCreatedWishlists() == null) return;
        
        for (Wishlist wl : user.getCreatedWishlists()) {
            if (wl.getId() == wishlistId) {
                // On s'assure que la liste n'est pas null
                if (wl.getGifts() != null) {
                    wl.getGifts().add(newGift);
                }
                break; // Liste trouvée, on arrête
            }
        }
    }

    // 2. Supprimer un cadeau de la session
    private void removeGiftFromMemory(User user, int wishlistId, int giftId) {
        if (user.getCreatedWishlists() == null) return;

        for (Wishlist wl : user.getCreatedWishlists()) {
            if (wl.getId() == wishlistId && wl.getGifts() != null) {
                // Utilisation de removeIf (Java 8+) pour supprimer proprement
                wl.getGifts().removeIf(g -> g.getId() == giftId);
                break;
            }
        }
    }

    // 3. Mettre à jour (Update ou Priorité)
    private void updateGiftInMemory(User user, int wishlistId, int giftId, Gift newData, Integer newPriority) {
        if (user.getCreatedWishlists() == null) return;

        for (Wishlist wl : user.getCreatedWishlists()) {
            if (wl.getId() == wishlistId && wl.getGifts() != null) {
                for (Gift g : wl.getGifts()) {
                    if (g.getId() == giftId) {
                        // Mise à jour de la priorité si demandée
                        if (newPriority != null) {
                            g.setPriority(newPriority);
                        }
                        // Mise à jour des infos si demandées
                        if (newData != null) {
                            g.setName(newData.getName());
                            g.setPrice(newData.getPrice());
                            g.setSiteUrl(newData.getSiteUrl());
                            g.setPhotoUrl(newData.getPhotoUrl());
                            if(newData.getPriority() != null) g.setPriority(newData.getPriority());
                        }
                        return; // Cadeau trouvé et mis à jour
                    }
                }
            }
        }
    }

    private Gift extract(HttpServletRequest req) {
        Gift g = new Gift();
        g.setName(req.getParameter("name"));
        g.setDescription(req.getParameter("description"));
        
        String priceStr = req.getParameter("price");
        g.setPrice(priceStr != null && !priceStr.isEmpty() ? Double.parseDouble(priceStr) : 0.0);
        
        g.setPhotoUrl(req.getParameter("photoUrl"));
        g.setSiteUrl(req.getParameter("siteUrl")); 
        
        String p = req.getParameter("priority");
        g.setPriority((p != null && !p.isEmpty()) ? Integer.parseInt(p) : null);
        
        return g;
    }
}