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
        .wishlist-actions { display: flex; gap: 10px; margin-top: 10px; align-items: center; }
        .shared-gifts-display { margin-top: 15px; padding: 15px; background: #fff; border-radius: 8px; border: 1px solid #ddd; }
        .gift-item-shared { display: flex; justify-content: space-between; align-items: center; padding: 10px; border-bottom: 1px solid #eee; }
        .btn-view-gifts { background-color: #2196f3; color: white; border: none; padding: 8px 15px; border-radius: 4px; cursor: pointer; }
        .badge-expired { background-color: #f44336; color: white; padding: 4px 8px; border-radius: 4px; font-size: 0.8em; }
        
        /* STYLE POUR WISHLIST BLOQUÉE (INACTIVE OU EXPIRÉE) */
        .wishlist-item.is-inactive {
            background-color: #f2f2f2;
            border-left: 5px solid #bdc3c7;
            filter: grayscale(0.5);
            opacity: 0.8;
        }
        
        /* Blocage des interactions cadeaux */
        .is-inactive .add-gift-box, 
        .is-inactive .gift-actions button,
        .is-inactive .btn-delete,
        .is-inactive .btn-edit {
            pointer-events: none;
            opacity: 0.5;
            cursor: not-allowed;
        }

        /* Le bouton d'activation reste cliquable pour pouvoir réactiver la liste */
        .is-inactive .wishlist-actions {
            pointer-events: auto !important;
            opacity: 1 !important;
        }

        .status-msg { color: #e74c3c; font-size: 0.85em; font-weight: bold; margin-top: 5px; }
    </style>
</head>
<body>

    <div class="container">
        <header class="nav-header">
            <div>
                <h1>Bienvenue, <c:out value="${user.username}" /> !</h1>
                <p>Votre tableau de bord : <strong><c:out value="${user.email}" /></strong></p>
            </div>
            <a href="${pageContext.request.contextPath}/home" class="btn-nav">🏠 Retour à l'accueil</a>
        </header>
        
        <section>
            <h2>Mes Listes de Souhaits</h2>

            <%-- Formulaire de création de liste --%>
            <div class="add-gift-box" style="background-color: #e3f2fd; margin-bottom: 20px; border-color: #2196F3;">
                <h3>✨ Créer une nouvelle liste</h3>
                <form action="${pageContext.request.contextPath}/wishlist/create" method="POST" class="gift-form">
                    <input type="text" name="title" placeholder="Titre (ex: Noël)" required style="flex: 2;">
                    <input type="text" name="occasion" placeholder="Occasion" required style="flex: 1;">
                    <input type="date" name="expirationDate" required>
                    <select name="status">
                        <option value="ACTIVE">Active</option>
                        <option value="PRIVATE">Privée</option>
                    </select>
                    <button type="submit" style="background-color: #2196F3;">Créer</button>
                </form>
            </div>

            <c:choose>
                <c:when test="${not empty user.createdWishlists}">
                    <ul class="wishlist-list">
                        <c:forEach var="wl" items="${user.createdWishlists}">
                            <%-- LOGIQUE DE BLOCAGE : Statut non-ACTIF ou Date dépassée --%>
                            <c:set var="today" value="<%= java.time.LocalDate.now() %>" />
                            <c:set var="isExpired" value="${wl.expirationDate.isBefore(today)}" />
                            <c:set var="isBlocked" value="${wl.status != 'ACTIVE' || isExpired}" />
                            
                            <li class="wishlist-item ${isBlocked ? 'is-inactive' : ''}">
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
                                    <a href="${pageContext.request.contextPath}/share?wishlistId=${wl.id}&title=${wl.title}" class="btn-share">🔗 Partager</a>

                                    <c:choose>
                                        <c:when test="${isExpired}">
                                            <span class="badge-expired">⚠️ Expirée (Changez la date pour réactiver)</span>
                                        </c:when>
                                        <c:otherwise>
                                            <form action="${pageContext.request.contextPath}/wishlist/toggleStatus" method="POST" style="display:inline;">
                                                <input type="hidden" name="wishlistId" value="${wl.id}">
                                                <button type="submit" class="btn-copy-link" 
                                                        style="background-color: ${wl.status == 'ACTIVE' ? '#ff9800' : '#4caf50'}">
                                                    ${wl.status == 'ACTIVE' ? '⏸️ Désactiver' : '▶️ Activer'}
                                                </button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="add-gift-box" style="margin-top:15px;">
                                    <h4>➕ Ajouter un cadeau</h4>
                                    <form action="${pageContext.request.contextPath}/gift/add" method="POST" class="gift-form">
                                        <input type="hidden" name="wishlistId" value="${wl.id}">
                                        <input type="text" name="name" placeholder="Nom" required ${isBlocked ? 'disabled' : ''}>
                                        <input type="number" name="price" placeholder="Prix (€)" step="0.01" required ${isBlocked ? 'disabled' : ''}>
                                        <input type="text" name="description" placeholder="Description" ${isBlocked ? 'disabled' : ''}>
                                        <input type="text" name="photoUrl" placeholder="URL Image" ${isBlocked ? 'disabled' : ''}>
                                        <input type="text" name="siteUrl" placeholder="Lien Marchand" ${isBlocked ? 'disabled' : ''}>
                                        <button type="submit" ${isBlocked ? 'disabled' : ''}>Ajouter</button>
                                    </form>
                                    <c:if test="${isBlocked}">
                                        <p class="status-msg">⚠️ Action impossible : la liste doit être ACTIVE et non expirée.</p>
                                    </c:if>
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
                                                        <c:if test="${not empty gift.siteUrl}">
                                                            <a href="<c:out value='${gift.siteUrl}'/>" target="_blank" style="margin-left:10px; font-size: 0.8em; color: #2196F3;">🌐 Voir le produit</a>
                                                        </c:if>
                                                    </div>
                                                    <div class="gift-actions">
                                                        <button type="button" class="btn-edit" onclick="toggleModifyForm(${gift.id})" ${isBlocked ? 'disabled' : ''}>Modifier</button>
                                                        <form action="${pageContext.request.contextPath}/gift/delete" method="POST" style="display:inline;">
                                                            <input type="hidden" name="giftId" value="${gift.id}">
                                                            <input type="hidden" name="wishlistId" value="${wl.id}">
                                                            <button type="submit" class="btn-delete" onclick="return confirm('Supprimer ce cadeau ?')" ${isBlocked ? 'disabled' : ''}>Supprimer</button>
                                                        </form>
                                                    </div>
                                                </div>
                                                <%-- Formulaire de modification --%>
                                                <div id="modify-form-${gift.id}" class="add-gift-box" style="display:none; margin-top: 10px; background-color: #fff9c4;">
                                                    <form action="${pageContext.request.contextPath}/gift/update" method="POST" class="gift-form">
                                                        <input type="hidden" name="giftId" value="${gift.id}">
                                                        <input type="hidden" name="wishlistId" value="${wl.id}">
                                                        <input type="text" name="name" value="<c:out value='${gift.name}'/>" required>
                                                        <input type="number" name="price" value="<c:out value='${gift.price}'/>" step="0.01" required>
                                                        <input type="text" name="siteUrl" value="<c:out value='${gift.siteUrl}'/>" placeholder="Lien Marchand">
                                                        <button type="submit">Enregistrer</button>
                                                        <button type="button" onclick="toggleModifyForm(${gift.id})">Annuler</button>
                                                    </form>
                                                </div>
                                            </li>
                                        </c:forEach>
                                        </ul>
                                    </c:when>
                                    <c:otherwise><p class="empty-msg">Aucun cadeau dans cette liste.</p></c:otherwise>
                                </c:choose>
                            </li>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise><p class="info-card">Vous n'avez pas encore créé de liste.</p></c:otherwise>
            </c:choose>
        </section>

        <%-- SECTION DES LISTES PARTAGÉES --%>
        <section class="shared-section">
            <h2>Listes partagées avec moi</h2>
            <c:choose>
                <c:when test="${not empty user.sharedWishlists}">
                    <ul class="wishlist-list shared">
                        <c:forEach var="wl" items="${user.sharedWishlists}">
                            <%-- On n'affiche que les listes actives pour les invités --%>
                            <c:if test="${wl.status == 'ACTIVE'}">
                                <li class="wishlist-item shared-item">
                                    <div style="display: flex; justify-content: space-between; align-items: center;">
                                        <h3>🌟 <c:out value="${wl.title}" /></h3>
                                        <span class="badge" style="background-color: #ff9800;">Invité</span>
                                    </div>
                                    <div class="wishlist-actions">
                                        <button type="button" class="btn-view-gifts" onclick="toggleSharedGifts(${wl.id})">🎁 Voir les cadeaux</button>
                                    </div>
                                    <div id="shared-gifts-container-${wl.id}" class="shared-gifts-display" style="display:none;">
                                        <c:choose>
                                            <c:when test="${not empty wl.gifts}">
                                                <div class="gift-list">
                                                    <c:forEach var="gift" items="${wl.gifts}">
                                                        <div class="gift-item-shared">
                                                            <span><strong>${gift.name}</strong> - ${gift.price}€</span>
                                                            <button class="btn-share" onclick="alert('Fonction de réservation à venir !')">💰 Contribuer</button>
                                                        </div>
                                                    </c:forEach>
                                                </div>
                                            </c:when>
                                            <c:otherwise><p><small>Cette liste est vide.</small></p></c:otherwise>
                                        </c:choose>
                                    </div>
                                </li>
                            </c:if>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise><p class="empty-msg">Aucune liste n'est partagée avec vous.</p></c:otherwise>
            </c:choose>
        </section>

        <div class="logout-container">
            <a href="${pageContext.request.contextPath}/logout">Se Déconnecter</a>
        </div>
    </div>

    <script>
        function toggleSharedGifts(id) {
            const container = document.getElementById('shared-gifts-container-' + id);
            if (container) container.style.display = container.style.display === 'none' ? 'block' : 'none';
        }
        function toggleModifyForm(giftId) {
            const form = document.getElementById('modify-form-' + giftId);
            if (form) form.style.display = form.style.display === 'none' ? 'block' : 'none';
        }
        function copyInviteLink(button) {
            const wishlistId = button.getAttribute('data-id');
            const fullUrl = window.location.origin + "${pageContext.request.contextPath}/invite?wishlistId=" + wishlistId;
            navigator.clipboard.writeText(fullUrl).then(() => alert("Lien d'invitation copié !"));
        }
    </script>
</body>
</html>