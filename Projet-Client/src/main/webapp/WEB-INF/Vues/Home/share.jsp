<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="user" scope="session" type="be.project.MODEL.User" />

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Partager ma Liste</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/main.css">
</head>
<body>
    <div class="container">
        <div class="share-container">
            <h1>Partager la liste : <c:out value="${wishlistId}" /></h1>
            
            <input type="text" id="searchInput" class="search-box" placeholder="Rechercher un ami (nom ou email)..." onkeyup="filterUsers()">

            <form action="${pageContext.request.contextPath}/share" method="POST" onsubmit="return validateSelection()">
                <input type="hidden" name="wishlistId" value="${wishlistId}">
                
                <div class="user-grid" id="userList">
                    <c:forEach var="u" items="${users}">
                        <c:if test="${u.id != user.id}">
                            <label class="user-card" id="card-${u.id}">
                                <input type="radio" name="targetUserId" value="${u.id}" onclick="highlightCard(${u.id})">
                                <strong><c:out value="${u.username}" /></strong><br>
                                <span style="font-size: 0.8em; color: #666;"><c:out value="${u.email}" /></span>
                            </label>
                        </c:if>
                    </c:forEach>
                </div>

                <div style="margin-top: 20px;">
                    <label><strong>Petit message pour votre ami :</strong></label>
                    <textarea name="notification" style="width: 100%; height: 60px; margin-top: 8px; padding: 10px; border-radius: 5px; border: 1px solid #ccc;" placeholder="Ex: Voici des idées pour mon anniversaire !"></textarea>
                </div>

                <button type="submit" class="btn-submit">Confirmer le partage</button>
                <p style="text-align: center;"><a href="${pageContext.request.contextPath}/home">Annuler et retourner à l'accueil</a></p>
            </form>
        </div>
    </div>

    <script>
        function highlightCard(id) {
            // Retire la classe 'selected' de toutes les cartes
            document.querySelectorAll('.user-card').forEach(card => card.classList.remove('selected'));
            // Ajoute la classe à la carte cliquée
            document.getElementById('card-' + id).classList.add('selected');
        }

        function filterUsers() {
            let filter = document.getElementById('searchInput').value.toLowerCase();
            let cards = document.querySelectorAll('.user-card');
            cards.forEach(card => {
                let text = card.innerText.toLowerCase();
                card.style.display = text.includes(filter) ? "" : "none";
            });
        }

        function validateSelection() {
            let selected = document.querySelector('input[name="targetUserId"]:checked');
            if (!selected) {
                alert("Veuillez sélectionner un ami avant de partager !");
                return false;
            }
            return true;
        }
    </script>
</body>
</html>