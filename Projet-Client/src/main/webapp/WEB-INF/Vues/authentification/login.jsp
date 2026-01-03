<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Authentification</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/forms.css">
<link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/logo.png" />
</head>
<body>
    
    <div class="auth-container">
        <h1>Connexion</h1>
        
        <c:if test="${not empty errorMessage}">
            <p style="color: red;">${errorMessage}</p>
        </c:if>
        
        <form action="${pageContext.request.contextPath}/auth" method="POST">
            <div>
                <label for="email">Email :</label>
                <input type="email" id="email" name="email" required 
                       value="${email}">
            </div>
            <br>
    		<div>
    		    <label for="password">Mot de passe :</label>
    		    <input type="password" id="password" name="psw" required> 
    	    </div>
            <br>
            <button type="submit">Se connecter</button>
        </form>
        
        <p>Pas encore de compte ? <a href="${pageContext.request.contextPath}/register">S'inscrire</a></p>
    </div>
</body>
</html>