package be.project.DAO;
import be.project.MODEL.*;

public abstract class AbstractDAOFactory {
    public static final int DAO_FACTORY = 0;
    public static final int XML_DAO_FACTORY = 1; 

    public abstract DAO<Contribution> getMemberDAO();
    public abstract DAO<Gift> getBikeDAO();
    public abstract DAO<SharedWishlist> getVehicleDAO();
    public abstract DAO<User> getInscriptionDAO();
    public abstract DAO<Wishlist> getRideDAO();

    // Méthode factory statique
    public static AbstractDAOFactory getFactory(int type) {
        switch (type) {
            case DAO_FACTORY:
                return new DAOFactory();
            default:
                return null;
        }
    }
}