package mx.holhins.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

/**
 * Filtro de autenticacion que intercepta todas las peticiones antes de que
 * lleguen a los servlets protegidos. Si el usuario no tiene sesion activa, lo
 * mandamos de vuelta al login (viene siendo lo mismo que un middleware de
 * Express o el @login_required de Flask, nada mas que declarado con @WebFilter).
 *
 * Se salta el filtro para las rutas publicas: el propio login, la raiz y los
 * archivos estaticos. Lo de los estaticos importa mas de lo que parece: la
 * pantalla de login todavia no tiene sesion, asi que si el CSS o el logo no
 * estuvieran en la lista blanca, el navegador los pediria, el filtro los
 * mandaria al login y la pantalla se veria sin estilos y con la imagen rota.
 */
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

        // Trabajamos con la ruta relativa al contexto y no con el URI completo.
        // Si nos quedaramos con getRequestURI() tendriamos que cargar con el
        // "/casa-holhins-web" de adelante y las comparaciones saldrian mas laxas
        // de la cuenta.
        String ruta = req.getRequestURI().substring(req.getContextPath().length());

        boolean esLogin = ruta.equals("/login");
        boolean esRaiz = ruta.isEmpty() || ruta.equals("/") || ruta.equals("/index.jsp");
        boolean esEstatico = esRecursoEstatico(ruta);

        // getSession(false) no crea sesion si no existe, justo lo que queremos:
        // si creara una, cada visita anonima dejaria una sesion vacia colgada.
        boolean autenticado = session != null && session.getAttribute("usuario") != null;

        if (autenticado || esLogin || esRaiz || esEstatico) {
            // Si ya inicio sesion y aun asi pide el login, no tiene caso
            // mostrarselo otra vez: derecho al dashboard.
            if (autenticado && (esLogin || esRaiz)) {
                res.sendRedirect(req.getContextPath() + "/dashboard");
            } else {
                chain.doFilter(request, response);
            }
        } else {
            res.sendRedirect(req.getContextPath() + "/login");
        }
    }

    /**
     * Rutas de archivos que el navegador debe poder bajar sin haber iniciado
     * sesion: hojas de estilo, scripts, imagenes y el favicon.
     */
    private boolean esRecursoEstatico(String ruta) {
        return ruta.startsWith("/css/")
            || ruta.startsWith("/js/")
            || ruta.startsWith("/img/")
            || ruta.startsWith("/images/")
            || ruta.startsWith("/favicon");
    }

    @Override
    public void destroy() {}
}
