package org.makumba.parade.view.managers;

import java.util.List;

import org.makumba.parade.model.Row;
import org.makumba.parade.model.RowAnt;
import org.makumba.parade.view.interfaces.HeaderView;
import org.makumba.parade.view.interfaces.ParadeView;

import freemarker.template.SimpleHash;

public class AntViewManager implements ParadeView, HeaderView {

    public void setParadeViewHeader(List<String> headers) {
        headers.add("Ant buildfile");
    }

    public void setParadeView(SimpleHash rowInformation, Row r) {
        SimpleHash antModel = new SimpleHash();
        RowAnt antdata = (RowAnt) r.getRowdata().get("ant");
        antModel.put("buildfile", antdata.getBuildfile());
        antModel.put("targets", antdata.getAllowedOperations());
        rowInformation.put("ant", antModel);
    }

    public void setHeaderView(SimpleHash root, Row r, String path) {
        List<String> allowedOps = ((RowAnt) r.getRowdata().get("ant")).getAllowedOperations();
        root.put("antTargets", allowedOps);
    }

}
