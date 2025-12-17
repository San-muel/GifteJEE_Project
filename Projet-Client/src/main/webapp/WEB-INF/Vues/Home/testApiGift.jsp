<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<jsp:useBean id="user" scope="session" type="be.project.MODEL.User" class="be.project.MODEL.User" />

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test API Directe - Création Cadeau (POST)</title>
    <style>
        body { font-family: sans-serif; padding: 20px; }
        .success { color: green; font-weight: bold; }
        .error { color: red; font-weight: bold; }
        .debug { color: blue; }
        .controls, .result { margin-top: 20px; padding: 10px; border: 1px solid #ccc; }
        input { display: block; margin-bottom: 5px; width: 300px; padding: 5px;}
        button { margin-top: 10px; padding: 10px; }
    </style>
</head>
<body>

    <h1>Test API Directe (POST Cadeau)</h1>
    <p class="debug">
        **Utilisateur Connecté :** <c:out value="${user.username}" /> (ID: <c:out value="${user.id}" />)
    </p>

    <div class="controls">
        <h2>Paramètres du Cadeau</h2>
        
        <label for="wishlistId">ID de la Wishlist (Doit appartenir à l'utilisateur!)</label>
        <input type="number" id="wishlistId" value="1" min="1" required>

        <label for="name">Nom du Cadeau</label>
        <input type="text" id="name" value="Test Cadeau Web JS" required>
        
        <label for="price">Prix</label>
        <input type="number" id="price" value="49.99" step="0.01" min="0" required>
        
        <label for="priority">Priorité</label>
        <input type="number" id="priority" value="8" min="1" max="10">
        
        <label for="description">Description</label>
        <input type="text" id="description" value="Cadeau ajouté via test direct Fetch">
        
        <label for="photoUrl">URL Photo</label>
        <input type="text" id="photoUrl" value="http://image.test/photo.jpg">
        
        <button onclick="testCreateGift()">Tester POST</button>
    </div>

    <div class="result">
        <h2>Résultat de l'API</h2>
        <pre id="apiResponse"></pre>
    </div>

<script>
    // Token récupéré directement via EL (Expression Language) JSP – plus fiable et sans risque de balise cassée
    const USER_TOKEN = "${user.token}";

    // URL de l'API en dur – pas de risque d'erreur JSTL
    const API_BASE_URL = "http://localhost:11265/Projet-API/api";

    // Vérification du token
    if (!USER_TOKEN || USER_TOKEN.trim() === '' || USER_TOKEN === 'null') {
        document.getElementById('apiResponse').innerHTML = 
            '<span class="error">ERREUR: Token manquant. Ré-connectez-vous.</span>';
    } else {
        console.log("Token chargé avec succès (début) :", USER_TOKEN.substring(0, 30) + "...");
    }

    async function testCreateGift() {
        const url = API_BASE_URL + "/gifts";

        console.log("URL utilisée pour le POST :", url); // Doit être http://localhost:11265/Projet-API/api/gifts

        const giftData = {
            wishlistId: parseInt(document.getElementById('wishlistId').value),
            name: document.getElementById('name').value.trim(),
            price: parseFloat(document.getElementById('price').value),
            priority: document.getElementById('priority').value ? parseInt(document.getElementById('priority').value) : null,
            description: document.getElementById('description').value.trim(),
            photoUrl: document.getElementById('photoUrl').value.trim()
        };

        document.getElementById('apiResponse').textContent = 'Envoi en cours vers l\'API...';

        try {
            const response = await fetch(url, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + USER_TOKEN
                },
                body: JSON.stringify(giftData)
            });

            const body = await response.text();
            let message = `Status: ${response.status} ${response.statusText}\n\n`;

            if (response.status === 201) {
                message += "SUCCÈS ! Cadeau créé avec ID renvoyé.";
                document.getElementById('apiResponse').innerHTML = `<span class="success">${message}</span><pre>${body}</pre>`;
            } else if (response.status === 400) {
                message += "Erreur 400 : Données invalides ou manquantes.";
                document.getElementById('apiResponse').innerHTML = `<span class="error">${message}</span><pre>${body}</pre>`;
            } else if (response.status === 401) {
                message += "Erreur 401 : Token invalide ou expiré.";
                document.getElementById('apiResponse').innerHTML = `<span class="error">${message}</span><pre>${body}</pre>`;
            } else if (response.status === 403) {
                message += "Erreur 403 : Non autorisé (wishlist ne vous appartient pas ?).";
                document.getElementById('apiResponse').innerHTML = `<span class="error">${message}</span><pre>${body}</pre>`;
            } else {
                message += "Erreur inattendue du serveur.";
                document.getElementById('apiResponse').innerHTML = `<span class="error">${message}</span><pre>${body}</pre>`;
            }

        } catch (err) {
            document.getElementById('apiResponse').innerHTML = `<span class="error">Erreur réseau ou CORS : ${err.message}</span>`;
            console.error("Erreur fetch :", err);
        }
    }
</script>
</body>
</html>