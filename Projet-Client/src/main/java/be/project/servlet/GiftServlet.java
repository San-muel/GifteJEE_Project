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
            String wIdParam = request.getParameter("wishlistId");
            int wId = (wIdParam != null) ? Integer.parseInt(wIdParam) : 0;
            
            if ("/priority".equals(path)) {
                int gId = Integer.parseInt(request.getParameter("giftId"));
                int currentPrio = Integer.parseInt(request.getParameter("currentPriority"));
                
                Gift gift = new Gift();
                gift.setId(gId);
                gift.setPriority(currentPrio);
                
                gift.calculateNewPriority(request.getParameter("direction"));

                if (gift.updatePriority(wId, user, giftDAO)) {
                    user.syncGiftUpdate(wId, gift); 
                }
                response.sendRedirect(request.getContextPath() + "/dashboard?status=priority_ok");

            } else if ("/add".equals(path)) {
                Gift gift = extract(request);
                
                if (gift.save(wId, user, giftDAO)) {
                    user.syncGiftAddition(wId, gift); 
                    response.sendRedirect(request.getContextPath() + "/dashboard?status=added");
                } else {
                    response.sendRedirect(request.getContextPath() + "/dashboard?error=failed_to_save");
                }

            } else if ("/update".equals(path)) {
                Gift gift = extract(request); 
                gift.setId(Integer.parseInt(request.getParameter("giftId")));
                
                if (gift.update(wId, user, giftDAO)) {
                    user.syncGiftUpdate(wId, gift); 
                }
                response.sendRedirect(request.getContextPath() + "/dashboard?status=updated");

            } else if ("/delete".equals(path)) {
                int gId = Integer.parseInt(request.getParameter("giftId"));
                Gift gift = new Gift();
                gift.setId(gId);
                
                if (gift.delete(wId, user, giftDAO)) {
                    user.syncGiftRemoval(wId, gId); 
                }
                response.sendRedirect(request.getContextPath() + "/dashboard?status=deleted");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/dashboard?error=1");
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
        if (p != null && !p.isEmpty()) g.setPriority(Integer.parseInt(p));
        
        return g;
    }
}