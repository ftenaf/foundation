package es.tena.foundation.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Francisco Tena<francisco.tena@gmail.com>
 */
public class ReflectUtil {

    /**
     * Gets the value of a field in the object provided
     *
     * @param o
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object fieldValueOf(Object o, String fieldName)
            throws NoSuchFieldException, IllegalAccessException,
            InvocationTargetException {
        if (o == null) {
            return null;
        }
        return fieldValueOf(o, fieldName, null, false);
    }

    /**
     * Gets the value of a field in the object provided using its 'get'
     * procedure as specifies the JavaBeans 1.01 specifications
     * http://download.oracle.com/otndocs/jcp/7224-javabeans-1.01-fr-spec-oth-JSpec/
     *
     * @param o
     * @param field
     * @return
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Object getFieldValue(Object o, Field field)
            throws IllegalAccessException, InvocationTargetException {
        Class claux = field.getDeclaringClass();
        String methodName = "get"
                + field.getName().substring(0, 1).toUpperCase()
                + field.getName().substring(1);
        Method method = null;
        try {
            method = claux.getDeclaredMethod(methodName, (Class[]) null);
        } catch (NoSuchMethodException e) {
            if (boolean.class.isAssignableFrom(field.getType())) {
                methodName = "is"
                        + field.getName().substring(0, 1).toUpperCase()
                        + field.getName().substring(1);
                try {
                    method = claux
                            .getDeclaredMethod(methodName, (Class[]) null);
                } catch (NoSuchMethodException dummy) {
                    // method will be null
                }
            }
        }
        if (method == null) {
            field.setAccessible(true);
            return field.get(o);
        }
        method.setAccessible(true);
        return method.invoke(o, (Object[]) null);

    }

    /**
     * Updates a field with the value provided using its 'set'
     * procedure as specifies the JavaBeans 1.01 specifications
     * http://download.oracle.com/otndocs/jcp/7224-javabeans-1.01-fr-spec-oth-JSpec/
     * @param o
     * @param field
     * @param value
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void setFieldValue(Object o, Field field, Object value)
            throws IllegalAccessException, InvocationTargetException {
        Class claux = field.getDeclaringClass();
        String methodName = "set"
                + field.getName().substring(0, 1).toUpperCase()
                + field.getName().substring(1);
        Method method = null;
        try {
            method = claux.getDeclaredMethod(methodName, new Class[]{field
                .getType()});
        } catch (NoSuchMethodException e) {
            // method will be null
        }
        if (method == null) {
            field.setAccessible(true);
            field.set(o, value);
            return;
        }
        method.setAccessible(true);
        method.invoke(o, new Object[]{value});

    }

    static int indexOfCollection(Class clazz, List fields)
            throws NoSuchFieldException, ClassNotFoundException {
        int i = 0;
        Class classAux = clazz;
        for (Iterator iter = fields.iterator(); iter.hasNext();) {
            String fieldName = (String) iter.next();
            Field field = fieldOf(classAux, fieldName);
            classAux = field.getType();
            if (Collection.class.isAssignableFrom(classAux)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    /**
     * Returns an Object array with all the fields of a class
     * @param clazz
     * @param fieldName
     * @return
     * @throws NoSuchFieldException
     * @throws ClassNotFoundException 
     */
    public static Object[] desguaceCollection(Class clazz, String fieldName)
            throws NoSuchFieldException, ClassNotFoundException {
        List fieldsWithoutClasses = fieldsWithoutClases(fieldName);
        int pos = indexOfCollection(clazz, fieldsWithoutClasses);
        Object[] array = new Object[4];
        if (pos != -1) {
            int r = 0;
            String result = "";
            for (int i = 0; i < pos; i++) {
                result += fieldsWithoutClasses.get(i);
                if (i != pos - 1) {
                    result += ".";
                }
            }
            array[r] = result;
            r++;
            array[r] = (String) fieldsWithoutClasses.get(pos);
            r++;
            List fieldsWithClasses = fieldsWithClasses(fieldName);
            String fieldWithClass = (String) fieldsWithClasses.get(pos);
            Pattern p = Pattern.compile("\\[(.+)?\\]");
            Matcher m = p.matcher(fieldWithClass);
            while (m.find()) {
                array[r] = Class.forName(m.group(1));
            }
            r++;
            result = "";
            for (int i = pos + 1; i < fieldsWithClasses.size(); i++) {
                result += fieldsWithClasses.get(i);
                if (i != fieldsWithClasses.size() - 1) {
                    result += ".";
                }
            }
            array[r] = result;
        }
        return array;

    }

    public static String untilCollection(Class clase, String fieldName)
            throws NoSuchFieldException, ClassNotFoundException {
        List camposWithoutClases = fieldsWithoutClases(fieldName);
        int pos = indexOfCollection(clase, camposWithoutClases);
        String result = "";
        if (pos != -1) {
            for (int i = 0; i < pos; i++) {
                result += camposWithoutClases.get(i);
                if (i != pos - 1) {
                    result += ".";
                }
            }
        }
        return result;
    }

    public static String collectionField(Class clase, String fieldName)
            throws NoSuchFieldException, ClassNotFoundException {
        List camposWithoutClases = fieldsWithoutClases(fieldName);
        int pos = indexOfCollection(clase, camposWithoutClases);
        if (pos != -1) {
            return (String) camposWithoutClases.get(pos);
        }
        return null;
    }

    public static Class collectionClass(Class clase, String fieldName)
            throws NoSuchFieldException, ClassNotFoundException {
        List camposWithoutClases = fieldsWithoutClases(fieldName);
        int pos = indexOfCollection(clase, camposWithoutClases);
        if (pos != -1) {
            String campoConClass = (String) fieldsWithClasses(fieldName).get(pos);
            Pattern p = Pattern.compile("\\[(.+)?\\]");
            Matcher m = p.matcher(campoConClass);
            while (m.find()) {
                return Class.forName(m.group(1));
            }
        }
        return null;
    }

    public static String afterCollection(Class clase, String fieldName)
            throws NoSuchFieldException, ClassNotFoundException {
        List camposWithoutClases = fieldsWithoutClases(fieldName);
        int pos = indexOfCollection(clase, camposWithoutClases);
        String result = "";
        if (pos != -1) {

            for (int i = pos + 1; i < camposWithoutClases.size(); i++) {
                result += camposWithoutClases.get(i);
                if (i != camposWithoutClases.size() - 1) {
                    result += ".";
                }
            }
        }
        return result;
    }

    public static List<String> fieldsWithClasses(String fieldName) {
        List<String> campos = new ArrayList<>();
        Pattern p = Pattern.compile("([a-zA-Z]+\\[.+?\\]|[a-zA-Z]+)");
        Matcher m = p.matcher(fieldName);
        while (m.find()) {
            campos.add(m.group(1));
        }
        return campos;
    }

    /**
     * Returns the path to be followed to get the fieldName attribute
     * @param fieldName
     * @return 
     */
    public static List<String> fieldsWithoutClases(String fieldName) {
        String pattern = "\\.|\\[.+?\\]\\.|\\[.+?\\]";
        return Arrays.asList(fieldName.split(pattern));
    }

    public static boolean containsCollection(Class clase, String fieldName)
            throws NoSuchFieldException, ClassNotFoundException {
        return indexOfCollection(clase, fieldsWithoutClases(fieldName)) != -1;
    }

    /**
     * 
     * @param o
     * @param fieldName
     * @param value
     * @param set
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws InvocationTargetException 
     */
    static Object fieldValueOf(Object o, String fieldName, Object value,
            boolean set) throws NoSuchFieldException, IllegalAccessException,
            InvocationTargetException {
        if (o == null) {
            return null;
        }
        int bracketPos = 0;
        List listKeys = new ArrayList();
        while ((fieldName.substring(bracketPos) + "    ").indexOf("[") > 0) {
            bracketPos += fieldName.substring(bracketPos).indexOf("[") + 2;
            String strMapKey = fieldName.substring(fieldName.indexOf("["));
            strMapKey = strMapKey.substring(1, strMapKey.indexOf("]"));
            //if it is a literal
            if (strMapKey.startsWith("'") || strMapKey.startsWith("\"")) { 
                strMapKey = strMapKey.substring(1, strMapKey.length() - 1);
                listKeys.add(strMapKey);
            } else {
                // if it's not a literal find it by the field value
                Object oKey = fieldValueOf(o, strMapKey);
                listKeys.add(oKey);
            }
            fieldName = fieldName.substring(0, fieldName.indexOf("[")) + "[]"
                    + fieldName.substring(fieldName.indexOf("]") + 1);
        }
        Iterator iteratorKeys = listKeys.iterator();

        String[] fields = fieldName.split("\\.");
        Object auxo = o;
        try {
            Class claux = o.getClass();
            Field field = null;
            for (int k = 0; k < fields.length; k++) {
                Class clauxOriginal = claux;
                boolean lastField = k == fields.length - 1;
                String currentField = fields[k];
                String strMapKey = null;
                if (currentField.contains("[")) {
                    strMapKey = currentField.substring(currentField.indexOf("[") + 1);
                    strMapKey = strMapKey.substring(0, strMapKey.indexOf("]"));
                    currentField = currentField.substring(0, currentField
                            .indexOf("["));
                }
                field = null;
                try {
                    while (field == null && claux != null) {
                        try {
                            field = claux.getDeclaredField(currentField);
                        } catch (NoSuchFieldException e) {
                            if (claux.getSuperclass() == null) {
//                                logger.debug("No se ha encontrado el campo '" + currentField + "' en '" + clauxOriginal.getName() + "' ( + Object: " + o + (o != null ? " (" + o.getClass().getName() + ")" : "") + ", fieldName: " + fieldName + ", value: " + value + ")");
                                throw e;
                            }
                            claux = claux.getSuperclass(); // busco en el padre
                        }
                    }
                    String methodName = (set && lastField ? "set" : "get")
                            + field.getName().substring(0, 1).toUpperCase()
                            + field.getName().substring(1);
                    Method method = null;
                    try {
                        method = claux.getDeclaredMethod(methodName, set
                                && lastField ? new Class[]{field.getType()}
                                : null);
                    } catch (NoSuchMethodException e) {
                        if (!set && boolean.class.isAssignableFrom(field.getType())) {
                            methodName = "is" + field.getName().substring(0, 1)
                                    .toUpperCase()
                                    + field.getName().substring(1);
                            try {
                                method = claux.getDeclaredMethod(methodName
                                        , set && lastField ? new Class[]{field.getType()} : null);
                            } catch (NoSuchMethodException dummy) {
                                // method will be null
                            }
                        }
                    }
                    if (method == null) {
                        field.setAccessible(true);
                        if (set && lastField) {
                            field.set(auxo, value);
                        } else {
                            auxo = field.get(auxo);
                        }
                    } else {
                        method.setAccessible(true);
                        if (set && lastField) {
                            method.invoke(auxo, new Object[]{value});
                        } else {
                            auxo = method.invoke(auxo, (Object[]) null);
                        }
                    }
                    claux = auxo.getClass();
                    // TODO: revisar para Maps el añadir elementos
                    if (auxo instanceof Map) {
                        if (strMapKey != null) {
                            Object oo = iteratorKeys.next();
                            auxo = ((Map) auxo).get(oo);
                        }
                    }
                } catch (NoSuchFieldException ex) {
                    if (set) {
                        throw new RuntimeException(
                                "No se encuentra el campo para el 'set' (set"
                                + fields[k] + ")");
                    }
                    Class clase = auxo.getClass();
                    try { 
                        if (currentField.length() > 0) {
                            String stringMethod = "get"
                                    + currentField.substring(0, 1)
                                    .toUpperCase()
                                    + currentField.substring(1);
                            Method method = clase.getMethod(stringMethod,
                                    new Class[]{});
                            auxo = method.invoke(auxo, new Object[]{});
                        } else {
                            if (auxo instanceof Map) {
                                if (strMapKey != null) {
                                    strMapKey = strMapKey.substring(1,
                                            strMapKey.length() - 1);
                                    auxo = ((Map) auxo).get(strMapKey);
                                }
                            }
                        }
                        if (auxo != null) {
                            claux = auxo.getClass();
                        }
                    } catch (NoSuchMethodException ex2) {
                        try {
                            Method method = clase.getMethod(fieldName,
                                    new Class[]{});
                            auxo = method.invoke(auxo, new Object[]{});
                            if (auxo != null) {
                                claux = auxo.getClass();
                            }
                        } catch (InvocationTargetException ex3) {
                            ex.printStackTrace();
                            ex2.printStackTrace();
                            ex3.printStackTrace();
//                            logger.info("Campo no encontrado: " + o.getClass()
//                                    + "." + currentField);
                            throw ex;
                        } catch (NoSuchMethodException ex3) {
//                            logger.debug(ex);
//                            logger.debug(ex2);
//                            logger.debug(ex3);
//                            logger.info("Campo no encontrado: " + o.getClass()
//                                    + "." + currentField);
                            throw ex;
                        }
                    } catch (Exception ex2) {
//                        logger.error(clase, ex);
//                        logger.error(clase, ex2);
//                        logger.info("Campo no encontrado: " + o.getClass()
//                                + "." + currentField + " (" + fieldName + ")");
                        throw ex;
                    }
                }
            }
        } catch (NullPointerException ex) { // Si llegamos a un objeto que es
            // nulo, retornamos nulo
            auxo = null;
        }
        return auxo;
    }

    /**
     * Asigna un valor a un campo de un objeto
     *
     * @param o Objeto que se desea modificar
     * @param fieldName Nombre del campo a modificar
     * @param value Valor que se desea asignar
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static void setFieldValue(Object o, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException,
            InvocationTargetException {
        if (o == null) {
            throw new NullPointerException("El objeto no puede ser nulo");
        }
        fieldValueOf(o, fieldName, value, true);
    }

    /**
     * @param cl Clase de la que se desea obtener el campo
     * @param fieldName
     * @return Objeto de tipo Field
     * @throws NoSuchFieldException
     */
    public static Field fieldOf(Class cl, String fieldName)
            throws NoSuchFieldException, ClassNotFoundException {
        //String[] fields = fieldName.split("\\.");
        String[] fields = fieldName.split("\\.");
        Class claux = cl;
        Field field = null;
        for (int k = 0; k < fields.length; k++) {
            String aux = fields[k];
            field = null;
            while (field == null) {
                try {
                    Pattern p = Pattern.compile("\\[(.+)?\\]");
                    Matcher m = p.matcher(aux);
                    if (m.matches()) {
                        field = fieldOfWithCollection(claux, aux);
                    } else {
                        field = claux.getDeclaredField(aux);
                    }
                } catch (NoSuchFieldException e) {
                    if (claux.getSuperclass() == null) { // no está el campo
//                        logger.error("No existe el campo '" + aux + "'. Clase: '" + cl.getName() + " . fieldName: " + fieldName);
                        throw e;
                    }
                    claux = claux.getSuperclass();
                }
            }
            claux = field.getType();
        }
        return field;
    }

    /**
     * @param cl Clase de la que obtener el campo
     * @param fieldName Nombre del campo
     * @return Clase del campo llamado "fieldName"
     * @throws NoSuchFieldException
     */
    public static Class fieldClassOf(Class cl, String fieldName)
            throws NoSuchFieldException, ClassNotFoundException {
        return fieldOf(cl, fieldName).getType();
    }

    public static Field fieldOfWithCollection(Class cl, String fieldName)
            throws NoSuchFieldException, ClassNotFoundException {
        List fields = fieldsWithoutClases(fieldName);
        Class claux = cl;
        Field field = null;
        for (int k = 0; k < fields.size(); k++) {
            String aux = (String) fields.get(k);
            field = null;
            while (field == null) {
                try {
                    field = claux.getDeclaredField(aux);
                } catch (NoSuchFieldException e) {
                    if (claux.getSuperclass() == null) // no está el campo
                    {
                        throw e;
                    }
                    claux = claux.getSuperclass();
                }
            }
            claux = field.getType();
            if (Collection.class.isAssignableFrom(claux)) {
                List fieldsWithClass = fieldsWithClasses(fieldName);
                Pattern p = Pattern.compile("\\[(.+)?\\]");
                Matcher m = p.matcher((String) fieldsWithClass.get(k));
                if (m.find()) {
                    claux = Class.forName(m.group(1));
                }
            }
        }
        return field;
    }

    public static Class fieldClassOfWithCollection(Class cl, String fieldName)
            throws NoSuchFieldException, ClassNotFoundException {
        List fields = fieldsWithoutClases(fieldName);
        Class claux = cl;
        Field field = null;
        for (int k = 0; k < fields.size(); k++) {
            String aux = (String) fields.get(k);
            field = null;
            while (field == null) {
                try {
                    field = claux.getDeclaredField(aux);
                } catch (NoSuchFieldException e) {
                    if (claux.getSuperclass() == null) // no está el campo
                    {
                        throw e;
                    }
                    claux = claux.getSuperclass();
                }
            }
            claux = field.getType();
            if (Collection.class.isAssignableFrom(claux)) {
                List fieldsWithClass = fieldsWithClasses(fieldName);
                Pattern p = Pattern.compile("\\[(.+)?\\]");
                Matcher m = p.matcher((String) fieldsWithClass.get(k));
                if (m.find()) {
                    claux = Class.forName(m.group(1));
                }
            }
        }
        return claux;
    }

    /**
     * Indica si el campo existe
     *
     * @param cl Clase de la que se desea saber si el campo existe
     * @param fieldName Nombre del campo
     * @return Existencia del campo
     */
    public static boolean fieldExists(Class cl, String fieldName) throws ClassNotFoundException {
        try {
            fieldOf(cl, fieldName);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    /**
     * Comprueba si existen elementos de la clase cl en el conjunto
     *
     * @param coleccion Conjunto de elementos
     * @param cl Clase de la que se desea obtener si existe alguna instancia
     * @return Verdadero si en el conjunto existe algún elemento nulo o de la
     * clase cl
     */
    public static boolean containsClassInCollecction(Set coleccion, Class cl) {
        for (Iterator iter = coleccion.iterator(); iter.hasNext();) {
            Object elemento = iter.next();
            if (elemento == null) {
                return false;
            }
            if (elemento.getClass().isAssignableFrom(cl)) {
                return true;
            }
        }

        return false;
    }
}
