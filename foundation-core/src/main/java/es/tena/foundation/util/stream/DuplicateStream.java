/*
 * 
 */
package es.tena.foundation.util.stream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class DuplicateStream {

    private InputStream inputStream;

    public DuplicateStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public InputStream cloneStream() {
        OutputStream outputStream = getOutputStreamByInputStream();
        InputStream inputStreamReturned = getInputStreamByOutputStream(outputStream);
        inputStream = getInputStreamByOutputStream(outputStream);
        closeOutpuStream(outputStream);
        return inputStreamReturned;
    }

    private OutputStream getOutputStreamByInputStream() {
        return new InputStreamToByteArrayOutputStream().getOutpuStream(inputStream);
    }

    private InputStream getInputStreamByOutputStream(OutputStream outputStream) {
        ByteArrayOutputStream byteOupPut = (ByteArrayOutputStream) outputStream;
        return new ByteArrayInputStream(byteOupPut.toByteArray());
    }

    static public void closeOutpuStream(OutputStream outputStream) {
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
