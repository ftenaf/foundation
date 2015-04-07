/*
 * 
 */

package es.tena.foundation.file;

import java.io.IOException;
import java.nio.file.Path;
import org.apache.tika.Tika;

/**
 *
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class FileTypeDetector extends java.nio.file.spi.FileTypeDetector {
 
    private final Tika tika = new Tika();
 
    @Override
    public String probeContentType(Path path) throws IOException {
        return tika.detect(path.toFile());
    }
}