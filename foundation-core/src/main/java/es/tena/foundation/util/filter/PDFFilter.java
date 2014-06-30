package es.tena.foundation.util.filter;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author ftena
 */
public class PDFFilter implements FileFilter {

    private final String[] okFileExtensions = new String[]{"pdf"};

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
