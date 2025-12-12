<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page import="be.project.MODEL.Contribution" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Détail de la Contribution</title>
</head>
<body>

    <c:set var="contribution" value="${requestScope.contribution}" />

    <h1>Détail de la Contribution #${contribution.id}</h1>
    
    <c:if test="${empty contribution}">
        <p>Erreur : Aucune contribution à afficher.</p>
    </c:if>

    <c:if test="${not empty contribution}">
        <ul>
            <li><strong>ID :</strong> ${contribution.id}</li>
            <li>
                <strong>Montant :</strong> 
                <fmt:formatNumber value="${contribution.amount}" type="currency" currencySymbol="€" maxFractionDigits="2"/>
            </li>
            <li>
                <strong>Date de la contribution :</strong> 
                <fmt:formatDate value="${contribution.contributedAt}" pattern="dd/MM/yyyy HH:mm:ss" />
            </li>
            <li><strong>Commentaire :</strong> ${contribution.comment}</li>
            <li>
                <strong>Cadeau lié (ID) :</strong> 
                <c:choose>
                    <c:when test="${not empty contribution.gift}">
                        ${contribution.gift.id}
                    </c:when>
                    <c:otherwise>
                        Non spécifié
                    </c:otherwise>
                </c:choose>
            </li>
        </ul>
    </c:if>

    <p><a href="/">Retour à l'accueil</a></p>

</body>
</html>