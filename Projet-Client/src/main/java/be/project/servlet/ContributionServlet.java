package be.project.servlet;

import be.project.MODEL.Contribution;
import be.project.MODEL.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/contribution/*")
public class ContributionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID requis.");
            return;
        }
        
        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            
            Contribution contribution = Contribution.find(id);

            if (contribution != null) {
                request.setAttribute("contribution", contribution);
                request.getRequestDispatcher("/WEB-INF/Vues/contributionDetail.jsp").forward(request, response);
            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Introuvable.");
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID invalide.");
        } catch (Exception e) {
            e.printStackTrace(); 
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        User currentUser = (User) request.getSession().getAttribute("user");

        if (currentUser == null) {
            response.sendRedirect(request.getContextPath() + "/login"); 
            return;
        }

        try {
            int giftId = Integer.parseInt(request.getParameter("giftId"));
            int wishlistId = Integer.parseInt(request.getParameter("wishlistId"));
            double amount = Double.parseDouble(request.getParameter("amount"));
            String comment = request.getParameter("comment");

            Contribution contrib = new Contribution();
            contrib.setAmount(amount);
            contrib.setComment(comment);
            contrib.setUserId(currentUser.getId()); 

            Contribution createdContrib = contrib.create(giftId, currentUser); 

            if (createdContrib != null) {
                currentUser.syncContributionAddition(createdContrib);
                
                response.sendRedirect(request.getContextPath() + "/wishlistDetail?id=" + wishlistId + "&success=contrib");
            } else {
                response.sendRedirect(request.getContextPath() + "/wishlistDetail?id=" + wishlistId + "&error=contrib_failed");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect(request.getContextPath() + "/dashboard?error=server");
        }
    }
}