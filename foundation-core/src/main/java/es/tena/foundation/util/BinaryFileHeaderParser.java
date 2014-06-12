package es.tena.foundation.util;

/**
 * File Header parser to find the MIME type
 *
 * @author Francisco Tena<francisco.tena@gmail.com>
 */
public class BinaryFileHeaderParser extends Object {

    protected static final byte[][] headers = {
        {'%', 'P', 'D', 'F'},   // 0 - Adobe Acrobat Reader
        {(byte) 0xd0, (byte) 0xcf, (byte) 0x11, (byte) 0xe0, (byte) 0xa1,
            (byte) 0xb1, (byte) 0x1a, (byte) 0xe1},             // 1 - MS Word
        {(byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xba},   // 2 - MPEG
        {(byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xb3},   // 3 - MPEG
        {'F', 'W', 'S'},        // Macromedia Flash
        {'C', 'W', 'S', 0x06}   // Macromedia Flash MX
    };

    protected static final String[] mimeTypes = {"application/pdf",
        "application/msword", "video/mpeg", "video/mpeg",
        "application/x-shockwave-flash", "application/x-shockwave-flash"};

    /**
     * 0 - text
     * 1 - image 
     * 2 - audio 
     * 3 - video 
     * 4 - document
     * 5 - flash
     */
    protected static final int[] mediaTypes = {4, 4, 3, 3, 5};

    protected static final String defaultMimeType = "text/plain";

    public static boolean firstBytesEquals(byte[] b1, byte[] b2) {
        if (b1 == null || b2 == null) {
            return false;
        }
        int min = Math.min(b1.length, b2.length);
        for (int i = 0; i < min; i++) {
            if (b1[i] != b2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Gets the MIME Type from a byte array
     * @param bytes
     * @return 
     */
    public static String getMimeTypeFromByteArray(byte[] bytes) {
        int i = getPosFromByteArray(bytes);
        if (i >= 0) {
            return mimeTypes[i];
        } else {
            return defaultMimeType;
        }
    }

    protected static int getPosFromByteArray(byte[] bytes) {
        for (int i = 0; i < headers.length; i++) {
            if (firstBytesEquals(headers[i], bytes)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the media type for a byte array
     * 
     * @param bytes
     * @return <ul><li>0 - text</li>
     * <li>1 - image </li>
     * <li>2 - audio </li>
     * <li>3 - video </li>
     * <li>4 - document</li>
     * <li>5 - flash</li>
     * </ul>
     */
    public static int getMediaTypeFromByteArray(byte[] bytes) {
        int i = getPosFromByteArray(bytes);
        if (i >= 0) {
            return mediaTypes[i];
        } else {
            return 0;
        }
    }
}
