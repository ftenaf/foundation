/*
 * 
 */
package es.tena.foundation.util.stream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class InputStreamToByteArrayOutputStream {
    
    private static byte[] bytes = new byte[1024 * 1024 * 2];

    public OutputStream getOutpuStream(InputStream inputStream) {
        ByteArrayOutputStream byteOuputStream = new ByteArrayOutputStream();
        copyInputStreamToOutputStream(inputStream, byteOuputStream);
        return byteOuputStream;
    }    

    static public void copyInputStreamToOutputStream(InputStream inputStream, OutputStream outPutStream) {
        try {
            int read = 0;
            while ((read = inputStream.read(bytes)) != -1) {
                outPutStream.write(bytes, 0, read);
            }
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
