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
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Wishlist> validWishlists = Wishlist.findActiveAndValid();

        request.setAttribute("wishlists", validWishlists);
        request.getRequestDispatcher("/WEB-INF/Vues/Home/homePage.jsp").forward(request, response);
    }
}