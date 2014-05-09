package es.tena.foundation.util;

import java.util.Enumeration;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class HTTPUtil {

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
