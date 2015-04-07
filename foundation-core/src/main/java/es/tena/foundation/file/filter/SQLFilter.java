package es.tena.foundation.file.filter;

import java.io.File;
import java.io.FileFilter;

/**
 *
 * @author ftena
 */
public class SQLFilter implements FileFilter {

    private final String[] okFileExtensions = new String[]{"sql"};

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
