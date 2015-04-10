package es.tena.foundation.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

/**
 * @author francisco.tena@gmail.com
 *
 */
public class StringUtil {

    static final public String DATE_FORMAT = "yyyyMMdd";
    static final public SimpleDateFormat sdfecha = new SimpleDateFormat("dd/MM/yyyy");
    static final public SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
    static final public String regExpCP = "(0[1-9]|5[0-2]|[0-4][0-9])[0-9]{3}";
    static final public String regExpNotNumbers = "^[a-zA-Z]";
    static final public String numbers = "0123456789";
    static final public String alphabetWithAccents = "ABCÇDEFGHIJKLMNÑOPQRSTUVWXYZabcçdefghijklmnñopqrstuvwxyzÁÉÍÓÚÀÈÌÒÌÙÄËÏÖÜÂÊÎÔÛáéíóúàèìòìäëïöüâêîôû";
    static final public String alphabetASCII = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    static final public String validNIFChars = "KLMklm";
    static final public String validNIEChars = "XYZxyz";
    static final public String nameChars = alphabetWithAccents+"-'";
    static final public String notNumbers = nameChars + ":;,./\\ºª!|#$%&¬()=?'¿¡^`*+[]¨´{}Çç_<>";
    static final public String emailChars = numbers + alphabetASCII + "@._-";
    static final public String notValidXML10 = "#x((10?|[2-F])FFF[EF]|FDD[0-9A-F]|7F|8[0-46-9A-F]9[0-9A-F])";
    static final public String notValidXML11 = "#x((10?|[2-F])FFF[EF]|FDD[0-9A-F]|[19][0-9A-F]|7F|8[0-46-9A-F]|0?[1-8BCEF])";
    static final public String MASK_DATE = "(201[1|2][0-1][0-9][0-3][0-9])";

    /**
     * Object to String or Zero
     *
     * @param object
     * @return String
     */
    public static String nullToZero(Object object) {
        if (nullToString(object).equals("")) {
            return "0";
        } else {
            return object.toString();
        }
    }

    /**
     * Checks if the string provided is 0.00
     *
     * @param objeto
     * @return
     */
    public static boolean isZero(String objeto) {
        return (objeto.equals("0.00"));
    }

    /**
     * String to upper case or "" if null or empty
     *
     * @param minuscula
     * @return
     */
    public static String LowerToUpper(String minuscula) {
        if (minuscula == null || minuscula.equals("")) {
            return "";
        } else {
            return minuscula.toUpperCase();
        }
    }

    /**
     * Checks if the Bigdecimal or double provided has 2 or more decimals
     *
     * @param amount
     * @return
     */
    public static boolean isScale(Object amount) {
        if (amount instanceof BigDecimal) {
            BigDecimal importeBigDecimal = (BigDecimal) amount;
            return importeBigDecimal.scale() > 2;
        } else {
            if (amount instanceof Float) {
                Float importeFloat = (Float) amount;
                return hasTwoOrMoreDecimals(importeFloat.toString());
            } else {
                if (amount instanceof Double) {
                    Double importeDouble = (Double) amount;
                    return hasTwoOrMoreDecimals(importeDouble.toString());
                } else {
                    return false;
                }
            }
        }
    }

    public static boolean hasTwoOrMoreDecimals(String amount) {
        return amount.substring(amount.indexOf(".") + 1).length() > 2;
    }

    /**
     * Converts a String with the format nnnn.d changing the ',' to a '.'
     *
     * @param amount
     * @return
     * @throws RuntimeException
     */
    public static String amountToOneDecimal(String amount) throws RuntimeException {

        int beginIndex = amount.indexOf(".");
        if (beginIndex == -1 || (amount.substring(beginIndex + 1).length()) <= 1) {
            amount = stringToOneDecimal(amount);
        } else {
            if (amount.substring(beginIndex + 1).length() > 1) {
                throw new RuntimeException("Amount must have only 1 decimal");
            }
        }
        return amount;
    }

    /**
     * Converts a String representing a decimal to a 1 decimal and a '.' decimal
     * separator instead of a ','
     *
     * @param amount
     * @return
     * @throws RuntimeException
     */
    public static String stringToOneDecimal(String amount)
            throws RuntimeException {
        DecimalFormat df2 = new DecimalFormat("####0.0");
        try {
            amount = df2.format(Double.parseDouble(amount));
            amount = amount.replace(",", ".");
        } catch (NumberFormatException e) {
            throw new RuntimeException("Format not supported (####0.0):" + amount);
        }
        return amount;
    }

    /**
     * Converts a String with the format nnnn.dd changing the ',' to a '.'
     *
     * @param amount
     * @return
     */
    public static String amountToTwoDecimals(String amount) throws RuntimeException {

        int beginIndex = amount.indexOf(".");
        if (beginIndex == -1 || (amount.substring(beginIndex + 1).length()) <= 2) {
            amount = stringToTwoDecimals(amount);
        } else {
            if (amount.substring(beginIndex + 1).length() > 2) {
                throw new RuntimeException("Amount must have only 1 decimal");
            }
        }
        return amount;
    }

    /**
     * Converts a String representing a decimal to a 2 decimals and a '.'
     * decimal separator instead of a ','
     *
     * @param amount
     * @return
     * @throws RuntimeException
     */
    public static String stringToTwoDecimals(String amount)
            throws RuntimeException {
        DecimalFormat df2 = new DecimalFormat("####0.00");
        try {
            amount = df2.format(Double.parseDouble(amount));
            amount = amount.replace(",", ".");
        } catch (NumberFormatException e) {
            throw new RuntimeException("Format not supported (####0.0):" + amount);
        }
        return amount;
    }

    /**
     * Converts a String amount with thousand '.' separator and decimals ',' and
     * EUR symbol appended
     *
     * @param amount
     * @return
     * @throws RuntimeException
     */
    public static String eurAmount(String amount)
            throws RuntimeException {

        DecimalFormatSymbols simbolos = new DecimalFormatSymbols();
        simbolos.setDecimalSeparator(',');
        simbolos.setPerMill('.');
        DecimalFormat df = new DecimalFormat("#,###,##0.00", simbolos);

        try {
            amount = df.format(Double.parseDouble(amount));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Format not supported (#,###,##0.00):" + amount);
        }
        return amount + "&euro;";
    }

    /**
     * True if it is integer
     *
     * @param number
     * @return
     */
    public static boolean isInteger(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * True if it is a number with decimals
     *
     * @param number
     * @return
     */
    public static boolean isNumberWithDecimals(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Object to String or null
     *
     * @param object
     * @return String
     */
    public static String nullToString(Object object) {
        if (object == null) {
            return "";
        } else {
            return object.toString();
        }
    }

    /**
     *
     * @param object
     * @return
     */
    public static String[] nullToArrayString(String[] object) {
        if (object == null) {
            return new String[]{};
        } else {
            return object;
        }
    }

    /**
     * String trimmed or "" if it is null
     *
     * @param object
     * @return
     */
    public static String nullToStringTrimmed(Object object) {
        if (object == null) {
            return "";
        } else {
            return object.toString().trim();
        }
    }

    /**
     * Get a key from a hashmap or "" if it is null
     *
     * @param parameters
     * @param key
     * @return
     */
    public static String getValue(Map<String, String> parameters, String key) {
        return (parameters != null && parameters.get(key) != null ? parameters.get(key) : "");
    }

    /**
     * Gets a String from an InputStream. You should close it or reset it after
     * using it.
     *
     * @param is
     * @param charset
     * @return
     * @throws IOException
     */
    public static String inputStreamToString(InputStream is, String charset) throws IOException {
        StringBuilder stringBuffer = new StringBuilder();
        InputStreamReader inputStreamReader = charset == null ? new InputStreamReader(is) : new InputStreamReader(is, charset);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = null;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuffer.append(line).append("\n");
        }
        return stringBuffer.toString();
    }

    /**
     * Gets a String from an InputStream. You should close it after using it.
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String inputStreamToString(InputStream is)
            throws IOException {
        return inputStreamToString(is, null);
    }

    /**
     * Gets a String from an InputStream. You should close it after using it.
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String inputStreamToStringUTF8(InputStream is)
            throws IOException {
        return inputStreamToString(is, "utf-8");
    }

    /**
     * Transform a Boolean to a string 'S' if true or 'N' if false 
     * @param bool
     * @return String
     */
    public static String BooleanToS_N(Boolean bool) {
        return bool?"S":"N";
    }



    /**
     * Transform a string to a Boolean true if 'S' or false if 'N' 
     * @param SN
     * @return 
     */
    public static Boolean S_NToBoolean(String SN) {
        return SN.equalsIgnoreCase("S");
    }

    /**
     * Finds a date into a string
     * @param str
     * @return 
     */
    public static String findDate(String str) {
        Pattern mask = Pattern.compile(MASK_DATE);
        Matcher matcher = mask.matcher(str);
        String fecha = null;
        while (matcher.find()) {
            fecha = matcher.group();
        }
        return fecha;
    }

    /**
     * Finds a spanish postal code into a string
     * @param value
     * @return
     */
    public static String getCodigoPostal(String value) {
        Pattern mask = Pattern.compile(regExpCP);
        Matcher matcher = mask.matcher(value);
        String cp = null;
        while (matcher.find()) {
            cp = matcher.group();
        }
        return cp;
    }

    /**
     * Left pad String
     * @param s String to pad
     * @param length new length after padding
     * @param padChar pad character
     * @return
     */
    public static String lpad(String s, int length, char padChar) {
        if (s == null) {
            return null;
        }

        if (s.length() >= length) {
            return s;
        }

        String prefix = "";
        for (int i = 0; i < length - s.length(); i++) {
            prefix += padChar;
        }

        return prefix + s;
    }

    /**
     * Right pad a string
     * @param s String to pad
     * @param length new length after padding
     * @param padChar pad character
     * @return
     */
    public static String rpad(String s, int length, char padChar) {
        if (s == null) {
            return null;
        }

        if (s.length() >= length) {
            return s;
        }

        String suffix = "";
        for (int i = 0; i < length - s.length(); i++) {
            suffix += padChar;
        }

        return s + suffix;
    }

    /**
     * n array coalesce
     * @param par
     * @return 
     */
    public static Object coalesce(Object... par) {
        for (Object o : par) {
            if (o != null) {
                return o;
            }
        }
        return null;
    }

    /**
     * 3 string coalesce
     * @param s1
     * @param s2
     * @param s3
     * @return 
     */
    public static String coalesce3String(String s1, String s2, String s3) {
        return (String) coalesce(s1, s2, s3);
    }

    /**
     * Gets the Boolean string converting null to 'false'
     * @param b
     * @return 
     */
    public static String getBoolean(Boolean b) {
        if (b != null) {
            return "'" + b + "'";
        } else {
            return "'false'";
        }
    }

    /**
     * Counts how many times a characters can be found in a string
     * @param sql
     * @param caracter
     * @return
     */
    public static int countOccurances(String sql, char caracter) {
        int cont = 0;
        char[] cs = sql.toCharArray();
        for (char c : cs) {
            if (c == caracter) {
                cont++;
            }
        }
        return cont;
    }

    /**
     * Deletes all non alphanumeric characters, accents removed too
     * @param ristra
     * @return la ristra sin dichos caracteres
     * @throws RuntimeException
     */
    public static String deleteNotAllowedChars(String ristra) throws RuntimeException {
        String ristraCorregida = "";
        if (ristra != null && !ristra.equals("")) {
            for (int i = 0; i < ristra.length(); i++) {
                if ((alphabetASCII + numbers).indexOf(ristra.charAt(i)) >= 0) {
                    ristraCorregida += ristra.charAt(i);
                }
            }
        }
        return ristraCorregida;
    }

    /**
     * Deletes all non alphanumeric characters with the space and '(' and ')'
     * @param ristra
     * @return la ristra sin dichos caracteres
     * @throws RuntimeException
     */
    public static String deleteNotAlphanumeric(String ristra) throws RuntimeException {
        String ristraCorregida = "";
        if (ristra != null && !ristra.equals("")) {
            for (int i = 0; i < ristra.length(); i++) {
                if ((alphabetWithAccents + numbers + " " + "(" + ")").indexOf(ristra.charAt(i)) >= 0) {
                    ristraCorregida += ristra.charAt(i);
                }
            }
        }
        return trim(ristraCorregida);
    }

    /**
     * Deletes every useless spaces at the start of the string
     * @param source
     * @return
     */
    public static String ltrim(String source) {
        return source.replaceAll("^\\s+", "");
    }

    /**
     * Deletes every useless spaces at the end of the string
     * @param source
     * @return
     */
    public static String rtrim(String source) {
        return source.replaceAll("\\s+$", "");
    }

    /**
     * Replaces the useless spaces between words leaving only one
     * @param source
     * @return
     */
    public static String itrim(String source) {
        return source.replaceAll("\\b\\s{2,}\\b", " ");
    }

    /**
     * Deletes useless blank spaces
     * @param source
     * @return
     */
    public static String trim(String source) {
        if (source != null) {
            return itrim(lrtrim(source));
        } else {
            return "";
        }
    }

    /**
     * Deletes useless blank spaces at the begining and the end of the string
     * @param source
     * @return
     */
    public static String lrtrim(String source) {
        return ltrim(rtrim(source));
    }

    /**
     * Deletes every non number character or '/' or '-'. Usefull for parsing dates
     * @param s
     * @return
     */
    public static String removeNotNumbersDate(String s) {
        return trim(s).replaceAll("[^0-9|\\+|/|-]", "");
    }

    /**
     * Deletes every non number character
     * @param s
     * @return
     */
    public static String removeNotNumbers(String s) {
        return trim(s).replaceAll("[^0-9|\\+]", "");
    }

    /**
     * Splits a string by the delimiter provided in a more efficient memory way
     * @param s
     * @param delimiter
     * @return 
     */
    public static List<String> split(String s, String delimiter) {
        List<String> list = new ArrayList<>();
        int pos = 0, end;
        while ((end = s.indexOf(delimiter, pos)) >= 0) {
            String r = s.substring(pos, end).trim().replace("'", "''");
            if (!r.isEmpty()) list.add(r);
            pos = end + 1;
        }
        list.add(s.substring(pos, s.length()).trim().replace("'", "''"));
        
        return list;
    }
    
    
    /**
     * Splits a string by the delimiter provided in a more efficient memory way
     * @param s string to be splitted by a delimiter
     * @param delimiter used to separate tokens
     * @param qualifier used in case a delimiter is used in a token
     * @return List of Strings with all the tokens splitted
     */
    public static List<String> split(String s, char delimiter, char qualifier) {
        List<String> list = new ArrayList<>();
        String token = "";
        boolean q = false;
        int i = 0;
        if (s.charAt(s.length()-1) != delimiter) s += delimiter;
        while (i < s.length()-1){
            char c = s.charAt(i);
            //if it's the begining of the string or a qualifier is preceeded by the delimiter, begins the token
            if ((isDelimiter(c, delimiter) || i == 0) && s.charAt(i+1) == qualifier){
                if (!token.equals("")){
                    list.add(token);
                    token = "";
                }
                token += s.charAt(i+2);
                i = i+3;
                q = true;
            }else if (q && c == qualifier && isDelimiter(s.charAt(i+1),delimiter)){
                list.add(token);
                token = "";
                i = i+3;
                q = false;
            }else if (!q && c == delimiter){
                list.add(token);
                token = "";
                i = i+1;
            }else{
                token += c;
                i++;
            }
        }
        
        return list;
    }
    
    private static boolean isDelimiter (char c, char delimiter){
        return c == delimiter;
    }
}
