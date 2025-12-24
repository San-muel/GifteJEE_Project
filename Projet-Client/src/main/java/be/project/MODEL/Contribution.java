package be.project.MODEL;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import be.project.DAO.ContributionDAO;

public class Contribution implements Serializable {

	private static final long serialVersionUID = -5457008270231888261L;
	private int id;
	private int userId; 
    private int giftId;
    private double amount;
    private LocalDateTime contributedAt;
    private String comment;
    private Set<User> users = new HashSet<>();
    
    public Contribution() {}

    public Contribution(int id, double amount, LocalDateTime contributedAt, String comment) {
        this();
        this.id = id;
        this.amount = amount;
        this.contributedAt = contributedAt;
        this.comment = comment;
    }

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public int getGiftId() {
		return giftId;
	}

	public void setGiftId(int giftId) {
		this.giftId = giftId;
	}
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Le montant ne peut pas être négatif");
        }
        this.amount = amount;
    }

    public LocalDateTime getContributedAt() {
        return contributedAt;
    }

    public void setContributedAt(LocalDateTime contributedAt) {
        this.contributedAt = contributedAt;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }
    
    public void addUser(User user) {
        this.users.add(user);
    }

    @Override
    public String toString() {
        return "Contribution{" +
                "id=" + id +
                ", amount=" + amount +
                ", contributedAt=" + contributedAt +
                ", comment='" + comment + '\'' +
                '}';
    }
    
 // be.project.MODEL.Contribution

    public Contribution create(int giftId, User user) {
        ContributionDAO dao = new ContributionDAO();
        // On appelle la méthode du DAO qui renvoie un Optional
        java.util.Optional<Contribution> result = dao.createContribution(this, giftId, user);
        
        // Si la création a réussi, on met à jour l'objet actuel avec l'ID et la date générés par l'API
        if (result.isPresent()) {
            this.id = result.get().getId();
            this.contributedAt = result.get().getContributedAt();
            return this;
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contribution that = (Contribution) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}