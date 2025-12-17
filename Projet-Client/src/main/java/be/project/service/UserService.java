package be.project.service;

import be.project.DAO.UserDAO;
import be.project.MODEL.User;

/**
 * Couche Service (Business Layer) pour la gestion de l'authentification.
 * Elle contient la logique métier et utilise le DAO du client pour communiquer avec l'API REST.
 */
public class UserService {

    // Instance du DAO du client (qui gère les appels HTTP à l'API RESTful)
    // C'est ici que la dépendance au DAO est gérée, pas dans la Servlet.
    private final UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    /**
     * Tente d'authentifier un utilisateur.
     * * @param email L'email fourni par l'utilisateur.
     * @param password Le mot de passe fourni par l'utilisateur.
     * @return L'objet User authentifié (incluant le token) ou null si l'authentification échoue.
     * @throws Exception En cas d'erreur de communication (API inaccessible, erreur JSON, etc.).
     */
    public User authenticateUser(String email, String password) throws Exception {
        
        System.out.println("SERVICE LOGIC: Tentative d'authentification pour " + email);
        
        // Délégation de la tâche à la couche DAO du client.
        // C'est le DAO qui sait comment parler à l'API RESTful.
        User authenticatedUser = userDAO.authenticate(email, password);
        
        if (authenticatedUser != null) {
            System.out.println("SERVICE LOGIC: Authentification API réussie.");
        } else {
            System.out.println("SERVICE LOGIC: Échec de l'authentification.");
        }
        
        return authenticatedUser;
    }
    public boolean register(User user) {
        // Logique métier : par exemple, vérifier que l'email n'est pas vide
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            return false;
        }
        
        // Appel au DAO pour la persistance REST
        return userDAO.create(user);
    }
}