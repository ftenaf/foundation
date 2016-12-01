/*
 * 
 */
package es.tena.foundation.util.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class CopyInputStream {

    private final InputStream _is;
    private final ByteArrayOutputStream _copy = new ByteArrayOutputStream();

    public CopyInputStream(InputStream is) {
        _is = is;

        try {
            copy();
        } catch (IOException ex) {
            // do nothing
        }
    }

    private int copy() throws IOException {
        int read = 0;
        int chunk = 0;
        byte[] data = new byte[256];

        while (-1 != (chunk = _is.read(data))) {
            read += data.length;
            _copy.write(data, 0, chunk);
        }

        return read;
    }

    public InputStream getCopy() {
        return (InputStream) new ByteArrayInputStream(_copy.toByteArray());
    }
}
