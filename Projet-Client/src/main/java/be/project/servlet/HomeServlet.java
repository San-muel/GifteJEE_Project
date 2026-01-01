package be.project.servlet;

import be.project.MODEL.Status; // IMPORTANT : Importez votre Enum
import be.project.MODEL.Wishlist;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // 1. Récupérer toutes les listes via la méthode statique du Modèle
        List<Wishlist> allWishlists = Wishlist.findAll();

        // 2. FILTRAGE : On garde uniquement les listes ACTIVES et NON EXPIRÉES
        LocalDate today = LocalDate.now();

        List<Wishlist> validWishlists = allWishlists.stream()
            // Comparaison propre avec l'Enum (plus sûr que le String)
            .filter(w -> w.getStatus() == Status.ACTIVE)
            // On garde si la date est nulle (pas d'expiration) OU si elle est future/présente
            .filter(w -> w.getExpirationDate() == null || !w.getExpirationDate().isBefore(today))
            .collect(Collectors.toList());

        // 3. On envoie la liste filtrée à la JSP
        request.setAttribute("wishlists", validWishlists);

        // 4. Affichage de la vue
        request.getRequestDispatcher("/WEB-INF/Vues/Home/homePage.jsp").forward(request, response);
    }
}