<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/includes/header.jsp" />

<div class="row mb-4">
    <div class="col-12">
        <h1 class="display-5">Bienvenid@, ${sessionScope.usuario.nombreCompleto}</h1>
        <p class="lead text-muted">Panel de control principal - Casa Holhins CRM</p>
    </div>
</div>

<div class="row mb-5">
    <!-- Metrica Clientes -->
    <div class="col-md-6 mb-3">
        <div class="card bg-secondary text-white shadow">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="text-uppercase mb-1">Total Clientes Activos</h6>
                        <h2 class="mb-0 font-weight-bold">${totalClientes}</h2>
                    </div>
                    <i class="fas fa-users fa-3x text-white-50"></i>
                </div>
            </div>
            <div class="card-footer bg-transparent border-top-0">
                <a href="${pageContext.request.contextPath}/clientes" class="text-white text-decoration-none">
                    Ver todos los clientes <i class="fas fa-arrow-right"></i>
                </a>
            </div>
        </div>
    </div>
    
    <!-- Metrica Servicios -->
    <div class="col-md-6 mb-3">
        <div class="card bg-secondary text-white shadow">
            <div class="card-body">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <h6 class="text-uppercase mb-1">Servicios Activos</h6>
                        <h2 class="mb-0 font-weight-bold">${totalServicios}</h2>
                    </div>
                    <i class="fas fa-spa fa-3x text-white-50"></i>
                </div>
            </div>
            <div class="card-footer bg-transparent border-top-0">
                <a href="${pageContext.request.contextPath}/servicios" class="text-white text-decoration-none">
                    Gestionar servicios <i class="fas fa-arrow-right"></i>
                </a>
            </div>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-lg-12">
        <div class="card shadow-sm">
            <div class="card-header d-flex justify-content-between align-items-center">
                <h5 class="mb-0"><i class="fas fa-clock"></i> Últimos clientes registrados</h5>
                <a href="${pageContext.request.contextPath}/clientes?nuevo" class="btn btn-sm btn-primary">Nuevo Cliente</a>
            </div>
            <div class="card-body p-0">
                <div class="table-responsive">
                    <table class="table table-hover mb-0">
                        <thead>
                            <tr>
                                <th>Nombre</th>
                                <th>Teléfono</th>
                                <th>Estatus</th>
                                <th>Fecha Registro</th>
                                <th>Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="cli" items="${ultimosClientes}">
                                <tr>
                                    <td>${cli.nombreCompleto}</td>
                                    <td>${cli.telefono}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${cli.estatus == 'NUEVO'}"><span class="badge bg-info text-dark">Nuevo</span></c:when>
                                            <c:when test="${cli.estatus == 'LAPSADO'}"><span class="badge bg-warning text-dark">Lapsado</span></c:when>
                                            <c:otherwise><span class="badge bg-success">Activo</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td><fmt:formatDate value="${cli.fechaRegistro}" pattern="dd/MM/yyyy HH:mm" /></td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/clientes?id=${cli.id}" class="btn btn-sm btn-outline-secondary">Ver</a>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty ultimosClientes}">
                                <tr>
                                    <td colspan="5" class="text-center text-muted py-3">No hay clientes recientes.</td>
                                </tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />
