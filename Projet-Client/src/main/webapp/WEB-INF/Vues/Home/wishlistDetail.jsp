<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Détail de la liste</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/main.css">
</head>
<body class="home-body">
    <div class="container">
        <a href="${pageContext.request.contextPath}/home">← Retour aux listes</a>
        
        <header>
            <h1><c:out value="${selectedWishlist.title}" /></h1>
            <p>Occasion : ${selectedWishlist.occasion}</p>
        </header>

        <section class="gift-list">
            <c:forEach var="gift" items="${selectedWishlist.gifts}">
                <div class="gift-item">
                    <img src="${gift.photoUrl}" style="width:100px;">
                    <div>
                        <strong>${gift.name}</strong> - ${gift.price}€
                        <p>${gift.description}</p>
                    </div>
                    <button class="btn-contribute">Contribuer</button>
                </div>
            </c:forEach>
        </section>
    </div>
</body>
</html>