package es.tena.foundation.file.plain;

import java.util.Locale;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * Every "column" is represented by a field 
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class Field {
    private static final Logger logger = Logger.getLogger(Field.class.getSimpleName());
    
    String name;
    DataType type = DataType.ALPHANUMERIC;
    boolean included = true;
    boolean mandatory = false;
    int position;
    int length;
    boolean isVariableLength;
    String startChar;
    int startPosition;
    String value;
    Locale locale;
    
    public Field(String name, DataType type) {
        this.name = name;
        this.type = type;
        position = 0;
        length = 0;
    }
    
    public Field(String name) {
        this.name = name;
        position = 0;
        length = 0;
    }

    public Field(String name, DataType type, int position, int length, boolean mandatory) {
        this.name = name;
        this.type = type;
        this.position = position;
        this.length = length;
        this.mandatory = mandatory;
    }
    
    public Field(String name, DataType type, int position, int length, boolean mandatory, String value) {
        this.name = name;
        this.type = type;
        this.position = position;
        this.length = length;
        this.mandatory = mandatory;
        this.value = value;
    }

    public Field(String name, DataType type, int position, int length, boolean isVariableLength, String startChar, int startPosition) {
        this.name = name;
        this.type = type;
        this.position = position;
        this.length = length;
        this.isVariableLength = isVariableLength;
        this.startChar = startChar;
        this.startPosition = startPosition;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DataType getType() {
        return type;
    }

    public void setType(DataType type) {
        this.type = type;
    }

    public boolean isIncluded() {
        return included;
    }

    public void setIncluded(boolean included) {
        this.included = included;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public boolean isIsVariableLength() {
        return isVariableLength;
    }

    public void setIsVariableLength(boolean isVariableLength) {
        this.isVariableLength = isVariableLength;
    }

    public String getStartChar() {
        return startChar;
    }

    public void setStartChar(String startChar) {
        this.startChar = startChar;
    }

    public int getStartPosition() {
        return startPosition;
    }

    public void setStartPosition(int startPosition) {
        this.startPosition = startPosition;
    }

    public String getValue() {
        return value;
    }

    /**
     * gets the locale defined for this field, default US
     * @return 
     */
    public Locale getLocale() {
        return (locale == null? Locale.US:locale);
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
    
    
    
    public String getValueFixed() {
        String valueFixed = value;
        if (getType().equals(DataType.CURRENCY)){
//            try {
//                valueFixed = NumberFormat.getNumberInstance(getLocale()).parse(value).toString();
//            } catch (ParseException ex) {
//                logger.log(Level.SEVERE, "Error al procesar el importe: " + value, ex);
//            }
        }
        return valueFixed;
    }


    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Field other = (Field) obj;
        return Objects.equals(this.name, other.name);
    }   

    @Override
    public String toString() {
        return name +"["+ type +"] = '"+ value +"'";
    }

    
}
