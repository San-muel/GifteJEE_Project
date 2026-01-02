<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %> 
<jsp:useBean id="user" scope="session" type="be.project.MODEL.User" />

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Partager ma Liste - Giveo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/forms.css">
    <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/logo.png" />

    <style>
        /* Conteneur de la grille avec SCROLL */
        .user-grid {
            display: grid;
            /* Crée des colonnes automatiques de min 220px de large */
            grid-template-columns: repeat(auto-fill, minmax(220px, 1fr));
            gap: 15px;
            
            /* Gère la hauteur max et le scroll */
            width: 100%;
            max-height: 450px;       /* Hauteur fixe visible */
            overflow-y: auto;        /* Barre de défilement si ça dépasse */
            
            padding: 15px;
            background-color: #fafafa;
            border: 1px solid #e0e0e0;
            border-radius: 8px;
            box-sizing: border-box;
            margin-bottom: 20px;
        }

        /* Design de la Carte Utilisateur */
        .user-card {
            display: flex;
            align-items: center; /* Centre verticalement */
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 10px;
            cursor: pointer;
            transition: all 0.2s ease;
            position: relative;
        }

        /* Effet au survol */
        .user-card:hover {
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0,0,0,0.1);
            border-color: #40B5AD; /* Couleur Turquoise */
        }

        /* Effet quand SÉLECTIONNÉ */
        .user-card.selected {
            border: 2px solid #40B5AD;
            background-color: #e0f7fa;
        }

        /* Masquer le bouton radio moche */
        .user-card input[type="radio"] {
            position: absolute;
            opacity: 0;
            pointer-events: none;
        }

        /* Avatar rond */
        .user-avatar {
            width: 40px;
            height: 40px;
            min-width: 40px; /* Empêche l'écrasement */
            background-color: #FFD700; /* Jaune/Or */
            color: white;
            border-radius: 50%;
            display: flex;
            align-items: center;
            justify-content: center;
            font-weight: bold;
            font-size: 1.1em;
            margin-right: 12px;
            text-transform: uppercase;
        }

        /* Conteneur Texte (Nom + Email) */
        .user-info {
            display: flex;
            flex-direction: column;
            overflow: hidden; /* Pour gérer les noms longs */
        }

        .user-name {
            font-weight: 600;
            color: #333;
            font-size: 0.95em;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis; /* Met des "..." si trop long */
        }

        .user-email {
            font-size: 0.75em;
            color: #888;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        /* Style de la barre de recherche */
        .search-box {
            width: 100%;
            padding: 12px 15px;
            border: 2px solid #eee;
            border-radius: 25px;
            margin-bottom: 20px;
            font-size: 1em;
            box-sizing: border-box;
        }
        .search-box:focus {
            border-color: #40B5AD;
            outline: none;
        }
    </style>
</head>
<body>

    <div style="position: absolute; top: 20px; left: 20px;">
        <a href="${pageContext.request.contextPath}/home" style="font-weight: bold; text-decoration: none; color: #333;">&larr; Retour</a>
    </div>

    <div class="container">
        
        <div class="share-header">
            <h1>🎁 Partager la liste <span style="color:#40B5AD;">#<c:out value="${wishlistId}" /></span></h1>
            <p style="color: #666;">Choisissez un ami pour lui envoyer vos idées cadeaux.</p>
        </div>

        <form action="${pageContext.request.contextPath}/share" method="POST" onsubmit="return validateSelection()">
            <input type="hidden" name="wishlistId" value="${wishlistId}">
            
            <input type="text" id="searchInput" class="search-box" placeholder="🔍 Rechercher un ami par nom ou email..." onkeyup="filterUsers()">

            <div class="user-grid" id="userList">
                <c:forEach var="u" items="${users}">
                    <c:if test="${u.id != user.id}">
                        <label class="user-card" id="card-${u.id}" onclick="selectUser(${u.id})">
                            <input type="radio" name="targetUserId" value="${u.id}">
                            
                            <div class="user-avatar">
                                <c:out value="${fn:substring(u.username, 0, 1)}" />
                            </div>
                            
                            <div class="user-info">
                                <div class="user-name"><c:out value="${u.username}" /></div>
                                <div class="user-email"><c:out value="${u.email}" /></div>
                            </div>
                        </label>
                    </c:if>
                </c:forEach>
            </div>

            <div style="margin-top: 20px;">
                <label for="msg"><strong>Petit mot doux (optionnel) :</strong></label>
                <textarea id="msg" name="notification" class="message-area" rows="3" 
                    style="width:100%; padding:10px; border-radius:8px; border:1px solid #ddd; margin-top:5px;" 
                    placeholder="Ex: Salut ! Voici ma liste pour Noël, merci d'avance !"></textarea>
            </div>

            <button type="submit" class="btn-submit" style="margin-top: 20px; width:100%; padding:12px; background-color:#40B5AD; color:white; border:none; border-radius:6px; font-weight:bold; cursor:pointer;">Envoyer l'invitation ✨</button>
            
            <p style="text-align: center; margin-top: 15px;">
                <a href="${pageContext.request.contextPath}/home" style="color: #999; font-size: 0.9em;">Annuler</a>
            </p>
        </form>
    </div>

    <script>
        // Fonction pour gérer l'aspect visuel de la sélection
        function selectUser(id) {
            // 1. On retire la classe 'selected' de toutes les cartes
            document.querySelectorAll('.user-card').forEach(card => {
                card.classList.remove('selected');
            });
            
            // 2. On ajoute la classe 'selected' à celle qu'on vient de cliquer
            const selectedCard = document.getElementById('card-' + id);
            if(selectedCard) {
                selectedCard.classList.add('selected');
                // On force le check du radio button
                const radio = selectedCard.querySelector('input[type="radio"]');
                if(radio) radio.checked = true;
            }
        }

        // Fonction de filtrage
        function filterUsers() {
            let filter = document.getElementById('searchInput').value.toLowerCase();
            let cards = document.querySelectorAll('.user-card');
            
            cards.forEach(card => {
                let name = card.querySelector('.user-name').innerText.toLowerCase();
                let email = card.querySelector('.user-email').innerText.toLowerCase();
                
                if (name.includes(filter) || email.includes(filter)) {
                    card.style.display = "flex"; 
                } else {
                    card.style.display = "none";
                }
            });
        }

        function validateSelection() {
            let selected = document.querySelector('input[name="targetUserId"]:checked');
            if (!selected) {
                alert("Oups ! Vous avez oublié de sélectionner un ami.");
                return false;
            }
            return true;
        }
    </script>
</body>
</html>