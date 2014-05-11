package es.tena.foundation.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 *
 * @author Francisco Tena<francisco.tena@gmail.com>
 */
public abstract class AbstractEnumeration implements Serializable {

    protected String value;

    protected AbstractEnumeration() {
        value = null;
    }

    protected AbstractEnumeration(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (super.equals(o)) {
            return true;
        }
        if (!(o instanceof AbstractEnumeration)) {
            return false;
        }
        AbstractEnumeration aux = (AbstractEnumeration) o;
        if (getValue() == null) {
            return false;
        }
        return getValue().equals(aux.getValue());
    }

    @Override
    public int hashCode() {
        if (getValue() == null) {
            return 0;
        }
        return getValue().hashCode();
    }

    @Override
    public String toString() {
        return getValue();
    }

    /**
     * @return Returns the valor.
     */
    public String getValue() {
        return value;
    }

    public static AbstractEnumeration parse(Class cl, String s) {
        Class aux = cl;
        while (AbstractEnumeration.class.isAssignableFrom(aux)) {
            Field[] fields = aux.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                if (AbstractEnumeration.class.isAssignableFrom(field.getType())
                        && (field.getModifiers()
                        & (Modifier.STATIC
                        | Modifier.FINAL
                        | Modifier.PUBLIC)) != 0) {
                    try {
                        AbstractEnumeration ae = (AbstractEnumeration) field.get(null);
                        if (ae.getValue().equals(s)) {
                            return ae;
                        }
                    } catch (IllegalAccessException | IllegalArgumentException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            aux = aux.getSuperclass();
        }
        throw new RuntimeException("A declared public static final must exists: '"
                + s + "' in the class '" + cl.getName() + "'");
    }

    public AbstractEnumeration parse(String s) {
        return AbstractEnumeration.parse(this.getClass(), s);
    }

}
