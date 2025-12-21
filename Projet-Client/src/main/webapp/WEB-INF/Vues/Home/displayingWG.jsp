<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="user" scope="session" type="be.project.MODEL.User" class="be.project.MODEL.User" />

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Mon Espace Cadeaux</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/main.css">
</head>
<body>

    <div class="container">
        <header class="nav-header">
            <div>
                <h1>Bienvenue, <c:out value="${user.username}" /> !</h1>
                <p>Votre tableau de bord personnel : <strong><c:out value="${user.email}" /></strong></p>
            </div>
            <a href="${pageContext.request.contextPath}/home" class="btn-nav">
                🏠 Retour à l'affichage des listes de cadeaux
            </a>
        </header>
        
        <section>
            <h2>Mes Listes de Souhaits</h2>
            <c:choose>
                <c:when test="${not empty user.createdWishlists}">
                    <ul class="wishlist-list">
                        <c:forEach var="wl" items="${user.createdWishlists}">  
                            <li class="wishlist-item">
                                <div class="wishlist-header">
                                    <h3>
                                        <c:out value="${wl.title}" /> 
                                        <span class="badge"><c:out value="${wl.occasion}" /></span>
                                    </h3>
                                    <p class="wishlist-meta">
                                        Fin : <c:out value="${wl.expirationDate}" /> | Statut : <strong><c:out value="${wl.status}" /></strong>
                                    </p>
                                </div>

                                <div class="share-action">
                                    <a href="${pageContext.request.contextPath}/share?wishlistId=${wl.id}&title=${wl.title}" class="btn-share">
                                        🔗 Partager cette liste
                                    </a>
                                </div>

                                <div class="add-gift-box">
                                    <h4>➕ Ajouter un nouveau cadeau</h4>
                                    <form action="${pageContext.request.contextPath}/gift/add" method="POST" class="gift-form">
                                        <input type="hidden" name="wishlistId" value="${wl.id}">
                                        <input type="text" name="name" placeholder="Nom" required>
                                        <input type="number" name="price" placeholder="Prix (€)" step="0.01" min="0" required>
                                        <input type="text" name="description" placeholder="Description">
                                        <input type="text" name="photoUrl" placeholder="URL de l'image (ex: http://...)">
                                        <input type="number" name="priority" placeholder="Prio (1-10)" min="1" max="10">
                                        <button type="submit">Ajouter</button>
                                    </form>
                                </div>
                                
                                <hr>

                                <c:choose>
                                    <c:when test="${not empty wl.gifts}">
                                        <h4>🎁 Cadeaux de cette liste :</h4>
                                        <ul class="gift-list">
                                            <c:forEach var="gift" items="${wl.gifts}">
                                                <li class="gift-item">
                                                <div class="gift-info">
                                                    <c:if test="${not empty gift.photoUrl}">
                                                        <img src="${gift.photoUrl}" alt="${gift.name}" style="width:100px; height:auto; border-radius:8px; margin-bottom:10px; display:block;">
                                                    </c:if>
                                                
                                                    <strong><c:out value="${gift.name}" /></strong> 
                                                    (<c:out value="${gift.price}" />€)
                                                    </div>
                                                    
                                                    <div class="gift-actions">
                                                        <button class="btn-edit" onclick="toggleModifyForm(${gift.id})">Modifier</button>
                                                        <form action="${pageContext.request.contextPath}/gift/delete" method="POST" style="display:inline;">
                                                            <input type="hidden" name="giftId" value="${gift.id}">
                                                            <input type="hidden" name="wishlistId" value="${wl.id}">
                                                            <button type="submit" class="btn-delete" onclick="return confirm('Supprimer ce cadeau ?')">Supprimer</button>
                                                        </form>
                                                    </div>
                                                    <div id="modify-form-${gift.id}" class="modify-form">
                                                        <h5>Modification de ${gift.name}</h5>
                                                        <form action="${pageContext.request.contextPath}/gift/update" method="POST">
                                                            <input type="hidden" name="giftId" value="${gift.id}">
                                                            <input type="hidden" name="wishlistId" value="${wl.id}">
                                                            
                                                            <input type="text" name="name" value="${gift.name}" placeholder="Nom" required>
                                                            <input type="number" name="price" value="${gift.price}" step="0.01" placeholder="Prix">
                                                            <input type="text" name="description" value="${gift.description}" placeholder="Description">
                                                            
                                                            <input type="text" name="photoUrl" value="${gift.photoUrl}" placeholder="URL de l'image">
                                                            
                                                            <button type="submit">Enregistrer</button>
                                                            <button type="button" onclick="toggleModifyForm(${gift.id})">Annuler</button>
                                                        </form>
                                                    </div>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:when>
                                    <c:otherwise>
                                        <p class="empty-msg">Aucun cadeau pour le moment.</p>
                                    </c:otherwise>
                                </c:choose>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    <p class="info-card">Vous n'avez pas encore de liste. <a href="#">Créez-en une !</a></p>
                </c:otherwise>
            </c:choose>
        </section>

        <section class="shared-section">
            <h2>Listes partagées avec moi</h2>
            <c:choose>
                <c:when test="${not empty user.sharedWishlists}">
                    <ul class="wishlist-list shared">
                        <c:forEach var="wl" items="${user.sharedWishlists}">
                            <li class="wishlist-item shared-item">
                                <div class="shared-header">
                                    <h3>🌟 <c:out value="${wl.title}" /></h3>
                                    
                                    <c:forEach var="info" items="${user.sharedWishlistInfos}">
                                        <c:if test="${info.id == wl.id}">
                                            <div class="shared-meta-box">
                                                <p>📅 Partagé le : <c:out value="${info.sharedAt}" /></p>
                                                <p>💬 <em>"<c:out value="${info.notification}" />"</em></p>
                                            </div>
                                        </c:if>
                                    </c:forEach>
                                </div>

                                <c:choose>
                                    <c:when test="${not empty wl.gifts}">
                                        <ul class="gift-list">
                                            <c:forEach var="gift" items="${wl.gifts}">
                                                <li class="gift-item shared-gift">
                                                    <p><strong><c:out value="${gift.name}" /></strong> - <c:out value="${gift.price}" />€</p>
                                                    <button class="btn-contribute">Contribuer</button>
                                                </li>
                                            </c:forEach>
                                        </ul>
                                    </c:when>
                                </c:choose>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise>
                    <p class="empty-msg">Aucune liste partagée pour le moment.</p>
                </c:otherwise>
            </c:choose>
        </section>

        <div class="logout-container" style="display: flex; gap: 20px; align-items: center;">
            <a href="${pageContext.request.contextPath}/home">Retour à l'affichage des listes</a>
            <a href="${pageContext.request.contextPath}/logout">Se Déconnecter</a>
        </div>
    </div>

    <script>
        function toggleModifyForm(giftId) {
            const form = document.getElementById('modify-form-' + giftId);
            if (form) {
                form.style.display = (form.style.display === 'block') ? 'none' : 'block';
            }
        }
    </script>
</body>
</html>