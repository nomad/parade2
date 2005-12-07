package org.makumba.parade;


import java.util.HashMap;
import java.util.Map;

import org.makumba.parade.ifc.DirectoryRefresher;
import org.makumba.parade.ifc.RowRefresher;
import org.makumba.parade.model.Row;
import org.makumba.parade.model.RowTracker;

public class TrackerManager implements DirectoryRefresher, RowRefresher {

	public void directoryRefresh(Row row, String path) {
		// TODO Auto-generated method stub
		
	}

	public void rowRefresh(Row row) {
		// TODO Auto-generated method stub
		
		/* Some example data */
		RowTracker trackerdata = new RowTracker();
		trackerdata.setDataType("tracker");
		trackerdata.setRow(row);
		
		Map rowdata = row.getRowdata();
		if(rowdata == null) {
			rowdata = new HashMap();
		}
				
		rowdata.put("tracker",trackerdata);
		row.setRowdata(rowdata);
		
	}
	

}
