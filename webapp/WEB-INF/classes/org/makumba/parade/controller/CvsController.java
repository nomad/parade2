package org.makumba.parade.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Vector;

import org.apache.commons.collections.map.MultiValueMap;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.makumba.parade.init.InitServlet;
import org.makumba.parade.listeners.ParadeJNotifyListener;
import org.makumba.parade.model.File;
import org.makumba.parade.model.managers.CVSManager;
import org.makumba.parade.model.managers.FileManager;
import org.makumba.parade.tools.Execute;
import org.makumba.parade.tools.HtmlUtils;
import org.makumba.providers.QueryAnalysisProvider;
import org.makumba.providers.QueryProvider;
import org.makumba.providers.query.Pass1ASTPrinter;

public class CvsController {

    private static QueryAnalysisProvider qap = QueryProvider.getQueryAnalzyer("hql");

    public static Response onCheck(String context, String absolutePath) {
        java.io.File f = new java.io.File(absolutePath);
        if (!f.exists() || !f.canRead()) {
            return new Response("Error: the file path is not accessible.");
        }

        Vector<String> cmd = new Vector<String>();
        cmd.add("cvs");
        cmd.add("-n");
        cmd.add("-l");
        cmd.add("update");
        cmd.add("-P");
        cmd.add("-d");

        StringWriter result = new StringWriter();
        PrintWriter out = new PrintWriter(result);

        Execute.exec(cmd, f, getPrintWriterCVS(out));

        CVSManager.updateCvsCache(context, absolutePath, true);
        return new Response(result.toString());
    }

    public static Response onUpdate(String context, String absolutePath) {
        java.io.File f = new java.io.File(absolutePath);
        if (!f.exists() || !f.canRead()) {
            return new Response("Error: the file path is not accessible.");
        }

        Vector<String> cmd = new Vector<String>();
        cmd.add("cvs");
        cmd.add("update");
        cmd.add("-P");
        cmd.add("-d");
        cmd.add("-l");

        StringWriter result = new StringWriter();
        PrintWriter out = new PrintWriter(result);

        ParadeJNotifyListener.createDirectoryLock(f.getAbsolutePath());
        Execute.exec(cmd, f, getPrintWriterCVS(out));
        ParadeJNotifyListener.removeDirectoryLock(f.getAbsolutePath());

        // cvs update modifies state of file and of cvs data, locally
        FileManager.updateDirectoryCache(context, absolutePath, true);
        CVSManager.updateCvsCache(context, absolutePath, true);
        return new Response(result.toString());
    }

    public static Response onRUpdate(String context, String absolutePath) {
        java.io.File f = new java.io.File(absolutePath);
        if (!f.exists() || !f.canRead()) {
            return new Response("Error: the file path is not accessible.");
        }

        Vector<String> cmd = new Vector<String>();
        cmd.add("cvs");
        cmd.add("update");
        cmd.add("-P");
        cmd.add("-d");

        StringWriter result = new StringWriter();
        PrintWriter out = new PrintWriter(result);

        ParadeJNotifyListener.createDirectoryLock(f.getAbsolutePath());
        Execute.exec(cmd, f, getPrintWriterCVS(out));
        ParadeJNotifyListener.removeDirectoryLock(f.getAbsolutePath());

        // cvs recursive update modifies state of file and of cvs data, recursively
        FileManager.updateDirectoryCache(context, absolutePath, false);
        CVSManager.updateCvsCache(context, absolutePath, false);
        return new Response(result.toString());
    }

    public static Response onCommit(String context, String[] files, String message) {

        String resultString = "";

        StringWriter result = new StringWriter();
        PrintWriter out = new PrintWriter(result);

        MultiValueMap modelFiles = new MultiValueMap();

        Session s = null;
        try {
            s = InitServlet.getSessionFactory().openSession();
            Transaction tx = s.beginTransaction();

            // retrieve the File objects for each file based on its URL
            for (String file : files) {
                String query = "select f.row.rowpath as rowpath, f as file from File f where f.fileURL() = :fileURL";
                Object res = s.createQuery(Pass1ASTPrinter.printAST(qap.inlineFunctions(query)).toString()).setString(
                        "fileURL", file).uniqueResult();
                Object[] data = (Object[]) res;
                modelFiles.put(data[0], data[1]);
            }

            for (Object rowPath : modelFiles.keySet()) {
                @SuppressWarnings("unchecked")
                Collection<File> filesInRow = modelFiles.getCollection(rowPath);

                Vector<String> cmd = new Vector<String>();
                cmd.add("cvs");
                cmd.add("commit");
                cmd.add("-m");
                cmd.add("\"" + message + "\"");

                for (File f : filesInRow) {
                    cmd.add(f.getPath().substring(f.getRow().getRowpath().length() + 1));
                }

                Execute.exec(cmd, new java.io.File((String) rowPath), getPrintWriterCVS(out));
                resultString += "\n" + result.toString();

                CVSManager.updateMultipleCvsCache(context, filesInRow);

                // in case of a cvs commit that leads to the deletion of a file of the repository
                // we need to make sure that the file gets removed from the cache or it will appear as "zombie"
                FileManager.checkShouldCache(context, filesInRow);

                tx.commit();

            }
        } finally {
            if (s != null)
                s.close();
        }
        return new Response(resultString);
    }

    public static Response onDiff(String context, String absolutePath, String file) {
        java.io.File f = new java.io.File(file);
        java.io.File p = new java.io.File(absolutePath);
        if (!f.exists() || !f.canRead()) {
            return new Response("Error: the file path is not accessible.");
        }

        Vector<String> cmd = new Vector<String>();
        cmd.add("cvs");
        cmd.add("diff");
        cmd.add(f.getName());

        StringWriter result = new StringWriter();
        PrintWriter out = new PrintWriter(result);

        Execute.exec(cmd, p, getPrintWriterCVS(out));

        return new Response(result.toString());
    }

    public static Response onAdd(String context, String absolutePath, String file) {
        java.io.File f = new java.io.File(file);
        java.io.File p = new java.io.File(absolutePath);
        if (!f.exists() || !f.canRead()) {
            return new Response("Error: the file path is not accessible.");
        }

        Vector<String> cmd = new Vector<String>();
        cmd.add("cvs");
        cmd.add("add");
        cmd.add(f.getName());

        StringWriter result = new StringWriter();
        PrintWriter out = new PrintWriter(result);

        ParadeJNotifyListener.createFileLock(f.getAbsolutePath());
        Execute.exec(cmd, p, getPrintWriterCVS(out));
        CVSManager.updateSimpleCvsCache(context, f.getAbsolutePath());
        ParadeJNotifyListener.removeFileLock(f.getAbsolutePath());

        return new Response(result.toString());
    }

    public static Response onAddBinary(String context, String absolutePath, String file) {
        java.io.File f = new java.io.File(file);
        java.io.File p = new java.io.File(absolutePath);
        if (!f.exists() || !f.canRead()) {
            return new Response("Error: the file path is not accessible.");
        }

        Vector<String> cmd = new Vector<String>();
        cmd.add("cvs");
        cmd.add("add");
        cmd.add("-kb");
        cmd.add(f.getName());

        StringWriter result = new StringWriter();
        PrintWriter out = new PrintWriter(result);

        ParadeJNotifyListener.createFileLock(f.getAbsolutePath());
        Execute.exec(cmd, p, getPrintWriterCVS(out));
        CVSManager.updateSimpleCvsCache(context, f.getAbsolutePath());
        ParadeJNotifyListener.removeFileLock(f.getAbsolutePath());

        return new Response(result.toString());
    }

    public static Response onUpdateFile(String context, String absolutePath, String absoluteFilePath) {
        java.io.File f = new java.io.File(absoluteFilePath);
        java.io.File p = new java.io.File(absolutePath);

        Vector<String> cmd = new Vector<String>();
        cmd.add("cvs");
        cmd.add("update");
        cmd.add(f.getName());

        StringWriter result = new StringWriter();
        PrintWriter out = new PrintWriter(result);

        ParadeJNotifyListener.createFileLock(absoluteFilePath);
        Execute.exec(cmd, p, getPrintWriterCVS(out));
        FileManager.updateSimpleFileCache(context, absoluteFilePath);
        CVSManager.updateSimpleCvsCache(context, absoluteFilePath);
        ParadeJNotifyListener.removeFileLock(absoluteFilePath);

        return new Response(result.toString());
    }

    public static Response onDeleteFile(String context, String absolutePath, String absoluteFilePath) {
        java.io.File f = new java.io.File(absoluteFilePath);
        java.io.File p = new java.io.File(absolutePath);

        Vector<String> cmd = new Vector<String>();
        cmd.add("cvs");
        cmd.add("delete");
        cmd.add(f.getName());

        StringWriter result = new StringWriter();
        PrintWriter out = new PrintWriter(result);

        ParadeJNotifyListener.createFileLock(absoluteFilePath);
        Execute.exec(cmd, p, getPrintWriterCVS(out));
        FileManager.updateSimpleFileCache(context, absoluteFilePath);
        CVSManager.updateSimpleCvsCache(context, absoluteFilePath);
        ParadeJNotifyListener.removeFileLock(absoluteFilePath);

        return new Response(result.toString());
    }

    /**
     * 
     * @author Joao Andrade
     * @param context
     * @param absolutePath
     * @param absoluteFilePath
     * @return
     */
    public static Response onDeleteDirectory(String context, String absolutePath, String absoluteFilePath) {
        java.io.File f = new java.io.File(absoluteFilePath);
        java.io.File p = new java.io.File(absolutePath);

        Vector<String> cmd = new Vector<String>();
        cmd.add("cvs");
        cmd.add("update");
        cmd.add("-P");
        cmd.add(f.getName());

        StringWriter result = new StringWriter();
        PrintWriter out = new PrintWriter(result);

        ParadeJNotifyListener.createFileLock(absoluteFilePath);
        Execute.exec(cmd, p, getPrintWriterCVS(out));
        FileManager.updateSimpleFileCache(context, absoluteFilePath);
        CVSManager.updateSimpleCvsCache(context, absoluteFilePath);
        ParadeJNotifyListener.removeFileLock(absoluteFilePath);

        // cvs recursive update modifies state of file and of cvs data, recursively
        FileManager.updateDirectoryCache(context, absolutePath, false);
        CVSManager.updateCvsCache(context, absolutePath, false);

        return new Response(result.toString());
    }

    /* displays output with colors */
    public static PrintWriter getPrintWriterCVS(PrintWriter out) {
        final PrintWriter o = out;
        return new PrintWriter(new ByteArrayOutputStream()) {
            @Override
            public void print(String s) {
                try {
                    o.print(s);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }

            @Override
            public void println(String s) {
                try {
                    String style = "color:#444444"; // default
                    if (s.startsWith("M "))
                        style = "color:blue"; // modified
                    if (s.startsWith("A "))
                        style = "color:purple"; // added
                    if (s.startsWith("R "))
                        style = "color:purple"; // removed
                    if (s.startsWith("U "))
                        style = "color:green"; // updated
                    if (s.startsWith("P "))
                        style = "color:green"; // patched
                    if (s.startsWith("C "))
                        style = "color:red; font:bold"; // conflict
                    if (s.startsWith("? "))
                        style = "color:purple"; // unknown
                    if (s.startsWith("< "))
                        style = "background:#ffdddd"; // content removed
                    if (s.startsWith("> "))
                        style = "background:lightblue"; // content added
                    if (s.startsWith("exec: "))
                        style = "color:black";
                    if (s.endsWith("was lost"))
                        style = "color: brown; background:pink";
                    if (s.endsWith(" -- ignored"))
                        style = "color: green";
                    o.println("<span style=\"" + style + "\">" + HtmlUtils.string2html(s) + "</span><br>");
                    o.flush();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        };
    }

}
