package es.tena.foundation.file.plain;

import java.util.Objects;

/**
 * Every "column" is represented by a field 
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class Field {
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
    
    public String getValueFixed() {
        String valueFixed = value;
        if (valueFixed.contains(",") && valueFixed.contains(".")){
            valueFixed = valueFixed.replace(".", "");
            valueFixed = valueFixed.replace(",", ".");
        }else if (valueFixed.contains(",")) {
            valueFixed = valueFixed.replace(",", ".");
        }
        valueFixed = valueFixed.replace(" ", "");
        valueFixed = valueFixed.replaceAll("[a-zA-Z]", "");
//        String valueFixed = value.replace(".", "");
//        valueFixed = valueFixed.replace(",", ".");
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
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return true;
    }   

    @Override
    public String toString() {
        return name +"["+ type +"] = '"+ value +"'";
    }

    
}
