<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %> 

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Détail de la liste - <c:out value="${selectedWishlist.title}" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/main.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/logo.png" />
    <style>
        .contribution-box { display: none; margin-top: 10px; padding: 10px; background: #f9f9f9; border-radius: 5px; border: 1px solid #ddd; }
        .gift-item { display: flex; align-items: center; gap: 20px; padding: 15px; border-bottom: 1px solid #eee; background: white; margin-bottom: 10px; border-radius: 8px; flex-wrap: wrap; }
        .gift-details { flex-grow: 1; min-width: 200px; }
        .input-amount { padding: 5px; width: 80px; border-radius: 4px; border: 1px solid #ccc; }
        
        .btn-contribute { background-color: #4caf50; color: white; border: none; padding: 8px 12px; border-radius: 4px; cursor: pointer; font-weight: bold; }
        .btn-reserve { background-color: #2196F3; color: white; border: none; padding: 8px 12px; border-radius: 4px; cursor: pointer; font-weight: bold; margin-left: 5px; }
        .btn-submit-contribution { background-color: #4caf50; color: white; border: none; padding: 5px 10px; border-radius: 4px; cursor: pointer; }
        .btn-completed { background-color: #ccc; cursor: not-allowed; color: #666; padding: 8px 12px; border:none; border-radius: 4px;}
        
        .progress-container { width: 100%; background-color: #e0e0e0; border-radius: 10px; margin: 10px 0; height: 10px; overflow: hidden; }
        .progress-bar { height: 100%; background-color: #4caf50; transition: width 0.5s ease-in-out; }
        .amount-info { font-size: 0.9em; color: #555; display: flex; justify-content: space-between; }
        .missing-amount { color: #d32f2f; font-weight: bold; }
        .fully-funded { color: #4caf50; font-weight: bold; }

        .gift-action-area { display: flex; flex-direction: column; align-items: flex-end; gap: 5px; }
        .button-group { display: flex; gap: 10px; }
    </style>
</head>
<body class="home-body">
    <div class="container">
        <header style="margin-bottom: 20px;">
            <a href="${pageContext.request.contextPath}/home" class="btn-nav">← Retour aux listes</a>
            <h1 style="margin-top: 15px;"><c:out value="${selectedWishlist.title}" /></h1>
            <p class="badge">📅 Expire le : ${selectedWishlist.expirationDate}</p>
        </header>

        <section class="gift-list-container">
            <c:choose>
                <c:when test="${not empty selectedWishlist.gifts}">
                    <c:forEach var="gift" items="${selectedWishlist.gifts}">
                        
                        <c:set var="percentage" value="${(gift.collectedAmount / gift.price) * 100}" />
                        
                        <div class="gift-item">
                            <c:if test="${not empty gift.photoUrl}">
                                <img src="${gift.photoUrl}" alt="${gift.name}" style="width:100px; height:100px; object-fit: cover; border-radius: 5px;">
                            </c:if>
                            
                            <div class="gift-details">
                                <h3 style="margin:0;"><c:out value="${gift.name}" /></h3>
                                <p style="color: #2c3e50; font-weight: bold; margin: 5px 0;">
                                    Prix : <fmt:formatNumber value="${gift.price}" type="currency" currencySymbol="€"/>
                                </p>
                                <p style="font-size: 0.9em; color: #666;"><c:out value="${gift.description}" /></p>

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

                            <div class="gift-action-area">
                                <c:choose>
                                    <c:when test="${gift.remainingAmount > 0.01}">
                                        <div class="button-group">
                                            <button class="btn-contribute" onclick="toggleContributionForm(${gift.id})">
                                                💰 Participer
                                            </button>

                                            <c:if test="${gift.collectedAmount <= 0}">
                                                <form action="${pageContext.request.contextPath}/contribution/add" method="POST" style="display:inline;">
                                                    <input type="hidden" name="giftId" value="${gift.id}">
                                                    <input type="hidden" name="wishlistId" value="${selectedWishlist.id}">
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
                                                <input type="hidden" name="wishlistId" value="${selectedWishlist.id}">
                                                
                                                <label>Montant (€) :</label>
                                                <input type="number" name="amount" class="input-amount" step="0.01" min="1" max="${gift.remainingAmount}" placeholder="Max ${gift.remainingAmount}" required>

                                                <label style="margin-top: 5px;">Petit mot :</label>
                                                <input type="text" name="comment" placeholder="Message...">

                                                <button type="submit" class="btn-submit-contribution" style="width:100%; margin-top:5px;">Valider</button>
                                            </form>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <button class="btn-completed" disabled>
                                            ✔️ Complet
                                        </button>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p class="info-card">Aucun cadeau dans cette liste.</p>
                </c:otherwise>
            </c:choose>
        </section>
    </div>
    <script>
        function toggleContributionForm(id) {
            const form = document.getElementById('form-contribution-' + id);
            form.style.display = (form.style.display === 'block') ? 'none' : 'block';
        }
    </script>
</body>
</html>