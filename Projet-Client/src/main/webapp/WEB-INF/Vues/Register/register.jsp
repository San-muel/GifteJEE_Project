<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Création de compte</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/forms.css">
</head>
<body>
    <div class="container">
        <h2>Créer un compte</h2>
        
        <%-- Affichage de l'erreur si le Servlet renvoie success = false --%>
        <c:if test="${not empty error}">
            <p class="error-msg" style="color: #721c24; background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 10px; border-radius: 5px; margin-bottom: 15px;">
                <c:out value="${error}" />
            </p>
        </c:if>

        <form action="${pageContext.request.contextPath}/register" method="POST" class="auth-form">
            <div class="form-group">
                <label for="username">Nom d'utilisateur :</label>
                <input type="text" id="username" name="username" required placeholder="ex: Aziz">
            </div>
            
            <div class="form-group">
                <label for="email">Email :</label>
                <input type="email" id="email" name="email" required placeholder="ex: aziz@email.com">
            </div>
            
            <div class="form-group">
                <label for="password">Mot de passe :</label>
                <input type="password" id="password" name="password" required placeholder="••••••••">
            </div>

            <button type="submit" class="btn-register">Créer mon compte</button>
        </form>
        
        <div class="footer-links" style="margin-top: 20px; text-align: center;">
            <p>Déjà un compte ? <a href="${pageContext.request.contextPath}/auth">Connectez-vous ici</a></p>
        </div>
    </div>
</body>
</html>