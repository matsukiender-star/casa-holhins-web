<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<jsp:include page="/includes/header.jsp" />

<%-- Mismo formulario para alta y edicion: si llega el objeto en el request,
     los campos vienen precargados y se manda el id oculto. --%>
<h2>${empty cliente ? 'Nuevo Cliente' : 'Editar Cliente'}</h2>

<div class="card shadow-sm mt-3">
    <div class="card-body">
        <form action="${pageContext.request.contextPath}/clientes" method="post">
            <%-- Token CSRF: el servlet rechaza cualquier POST que no lo traiga. --%>
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <c:if test="${not empty cliente}">
                <input type="hidden" name="id" value="${cliente.id}">
            </c:if>
            
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label">Nombre Completo *</label>
                    <input type="text" class="form-control" name="nombreCompleto" value="${cliente.nombreCompleto}" required>
                </div>
                <div class="col-md-6 mb-3">
                    <label class="form-label">Teléfono * (Ej: 5512345678)</label>
                    <input type="text" class="form-control" name="telefono" value="${cliente.telefono}" required pattern="[0-9]{10}">
                </div>
            </div>
            
            <div class="row">
                <div class="col-md-6 mb-3">
                    <label class="form-label">Correo Electrónico</label>
                    <input type="email" class="form-control" name="correo" value="${cliente.correo}">
                </div>
                <div class="col-md-6 mb-3">
                    <label class="form-label">Fecha de Nacimiento</label>
                    <input type="date" class="form-control" name="fechaNacimiento" value="${cliente.fechaNacimiento}">
                </div>
            </div>
            
            <div class="mb-3">
                <label class="form-label">Estatus</label>
                <select class="form-select" name="estatus">
                    <option value="NUEVO" ${cliente.estatus == 'NUEVO' ? 'selected' : ''}>Nuevo</option>
                    <option value="ACTIVO" ${cliente.estatus == 'ACTIVO' ? 'selected' : ''}>Activo</option>
                    <option value="LAPSADO" ${cliente.estatus == 'LAPSADO' ? 'selected' : ''}>Lapsado</option>
                </select>
            </div>
            
            <div class="mb-3">
                <label class="form-label">Notas médicas o preferencias</label>
                <textarea class="form-control" name="notas" rows="3">${cliente.notas}</textarea>
            </div>
            
            <button type="submit" class="btn btn-primary"><i class="fas fa-save"></i> Guardar</button>
            <a href="${pageContext.request.contextPath}/clientes" class="btn btn-secondary">Cancelar</a>
        </form>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />
