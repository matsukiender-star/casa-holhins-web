<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="mx.holhins.util.CsrfUtil" %>
<%--
    Cabecera comun a todas las pantallas: el <head>, la barra de navegacion y la
    apertura del container. Cada vista la incluye al inicio y cierra con
    footer.jsp, asi el layout vive en un solo lugar (viene siendo el
    base.html del que heredan las plantillas en Django o Jinja).
--%>
<%
    // Nos aseguramos de que la sesion tenga token CSRF antes de pintar nada,
    // porque todos los formularios de abajo lo van a necesitar como campo oculto.
    CsrfUtil.obtenerToken(session);
%>
<!DOCTYPE html>
<html lang="es-MX">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Casa Holhins Web System</title>

    <%-- Version oscura del logo: en la pestana del navegador el fondo suele ser claro. --%>
    <link rel="icon" type="image/svg+xml" href="${pageContext.request.contextPath}/favicon.svg">

    <!-- Bootstrap 5 CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <!-- FontAwesome para iconos -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <!-- CSS Propio -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/holhins.css">
</head>
<body>
<nav class="navbar navbar-expand-lg navbar-dark mb-4">
    <div class="container">
        <%-- El logo va en su version blanca porque el navbar es verde salvia oscuro. --%>
        <a class="navbar-brand d-flex align-items-center gap-2" href="${pageContext.request.contextPath}/dashboard">
            <img src="${pageContext.request.contextPath}/img/logo-holhins.svg"
                 alt="Logo de Casa Holhins" class="navbar-logo">
            <span class="d-flex flex-column lh-sm">
                <strong>Casa Holhins</strong>
                <small class="navbar-tagline">Bienestar integral y armonía espiritual</small>
            </span>
        </a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
            <span class="navbar-toggler-icon"></span>
        </button>

        <%-- El menu solo aparece con sesion iniciada; en el login no hay nada que navegar. --%>
        <% if (session.getAttribute("usuario") != null) { %>
        <div class="collapse navbar-collapse" id="navbarNav">
            <ul class="navbar-nav me-auto">
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/dashboard"><i class="fas fa-chart-line"></i> Dashboard</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/clientes"><i class="fas fa-users"></i> Clientes</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/servicios"><i class="fas fa-spa"></i> Servicios</a>
                </li>
            </ul>
            <ul class="navbar-nav ms-auto">
                <li class="nav-item">
                    <span class="navbar-text me-3">
                        <i class="fas fa-user-circle"></i> ${sessionScope.usuario.nombreCompleto} (${sessionScope.usuario.rol})
                    </span>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="${pageContext.request.contextPath}/logout"><i class="fas fa-sign-out-alt"></i> Salir</a>
                </li>
            </ul>
        </div>
        <% } %>
    </div>
</nav>
<div class="container">
