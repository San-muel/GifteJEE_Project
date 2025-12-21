package be.project.servlet;

import be.project.MODEL.Gift;
import be.project.MODEL.User;
import be.project.DAO.GiftDAO; 
import java.io.IOException;
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
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) { 
            response.sendError(HttpServletResponse.SC_FORBIDDEN); 
            return; 
        }
        
        try {
        	if ("/add".equals(path)) {
                System.out.println("CLIENT SERVLET: Début extraction cadeau...");
                Gift gift = extract(request);
                
                String wIdStr = request.getParameter("wishlistId");
                System.out.println("CLIENT SERVLET: wishlistId reçu -> " + wIdStr);
                int wId = Integer.parseInt(wIdStr);
                
                System.out.println("CLIENT SERVLET: Envoi à l'API via gift.save()...");
                boolean success = gift.save(wId, user, giftDAO);
                System.out.println("CLIENT SERVLET: Résultat save -> " + success);
                
                if (success) {
                    user.addGiftLocally(wId, gift); 
                    response.sendRedirect(request.getContextPath() + "/dashboard?status=added");
                } else {
                    System.err.println("CLIENT SERVLET: Échec du save() côté DAO Client");
                    response.sendRedirect(request.getContextPath() + "/dashboard?error=failed_to_save");
                }

            } else if ("/update".equals(path)) {
                Gift gift = extract(request);
                int gId = Integer.parseInt(request.getParameter("giftId"));
                gift.setId(gId);
                int wId = Integer.parseInt(request.getParameter("wishlistId"));
                
                boolean success = gift.update(wId, user, giftDAO);
                
                if (success) {
                    user.updateGiftLocally(wId, gift);
                }
                response.sendRedirect(request.getContextPath() + "/dashboard?status=updated");

            } else if ("/delete".equals(path)) {
                int gId = Integer.parseInt(request.getParameter("giftId"));
                int wId = Integer.parseInt(request.getParameter("wishlistId"));
                
                Gift gift = new Gift();
                gift.setId(gId);
                
                boolean success = gift.delete(wId, user, giftDAO);
                
                if (success) {
                    user.removeGiftLocally(wId, gId);
                }
                response.sendRedirect(request.getContextPath() + "/dashboard?status=deleted");
            }
        } catch (Exception e) {
            response.sendRedirect(request.getContextPath() + "/dashboard?error=1");
        }
    }

    private Gift extract(HttpServletRequest req) {
        Gift g = new Gift();
        g.setName(req.getParameter("name"));
        g.setDescription(req.getParameter("description"));
        // Utilisation de Double.parseDouble avec gestion d'erreur possible selon ton besoin
        String priceStr = req.getParameter("price");
        g.setPrice(priceStr != null ? Double.parseDouble(priceStr) : 0.0);
        g.setPhotoUrl(req.getParameter("photoUrl"));
        String p = req.getParameter("priority");
        g.setPriority((p != null && !p.isEmpty()) ? Integer.parseInt(p) : null);
        return g;
    }
}