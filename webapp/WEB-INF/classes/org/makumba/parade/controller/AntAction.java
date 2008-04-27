package org.makumba.parade.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AntAction extends Action {

    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        String context = request.getParameter("context");
        String op = request.getParameter("op");
        String display = request.getParameter("display");
        String path = request.getParameter("path");
        if (path == null)
            path = (String) request.getAttribute("path");
        if (path == null)
            path = "";

        AntController antCtrl = new AntController();
        Object result[] = antCtrl.onAntAction(context, op);

        request.setAttribute("result", result[0]);
        request.setAttribute("success", result[1]);
        request.setAttribute("context", context);
        request.setAttribute("path", path);
        request.setAttribute("display", display);
        request.setAttribute("view", "commandOutput");

        return mapping.findForward(display);

    }
}
