package org.makumba.parade.tools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class ParadeLogFormatter extends SimpleFormatter {
    
    static final String TOMCAT_PREFIX = "[Tomcat]";
    static final String PARADE_PREFIX = "[ParaDe]";

    @Override
    public synchronized String format(LogRecord record) {
        String formatted = super.format(record);
        if(formatted.indexOf(PARADE_PREFIX) != -1)
            formatted = formatted.substring(formatted.indexOf(PARADE_PREFIX));
        String prefix = (String) record.getParameters()[0];

        // if the prefix is null, this was tomcat
        if (prefix == null)
            prefix = TOMCAT_PREFIX + "  ";
        else
            prefix += "  ";

        StringBuffer sb = new StringBuffer();
        BufferedReader b = new BufferedReader(new StringReader(record.getMessage()));
        
        String line = new String();

        try {
            while ((line = b.readLine()) != null) {
                sb.append(prefix + line);
                sb.append("\n");
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return sb.toString();
    }

}