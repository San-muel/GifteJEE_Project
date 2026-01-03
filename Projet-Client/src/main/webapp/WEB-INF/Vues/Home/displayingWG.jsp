<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 
<jsp:useBean id="user" scope="session" type="be.project.MODEL.User" class="be.project.MODEL.User" />

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Mon Espace Cadeaux</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/main.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/logo.png" />
    <style>
        .btn-copy-link { background-color: #4caf50; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; font-size: 0.85em; margin-top: 5px; }
        .btn-copy-link:hover { background-color: #45a049; }
        .wishlist-actions { display: flex; gap: 10px; margin-top: 10px; align-items: center; }
        
        .shared-gifts-display { margin-top: 15px; padding: 15px; background: #fff; border-radius: 8px; border: 1px solid #ddd; box-shadow: inset 0 2px 4px rgba(0,0,0,0.05); }
        .gift-item-shared { display: flex; flex-direction: column; padding: 15px; border-bottom: 1px solid #eee; background-color: #fafafa; margin-bottom: 10px; border-radius: 6px;} 
        
        .btn-view-gifts { background-color: #2196f3; color: white; border: none; padding: 8px 15px; border-radius: 4px; cursor: pointer; }
        .badge-expired { background-color: #f44336; color: white; padding: 4px 8px; border-radius: 4px; font-size: 0.8em; }

        .contribution-box { display: none; margin-top: 10px; padding: 10px; background: #fff; border-radius: 5px; border: 1px solid #ddd; }
        .input-amount { padding: 5px; width: 80px; border-radius: 4px; border: 1px solid #ccc; }
        .btn-contribute { background-color: #4caf50; color: white; border: none; padding: 6px 10px; border-radius: 4px; cursor: pointer; font-weight: bold; font-size: 0.9em; }
        .btn-reserve { background-color: #2196F3; color: white; border: none; padding: 6px 10px; border-radius: 4px; cursor: pointer; font-weight: bold; margin-left: 5px; font-size: 0.9em;}
        .btn-submit-contribution { background-color: #4caf50; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; }
        .btn-completed { background-color: #ccc; cursor: not-allowed; color: #666; padding: 6px 10px; border:none; border-radius: 4px;}
        
        .progress-container { width: 100%; background-color: #e0e0e0; border-radius: 10px; margin: 10px 0; height: 8px; overflow: hidden; }
        .progress-bar { height: 100%; background-color: #4caf50; transition: width 0.5s ease-in-out; }
        .amount-info { font-size: 0.85em; color: #555; display: flex; justify-content: space-between; margin-bottom: 5px;}
        .missing-amount { color: #d32f2f; font-weight: bold; }
        .fully-funded { color: #4caf50; font-weight: bold; }
        
        .gift-details-row { display: flex; justify-content: space-between; align-items: flex-start; width: 100%; gap: 15px; }
        .gift-content { flex-grow: 1; }
        .gift-actions-col { display: flex; flex-direction: column; align-items: flex-end; min-width: 140px;}

        .notification-center {
            background-color: #fff3cd; 
            border: 1px solid #ffeeba;
            color: #856404;
            padding: 15px;
            margin-bottom: 25px;
            border-radius: 8px;
            position: relative;
        }
        .notification-center h3 { margin-top: 0; font-size: 1.1em; display: flex; align-items: center; gap: 8px; }
        .notif-list { list-style: none; padding: 0; margin: 0; }
        .notif-item { 
            padding: 8px 0; 
            border-bottom: 1px solid rgba(133, 100, 4, 0.1); 
            font-size: 0.95em;
        }
        .notif-item:last-child { border-bottom: none; }
        .bell-icon { font-size: 1.2em; }

        .wishlist-item.is-inactive {
            background-color: #f2f2f2;
            border-left: 5px solid #bdc3c7;
            filter: grayscale(0.5);
            opacity: 0.8;
        }
        
        .status-msg { color: #e74c3c; font-size: 0.85em; font-weight: bold; margin-top: 5px; }
        .lock-msg { display:block; color: #d35400; font-size: 0.8em; font-weight: bold; margin-top: 5px; }
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

        <c:if test="${not empty notifications}">
            <div class="notification-center">
                <h3><span class="bell-icon">🔔</span> Activité récente sur les listes suivies</h3>
                <ul class="notif-list">
                    <c:forEach var="notif" items="${notifications}">
                        <li class="notif-item">${notif}</li>
                    </c:forEach>
                </ul>
            </div>
        </c:if>

        <section>
            <h2>Mes Listes de Souhaits</h2>

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
                                        <button type="button" class="btn-copy-link" 
                                                ${isBlocked ? 'disabled style="opacity:0.5; cursor:not-allowed; background-color:#999;"' : ''} 
                                                onclick="copyInviteLink('${pageContext.request.scheme}://${pageContext.request.serverName}:${pageContext.request.serverPort}${pageContext.request.contextPath}/register?wishlistId=${wl.id}')">
                                            📋 Copier le lien public
                                        </button>
                                    </div>
                                </div>
                                <div class="wishlist-actions">
                                    <c:choose>
                                        <c:when test="${isBlocked}">
                                            <span style="background-color: #ccc; color: #666; padding: 5px 10px; border-radius: 4px; cursor: not-allowed; font-size: 0.9em;">
                                                🔗 Partager
                                            </span>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/share?wishlistId=${wl.id}&title=${wl.title}" class="btn-share">
                                                🔗 Partager
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                    
                                    <c:choose>
                                        <c:when test="${isExpired}">
                                            <div style="display:flex; align-items:center; gap:8px; background-color: #ffebee; padding: 5px 8px; border-radius: 4px; border: 1px solid #ef9a9a;">
                                                <span style="color: #c62828; font-weight: bold; font-size: 0.85em;">⚠️ Expirée</span>
                                                
                                                <form action="${pageContext.request.contextPath}/wishlist/updateDate" method="POST" style="display:flex; align-items:center; gap:5px; margin:0;">
                                                    <input type="hidden" name="wishlistId" value="${wl.id}">
                                                    <input type="date" name="newDate" required min="<%= java.time.LocalDate.now() %>" 
                                                           style="font-size:0.8em; padding:3px; border:1px solid #cc8888; border-radius:3px; color:#c62828;">
                                                    <button type="submit" title="Mettre à jour la date"
                                                            style="background-color: #c62828; color: white; border: none; padding: 4px 8px; border-radius: 3px; cursor: pointer; font-size: 0.8em; font-weight:bold;">
                                                        🔄 Relancer
                                                    </button>
                                                </form>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <form action="${pageContext.request.contextPath}/wishlist/toggleStatus" method="POST" style="display:inline;">
                                                <input type="hidden" name="wishlistId" value="${wl.id}">
                                                <button type="submit" class="btn-copy-link" style="background-color: ${wl.status == 'ACTIVE' ? '#ff9800' : '#4caf50'}">
                                                    ${wl.status == 'ACTIVE' ? '⏸️ Désactiver' : '▶️ Activer'}
                                                </button>
                                            </form>
                                        </c:otherwise>
                                    </c:choose>
                                </div>

                                <div class="add-gift-box" style="margin-top:15px;">
                                    <h4>➕ Ajouter un cadeau</h4>
                                    <form action="${pageContext.request.contextPath}/gift/add" method="POST" class="gift-form" style="display: flex; gap: 10px; flex-wrap: wrap;">
                                        <input type="hidden" name="wishlistId" value="${wl.id}">
                                        <input type="text" name="name" placeholder="Nom du cadeau" required ${isBlocked ? 'disabled' : ''} style="flex: 2; min-width: 150px;">
                                        <input type="number" name="price" placeholder="Prix (€)" step="0.01" required ${isBlocked ? 'disabled' : ''} style="flex: 1; min-width: 80px;">
                                        
                                        <select name="priority" ${isBlocked ? 'disabled' : ''} style="flex: 1; min-width: 130px; padding: 8px; border: 1px solid #ccc; border-radius: 4px;" title="Niveau d'envie">
                                            <option value="1">⭐ 1 - Très envie</option>
                                            <option value="2">😍 2 - Forte</option>
                                            <option value="3" selected>🙂 3 - Normale</option>
                                            <option value="4">🤔 4 - Basse</option>
                                            <option value="5">🤷 5 - Optionnel</option>
                                        </select>
                                
                                        <input type="text" name="siteUrl" placeholder="Lien du produit" ${isBlocked ? 'disabled' : ''} style="flex: 2; min-width: 140px;">
                                        <input type="text" name="photoUrl" placeholder="Lien de l'image" ${isBlocked ? 'disabled' : ''} style="flex: 2; min-width: 140px;">
                                        <button type="submit" ${isBlocked ? 'disabled' : ''} style="background-color: #26a69a;">Ajouter</button>
                                    </form>
                                    <c:if test="${isBlocked}">
                                        <p class="status-msg">⚠️ Activez la liste pour ajouter des cadeaux.</p>
                                    </c:if>
                                </div>
                                <hr>
                                
                                <c:choose>
                                    <c:when test="${not empty wl.giftsSortedByPriority}">
                                        <ul class="gift-list">
                                        <c:forEach var="gift" items="${wl.giftsSortedByPriority}">
                                            <c:set var="isGiftLocked" value="${gift.readOnly}" />

                                            <li class="gift-item" style="flex-direction: column; align-items: stretch;">
                                                <div style="display: flex; justify-content: space-between; align-items: center; width: 100%;">
                                                    <div class="gift-info">
                                                        <c:if test="${not empty gift.photoUrl}">
                                                            <img src="${gift.photoUrl}" style="width:60px; margin-right:10px;">
                                                        </c:if>
                                                        <strong><c:out value="${gift.name}" /></strong> 
                                                        (<c:out value="${gift.price}" />€)
                                                        
                                                        <span style="font-size:0.8em; color:#e65100; font-weight:bold; margin-left:5px;">
                                                            <c:choose>
                                                                <c:when test="${gift.priority == 1}">⭐ Très envie</c:when>
                                                                <c:when test="${gift.priority == 2}">😍 Forte</c:when>
                                                                <c:when test="${gift.priority == 3}">🙂 Normale</c:when>
                                                                <c:when test="${gift.priority == 4}">🤔 Basse</c:when>
                                                                <c:when test="${gift.priority == 5}">🤷 Optionnel</c:when>
                                                                <c:otherwise>Prio: ${gift.priority}</c:otherwise>
                                                            </c:choose>
                                                        </span>
                                                        
                                                        <c:if test="${not empty gift.siteUrl}">
                                                             <a href="${gift.siteUrl}" target="_blank" style="font-size:0.8em;">🔗 Lien</a>
                                                        </c:if>

                                                        <c:if test="${isGiftLocked}">
                                                            <span class="lock-msg">🔒 Choisi : <c:out value="${gift.collectedAmount}" />€ (Modif. bloquée)</span>
                                                        </c:if>
                                                    </div>
                                                    <div class="gift-actions">
                                                        <button type="button" class="btn-edit" onclick="toggleModifyForm(${gift.id})" 
                                                                ${isBlocked || isGiftLocked ? 'disabled style="opacity:0.5;"' : ''}>Modifier</button>
                                                        
                                                        <form action="${pageContext.request.contextPath}/gift/delete" method="POST" style="display:inline;">
                                                            <input type="hidden" name="giftId" value="${gift.id}">
                                                            <input type="hidden" name="wishlistId" value="${wl.id}">
                                                            <button type="submit" class="btn-delete" onclick="return confirm('Supprimer ce cadeau ?')" 
                                                                    ${isBlocked || isGiftLocked ? 'disabled style="opacity:0.5;"' : ''}>Supprimer</button>
                                                        </form>
                                                    </div>
                                                </div>

                                                <c:if test="${not isGiftLocked && not isBlocked}">
                                                    <div id="modify-form-${gift.id}" class="add-gift-box" style="display:none; margin-top: 10px; background-color: #fff9c4; border: 1px solid #fbc02d;">
                                                        <form action="${pageContext.request.contextPath}/gift/update" method="POST" class="gift-form" style="display:flex; flex-direction: column; gap: 8px;">
                                                            <input type="hidden" name="giftId" value="${gift.id}">
                                                            <input type="hidden" name="wishlistId" value="${wl.id}">
                                                            
                                                            <div style="display: flex; gap: 10px; flex-wrap: wrap;">
                                                                <div style="flex: 3;">
                                                                    <label style="font-size:0.8em;">Nom :</label>
                                                                    <input type="text" name="name" value="<c:out value='${gift.name}'/>" required placeholder="Nom" style="width:100%;">
                                                                </div>
                                                                <div style="flex: 1;">
                                                                    <label style="font-size:0.8em;">Prix (€) :</label>
                                                                    <input type="number" name="price" value="<c:out value='${gift.price}'/>" step="0.01" required style="width:100%;">
                                                                </div>
                                                                <div style="flex: 1;">
                                                                    <label style="font-size:0.8em;">Priorité :</label>
                                                                    <input type="number" name="priority" value="${gift.priority != null ? gift.priority : 3}" min="1" max="5" required style="width: 100%;">
                                                                </div>
                                                            </div>
                                                            <div style="display: flex; gap: 10px; flex-wrap: wrap;">
                                                                <div style="flex: 1;">
                                                                    <label style="font-size:0.8em;">Lien du produit :</label>
                                                                    <input type="text" name="siteUrl" value="<c:out value='${gift.siteUrl}'/>" placeholder="https://..." style="width: 100%;">
                                                                </div>
                                                                <div style="flex: 1;">
                                                                    <label style="font-size:0.8em;">Lien de l'image :</label>
                                                                    <input type="text" name="photoUrl" value="<c:out value='${gift.photoUrl}'/>" placeholder="https://..." style="width: 100%;">
                                                                </div>
                                                            </div>
                                                            <div style="margin-top: 5px; display: flex; justify-content: flex-end; gap: 10px;">
                                                                <button type="button" onclick="toggleModifyForm(${gift.id})" style="background-color: #e0e0e0; color: black;">Annuler</button>
                                                                <button type="submit" style="background-color: #fbc02d; color: black;">💾 Enregistrer</button>
                                                            </div>
                                                        </form>
                                                    </div>
                                                </c:if>
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
                <c:otherwise><p class="info-card">Vous n'avez pas encore créé de liste.</p></c:otherwise>
            </c:choose>
        </section>

        <section class="shared-section">
            <h2>Listes partagées avec moi</h2>
            <c:choose>
                <c:when test="${not empty user.sharedWishlists}">
                    <ul class="wishlist-list shared">
                        <c:forEach var="wl" items="${user.sharedWishlists}">
                            <c:if test="${wl.status == 'ACTIVE'}">
                                <li class="wishlist-item shared-item">
                                    <div style="display: flex; justify-content: space-between; align-items: center;">
                                        <h3>🌟 <c:out value="${wl.title}" /></h3>
                                        <span class="badge" style="background-color: #ff9800;">Invité</span>
                                    </div>
                                    
                                    <div class="wishlist-actions">
                                        <button type="button" class="btn-view-gifts" onclick="toggleSharedGifts(${wl.id})">
                                            🎁 Voir les cadeaux & Contribuer
                                        </button>
                                    </div>

                                    <div id="shared-gifts-container-${wl.id}" class="shared-gifts-display" style="display:none;">
                                        
                                        <c:choose>
                                            <c:when test="${not empty wl.giftsSortedByPriority}">
                                                <div class="gift-list">
                                                    <c:forEach var="gift" items="${wl.giftsSortedByPriority}">
                                                        
                                                        <c:set var="percentage" value="${(gift.collectedAmount / gift.price) * 100}" />

                                                        <div class="gift-item-shared">
                                                            <div class="gift-details-row">
                                                                <div class="gift-content">
                                                                    <div style="display:flex; gap:15px; align-items:center;">
                                                                        <c:if test="${not empty gift.photoUrl}">
                                                                            <img src="${gift.photoUrl}" alt="${gift.name}" style="width:60px; height:60px; object-fit: cover; border-radius: 4px;">
                                                                        </c:if>
                                                                        <div>
                                                                            <h4 style="margin:0;"><c:out value="${gift.name}" /></h4>
                                                                            <small style="color:#666;"><c:out value="${gift.description}" /></small>
                                                                            
                                                                            <span style="font-size:0.75em; color:#e65100; font-weight:bold; display:block; margin-top:2px;">
                                                                                <c:choose>
                                                                                    <c:when test="${gift.priority == 1}">⭐ Très envie</c:when>
                                                                                    <c:when test="${gift.priority == 2}">😍 Forte</c:when>
                                                                                    <c:when test="${gift.priority == 3}">🙂 Normale</c:when>
                                                                                    <c:otherwise>Prio: ${gift.priority}</c:otherwise>
                                                                                </c:choose>
                                                                            </span>

                                                                            <p style="margin: 2px 0; font-weight:bold;">
                                                                                <fmt:formatNumber value="${gift.price}" type="currency" currencySymbol="€"/>
                                                                            </p>
                                                                        </div>
                                                                    </div>

                                                                    <div class="progress-container">
                                                                        <div class="progress-bar" style="width: ${percentage > 100 ? 100 : percentage}%;"></div>
                                                                    </div>
                                                                    <div class="amount-info">
                                                                        <span>
                                                                            Récolté : <fmt:formatNumber value="${gift.collectedAmount}" type="number" minFractionDigits="2" maxFractionDigits="2"/> €
                                                                        </span>
                                                                        <c:choose>
                                                                            <c:when test="${gift.remainingAmount > 0.01}">
                                                                                <span class="missing-amount">
                                                                                    Reste : <fmt:formatNumber value="${gift.remainingAmount}" type="number" minFractionDigits="2" maxFractionDigits="2"/> €
                                                                                </span>
                                                                            </c:when>
                                                                            <c:otherwise>
                                                                                <span class="fully-funded">✨ Financé !</span>
                                                                            </c:otherwise>
                                                                        </c:choose>
                                                                    </div>
                                                                </div>

                                                                <div class="gift-actions-col">
                                                                    <c:choose>
                                                                        <c:when test="${gift.remainingAmount > 0.01}">
                                                                            <div style="display:flex;">
                                                                                <button class="btn-contribute" onclick="toggleContributionForm(${gift.id})">
                                                                                    💰 Participer
                                                                                </button>
                                                                                <c:if test="${gift.collectedAmount <= 0}">
                                                                                    <form action="${pageContext.request.contextPath}/contribution/add" method="POST" style="display:inline;">
                                                                                        <input type="hidden" name="giftId" value="${gift.id}">
                                                                                        <input type="hidden" name="wishlistId" value="${wl.id}">
                                                                                        <input type="hidden" name="amount" value="${gift.price}">
                                                                                        <input type="hidden" name="comment" value="Cadeau réservé entièrement !">
                                                                                        <button type="submit" class="btn-reserve" onclick="return confirm('Voulez-vous réserver ce cadeau en payant la totalité (${gift.price}€) ?')">
                                                                                            🎁 Réserver
                                                                                        </button>
                                                                                    </form>
                                                                                </c:if>
                                                                            </div>
                                                                            
                                                                            <div id="form-contribution-${gift.id}" class="contribution-box">
                                                                                <form action="${pageContext.request.contextPath}/contribution/add" method="POST">
                                                                                    <input type="hidden" name="giftId" value="${gift.id}">
                                                                                    <input type="hidden" name="wishlistId" value="${wl.id}">
                                                                                    
                                                                                    <label style="display:block; margin-bottom:3px; font-size:0.9em;">Montant (€) :</label>
                                                                                    <input type="number" name="amount" class="input-amount" step="0.01" min="1" max="${gift.remainingAmount}" placeholder="Max ${gift.remainingAmount}" required>

                                                                                    <label style="display:block; margin-top: 5px; margin-bottom:3px; font-size:0.9em;">Petit mot :</label>
                                                                                    <input type="text" name="comment" placeholder="Message..." style="width: 100%; box-sizing: border-box; padding: 5px;">

                                                                                    <button type="submit" class="btn-submit-contribution" style="width:100%; margin-top:5px;">Valider</button>
                                                                                </form>
                                                                            </div>
                                                                        </c:when>
                                                                        <c:otherwise>
                                                                            <button class="btn-completed" disabled>✔️ Complet</button>
                                                                        </c:otherwise>
                                                                    </c:choose>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </c:forEach>
                                                </div>
                                            </c:when>
                                            <c:otherwise>
                                                <p class="empty-msg">Aucun cadeau dans cette liste.</p>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                </li>
                            </c:if>
                        </c:forEach>
                    </ul>
                </c:when>
                <c:otherwise><p class="info-card">Aucune liste partagée avec vous.</p></c:otherwise>
            </c:choose>
        </section>

    </div>
    
    <script>
        function toggleModifyForm(id) {
            var form = document.getElementById('modify-form-' + id);
            form.style.display = (form.style.display === 'none' || form.style.display === '') ? 'block' : 'none';
        }

        function toggleSharedGifts(id) {
            var div = document.getElementById('shared-gifts-container-' + id);
            div.style.display = (div.style.display === 'none' || div.style.display === '') ? 'block' : 'none';
        }

        function toggleContributionForm(id) {
            var form = document.getElementById('form-contribution-' + id);
            form.style.display = (form.style.display === 'none' || form.style.display === '') ? 'block' : 'none';
        }

        function copyInviteLink(url) {
            navigator.clipboard.writeText(url).then(function() {
                alert("Lien copié dans le presse-papiers !\n" + url);
            }).catch(function(err) {
                console.error('Erreur lors de la copie : ', err);
                const textArea = document.createElement("textarea");
                textArea.value = url;
                document.body.appendChild(textArea);
                textArea.select();
                document.execCommand("copy");
                document.body.removeChild(textArea);
                alert("Lien copié !");
            });
        }
    </script>
</body>
</html>