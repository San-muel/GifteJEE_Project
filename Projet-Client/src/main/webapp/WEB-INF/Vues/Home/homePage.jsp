<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Accueil - GiftManager</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/forms.css">
    <style>
        /* Correction spécifique pour la page d'accueil car le main.css centre le body */
        .home-body {
            display: block !important; 
            background-color: var(--color-background, #f4f7f6);
            margin: 0;
            font-family: 'Segoe UI', sans-serif;
        }

        .header {
            background: white;
            padding: 20px 50px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        
        .header h1 { border: none; margin: 0; font-size: 1.6rem; color: #2c3e50; }
        
        .grid {
            display: grid;
            grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
            gap: 25px;
            padding: 40px;
            max-width: 1200px;
            margin: 0 auto;
        }

        .card {
            background-color: white;
            border-top: 5px solid var(--color-primary, #1abc9c);
            padding: 25px;
            border-radius: 10px;
            text-align: center;
            cursor: pointer;
            transition: transform 0.3s, box-shadow 0.3s;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
            position: relative;
        }

        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 15px rgba(64, 181, 173, 0.2);
        }

        /* Style pour MES listes */
        .card-mine {
            border-top: 5px solid #f39c12 !important; /* Orange */
            background-color: #fffaf0;
        }

        .badge-mine {
            position: absolute;
            top: 10px;
            right: 10px;
            background: #f39c12;
            color: white;
            padding: 3px 8px;
            border-radius: 4px;
            font-size: 0.7rem;
            font-weight: bold;
        }

        .card-disabled {
            background-color: #f0f0f0 !important;
            border-top: 5px solid #bdc3c7 !important;
            color: #95a5a6;
            cursor: not-allowed !important;
            filter: grayscale(100%);
            opacity: 0.7;
            transform: none !important;
            box-shadow: none !important;
        }
        
        .status-badge {
            font-size: 0.8em;
            padding: 2px 8px;
            border-radius: 4px;
            font-weight: bold;
            display: inline-block;
            margin-bottom: 10px;
            text-transform: uppercase;
        }
        
        .badge-expired { background: #e74c3c; color: white; }
        .badge-inactive { background: #95a5a6; color: white; }

        .btn-dashboard {
            display: inline-block;
            padding: 10px 15px;
            background-color: var(--color-secondary, #34495e);
            color: white;
            border-radius: 6px;
            text-decoration: none;
            font-weight: bold;
        }

        .btn-login {
            background-color: var(--color-primary, #1abc9c);
            color: white;
            padding: 10px 20px;
            border-radius: 6px;
            text-decoration: none;
            font-weight: bold;
        }

        .owner-warning {
            color: #d35400;
            font-size: 0.8rem;
            margin-top: 10px;
            display: block;
            font-style: italic;
        }
    </style>
</head>
<body class="home-body">

    <div class="header">
        <div class="header-left">
            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <h1>Bonjour, <c:out value="${sessionScope.user.username}" /> ! 👋</h1>
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn-dashboard">Accéder à mon espace</a>
                </c:when>
                <c:otherwise>
                    <h1>Bienvenue sur GiftManager 🎁</h1>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="header-right">
            <c:if test="${empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/auth" class="btn-login">Se connecter</a>
            </c:if>
            <c:if test="${not empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/logout" style="color: #e74c3c; text-decoration: none;">Déconnexion</a>
            </c:if>
        </div>
    </div>
    
    <main class="grid">
        <c:forEach var="list" items="${wishlists}">
            <%-- 1. Calcul de l'appartenance --%>
            <c:set var="isMine" value="false" />
            <c:if test="${not empty sessionScope.user}">
                <c:forEach var="myList" items="${sessionScope.user.createdWishlists}">
                    <c:if test="${myList.id == list.id}">
                        <c:set var="isMine" value="true" />
                    </c:if>
                </c:forEach>
            </c:if>

            <%-- 2. Calcul du statut d'affichage --%>
            <c:set var="isDisabled" value="${list.status != 'ACTIVE'}" />
            
            <div class="card ${isDisabled ? 'card-disabled' : ''} ${isMine ? 'card-mine' : ''}" 
                 onclick="checkAccess(${list.id}, '${list.status}', ${isMine})">
                
                <c:choose>
                    <c:when test="${isDisabled}">
                        <span class="status-badge ${list.status == 'EXPIRED' ? 'badge-expired' : 'badge-inactive'}">
                            <c:out value="${list.status}" />
                        </span>
                    </c:when>
                    <c:when test="${isMine}">
                        <span class="badge-mine">MA LISTE</span>
                    </c:when>
                </c:choose>
                
                <h3><c:out value="${list.title}" /></h3>
                <p>Occasion: <strong><c:out value="${list.occasion}" /></strong></p>
                <small>Expire le: <c:out value="${list.expirationDate}" /></small>
                
                <c:if test="${isMine && !isDisabled}">
                    <span class="owner-warning">🚫 Consultation uniquement (Propriétaire)</span>
                </c:if>
            </div>
        </c:forEach>
    </main>

    <script>
        function checkAccess(id, status, isMine) {
            // 1. Bloquer si la liste appartient à l'utilisateur
            if (isMine) {
                if (confirm("C'est votre liste ! Souhaitez-vous aller dans votre espace pour la gérer ?")) {
                    window.location.href = "${pageContext.request.contextPath}/dashboard";
                }
                return;
            }

            // 2. Bloquer si le statut n'est pas ACTIVE
            if (status !== 'ACTIVE') {
                alert("Cette liste est actuellement " + status.toLowerCase() + " et n'est pas consultable.");
                return;
            }
            
            // 3. Vérifier la connexion
            const isConnected = ${not empty sessionScope.user};
            
            if (isConnected) {
                // Utilisation de l'URL que vous avez spécifiée
                window.location.href = "${pageContext.request.contextPath}/wishlistDetail?id=" + id;
            } else {
                alert("Veuillez vous connecter pour voir les cadeaux de cette liste.");
                window.location.href = "${pageContext.request.contextPath}/auth";
            }
        }
    </script>

</body>
</html>