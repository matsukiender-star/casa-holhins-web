<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/includes/header.jsp" />

<h2>${empty servicio ? 'Nuevo Servicio' : 'Editar Servicio'}</h2>

<div class="card shadow-sm mt-3">
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/servicios" method="post">
            <c:if test="${not empty servicio}">
                <input type="hidden" name="id" value="${servicio.id}">
            </c:if>
            
            <div class="mb-3">
                <label class="form-label">Nombre del Servicio *</label>
                <input type="text" class="form-control" name="nombre" value="${servicio.nombre}" required>
            </div>
            
            <div class="mb-3">
                <label class="form-label">Descripción</label>
                <textarea class="form-control" name="descripcion" rows="3">${servicio.descripcion}</textarea>
            </div>
            
            <div class="row">
                <div class="col-md-4 mb-3">
                    <label class="form-label">Tipo *</label>
                    <select class="form-select" name="tipo" required>
                        <option value="TERAPIA" ${servicio.tipo == 'TERAPIA' ? 'selected' : ''}>Terapia</option>
                        <option value="CURSO" ${servicio.tipo == 'CURSO' ? 'selected' : ''}>Curso</option>
                        <option value="DIPLOMADO" ${servicio.tipo == 'DIPLOMADO' ? 'selected' : ''}>Diplomado</option>
                        <option value="TALLER" ${servicio.tipo == 'TALLER' ? 'selected' : ''}>Taller</option>
                    </select>
                </div>
                <div class="col-md-4 mb-3">
                    <label class="form-label">Precio (MXN) *</label>
                    <input type="number" class="form-control" name="precio" value="${servicio.precio}" step="0.01" min="0.01" required>
                </div>
                <div class="col-md-4 mb-3">
                    <label class="form-label">Duración (minutos)</label>
                    <input type="number" class="form-control" name="duracionMinutos" value="${servicio.duracionMinutos}" min="1">
                </div>
            </div>
            
            <div class="mb-3 form-check">
                <input type="checkbox" class="form-check-input" id="activo" name="activo" ${empty servicio || servicio.activo ? 'checked' : ''}>
                <label class="form-check-label" for="activo">Servicio Activo</label>
            </div>
            
            <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Guardar</button>
            <a href="${pageContext.request.contextPath}/servicios" class="btn btn-secondary">Cancelar</a>
        </form>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />
