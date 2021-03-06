package org.makumba.parade.view.managers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.makumba.parade.init.InitServlet;
import org.makumba.parade.model.ActionLog;
import org.makumba.parade.model.Log;
import org.makumba.parade.tools.HtmlUtils;

import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class LogViewManager {

    private static final String LAST_HOUR = "hour";

    private static final String LAST_RESTART = "restart";

    private static final String LAST_DAY = "day";

    private static final String LAST_WEEK = "week";

    @SuppressWarnings("unchecked")
    public String getLogView(Session s, String context, String filter, Integer years, Integer months, Integer days) {
        StringWriter result = new StringWriter();
        PrintWriter out = new PrintWriter(result);

        Template temp = null;
        try {
            temp = InitServlet.getFreemarkerCfg().getTemplate("logs.ftl");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Creating the data model
        SimpleHash root = new SimpleHash();
        root.put("context", context);
        root.put("year", years.toString());
        root.put("month", "" + (months.intValue() + 1));
        root.put("day", days.toString());

        Calendar cal = Calendar.getInstance();
        cal.clear();

        // here we have several filtering possibilities
        // either by date, with day, month, year
        // then depending on the context, either a specific one, or the root one (parade), or all
        // then also by quick filter

        String contextQuery = "al.context = :context";

        if (context.equals("all"))
            contextQuery = "";
        if (context.equals("(root)"))
            contextQuery = "(al.context is null or al.context = 'parade2')";

        String dateQuery = "l.logDate > :myDate";

        boolean noContext = contextQuery.length() == 0;
        // FIXME the server restart should be detected solely as ActionLog (and generated as such in TriggerFilter), but
        // here we list ActionLog-Log couples
        String query = "from Log l, ActionLog al where l.actionLog = al and "
                + (noContext ? "" : "(" + contextQuery
                        + " or (al.origin = 'tomcat' and al.action='start' and l.origin='TriggerFilter')) and ")
                + dateQuery;

        Query q = s.createQuery(query);
        q.setCacheable(false);

        if (!context.equals("all") && !context.equals("(root)"))
            q.setString("context", context);

        if (!filter.equals("none")) {
            cal.setTime(new Date());

            if (filter.equals(LAST_HOUR)) {
                cal.add(Calendar.HOUR_OF_DAY, -1);
            }
            if (filter.equals(LAST_DAY)) {
                cal.add(Calendar.DAY_OF_MONTH, -1);
            }
            if (filter.equals(LAST_WEEK)) {
                cal.add(Calendar.WEEK_OF_MONTH, -1);
            }
            if (filter.equals(LAST_RESTART)) {
                // FIXME there's probably more performant way to do this
                // FIXME like, using a report query
                Query q1 = s.createQuery("from Log l where l.message = 'Server restart' order by l.logDate DESC");
                Date d = null;
                if (q1.list().size() > 0) {
                    d = ((Log) q1.list().get(0)).getLogDate();
                }
                if (d != null)
                    cal.setTime(d);
            }
        } else {
            cal.set(years.intValue(), months.intValue(), days.intValue());
        }

        q.setTimestamp("myDate", cal.getTime());

        List<Object[]> entries = q.list();
        List<SimpleHash> viewEntries = new LinkedList<SimpleHash>();
        for (int i = 0; i < entries.size(); i++) {
            Object[] res = entries.get(i);
            Log log = (Log) res[0];
            ActionLog actionLog = (ActionLog) res[1];

            SimpleHash entry = new SimpleHash();

            if (log.getMessage().trim().length() == 0) // skip blank lines
                continue;
            populateLogEntry(entry, log);

            entry.put("user", (actionLog.getUser() == null) ? "system" : actionLog.getUser());
            entry.put("context", (actionLog.getContext() == null) ? "parade2" : actionLog.getContext());

            viewEntries.add(entry);
        }

        root.put("entries", viewEntries);

        /* Merge data model with template */
        try {
            temp.process(root, out);
        } catch (TemplateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result.toString();

    }

    private void populateLogEntry(SimpleHash entry, Log log) {
        if (log.getMessage().equals("Server restart"))
            entry.put("serverRestart", true);
        else
            entry.put("serverRestart", false);
        entry.put("message", HtmlUtils.string2html(log.getMessage()));
        entry.put("date", log.getLogDate().toString());
        entry.put("level", log.getLevel());

    }

}