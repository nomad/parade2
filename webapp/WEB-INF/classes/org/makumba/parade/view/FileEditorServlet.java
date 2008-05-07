package org.makumba.parade.view;

import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.makumba.parade.init.InitServlet;
import org.makumba.parade.model.File;
import org.makumba.parade.model.Parade;
import org.makumba.parade.model.Row;
import org.makumba.parade.view.managers.CodePressFileEditViewManager;
import org.makumba.parade.view.managers.FileEditViewManager;

public class FileEditorServlet extends HttpServlet {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public void init() {
    }

    @Override
    public void service(ServletRequest req, ServletResponse resp) throws java.io.IOException, ServletException {
        PrintWriter out = resp.getWriter();

        Session s = InitServlet.getSessionFactory().openSession();
        Transaction tx = s.beginTransaction();
        /*
         * context - the context / rowname fileName - the name of the file to be edited path - the relative path
         * (displayed to the user) source - the source (content of the file) editor - the kind of editor
         */

        Parade p = (Parade) s.get(Parade.class, new Long(1));
        String context = req.getParameter("context");
        String fileName = req.getParameter("file");
        String path = req.getParameter("path");
        String[] source = req.getParameterValues("source");
        String editor = req.getParameter("editor");

        Row r = p.getRows().get(context);
        if (r == null) {
            out.println("Unknown context " + context);
        } else {
            
            
            // we need to build the absolute Path to the file
            String absoluteFilePath = Parade.constructAbsolutePath(context, path) + java.io.File.separator + fileName;

            File file = r.getFiles().get(absoluteFilePath);
            if (file == null) {
                out.println("Internal ParaDe error: cannot access file " + absoluteFilePath);
            } else {
                RequestDispatcher header = super.getServletContext().getRequestDispatcher("/layout/header.jsp?class=editor&pageTitle=Editor%20for%20"+file.getName());
                RequestDispatcher footer = super.getServletContext().getRequestDispatcher("/layout/footer.jsp");

                resp.setContentType("text/html");
                resp.setCharacterEncoding("UTF-8");

                FileEditViewManager oldFileEditV = new FileEditViewManager();
                CodePressFileEditViewManager codepressFileEditV = new CodePressFileEditViewManager();

                header.include(req, resp);
                if (editor.equals("codepress")) {
                    out.println(codepressFileEditV.getFileEditorView(r, path, file, source));
                    
                } else {
                    out.println(oldFileEditV.getFileEditorView(r, path, file, source));
                }
                footer.include(req, resp);
            }
        }

        tx.commit();

        s.close();

    }
}
