<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<jsp:include page="/includes/header.jsp" />

<%--
    Listado paginado de clientes. Los botones de la ultima columna son
    ver / editar / dar de baja; la baja va por POST con token CSRF porque
    modifica datos, no por un enlace.
--%>
<div class="row mb-3 align-items-center">
    <div class="col-md-8">
        <h2 class="mb-0">Listado de Clientes</h2>
    </div>
    <div class="col-md-4 text-end">
        <a href="${pageContext.request.contextPath}/clientes?nuevo=true" class="btn btn-primary"><i class="fas fa-plus"></i> Nuevo Cliente</a>
    </div>
</div>

<div class="card shadow-sm">
    <div class="card-body p-0">
        <table class="table table-striped table-hover mb-0 align-middle">
            <thead>
                <tr>
                    <th class="col-centrada">ID</th>
                    <th>Nombre</th>
                    <th>Teléfono</th>
                    <th>Correo</th>
                    <th class="col-centrada">Estatus</th>
                    <th class="col-centrada">Acciones</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="c" items="${clientes}">
                    <tr>
                        <td class="col-centrada">${c.id}</td>
                        <td>${c.nombreCompleto}</td>
                        <td>${c.telefono}</td>
                        <td>${c.correo}</td>
                        <td class="col-centrada">${c.estatus}</td>
                        <td class="col-centrada">
                            <a href="${pageContext.request.contextPath}/clientes?id=${c.id}" class="btn btn-sm btn-info text-white"><i class="fas fa-eye"></i></a>
                            <a href="${pageContext.request.contextPath}/clientes?id=${c.id}&editar=true" class="btn btn-sm btn-warning text-dark"><i class="fas fa-edit"></i></a>
                            <form action="${pageContext.request.contextPath}/clientes" method="post" class="d-inline" onsubmit="return confirm('¿Seguro que deseas dar de baja a este cliente?');">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                <input type="hidden" name="id" value="${c.id}">
                                <input type="hidden" name="baja" value="true">
                                <button type="submit" class="btn btn-sm btn-danger"><i class="fas fa-trash"></i></button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty clientes}">
                    <tr>
                        <td colspan="6" class="text-center text-muted py-4">No hay clientes que mostrar en esta página.</td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="/includes/footer.jsp" />
