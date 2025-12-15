<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="user" scope="session" type="be.project.MODEL.User" class="be.project.MODEL.User" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Page d'Accueil</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/main.css">
</head>
<body>

    <div class="container">
        <h1>Bienvenue, <c:out value="${user.username}" /> !</h1>
        
        <p>Votre email : <c:out value="${user.email}" /></p>
        
        <h2>Vos Wishlists Créées</h2>
    	
        <c:choose>
            <c:when test="${not empty user.createdWishlists}">
                <ul class="wishlist-list">
        			<c:forEach var="wl" items="${user.createdWishlists}">  
        	            <li class="wishlist-item">
                            <h3>
                                **<c:out value="${wl.title}" />** (Occasion: <c:out value="${wl.occasion}" /> | 
                                Expire: <c:out value="${wl.expirationDate}" /> |
                                Statut: <c:out value="${wl.status}" />)
                            </h3>

                            <h4>➕ Ajouter un nouveau cadeau</h4>
                            <form action="${pageContext.request.contextPath}/gift/add" method="POST" class="gift-form">
                                <input type="hidden" name="wishlistId" value="<c:out value="${wl.id}" />">
                                
                                <input type="text" name="name" placeholder="Nom du cadeau" required>
                                <input type="number" name="price" placeholder="Prix (€)" step="0.01" min="0" required>
                                <input type="text" name="description" placeholder="Description (optionnel)">
                                <input type="number" name="priority" placeholder="Priorité (1-10)" min="1" max="10">
                                <input type="text" name="photoUrl" placeholder="URL Photo (optionnel)">
                                
                                <button type="submit">Ajouter le Cadeau</button>
                            </form>
                            
                            <hr>

                            <c:choose>
                                <c:when test="${not empty wl.gifts}">
                                    <h4>🎁 Cadeaux de cette liste :</h4>
                                    <ul class="gift-list">
                                        <c:forEach var="gift" items="${wl.gifts}">
                                            <li class="gift-item" id="gift-<c:out value="${gift.id}" />">
                                                
                                                <p>
                                                    **<c:out value="${gift.name}" />** (<c:out value="${gift.price}" />€)
                                                    <c:if test="${not empty gift.priority}">
                                                        [P: <c:out value="${gift.priority}" />]
                                                    </c:if>
                                                    - Description: <c:out value="${gift.description}" />
                                                </p>
                                                
                                                <div class="gift-actions">
                                                    <button onclick="toggleModifyForm(<c:out value="${gift.id}" />)">Modifier</button>
                                                    
                                                    <form action="${pageContext.request.contextPath}/gift/delete" method="POST" style="display:inline;">
                                                        <input type="hidden" name="giftId" value="<c:out value="${gift.id}" />">
                                                        <input type="hidden" name="wishlistId" value="<c:out value="${wl.id}" />">
                                                        <button type="submit" onclick="return confirm('Êtes-vous sûr de vouloir supprimer ce cadeau ?')">Supprimer</button>
                                                    </form>
                                                </div>

                                                <div id="modify-form-<c:out value="${gift.id}" />" class="modify-form">
                                                    <h5>Modification de <c:out value="${gift.name}" /></h5>
                                                    <form action="${pageContext.request.contextPath}/gift/update" method="POST">
                                                        <input type="hidden" name="giftId" value="<c:out value="${gift.id}" />">
                                                        <input type="hidden" name="wishlistId" value="<c:out value="${wl.id}" />">
                                                        
                                                        <input type="text" name="name" value="<c:out value="${gift.name}" />" required>
                                                        <input type="number" name="price" value="<c:out value="${gift.price}" />" step="0.01" min="0" required>
                                                        <input type="text" name="description" value="<c:out value="${gift.description}" />" placeholder="Description (optionnel)">
                                                        <input type="number" name="priority" value="<c:out value="${gift.priority}" />" placeholder="Priorité (1-10)" min="1" max="10">
                                                        <input type="text" name="photoUrl" value="<c:out value="${gift.photoUrl}" />" placeholder="URL Photo (optionnel)">

                                                        <button type="submit">Enregistrer les modifications</button>
                                                        <button type="button" onclick="toggleModifyForm(<c:out value="${gift.id}" />)">Annuler</button>
                                                    </form>
                                                </div>
                                            </li>
                                        </c:forEach>
                                    </ul>
                                </c:when>
                                <c:otherwise>
                                    <p>Cette liste ne contient aucun cadeau pour le moment.</p>
                                </c:otherwise>
                            </c:choose>
                        </li>
        		    </c:forEach>
        	    </ul>
            </c:when>
            <c:otherwise>
                <p>Vous n'avez pas encore créé de liste de souhaits. <a href="#">Commencez maintenant !</a></p>
            </c:otherwise>
        </c:choose>
        
        <div class="logout-container">
            <a href="${pageContext.request.contextPath}/logout">Se Déconnecter</a>
        </div>
    </div>
    
    <script>
        function toggleModifyForm(giftId) {
            const formId = 'modify-form-' + giftId;
            const form = document.getElementById(formId);
            
            if (form) {
                // Bascule l'affichage entre 'none' et 'block'
                if (form.style.display === 'block') {
                    form.style.display = 'none';
                } else {
                    form.style.display = 'block';
                }
            }
        }
    </script>
</body>
</html>