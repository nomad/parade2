package org.makumba.parade.view.managers;

import java.util.List;

import org.apache.log4j.Logger;
import org.makumba.parade.init.ParadeProperties;
import org.makumba.parade.model.File;
import org.makumba.parade.model.Row;
import org.makumba.parade.model.RowCVS;
import org.makumba.parade.tools.ParadeException;
import org.makumba.parade.view.interfaces.ParadeView;

import freemarker.template.SimpleHash;

public class CVSViewManager implements ParadeView {

    static Logger logger = Logger.getLogger(CVSViewManager.class.getName());

    public void setParadeViewHeader(List<String> headers) {
        headers.add("CVS user");
        headers.add("module");
        headers.add("branch");
    }

    public void setParadeView(SimpleHash rowInformation, Row r) {
        SimpleHash cvsModel = new SimpleHash();
        RowCVS cvsdata = (RowCVS) r.getRowdata().get("cvs");

        if (cvsdata == null) {
            throw new ParadeException(
                    "Could not properly display CVS information, probably the index is being rebuilt. Please come back in 5 minutes.");
        }

        cvsModel.put("user", cvsdata.getUser() == null ? "" : cvsdata.getUser());
        cvsModel.put("module", cvsdata.getModule() == null ? "" : cvsdata.getModule());
        cvsModel.put("branch", cvsdata.getBranch() == null ? "" : cvsdata.getBranch());

        rowInformation.put("cvs", cvsModel);

    }

    public void setFileView(SimpleHash fileView, Row r, String path, File f) {

        RowCVS rowcvsdata = (RowCVS) r.getRowdata().get("cvs");

        String cvsweb = ParadeProperties.getProperty("cvs.site");
        String webPath = f.getPath().substring(r.getRowpath().length() + 1).replace(java.io.File.separatorChar, '/');
        String cvswebLink = cvsweb + rowcvsdata.getModule() + "/" + webPath;

        // populating model
        fileView.put("cvsWebLink", cvswebLink);
        fileView.put("cvsIsNull", f.getCvsStatus() == null);
        fileView.put("isConflictBackup", f.getName().startsWith(".#"));
        fileView.put("cvsRevision", f.getCvsRevision() == null ? "" : f.getCvsRevision());
        fileView.put("cvsStatus", f.getCvsStatus() == null ? "" : f.getCvsStatus());
    }

}
