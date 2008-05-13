package org.makumba.parade.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.makumba.parade.init.InitServlet;
import org.makumba.parade.model.Application;
import org.makumba.parade.model.Parade;
import org.makumba.parade.model.Row;
import org.makumba.parade.model.managers.ApplicationManager;

public class AdminAction extends DispatchAction {

    public ActionForward newRow(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Session s = null;
        Transaction tx = null;
        try {
            s = InitServlet.getSessionFactory().openSession();
            tx = s.beginTransaction();

            Parade p = (Parade) s.get(Parade.class, new Long(1));

            p.createNewRows();

            request.setAttribute("result", "New rows added!");
            request.setAttribute("success", new Boolean(true));

            return mapping.findForward("index");

        } finally {
            tx.commit();
            s.close();
        }

    }

    public ActionForward refreshRow(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Session s = null;
        Transaction tx = null;
        try {
            s = InitServlet.getSessionFactory().openSession();
            tx = s.beginTransaction();

            Parade p = (Parade) s.get(Parade.class, new Long(1));

            String context = request.getParameter("context");
            if (context == null) {
                s.close();
                request.setAttribute("result", "Error: no context given");
                request.setAttribute("success", new Boolean(false));
                return mapping.findForward("index");
            }

            Row r = p.getRows().get(context);
            if (r == null) {
                s.close();
                request.setAttribute("result", "Error: no row corresponding to context " + context);
                request.setAttribute("success", new Boolean(false));
                return mapping.findForward("index");
            }

            p.rebuildRowCache(r);

            request.setAttribute("result", "Row " + context + " refreshed !");
            request.setAttribute("success", new Boolean(true));

            return mapping.findForward("index");

        } finally {
            tx.commit();
            s.close();
        }

    }

    public ActionForward refreshParade(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Session s = null;
        Transaction tx = null;
        try {
            s = InitServlet.getSessionFactory().openSession();
            tx = s.beginTransaction();

            Parade p = (Parade) s.get(Parade.class, new Long(1));

            p.refresh();
            try {
                p.addJNotifyListeners();
            } catch (Throwable e) {
                e.printStackTrace();
            }

            request.setAttribute("result", "ParaDe refreshed !");
            request.setAttribute("success", new Boolean(true));

            return mapping.findForward("index");

        } finally {
            tx.commit();
            s.close();
        }
    }
    
    public ActionForward refreshApplications(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Session s = null;
        Transaction tx = null;
        try {
            s = InitServlet.getSessionFactory().openSession();
            tx = s.beginTransaction();

            Parade p = (Parade) s.get(Parade.class, new Long(1));
            
            ApplicationManager mgr = new ApplicationManager();
            
            for(Application a : p.getApplications().values()) {
                mgr.buildCVSlist(a);
            }

            request.setAttribute("result", "Applications cache refreshed !");
            request.setAttribute("success", new Boolean(true));

            return mapping.findForward("index");

            
        } finally {
            tx.commit();
            s.close();
        }
    }

}