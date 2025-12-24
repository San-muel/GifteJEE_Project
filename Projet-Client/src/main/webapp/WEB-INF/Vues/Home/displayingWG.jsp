<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="user" scope="session" type="be.project.MODEL.User" class="be.project.MODEL.User" />

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Mon Espace Cadeaux</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/main.css">
    <style>
        .btn-copy-link { background-color: #4caf50; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; font-size: 0.85em; margin-top: 5px; }
        .btn-copy-link:hover { background-color: #45a049; }
        .wishlist-actions { display: flex; gap: 10px; margin-top: 10px; }
        
        /* Styles pour les notifications et l'affichage dynamique */
        .notification-bubble { background: #f0f7ff; padding: 10px; border-left: 4px solid #2196f3; margin: 10px 0; border-radius: 4px; }
        .shared-gifts-display { margin-top: 15px; padding: 15px; background: #fff; border-radius: 8px; border: 1px solid #ddd; box-shadow: inset 0 2px 4px rgba(0,0,0,0.05); }
        .gift-item-shared { display: flex; justify-content: space-between; align-items: center; padding: 10px; border-bottom: 1px solid #eee; }
        .btn-view-gifts { background-color: #2196f3; color: white; border: none; padding: 8px 15px; border-radius: 4px; cursor: pointer; }
    </style>
</head>
<body>

    <div class="container">
        <header class="nav-header">
            <div>
                <h1>Bienvenue, <c:out value="${user.username}" /> !</h1>
                <p>Votre tableau de bord personnel : <strong><c:out value="${user.email}" /></strong></p>
            </div>
            <a href="${pageContext.request.contextPath}/home" class="btn-nav">🏠 Retour à l'accueil</a>
        </header>
        
        <section>
            <h2>Mes Listes de Souhaits</h2>

            <div class="add-gift-box" style="background-color: #e3f2fd; margin-bottom: 20px; border-color: #2196F3;">
                <h3>✨ Créer une nouvelle liste</h3>
                <form action="${pageContext.request.contextPath}/wishlist/create" method="POST" class="gift-form">
                    <input type="text" name="title" placeholder="Titre (ex: Anniversaire)" required style="flex: 2;">
                    <input type="text" name="occasion" placeholder="Occasion" required style="flex: 1;">
                    <input type="date" name="expirationDate" required>
                    <select name="status">
                        <option value="ACTIVE">Active</option>
                        <option value="ARCHIVED">Archivée</option>
                        <option value="PRIVATE">Privée</option>
                    </select>
                    <button type="submit" style="background-color: #2196F3;">Créer</button>
                </form>
            </div>

            <c:choose>
                <c:when test="${not empty user.createdWishlists}">
                    <ul class="wishlist-list">
                        <c:forEach var="wl" items="${user.createdWishlists}">  
                            <li class="wishlist-item">
                                <div class="wishlist-header">
                                    <div style="display: flex; justify-content: space-between; align-items: start;">
                                        <div>
                                            <h3><c:out value="${wl.title}" /> <span class="badge"><c:out value="${wl.occasion}" /></span></h3>
                                            <p class="wishlist-meta">Fin : <c:out value="${wl.expirationDate}" /> | Statut : <strong><c:out value="${wl.status}" /></strong></p>
                                        </div>
                                        <button type="button" class="btn-copy-link" data-id="${wl.id}" onclick="copyInviteLink(this)">📋 Copier le lien public</button>
                                    </div>
                                </div>

                                <div class="wishlist-actions">
                                    <a href="${pageContext.request.contextPath}/share?wishlistId=${wl.id}&title=${wl.title}" class="btn-share">🔗 Partager avec un membre</a>
                                </div>

                                <div class="add-gift-box" style="margin-top:15px;">
                                    <h4>➕ Ajouter un cadeau</h4>
                                    <form action="${pageContext.request.contextPath}/gift/add" method="POST" class="gift-form">
                                        <input type="hidden" name="wishlistId" value="${wl.id}">
                                        <input type="text" name="name" placeholder="Nom" required>
                                        <input type="number" name="price" placeholder="Prix (€)" step="0.01" required>
                                        <input type="text" name="description" placeholder="Description">
                                        <input type="text" name="photoUrl" placeholder="URL Image">
                                        <button type="submit">Ajouter</button>
                                    </form>
                                </div>
                                
                                <hr>

                                <c:choose>
                                    <c:when test="${not empty wl.gifts}">
                                        <ul class="gift-list">
										<c:forEach var="gift" items="${wl.gifts}">
										    <li class="gift-item" style="flex-direction: column; align-items: stretch;">
										        <div style="display: flex; justify-content: space-between; align-items: center; width: 100%;">
										            <div class="gift-info">
										                <c:if test="${not empty gift.photoUrl}">
										                    <img src="${gift.photoUrl}" style="width:60px; margin-right:10px;">
										                </c:if>
										                <strong><c:out value="${gift.name}" /></strong> (<c:out value="${gift.price}" />€)
										            </div>
										            <div class="gift-actions">
										                <button type="button" class="btn-edit" onclick="toggleModifyForm(${gift.id})">Modifier</button>
										                
										                <form action="${pageContext.request.contextPath}/gift/delete" method="POST" style="display:inline;">
										                    <input type="hidden" name="giftId" value="${gift.id}">
										                    <input type="hidden" name="wishlistId" value="${wl.id}">
										                    <button type="submit" class="btn-delete" onclick="return confirm('Supprimer ?')">Supprimer</button>
										                </form>
										            </div>
										        </div>
										
										        <div id="modify-form-${gift.id}" class="add-gift-box" style="display:none; margin-top: 10px; background-color: #fff9c4; border-color: #fbc02d;">
										            <h4 style="margin-top:0;">✏️ Modifier le cadeau</h4>
										            <form action="${pageContext.request.contextPath}/gift/update" method="POST" class="gift-form">
										                <input type="hidden" name="giftId" value="${gift.id}">
										                <input type="hidden" name="wishlistId" value="${wl.id}">
										                
										                <input type="text" name="name" value="<c:out value='${gift.name}'/>" required>
										                <input type="number" name="price" value="<c:out value='${gift.price}'/>" step="0.01" required>
										                <input type="text" name="description" value="<c:out value='${gift.description}'/>" placeholder="Description">
										                <input type="text" name="photoUrl" value="<c:out value='${gift.photoUrl}'/>" placeholder="URL Image">
										                
										                <div style="margin-top:10px;">
										                    <button type="submit" style="background-color: #fbc02d; color: black;">Enregistrer</button>
										                    <button type="button" onclick="toggleModifyForm(${gift.id})" style="background-color: #ccc;">Annuler</button>
										                </div>
										            </form>
										        </div>
										    </li>
										</c:forEach>
                                        </ul>
                                    </c:when>
                                    <c:otherwise><p class="empty-msg">Aucun cadeau.</p></c:otherwise>
                                </c:choose>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise><p class="info-card">Aucune liste créée pour le moment.</p></c:otherwise>
            </c:choose>
        </section>

        <section class="shared-section">
            <h2>Listes partagées avec moi</h2>
            <c:choose>
                <c:when test="${not empty user.sharedWishlists}">
                    <ul class="wishlist-list shared">
                        <c:forEach var="wl" items="${user.sharedWishlists}">
                            <li class="wishlist-item shared-item">
                                <div style="display: flex; justify-content: space-between; align-items: center;">
                                    <h3>🌟 <c:out value="${wl.title}" /></h3>
                                    <span class="badge" style="background-color: #ff9800;">Nouvelle invitation</span>
                                </div>

                                <c:forEach var="info" items="${user.sharedWishlistInfos}">
                                    <c:if test="${info.id == wl.id}">
                                        <div class="notification-bubble">
                                            <p style="margin: 0; font-style: italic;">" <c:out value="${info.notification}" /> "</p>
                                            <p style="margin-top: 5px;"><small>📅 Reçu le : <c:out value="${info.sharedAt}" /></small></p>
                                        </div>
                                    </c:if>
                                </c:forEach>
                                
                                <div class="wishlist-actions">
                                    <button type="button" class="btn-view-gifts" onclick="toggleSharedGifts(${wl.id})">
                                        🎁 Voir les cadeaux
                                    </button>
                                </div>

                                <div id="shared-gifts-container-${wl.id}" class="shared-gifts-display" style="display:none;">
                                    <h4 style="margin-top:0;">Cadeaux disponibles :</h4>
                                    <c:choose>
                                        <c:when test="${not empty wl.gifts}">
                                            <div class="gift-list">
                                                <c:forEach var="gift" items="${wl.gifts}">
                                                    <div class="gift-item-shared">
                                                        <span><strong>${gift.name}</strong> - ${gift.price}€</span>
                                                        <button class="btn-share" style="padding: 5px 10px; font-size: 0.8em;" onclick="alert('Option de contribution à venir !')">💰 Contribuer</button>
                                                    </div>
                                                </c:forEach>
                                            </div>
                                        </c:when>
                                        <c:otherwise><p><small>Cette liste ne contient pas encore de cadeaux.</small></p></c:otherwise>
                                    </c:choose>
                                </div>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise><p class="empty-msg">Rien à afficher.</p></c:otherwise>
            </c:choose>
        </section>

        <div class="logout-container">
            <a href="${pageContext.request.contextPath}/logout">Se Déconnecter</a>
        </div>
    </div>

    <script>
        // Fonction pour afficher/masquer les cadeaux partagés dynamiquement
        function toggleSharedGifts(id) {
            const container = document.getElementById('shared-gifts-container-' + id);
            if (container) {
                const isHidden = container.style.display === 'none';
                container.style.display = isHidden ? 'block' : 'none';
                if(isHidden) container.scrollIntoView({ behavior: 'smooth', block: 'nearest' });
            }
        }

        function toggleModifyForm(giftId) {
            const form = document.getElementById('modify-form-' + giftId);
            if (form) form.style.display = (form.style.display === 'block') ? 'none' : 'block';
        }

        function copyInviteLink(button) {
            const wishlistId = button.getAttribute('data-id');
            const contextPath = "${pageContext.request.contextPath}";
            const fullUrl = window.location.origin + contextPath + "/invite?wishlistId=" + wishlistId;
            
            navigator.clipboard.writeText(fullUrl).then(() => {
                alert("Lien public copié !\n" + fullUrl);
            });
        }
    </script>
</body>
</html>