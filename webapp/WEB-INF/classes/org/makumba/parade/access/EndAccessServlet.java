package org.makumba.parade.access;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * The servlet called at the end of each parade access, in all servlet contexts. It just logs the access that was made.
 * 
 * TODO remove this
 * 
 * @author Cristian Bogdan
 */
public class EndAccessServlet extends HttpServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    ServletContext context;

    @Override
    public void init(ServletConfig conf) {
        context = conf.getServletContext();
    }

    @Override
    public void service(HttpServletRequest req, HttpServletResponse resp) {
        final ServletRequest req1 = req;
        final ServletResponse resp1 = resp;

        /*
         * try { Config.invokeOperation("access", "accessMade", new PageContextDummy() { public ServletRequest
         * getRequest() { return req1; }
         * 
         * public ServletResponse getResponse() { return resp1; }
         * 
         * public ServletContext getServletContext() { return context; } }); } catch (ParadeException e) { throw new
         * RuntimeException("got parade exception " + e); }
         */
    }
}
