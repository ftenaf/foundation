package es.tena.foundation.util;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

/**
 * 
 * @author Francisco Tena<francisco.tena@gmail.com>
 */
public class AbstractIntEnumeration implements Serializable {

    protected int value;

    protected AbstractIntEnumeration() {
        value = 0;
    }

    protected AbstractIntEnumeration(int value) {
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
        if (!(o instanceof AbstractIntEnumeration)) {
            return false;
        }
        AbstractIntEnumeration aux = (AbstractIntEnumeration) o;
        return getValue() == aux.getValue();
    }

    @Override
    public int hashCode() {
        return ("" + getValue()).hashCode();
    }

    @Override
    public String toString() {
        return getValue() + "";
    }

    /**
     * @return Returns the valor.
     */
    public int getValue() {
        return value;
    }

    public static AbstractIntEnumeration parse(Class cl, int v) {
        Class aux = cl;
        while (AbstractIntEnumeration.class.isAssignableFrom(aux)) {
            Field[] fields = aux.getDeclaredFields();
            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                field.setAccessible(true);
                if (AbstractIntEnumeration.class.isAssignableFrom(field.getType())
                        && (field.getModifiers() & (Modifier.STATIC
                        | Modifier.FINAL | Modifier.PUBLIC)) != 0) {
                    try {
                        AbstractIntEnumeration ae = (AbstractIntEnumeration) field
                                .get(null);
                        if (ae.getValue() == v) {
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
                + v + "' in the class '" + cl.getName() + "'");
    }

    public AbstractIntEnumeration parse(int v) {
        return AbstractIntEnumeration.parse(this.getClass(), v);
    }

}
