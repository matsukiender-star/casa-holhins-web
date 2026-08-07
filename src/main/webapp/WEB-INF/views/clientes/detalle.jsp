<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/includes/header.jsp" />

<div class="row mb-3">
    <div class="col-12">
        <h2>Detalle de Cliente</h2>
        <a href="${pageContext.request.contextPath}/clientes" class="btn btn-sm btn-outline-secondary"><i class="fas fa-arrow-left"></i> Volver</a>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-header d-flex justify-content-between align-items-center">
        <h5 class="mb-0">${cliente.nombreCompleto}</h5>
        <span class="badge ${cliente.estatus == 'ACTIVO' ? 'bg-success' : 'bg-secondary'}">${cliente.estatus}</span>
    </div>
    <div class="card-body">
        <div class="row">
            <div class="col-md-6">
                <p><strong>Teléfono:</strong> ${cliente.telefono}</p>
                <p><strong>Correo:</strong> ${not empty cliente.correo ? cliente.correo : 'N/A'}</p>
                <p><strong>Fecha Nacimiento:</strong> <fmt:formatDate value="${cliente.fechaNacimiento}" pattern="dd/MM/yyyy" /></p>
            </div>
            <div class="col-md-6">
                <p><strong>Fecha Registro:</strong> <fmt:formatDate value="${cliente.fechaRegistro}" pattern="dd/MM/yyyy HH:mm" /></p>
                <p><strong>Última Visita:</strong> <fmt:formatDate value="${cliente.fechaUltimaVisita}" pattern="dd/MM/yyyy" /></p>
            </div>
        </div>
        <hr>
        <div class="row">
            <div class="col-12">
                <h6>Notas:</h6>
                <p class="text-muted">${not empty cliente.notas ? cliente.notas : 'Sin notas adicionales.'}</p>
            </div>
        </div>
    </div>
    <div class="card-footer bg-transparent">
        <a href="${pageContext.request.contextPath}/clientes?id=${cliente.id}&editar=true" class="btn btn-warning"><i class="fas fa-edit"></i> Editar</a>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />
