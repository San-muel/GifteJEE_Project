package be.project.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class GiftServlet
 */
@WebServlet("/GiftServlet")
public class GiftServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GiftServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	// Dans votre GiftServlet.java (à titre indicatif)

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    String path = request.getPathInfo(); // Récupère la partie de l'URL après /gift (ex: /add)
	    
	    if ("/add".equals(path)) {
	        // 1. Récupérer les paramètres du formulaire (wishlistId, name, price, etc.)
	        // 2. Créer un objet Gift
	        // 3. Appeler le GiftDAO pour insérer le Gift en base
	        // 4. Rediriger vers la page d'accueil
	    } else if ("/delete".equals(path)) {
	        // 1. Récupérer giftId et wishlistId
	        // 2. Appeler le GiftDAO pour supprimer
	        // 3. Rediriger vers la page d'accueil
	    } else if ("/update".equals(path)) {
	        // 1. Récupérer les nouveaux paramètres, y compris giftId
	        // 2. Créer un objet Gift mis à jour
	        // 3. Appeler le GiftDAO pour mettre à jour
	        // 4. Rediriger vers la page d'accueil
	    }
	    
	    // Après chaque action, assurez-vous de recharger l'objet User dans la session
	    // pour que la JSP affiche les données à jour.
	    // Example: response.sendRedirect(request.getContextPath() + "/home");
	}
}
