<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Détail de la liste - <c:out value="${selectedWishlist.title}" /></title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/main.css">
    <style>
        .contribution-box {
            display: none;
            margin-top: 10px;
            padding: 10px;
            background: #f9f9f9;
            border-radius: 5px;
            border: 1px solid #ddd;
        }
        .gift-item {
            display: flex;
            align-items: center;
            gap: 20px;
            padding: 15px;
            border-bottom: 1px solid #eee;
            background: white;
            margin-bottom: 10px;
            border-radius: 8px;
        }
        .gift-details { flex-grow: 1; }
        .input-amount {
            padding: 5px;
            width: 80px;
            border-radius: 4px;
            border: 1px solid #ccc;
        }
        .btn-submit-contribution {
            background-color: #4caf50;
            color: white;
            border: none;
            padding: 5px 10px;
            border-radius: 4px;
            cursor: pointer;
        }
    </style>
</head>
<body class="home-body">
    <div class="container">
        <header style="margin-bottom: 20px;">
            <a href="${pageContext.request.contextPath}/home" class="btn-nav">← Retour aux listes</a>
            <h1 style="margin-top: 15px;"><c:out value="${selectedWishlist.title}" /></h1>
            <p class="badge" style="display:inline-block;">📅 Expire le : ${selectedWishlist.expirationDate}</p>
            <p><strong>Occasion :</strong> <c:out value="${selectedWishlist.occasion}" /></p>
        </header>

        <section class="gift-list-container">
            <c:choose>
                <c:when test="${not empty selectedWishlist.gifts}">
                    <c:forEach var="gift" items="${selectedWishlist.gifts}">
                        <div class="gift-item">
                            <c:if test="${not empty gift.photoUrl}">
                                <img src="${gift.photoUrl}" alt="${gift.name}" style="width:100px; height:100px; object-fit: cover; border-radius: 5px;">
                            </c:if>
                            
                            <div class="gift-details">
                                <h3 style="margin:0;"><c:out value="${gift.name}" /></h3>
                                <p style="color: #2c3e50; font-weight: bold; margin: 5px 0;">Prix : ${gift.price}€</p>
                                <p style="font-size: 0.9em; color: #666;"><c:out value="${gift.description}" /></p>
                            </div>

                            <div class="gift-action-area">
                                <button class="btn-contribute" onclick="toggleContributionForm(${gift.id})">
                                    💰 Participer
                                </button>
                                
                                <div id="form-contribution-${gift.id}" class="contribution-box">
                                    <form action="${pageContext.request.contextPath}/gift/contribute" method="POST">
                                        <input type="hidden" name="giftId" value="${gift.id}">
                                        <input type="hidden" name="wishlistId" value="${selectedWishlist.id}">
                                        
                                        <label style="display:block; font-size: 0.8em; margin-bottom: 5px;">Montant (€) :</label>
                                        <input type="number" name="amount" class="input-amount" step="0.01" min="0.01" max="${gift.price}" required>
                                        <button type="submit" class="btn-submit-contribution">OK</button>
                                    </form>
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p class="info-card">Aucun cadeau n'a été ajouté à cette liste pour le moment.</p>
                </c:otherwise>
            </c:choose>
        </section>
    </div>

    <script>
        /**
         * Affiche ou masque le champ de saisie du montant pour un cadeau spécifique
         */
        function toggleContributionForm(giftId) {
            const formDiv = document.getElementById('form-contribution-' + giftId);
            if (formDiv.style.display === 'block') {
                formDiv.style.display = 'none';
            } else {
                // Optionnel : fermer les autres formulaires ouverts
                document.querySelectorAll('.contribution-box').forEach(el => el.style.display = 'none');
                
                formDiv.style.display = 'block';
            }
        }
    </script>
</body>
</html>