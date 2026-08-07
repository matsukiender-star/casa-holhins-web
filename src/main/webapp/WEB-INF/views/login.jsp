<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/includes/header.jsp" />

<%--
    Pantalla de acceso. Es la unica vista publica del sistema: AuthFilter deja
    pasar /login y los estaticos, todo lo demas exige sesion.

    Aqui el logo va en su version oscura porque el fondo es crema; la version
    blanca del navbar seria invisible sobre este fondo.
--%>
<div class="row justify-content-center mt-5">
    <div class="col-md-5">
        <div class="card shadow-sm">
            <div class="card-header text-center py-4">
                <img src="${pageContext.request.contextPath}/img/logo-holhins-dark.svg"
                     alt="Logo de Casa Holhins" class="login-logo mb-3">
                <h4 class="mb-1">Iniciar Sesión</h4>
                <small class="text-muted d-block">Acceso exclusivo al sistema CRM</small>
            </div>
            <div class="card-body p-4">
                <c:if test="${not empty error}">
                    <div class="alert alert-danger" role="alert">
                        <i class="fas fa-exclamation-triangle"></i> ${error}
                    </div>
                </c:if>
                <form action="${pageContext.request.contextPath}/login" method="post">
                    <%-- Token CSRF: sin el, LoginServlet rechaza el POST con un 403. --%>
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <div class="mb-3">
                        <label for="username" class="form-label">Usuario</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-user"></i></span>
                            <input type="text" class="form-control" id="username" name="username" required autofocus>
                        </div>
                    </div>
                    <div class="mb-4">
                        <label for="password" class="form-label">Contraseña</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="fas fa-lock"></i></span>
                            <input type="password" class="form-control" id="password" name="password" required>
                        </div>
                    </div>
                    <div class="d-grid gap-2">
                        <button type="submit" class="btn btn-primary btn-lg">Entrar</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />
