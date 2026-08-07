<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/includes/header.jsp" />

<div class="row mb-3">
    <div class="col-md-6">
        <h2>Catálogo de Servicios</h2>
    </div>
    <div class="col-md-6 text-end">
        <form class="d-inline" action="${pageContext.request.contextPath}/servicios" method="get">
            <select name="tipo" class="form-select form-select-sm d-inline-block w-auto" onchange="this.form.submit()">
                <option value="">Todos los tipos</option>
                <option value="TERAPIA" ${tipoFiltro == 'TERAPIA' ? 'selected' : ''}>Terapias</option>
                <option value="CURSO" ${tipoFiltro == 'CURSO' ? 'selected' : ''}>Cursos</option>
                <option value="DIPLOMADO" ${tipoFiltro == 'DIPLOMADO' ? 'selected' : ''}>Diplomados</option>
                <option value="TALLER" ${tipoFiltro == 'TALLER' ? 'selected' : ''}>Talleres</option>
            </select>
        </form>
        <a href="${pageContext.request.contextPath}/servicios?nuevo=true" class="btn btn-primary ms-2"><i class="fas fa-plus"></i> Nuevo Servicio</a>
    </div>
</div>

<div class="row row-cols-1 row-cols-md-3 g-4">
    <c:forEach var="s" items="${servicios}">
        <div class="col">
            <div class="card h-100 shadow-sm border-0" style="border-top: 4px solid var(--holhins-eucalyptus) !important;">
                <div class="card-body">
                    <h5 class="card-title">${s.nombre}</h5>
                    <h6 class="card-subtitle mb-2 text-muted">${s.tipo}</h6>
                    <p class="card-text">${s.descripcion}</p>
                    <ul class="list-unstyled">
                        <li><strong>Precio:</strong> <fmt:formatNumber value="${s.precio}" type="currency" currencySymbol="$" maxFractionDigits="2"/> MXN</li>
                        <c:if test="${not empty s.duracionMinutos}">
                            <li><strong>Duración:</strong> ${s.duracionMinutos} min</li>
                        </c:if>
                    </ul>
                </div>
                <div class="card-footer bg-transparent d-flex justify-content-between">
                    <a href="${pageContext.request.contextPath}/servicios?id=${s.id}&editar=true" class="btn btn-sm btn-outline-primary">Editar</a>
                    <form action="${pageContext.request.contextPath}/servicios" method="post" class="d-inline" onsubmit="return confirm('¿Seguro que deseas desactivar este servicio?');">
                        <input type="hidden" name="id" value="${s.id}">
                        <input type="hidden" name="desactivar" value="true">
                        <button type="submit" class="btn btn-sm btn-outline-danger">Desactivar</button>
                    </form>
                </div>
            </div>
        </div>
    </c:forEach>
    <c:if test="${empty servicios}">
        <div class="col-12">
            <div class="alert alert-info">No hay servicios disponibles con los filtros actuales.</div>
        </div>
    </c:if>
</div>

<jsp:include page="/includes/footer.jsp" />
