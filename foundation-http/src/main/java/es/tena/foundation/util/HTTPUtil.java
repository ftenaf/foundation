package es.tena.foundation.util;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 *
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class HTTPUtil {

    static long cont = 0;
    static final String BOUNDARY_PREFIX = "----------------------------------";
    
    public static String getBoundary() {
        return System.currentTimeMillis() + "x" + cont++;
    }

    public static String getBodyBoundaryString(String boundary) {
        return BOUNDARY_PREFIX + boundary;
    }

    public static String getEndBodyBoundaryString(String boundary) {
        return BOUNDARY_PREFIX + boundary + "--";
    }

    /**
     * Gets a byte array with the content of a multipart form data simple parameter
     * @param name
     * @param value
     * @param bodyBoundary
     * @param charset
     * @return 
     */
    public static byte[] getMultipartFormDataSimpleParameter(String name, String value, String bodyBoundary, String charset) {
        String s = bodyBoundary;
        s += "\r\n";
        s += "Content-Disposition: form-data; name=\"" + name + "\"";
        s += "\r\n\r\n";
        s += value;
        try {
            return s.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets a byte array with the content of a multipart form binary data file
     * @param name
     * @param fileName
     * @param value
     * @param bodyBoundary
     * @param charset
     * @return 
     */
    public static byte[] getMultipartFormDataBinaryFileParameter(
            String name, String fileName, byte[] value, String bodyBoundary, String charset) {
        return getMultipartFormDataFileParameter(name, fileName, value, bodyBoundary, charset, false);
    }

    /**
     * Gets a byte array with the content of a multipart form data text file
     * @param name
     * @param fileName
     * @param value
     * @param bodyBoundary
     * @param charset
     * @return 
     */
    public static byte[] getMultipartFormDataTextFileParameter(
            String name, String fileName, byte[] value, String bodyBoundary, String charset) {
        return getMultipartFormDataFileParameter(name, fileName, value, bodyBoundary, charset, true);
    }

    /**
     * Gets a byte array with the content of a multipart form data file
     * @param name
     * @param fileName
     * @param value
     * @param bodyBoundary
     * @param charset
     * @param isTextFile
     * @return 
     */
    public static byte[] getMultipartFormDataFileParameter(
            String name, String fileName, byte[] value, String bodyBoundary, String charset, boolean isTextFile) {
        String s = bodyBoundary;
        s += "\r\n";
        s += "Content-Disposition: form-data; name=\"" + name + "\"; filename=\"" + fileName + "\"";
        s += "\r\n";
        s += "Content-Type: " + (isTextFile ? "text/plain" : "application/octet-stream");
        s += "\r\n\r\n";
        s += value;
        byte[] part1 = null;
        try {
            part1 = s.getBytes(charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        byte[] result = new byte[part1.length + value.length];
        System.arraycopy(part1, 0, result, 0, part1.length);
        System.arraycopy(value, 0, result, value.length, value.length);
        return result;
    }

    /**
     * Gets the parameters of a HttpServletRequest in a Map
     * @param req
     * @return a Map of String (parameter name) and Object (parameter value) 
     * where Object can be a String, String[] or a org.apache.commons.fileupload.FileItem
     */
    static public Map<String, Object> getParamsMap(HttpServletRequest req) {
        try {
            Map<String, Object> r = new HashMap<>();
            if (ServletFileUpload.isMultipartContent(req)) {
                DiskFileItemFactory diskFileItemFactory = new DiskFileItemFactory();
                ServletFileUpload sfu = new ServletFileUpload(
                        diskFileItemFactory);
                List<FileItem> parameters = sfu.parseRequest(req);
                for (FileItem fileItem : parameters) {
                    if (fileItem.isFormField()) {
                        Object prev = r.get(fileItem.getFieldName());
                        Object o = null;
                        if (prev != null) {
                            String[] ss = null;
                            if (prev instanceof String[]) {
                                ss = new String[((String[]) prev).length + 1];
                                System.arraycopy(prev, 0, ss, 0, ss.length - 1);
                            } else {
                                ss = new String[2];
                                ss[0] = prev.toString();
                            }
                            ss[ss.length - 1] = fileItem.getString("utf-8");
                            o = ss;
                        } else {
                            o = fileItem.getString("utf-8");
                        }
                        r.put(fileItem.getFieldName(), o);
                    } else {
                        r.put(fileItem.getFieldName(), fileItem);
                    }
                }
            } else { 
                Map<String, String[]> map = req.getParameterMap();
                for (Map.Entry<String, String[]> entry : map.entrySet()) {
                    String name = entry.getKey();
                    String[] values = entry.getValue();
                    r.put(name,
                            values != null && values.length == 1 ? values[0]
                            : values);
                }
            }
            return r;
        } catch (UnsupportedEncodingException | FileUploadException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the parameter specified from the Request or returns it from the
     * attributes or "" if it is null
     *
     * @param request
     * @param parameter
     * @return the parameter value specified from the parameters or from the
     * attributes of the Request. If it's not found, it returns "".
     */
    public static String getParameter(HttpServletRequest request, String parameter) {
        String parametro = request.getParameter(parameter) != null ? request.getParameter(parameter) : "";
        if (Common.isEmpty(parametro)) {
            parametro = request.getAttribute(parameter) != null ? request.getAttribute(parameter).toString() : "";
        }
        return parametro;
    }

    /**
     * Gets the values for a parameter from the Request
     *
     * @param request
     * @param parameter
     * @return
     */
    public static String[] getParameterArray(HttpServletRequest request, String parameter) {
        String[] array = null;
        return (request.getParameterValues(parameter) != null ? request.getParameterValues(parameter) : array);
    }

    /**
     * Gets the parameter value or null if it doesn't exists
     *
     * @param request
     * @param parameter
     * @return String
     */
    public static String getParameterNull(HttpServletRequest request, String parameter) {
        return (request.getParameter(parameter) != null ? request.getParameter(parameter) : null);
    }

    /**
     * Gets the page number from the request for the component
     *
     * @param request
     * @return
     */
    public static int getPage(HttpServletRequest request) {
        int page = 0;
        Enumeration<String> paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String name = paramNames.nextElement();
            if (name != null && name.startsWith("d-") && name.endsWith("-p")) {
                String pageValue = request.getParameter(name);
                if (pageValue != null
                        && !pageValue.equals("null")) {
                    page = Integer.parseInt(pageValue) - 1;
                }
            }
        }
        return page;
    }

    /**
     * Gets the name of the resource. http://www..../.../list.jsp -->: list.jsp
     *
     * @param request
     * @return String
     */
    public static String getResourceName(HttpServletRequest request) {

        String requestURL = StringUtil.nullToString(request.getRequestURL().toString());

        String recurso = "";
        if (!requestURL.equals("")) {
            if (requestURL.lastIndexOf('/') > 0) {
                recurso = requestURL.substring(requestURL.lastIndexOf('/') + 1);
            }
        }
        return recurso;
    }

    /**
     * returns the alt and title attribute with the specified value
     *
     * @param text
     * @return
     */
    public static String getAltTitle(String text) {
        return "alt=\"" + text + "\" "
                + "title=\"" + text + "\""; //$NON-NLS-1$
    }

    /**
     * Gets the property specified to set the title and the alt attributes
     *
     * @param bundle
     * @param property
     * @return
     */
    public static String getAltTitle(ResourceBundle bundle, String property) {
        return getAltTitle(bundle, property, "");
    }

    /**
     * Gets the property specified to set the title and the alt attributes and
     * the text appended
     *
     * @param bundle
     * @param property
     * @param text
     * @return
     */
    public static String getAltTitle(ResourceBundle bundle, String property, String text) {
        return "alt=\"" + bundle.getString(property) + text + "\" "
                + "title=\"" + bundle.getString(property) + text + "\""; //$NON-NLS-1$
    }

}
