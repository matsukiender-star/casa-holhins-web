package mx.holhins.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
            
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        String uri = req.getRequestURI();
        
        // Rutas publicas que no requieren sesion
        boolean isLogin = uri.endsWith("/login");
        boolean isIndex = uri.endsWith("/index.jsp") || uri.endsWith("/");
        boolean isStatic = uri.contains("/css/") || uri.contains("/js/") || uri.contains("/images/");
        
        boolean loggedIn = session != null && session.getAttribute("usuario") != null;

        if (loggedIn || isLogin || isIndex || isStatic) {
            // Si va al login estando autenticado, mandalo al dashboard
            if (loggedIn && (isLogin || isIndex)) {
                res.sendRedirect(req.getContextPath() + "/dashboard");
            } else {
                chain.doFilter(request, response);
            }
        } else {
            // No esta logueado, redirigir al login
            res.sendRedirect(req.getContextPath() + "/login");
        }
    }

    @Override
    public void destroy() {}
}
