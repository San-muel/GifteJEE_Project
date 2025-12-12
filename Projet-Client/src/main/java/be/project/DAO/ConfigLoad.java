package be.project.DAO;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ConfigLoad implements ServletContextListener {

    public static String API_BASE_URL = "";
    // Je change le nom du paramètre pour être certain de ne pas avoir de conflit
    private static final String PARAM_NAME = "apiBaseUrl"; 

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();
        
        // --- DEBUT DU DEBOGAGE ---
        System.out.println("DEBUG: Démarrage de ConfigLoader.");
        
        // 1. Tente de récupérer le paramètre 'apiBaseUrl'
        String url = context.getInitParameter(PARAM_NAME);
        
        System.out.println("DEBUG: Valeur lue pour '" + PARAM_NAME + "' dans web.xml : " + url);
        // --- FIN DU DEBOGAGE ---

        if (url != null && !url.isEmpty()) {
            API_BASE_URL = url;
            System.out.println("CONFIG SUCCESS: URL de base de l'API stockée dans API_BASE_URL : " + API_BASE_URL);
        } else {
            // Cette ligne s'affichera si le paramètre n'est pas trouvé
            System.err.println("ERREUR CRITIQUE: Le paramètre '" + PARAM_NAME + "' est manquant ou vide dans web.xml. L'API_BASE_URL est vide.");
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        API_BASE_URL = null;
    }
}