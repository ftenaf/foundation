package es.tena.foundation.file.filter;

import java.io.File;
import java.io.FileFilter;
/**
 * 
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class File_Filter implements FileFilter {

    private final String[] okFileExtensions;

    public File_Filter(String[] fileExtensions) {
        okFileExtensions = fileExtensions;
    }
   
    
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
