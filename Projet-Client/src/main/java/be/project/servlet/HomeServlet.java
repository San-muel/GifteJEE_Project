package be.project.servlet;

import be.project.MODEL.Wishlist;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Active Record : On récupère toutes les listes pour les vignettes
        List<Wishlist> allWishlists = be.project.MODEL.Wishlist.findAll();
        request.setAttribute("wishlists", allWishlists);

        // On affiche la page avec les vignettes
        request.getRequestDispatcher("/WEB-INF/Vues/Home/homePage.jsp").forward(request, response);
    }
}