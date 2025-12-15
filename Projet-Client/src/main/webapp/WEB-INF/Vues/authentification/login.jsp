<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Authentification</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/main.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/forms.css">
</head>
<body>
    
    <div class="auth-container">
        <h1>Connexion</h1>
        
        <%-- Afficher un message d'erreur s'il existe --%>
        <% 
            String errorMessage = (String) request.getAttribute("errorMessage");
            if (errorMessage != null) {
        %>
            <p style="color: red;"><%= errorMessage %></p>
        <%
            }
        %>
        
        <form action="<%= request.getContextPath() %>/auth" method="POST">
            <div>
                <label for="email">Email :</label>
                <input type="email" id="email" name="email" required 
                       value="<%= (request.getAttribute("email") != null) ? request.getAttribute("email") : "" %>">
            </div>
            <br>
    		<div>
    		    <label for="password">Mot de passe :</label>
    		    <input type="password" id="password" name="psw" required> 
    	    </div>
            <br>
            <button type="submit">Se connecter</button>
        </form>
        
        <p>Pas encore de compte ? <a href="#">S'inscrire</a></p>
    </div>
</body>
</html>