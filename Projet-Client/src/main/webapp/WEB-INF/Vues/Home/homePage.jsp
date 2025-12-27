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
            background-color: var(--color-background);
        }

        /* Styles de la grille (à mettre idéalement dans main.css plus tard) */
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
            border-top: 5px solid var(--color-primary);
            padding: 20px;
            border-radius: 10px;
            text-align: center;
            cursor: pointer;
            transition: transform 0.3s, box-shadow 0.3s;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);
        }

        .card:hover {
            transform: translateY(-5px);
            box-shadow: 0 8px 15px rgba(64, 181, 173, 0.2);
        }

        .btn-dashboard {
            display: inline-block;
            padding: 10px 15px;
            background-color: var(--color-secondary);
            color: var(--color-text);
            border-radius: 6px;
            font-weight: bold;
            margin-top: 10px;
        }

        .btn-login {
            background-color: var(--color-primary);
            color: white;
            padding: 10px 20px;
            border-radius: 6px;
            font-weight: bold;
        }
        
        .header {
            background: white;
            padding: 20px 50px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
        }
        
        .header h1 { border: none; margin: 0; }
        
        /* Style pour les cartes désactivées (INACTIVE ou EXPIRED) */
		.card-disabled {
		    background-color: #f0f0f0 !important;
		    border-top: 5px solid #bdc3c7 !important; /* Gris au lieu du turquoise */
		    color: #95a5a6;
		    cursor: not-allowed !important; /* Curseur "interdit" */
		    filter: grayscale(100%);
		    opacity: 0.7;
		    transform: none !important; /* Pas d'effet de levée au survol */
		    box-shadow: none !important;
		}
		
		.status-badge {
		    font-size: 0.8em;
		    padding: 2px 8px;
		    border-radius: 4px;
		    font-weight: bold;
		    display: inline-block;
		    margin-bottom: 10px;
		}
		
		.badge-expired { background: #e74c3c; color: white; }
		.badge-inactive { background: #95a5a6; color: white; }
    </style>
</head>
<body class="home-body">

    <div class="header">
        <div class="header-left">
            <c:choose>
                <c:when test="${not empty sessionScope.user}">
                    <h1>Bonjour, <c:out value="${sessionScope.user.username}" /> !</h1>
                    <a href="${pageContext.request.contextPath}/dashboard" class="btn-dashboard">Accéder à mon espace</a>
                </c:when>
                <c:otherwise>
                    <h1>Bienvenue sur GiftManager</h1>
                </c:otherwise>
            </c:choose>
        </div>
        <div class="header-right">
            <c:if test="${empty sessionScope.user}">
                <a href="${pageContext.request.contextPath}/auth" class="btn-login">Se connecter</a>
            </c:if>
        </div>
    </div>
	
	<main class="grid">
	    <c:forEach var="list" items="${wishlists}">
	        <%-- On prépare une classe CSS si le statut n'est pas ACTIVE --%>
	        <c:set var="isDisabled" value="${list.status != 'ACTIVE'}" />
	        
	        <div class="card ${isDisabled ? 'card-disabled' : ''}" 
	             onclick="checkAccess(${list.id}, '${list.status}')">
	            
	            <c:if test="${isDisabled}">
	                <span class="status-badge ${list.status == 'EXPIRED' ? 'badge-expired' : 'badge-inactive'}">
	                    <c:out value="${list.status}" />
	                </span>
	            </c:if>
	            
	            <h3><c:out value="${list.title}" /></h3>
	            <p>Occasion: <c:out value="${list.occasion}" /></p>
	            <small>Expire le: <c:out value="${list.expirationDate}" /></small>
	        </div>
	    </c:forEach>
	</main>

    <script>
	    function checkAccess(id, status) {
	        // 1. Bloquer immédiatement si le statut n'est pas ACTIVE
	        if (status !== 'ACTIVE') {
	            alert("Cette liste est actuellement " + status.toLowerCase() + " et n'est pas consultable.");
	            return; // On arrête l'exécution ici
	        }
			
	        const isConnected = <%= (session.getAttribute("user") != null) %>;
	        
	        if (isConnected) {
	            window.location.href = "${pageContext.request.contextPath}/wishlistDetail?id=" + id;
	        } else {
	            alert("Veuillez vous connecter pour voir les cadeaux de cette liste.");
	            window.location.href = "${pageContext.request.contextPath}/auth";
	        }
	    }
    </script>

</body>
</html>