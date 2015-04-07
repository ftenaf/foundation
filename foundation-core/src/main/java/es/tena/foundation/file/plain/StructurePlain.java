package es.tena.foundation.file.plain;

import java.util.ArrayList;
import java.util.List;

/**
 * A plain structure is a composed by multiple fields 
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class StructurePlain {
    
    private List<Field> fields = new ArrayList<>();

    public List<Field> getFields() {
        return fields;
    }

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }
    
}
