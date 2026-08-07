<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    // Redirige al login de inmediato (equivalente a lo que haria Flask/Django redirigiendo en la vista root)
    response.sendRedirect(request.getContextPath() + "/login");
%>
