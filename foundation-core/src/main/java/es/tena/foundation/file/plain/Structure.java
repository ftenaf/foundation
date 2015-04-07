package es.tena.foundation.file.plain;

import java.util.ArrayList;
import java.util.List;

/**
 * An structure is made of multiple Blocks with different lines. This is usefull
 * when it's not a plain structure but a complex structure with different
 * structures
 *
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class Structure {

    List<Block> bloques;

    public Structure() {
        this.bloques = new ArrayList<>();
    }

    public List<Block> getBloques() {
        return bloques;
    }

    public void setBloques(List<Block> bloques) {
        this.bloques = bloques;
    }
    
    

}
