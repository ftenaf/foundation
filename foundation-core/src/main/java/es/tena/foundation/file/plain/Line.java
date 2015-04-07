package es.tena.foundation.file.plain;

import java.util.List;

/**
 * Every "row" is represented as a Line which has multiple Fields separated by a separator
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class Line {
    List<Field> fields;
    int line;
    String separator = ";";

    public Line(List<Field> fields, int line) {
        this.fields = fields;
        this.line = line;
    }
    
    public Line(List<Field> fields) {
        this.fields = fields;
    }

    public Line(List<Field> fields, String separator) {
        this.fields = fields;
        this.separator = separator;
    }       

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> campos) {
        this.fields = campos;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Field field : fields) {
            sb.append(field).append(separator);
        }
        return sb.toString();
    }

    
}
