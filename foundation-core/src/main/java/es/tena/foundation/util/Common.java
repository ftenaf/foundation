package es.tena.foundation.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Different utilities common to any project
 *
 * @author francisco.tena@gmail.com
 *
 */
public final class Common {

    private static int count;

    public static final String SUCESS = "SUCESS";

    public static final String FAILURE = "FAILURE";

    private static Common instance;

    public static Common getInstance() {
        if (instance == null) {
            instance = new Common();
        }
        return instance;
    }

    /**
     * Creates a new instance
     */
    private Common() {
    }

    public static void debug(String msg) {
        System.out.println(++count + " - " + msg);
    }

    /**
     * Check if the object provided is null or empty
     *
     * @param object
     * @return boolean
     */
    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        } else if (object.toString() == null) {
            return false;
        } else {
            return (object.toString().trim().equals(""));
        }
    }

    /**
     * Get classLoader from defaultObject
     *
     * @param defaultObject
     * @return
     */
    public static ClassLoader getCurrentClassLoader(Object defaultObject) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader == null) {
            loader = defaultObject.getClass().getClassLoader();
        }
        return loader;
    }

    /**
     * Prints in the logger the string at the specified level
     *
     * @param message
     * @param level
     */
    public static void print(String message, int level) {
        int levelAux = level;
        for (; level > 0; level--) {
            Logger.getLogger(Common.class.getName()).log(Level.FINEST, null, "\t");
        }
        Logger.getLogger(Common.class.getName()).log(Level.FINEST, null, message + "-" + levelAux);
    }
    /**
     * Gets the stack trace from a throwable
     * @param th
     * @return 
     */
    public static String getStackTraceFromThrowable(Throwable th) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter pw = new PrintWriter(baos);
        th.printStackTrace(pw);
        pw.close();
        return baos.toString();
    }
    
    /**
     * Show system properties in the 'out' console
     */
    public static void showProperties() {
        Properties props = System.getProperties();
        for (Iterator iter = props.keySet().iterator(); iter.hasNext();) {
            String key = (String) iter.next();
            String value = props.getProperty(key);
            System.out.println(key + ": " + value);
        }
    }

    /**
     * Prints the memory status
     */
    public static void printSystemMem() {
        Runtime rt = Runtime.getRuntime();
        long totalMem = rt.totalMemory();
        long maxMem = rt.maxMemory();
        long freeMem = rt.freeMemory();
        double megs = 1048576.0;

        System.out.println("Total Memory: " + totalMem + " (" + (totalMem / megs) + " MiB)");
        System.out.println("Max Memory:   " + maxMem + "   (" + (maxMem / megs) + " MiB)");
        System.out.println("Free Memory:  " + freeMem + "  (" + (freeMem / megs) + " MiB)");
    }
}
