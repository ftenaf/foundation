package es.tena.foundation.file.plain;

import java.util.ArrayList;
import java.util.List;

/**
 * A block represents a starting line and an ending line, it has one header (a number of fields, his line number and a separator). We will use a name to identify it later.
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class Block {
    String name;
    int startLineNumber;
    int endLineNumber;    
    Line header;
    List<Line> lines = new ArrayList<>();

    public Block(String name, int startLine, int endLine, List<Field> fields) {
        this.name = name;
        this.startLineNumber = startLine;
        this.endLineNumber = endLine;
        this.header = new Line(fields);
    }
    
    public Block(String name, int startLine, int endLine) {
        this.name = name;
        this.startLineNumber = startLine;
        this.endLineNumber = endLine;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public int getStartLineNumber() {
        return startLineNumber;
    }

    public void setStartLineNumber(int startLineNumber) {
        this.startLineNumber = startLineNumber;
    }

    public int getEndLineNumber() {
        return endLineNumber;
    }

    public void setEndLineNumber(int endLineNumber) {
        this.endLineNumber = endLineNumber;
    }

    public List<Line> getLines() {
        return lines;
    }

    public void setLines(List<Line> lines) {
        this.lines = lines;
    }

    public Line getHeader() {
        return header;
    }

    public void setHeader(Line header) {
        this.header = header;
    }

    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(name);
        sb.append("-Start[").append(startLineNumber).append("]-End[").append(endLineNumber).append("]\r\n");
        for (Line line : lines) {
            sb.append(line.toString());
        }
        return sb.toString();
    }
}
