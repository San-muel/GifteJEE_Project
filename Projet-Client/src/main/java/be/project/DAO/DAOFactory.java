package be.project.DAO;

import be.project.MODEL.*;

public class DAOFactory extends AbstractDAOFactory {

    @Override
    public DAO<Contribution> getMemberDAO() { 
        return new ContributionDAO(); 
    }

    @Override
    public DAO<Gift> getBikeDAO() { 
        return new GiftDAO(); 
    }

    @Override
    public DAO<SharedWishlist> getVehicleDAO() { 
        return new SharedWishlistDAO(); 
    }

    @Override
    public DAO<User> getInscriptionDAO() { 
        return new UserDAO(); 
    }

    @Override
    public DAO<Wishlist> getRideDAO() { 
        return new WishlistDAO(); 
    }
}