<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="user" scope="session" type="be.project.MODEL.User" class="be.project.MODEL.User" />

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Page d'Accueil</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/main.css">
</head>
<body>

    <div class="container">
        <h1>Bienvenue, <c:out value="${user.username}" /> !</h1>
        
        <p>Votre email : <c:out value="${user.email}" /></p>
        
        <h2>Vos Wishlists Créées</h2>
    	
        <c:choose>
            <c:when test="${not empty user.createdWishlists}">
                <ul>
        			<c:forEach var="wl" items="${user.createdWishlists}">  
        	            <li>
                            **<c:out value="${wl.title}" />** (Occasion: <c:out value="${wl.occasion}" /> | 
                            Expiration: <c:out value="${wl.expirationDate}" />)
                        </li>
        		    </c:forEach>
        	    </ul>
            </c:when>
            <c:otherwise>
                <p>Vous n'avez pas encore créé de liste de souhaits. <a href="#">Commencez maintenant !</a></p>
            </c:otherwise>
        </c:choose>

        <div class="logout-container">
            <a href="${pageContext.request.contextPath}/logout">Se Déconnecter</a>
        </div>
    </div>
</body>
</html>