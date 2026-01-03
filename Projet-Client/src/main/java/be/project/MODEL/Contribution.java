package be.project.MODEL;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.Optional;

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

    public static Contribution find(int id) {
        ContributionDAO dao = new ContributionDAO();
        return dao.find(id);
    }

    public Contribution create(int giftId, User user) {
        ContributionDAO dao = new ContributionDAO();
        Optional<Contribution> result = dao.createContribution(this, giftId, user);
        
        if (result.isPresent()) {
            this.id = result.get().getId();
            this.contributedAt = result.get().getContributedAt();
            this.giftId = giftId; 
            return this;
        }
        return null;
    }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    public int getGiftId() { return giftId; }
    public void setGiftId(int giftId) { this.giftId = giftId; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getContributedAt() { return contributedAt; }
    public void setContributedAt(LocalDateTime contributedAt) { this.contributedAt = contributedAt; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Set<User> getUsers() { return users; }
    public void setUsers(Set<User> users) { this.users = users; }
    public void addUser(User user) { this.users.add(user); }

    public double getAmount() { return amount; }
    public void setAmount(double amount) {
        if (amount < 0) throw new IllegalArgumentException("Le montant ne peut pas être négatif");
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Contribution{" + "id=" + id + ", amount=" + amount + '}';
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contribution that = (Contribution) o;
        return id == that.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }
}