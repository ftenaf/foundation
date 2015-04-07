package es.tena.foundation.file.filter;

import java.io.File;
import java.io.FileFilter;
/**
 * 
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class CSVFilter implements FileFilter {

    private final String[] okFileExtensions = new String[]{"csv"};

    @Override
    public boolean accept(File file) {
        if (!file.isDirectory()) {
            for (String extension : okFileExtensions) {
                if (file.getName().toLowerCase().endsWith(extension)) {
                    return true;
                }
            }
        }
        return false;
    }
}
