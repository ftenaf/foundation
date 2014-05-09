/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.tena.foundation.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.ArrayUtils;

/**
 *
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class POIUtil {

    // ø no hace falta escaparlo pero es sospechoso                             && c != 61 && c != 72
    // parche para el POI que no es capaz de tratar con algunos bytes y genera errores al abrir el documento Excel 61 =, 63 ?, 62 H
    //(byte) -62
    static public byte[] bytesNotAllowed = {(byte) -65,  (byte) 16, (byte) 18, (byte) 24, (byte) 25, (byte) 127};

    static final String DATE_FORMAT = "yyyyMMdd";
    static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    static final SimpleDateFormat sdfecha = new SimpleDateFormat("dd/MM/yyyy");
    static final Calendar calendario = Calendar.getInstance();


    public static String getCelda(HSSFCell cellSugg) {
        String suggestion = "";
        if (cellSugg != null) {
            if (cellSugg.getCellType() == HSSFCell.CELL_TYPE_STRING) {
                suggestion = StringUtil.trim(StringEscapeUtils.escapeSql(cellSugg.getStringCellValue()));
            } else if (cellSugg.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
                BigDecimal big = new BigDecimal(cellSugg.getNumericCellValue());
                suggestion = big.toString();
            } // no hace falta else
            else {
                suggestion = StringUtil.trim(StringEscapeUtils.escapeSql(cellSugg.getStringCellValue()));
            }
            suggestion = SQLUtil.replace(suggestion);
        }
        return suggestion;
    }

    public static void generateXLS(String tabla, String filename, Connection conn, String encoding) throws SQLException {
        String query = "";
        try {
            query = "SELECT * FROM (" + tabla + ")";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rset = stmt.executeQuery();

            XSSFWorkbook wb = new XSSFWorkbook();
            XSSFSheet sheet = wb.createSheet(filename);
            String sheetRef = sheet.getPackagePart().getPartName().getName();
            String template = "c:\\temp\\template_" + filename + ".xlsx";
            FileOutputStream os = new FileOutputStream(template);
            wb.write(os);
            os.close();

            File tmp = File.createTempFile("sheet", ".xml");
            Writer fw = new OutputStreamWriter(new FileOutputStream(tmp), encoding);
            generate(fw, rset, encoding);
            rset.close();
            stmt.close();
            fw.close();

            FileOutputStream out = new FileOutputStream("c:\\temp\\" + filename + sdf.format(calendario.getTime()) + ".xlsx");
            FileUtil.substitute(new File(template), tmp, sheetRef.substring(1), out);
            out.close();
            Logger.getLogger(POIUtil.class.getName()).log(Level.INFO, "Creado con exito {0}", filename);
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(POIUtil.class.getName()).log(Level.SEVERE, null, query + "\n" + ex);
            System.out.println(query);
        } finally {
            conn.close();
        }
    }

    private static void generate(Writer out, ResultSet rset, String encoding) throws Exception {
        SimpleDateFormat sdfecha = new SimpleDateFormat("dd/MM/yyyy");
        SpreadSheetWriter sw = new SpreadSheetWriter(out);
        sw.beginSheet();
        ResultSetMetaData rmd = rset.getMetaData();

        HashMap<Integer, String> columnas = new HashMap<Integer, String>();
        //creamos la cabecera del excel
        sw.insertRow(0);
        int numeroColumnas = rmd.getColumnCount();
        for (int numeroColumna = 1; numeroColumna <= numeroColumnas; numeroColumna++) {
            String nombreColumna = rmd.getColumnName(numeroColumna).toUpperCase();
            columnas.put(numeroColumna, rmd.getColumnTypeName(numeroColumna));
            sw.createCell(numeroColumna - 1, nombreColumna);
        }

        sw.endRow();

        //creamos los datos del excel
        int rowNum = 1;
        while (rset.next()) {
            sw.insertRow(rowNum);
            for (int columna = 1; columna <= numeroColumnas; columna++) {
                if (columnas.get(columna).equals("CHAR") || columnas.get(columna).equals("VARCHAR2")) {
                    String valor = "";
                    valor = rset.getString(columna);
                    valor = fixPOICellValue(valor, encoding);
                    if (valor == null || valor.toLowerCase().equals("null")) {
                        valor = "";
                    }
                    sw.createCell(columna - 1, valor);
                } else if (columnas.get(columna).equals("DATE")) {
                    Date fecha = new Date();
                    fecha = rset.getDate(columna);
                    String valor = (fecha != null) ? sdfecha.format(fecha) : null;
                    if (valor == null || valor.toLowerCase().equals("null")) {
                        valor = "";
                    }
                    sw.createCell(columna - 1, valor);
                } else if (columnas.get(columna).equals("NUMBER")) {
                    String valor = "";
                    valor = rset.getString(columna);
                    if (valor == null || valor.toLowerCase().equals("null")) {
                        valor = "";
                    }
                    sw.createCell(columna - 1, valor);
                } else {
                    String valor = "";
                    valor = rset.getString(columna);
                    valor = fixPOICellValue(valor, encoding);
                    if (valor == null || valor.toLowerCase().equals("null")) {
                        valor = "";
                    }
                    sw.createCell(columna - 1, valor);
                }
            }
            sw.endRow();
            rowNum++;
        }
        sw.endSheet();
    }

    /**
     * Corrige algunos problemas con los caracteres inválidos al generar
     * ficheros grandes con el SpreadSheetWriter de POI
     *
     * @param valor
     * @return la ristra corregida escapando carateres no validos para un xml
     * asi como eliminando aquellos que no están en el juego de caracteres
     */
    public static String fixPOICellValue(String valor, String encoding) {
        try {
            //Poi para los ficheros xml se hace un lio con los &
            // y los considera entidades, para escaparlos deberia bastar con && pero no es asi

            byte[] bs = valor.getBytes(encoding);

            StringBuilder buff = new StringBuilder();
            if (contieneCaracteresNoPermitidos(bs)) {
                for (int i = 0; i < bs.length; i++) {
                    byte c = bs[i];
//                    Logger.getLogger(Utils.class.getName()).log(Level.INFO, "\t\t byte: [{0}]-{1}-{2}-\n", new Object[]{i, (char)c , c});
                    // substituyo caracteres extraños
                    if (c == 24 || c == 127) {
                        c = 32;// los espacios extraños por el nbsp
                    }
                    // si no es un caracter no permitido lo añadimos
                    if (!ArrayUtils.contains(bytesNotAllowed, c)) {
                        buff.append((char) c);
                    } else {
                        Logger.getLogger(POIUtil.class.getName()).log(Level.INFO, "\t\t ESCAPADO de ''{3}'' byte : [{0}]-{1}-''{2}''\n", new Object[]{i, c, (char) c, valor});
                    }
                }
                valor = buff.toString();
            } else {
                try {
                    StringValidator.checkInString(valor, StringUtil.notNumbers + StringUtil.numbers + " " + "@" + "\"" + "€" + "\r" + "\n" + "\t");
                } catch (RuntimeException e) {
                    Logger.getLogger(POIUtil.class.getName()).log(Level.FINE, "\t\t SOSPECHOSO [{0}]:{1}\n", new Object[]{valor, e.getMessage()});
                }
            }
            // Escapamos los caracteres conflictivos para que sean validos para el xml
            valor = StringEscapeUtils.escapeXml(valor);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(POIUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NullPointerException e) {
        }
        return valor;
    }

    /**
     * Devuelve si en el array de bytes se encuentra algún valor no permitido
     *
     * @param bs
     * @return
     */
    private static boolean contieneCaracteresNoPermitidos(byte[] bs) {
        for (byte b : bytesNotAllowed) {
            if (ArrayUtils.contains(bs, b)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Crea un map con los estilos para cada tipo de datos
     *
     * @param wb
     * @return un hashMap con las claves 'porcentaje', 'coeficiente', 'moneda',
     * 'fecha' y 'cabecera'
     */
    public static Map<String, XSSFCellStyle> createStyles(XSSFWorkbook wb) {
        Map<String, XSSFCellStyle> styles = new HashMap<String, XSSFCellStyle>();
        XSSFDataFormat fmt = wb.createDataFormat();

        XSSFCellStyle style1 = wb.createCellStyle();
        style1.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
        style1.setDataFormat(fmt.getFormat("0.0%"));
        styles.put("porcentaje", style1);

        XSSFCellStyle style2 = wb.createCellStyle();
        style2.setAlignment(XSSFCellStyle.ALIGN_CENTER);
        style2.setDataFormat(fmt.getFormat("0.0X"));
        styles.put("coeficiente", style2);

        XSSFCellStyle style3 = wb.createCellStyle();
        style3.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
        style3.setDataFormat(fmt.getFormat("€#,##0.00"));
        styles.put("moneda", style3);

        XSSFCellStyle style4 = wb.createCellStyle();
        style4.setAlignment(XSSFCellStyle.ALIGN_RIGHT);
        style4.setDataFormat(fmt.getFormat("mmm dd"));
        styles.put("fecha", style4);

        XSSFCellStyle style5 = wb.createCellStyle();
        XSSFFont headerFont = wb.createFont();
        headerFont.setBold(true);
        style5.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style5.setFillPattern(XSSFCellStyle.SOLID_FOREGROUND);
        style5.setFont(headerFont);
        styles.put("cabecera", style5);

        return styles;
    }
}
