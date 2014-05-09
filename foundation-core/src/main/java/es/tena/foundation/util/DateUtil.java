package es.tena.foundation.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

/**
 *
 * @author francisco.tena@gmail.com
 */
public class DateUtil {

    /**
     * Transforms a String date (dd/MM/yyyy) into a calendar using the specified separator 
     * @param strDate date (dd/MM/yyyy) 
     * @param separator date separator ('/') 
     * @return corresponding calendar
     * @throws RuntimeException 
     */
    static public Calendar getFecha(String strDate, String separator) throws RuntimeException {
        try {
            getFormattedDate(strDate, separator);
            StringTokenizer st = new StringTokenizer(strDate, separator);
            int dia = 0, mes = 1, anio = 0;
            if (st.hasMoreElements()) {
                dia = Integer.parseInt((String) st.nextElement());
            }
            if (st.hasMoreElements()) {
                mes = Integer.parseInt((String) st.nextElement()) - 1;
            }
            // El mes empieza en 0 en Java
            if (st.hasMoreElements()) {
                anio = Integer.parseInt((String) st.nextElement());
            }
            Calendar cal = Calendar.getInstance();
            cal.set(anio, mes, dia);
            return cal;
        } catch (NumberFormatException | ParseException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Checks if the start date is before the end date
     * @param startDate in dd/MM/yyyy format 
     * @param endDate in dd/MM/yyyy format 
     * @return true if the startDate is before the endDate
     * @throws ParseException 
     */
    public static boolean before(String startDate, String endDate) throws ParseException {
        Date d1 = getDate(getFormattedDate(startDate, null));
        Date d2 = getDate(getFormattedDate(endDate, null));
        return d1.before(d2);
    }

    /**
     * Checks if the start date is before or equal the end date exactly
     * @param startDate
     * @param endDate
     * @return Checks if the start date is before or equal the end date
     * @throws ParseException 
     */
    public static boolean beforeOrEqualExactly(Calendar startDate, Calendar endDate) throws ParseException {
        return (startDate.before(endDate) || startDate.equals(endDate));
    }

    
    /**
     * Checks if the start date is before or equal the end date without taking into account hours, minutes or seconds
     * @param startDate
     * @param endDate
     * @return Checks if the start date is before or equal the end date
     * @throws ParseException 
     */
    public static boolean beforeOrEqual(Calendar startDate, Calendar endDate) throws ParseException {
        return (getJustDate(startDate).before(getJustDate(endDate)) || getJustDate(startDate).equals(getJustDate(endDate)));
    }

    
    /**
     * Initializes the hour, minute, second and milliseconds of date provided to 0
     * @param date 
     */
    public static void justDate(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Returns a copy of date (new Calendar) but with hour, minute, second and milliseconds of date provided to 0
     * @param date
     * @return 
     */
    public static Calendar getJustDate(Calendar date) {
        Calendar fechaAuxiliar = Calendar.getInstance();
        fechaAuxiliar.setTime(date.getTime());
        justDate(fechaAuxiliar);
        return fechaAuxiliar;
    }

    /**
     * Checks if the start date is before the end date without taking into account hours, minutes or seconds
     * @param startDate
     * @param endDate
     * @return true if the start date is before or equal the end date
     * @throws ParseException 
     */
    public static boolean before(Calendar startDate, Calendar endDate) throws ParseException {
        return (getJustDate(startDate).before(getJustDate(endDate)));

    }

    /**
     * Checks if the start date is before or equal the end date
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException 
     */
    public static boolean beforeFull(Calendar startDate, Calendar endDate) throws ParseException {
        return (startDate.before(endDate));

    }

    /**
     * Checks if the start date is after or equal the end date
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException 
     */
    public static boolean afterFull(Calendar startDate, Calendar endDate) throws ParseException {
        return (startDate.after(endDate));

    }

    /**
     *
     * Checks if the start date is after the end date without taking into account hours, minutes or seconds
     *
     * @param startDate
     * @param endDate
     * @return true if the start date is after the end date without taking into account hours, minutes or seconds
     * @throws ParseException
     */
    public static boolean after(Calendar startDate, Calendar endDate) throws ParseException {
        return (getJustDate(startDate).after(getJustDate(endDate)));
    }

    /**
     * Checks if the start date is after the end date
     * @param startDate in dd/MM/yyyy format 
     * @param endDate in dd/MM/yyyy format 
     * @return true if the startDate is after the endDate
     * @throws ParseException 
     */
    public static boolean after(String startDate, String endDate) throws ParseException {
        Date d1 = getDate(getFormattedDate(startDate, null));
        Date d2 = getDate(getFormattedDate(endDate, null));
        return d1.after(d2);
    }

    /**
     * Checks if the start date is equal the end date
     * @param startDate in dd/MM/yyyy format 
     * @param endDate in dd/MM/yyyy format 
     * @return true if the startDate is equal the endDate
     * @throws ParseException
     */
    public static boolean isEqual(String startDate, String endDate) throws ParseException {
        Date d1 = getDate(getFormattedDate(startDate, null));
        Date d2 = getDate(getFormattedDate(endDate, null));
        return d1.equals(d2);
    }

    /**
     * Converts a Date into a dd/MM/yyyy H:m:s formatted string
     * @param date formatted string  dd/MM/yyyy H:m:s 
     * @return 
     */
    public static String dateToStringWithSeconds(Date date) {
        String dateStr = "";
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy H:m:s");
        if (date != null) {
            dateStr = formato.format(date);
        }
        return dateStr;
    }

    /**
     * Converts a Date into a dd/MM/yyyy formatted string
     * @param date
     * @return 
     */
    public static String dateToString(Date date) {
        String dateStr = "";
        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        if (date != null) {
            dateStr = formato.format(date);
        }
        return dateStr;
    }

    /**
     * Converts a formatted String (dd/MM/yyyy) into a Date
     * @param date
     * @return
     * @throws ParseException 
     */
    public static Date fechaToDate(String date) throws ParseException {
        return getDate(getFormattedDate(date, null));
    }

    /**
     * Get a string date (dd/MM/yyyy) in text format
     * @param date string date (dd/MM/yyyy) 
     * @return
     * @throws RuntimeException 
     */
    static public String getDateTexto(String date) throws RuntimeException {
        try {
            String fechaTexto = "";
            String meses[] = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
            String separador = "/";
            StringTokenizer st = new StringTokenizer(date, separador);
            int dia = 0, mes = 1, anio = 0;
            if (st.hasMoreElements()) {
                dia = Integer.parseInt((String) st.nextElement());
            }
            if (st.hasMoreElements()) {
                mes = Integer.parseInt((String) st.nextElement()) - 1;
            }
            // El mes empieza en 0 en Java
            if (st.hasMoreElements()) {
                anio = Integer.parseInt((String) st.nextElement());
            }
            fechaTexto = dia + " de " + meses[mes] + " de " + anio;
            return fechaTexto;
        } catch (NumberFormatException e) {
            throw new RuntimeException();
        }
    }
    
    
    /**
     * Get a string date (dd/MM/yyyy) in text format
     * @param date string date (dd/MM/yyyy) 
     * @return
     * @throws RuntimeException 
     */
    static public String getDateText(String date) throws RuntimeException {
        try {
            String fechaTexto = "";
            String meses[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            String separador = "/";
            StringTokenizer st = new StringTokenizer(date, separador);
            int dia = 0, mes = 1, anio = 0;
            if (st.hasMoreElements()) {
                dia = Integer.parseInt((String) st.nextElement());
            }
            if (st.hasMoreElements()) {
                mes = Integer.parseInt((String) st.nextElement()) - 1;
            }
            if (st.hasMoreElements()) {
                anio = Integer.parseInt((String) st.nextElement());
            }
            fechaTexto = dia + " de " + meses[mes] + " de " + anio;
            return fechaTexto;
        } catch (NumberFormatException e) {
            throw new RuntimeException();
        }
    }

    
    /**
     * Add the number of months to a String formatted date
     * @param date string formatted date
     * @param separator date separator ('/')
     * @param numMonths number of months to add
     * @return
     * @throws RuntimeException 
     */
    static public Calendar addDateMonths(String date, String separator, int numMonths) throws RuntimeException {
        try {
            Calendar cal = getFecha(date, separator);
            cal.add(Calendar.MONTH, numMonths);
            return cal;
        } catch (RuntimeException e) {
            throw new RuntimeException();
        }
    }

    /**
     * Calculates the number of days between 2 dates
     * @param startDate
     * @param endDate
     * @return 
     */
    public static int getDays(Date startDate, Date endDate) {
        // only natural day needed, hours, minutes, seconds and milliseconds removed
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM);
        String fechaInicioString = df.format(startDate);
        String fechaFinalString = df.format(endDate);
        try {
            startDate = df.parse(fechaInicioString);
            endDate = df.parse(fechaFinalString);
        } catch (ParseException e) {
            return 0;
        }

        long fechaInicialMs = startDate.getTime();
        long fechaFinalMs = endDate.getTime();
        long diferencia = fechaFinalMs - fechaInicialMs;
        double dias = Math.floor(diferencia / (1000 * 60 * 60 * 24));
        return ((int) dias);
    }

    /**
     * Calculates the number of months between 2 dates
     * @param startDate
     * @param endDate
     * @return 
     */
    public static int getMonths(Date startDate, Date endDate) {
        Integer numDias = getDays(startDate, endDate);
        Integer numMeses = numDias / 30;
        Integer restoDias = numDias % 30;
        if (restoDias >= 15) {
            numMeses += 1;
        }
        return numMeses;
    }

    
    /**
     * @deprecated 
     * Formats a date to dd/mm/yyyy
     * @param date
     * @param delimitator default ('/')
     * @return formatted string dd/mm/yyyy
     * @throws ParseException 
     */
    private static String getFormattedDate(String dateIn, String delimitator) throws ParseException {
        String[] fecha = dateIn.split(delimitator != null ? delimitator : "\\/");
        Calendar cal = new GregorianCalendar();
        if (dateIn != null && !dateIn.trim().equals("") && fecha.length == 3
                && (fecha[0].length() == 2 || fecha[0].length() == 1)
                && (fecha[1].length() == 2 || fecha[1].length() == 1)
                && fecha[2].length() == 4) {

            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            String fechaDia = fecha[0];
            String fechaMes = fecha[1];
            String fechaAno = fecha[2];
            if (fechaDia.length() == 1) {
                fechaDia = "0" + fechaDia;
            }
            if (fechaMes.length() == 1) {
                fechaMes = "0" + fechaMes;
            }
            try {
                if (Integer.parseInt(fechaDia) <= 0 || Integer.parseInt(fechaMes) <= 0 || Integer.parseInt(fechaAno) <= 0) {
                    throw new ParseException("Format not supported", 0);
                }
                Integer.parseInt(fechaDia);
                Integer.parseInt(fechaMes);
                Integer.parseInt(fechaAno);
            } catch (NumberFormatException e) {
                throw new ParseException("Format not supported", 0);
            }
            if (fechaDia.length() == 1) {
                fechaDia = "0" + fechaDia;
            }
            if (fechaMes.length() == 1) {
                fechaMes = "0" + fechaMes;
            }
            dateIn = fechaDia + "/" + fechaMes + "/" + fechaAno;
            try {
                cal = formato.getCalendar();
                cal.setLenient(false);
                Date date = (Date) formato.parse(dateIn);
            } catch (ParseException e) {
                throw new ParseException("Not valid date. Format not supported use DD/MM/YYYY. " + dateIn, 0);
            }
        } else if (dateIn == null || dateIn.trim().equals("")) {
            return null;
        } else {
            throw new ParseException("Format not supported", 0);
        }
        return dateIn;
    }



    /**
     * Checks if the date provided is valid 
     * @param date
     * @return true if is valid, false in other case (null or empty too)
     */
    public static boolean isValidDate(String date) {

        try {
            if (date == null || date.trim().equals("")) {
                return false;
            } else {
                getDate(getFormattedDate(date, null));
            }
        } catch (ParseException e) {
            return false;
        }
        return true;
    }

    /**
     * Return a date from a formatted string dd/MM/yyyyy
     * @param fechaS
     * @return
     * @throws ParseException 
     */
    private static Date getDate(String fechaS) throws ParseException {
        if (fechaS != null) {
            SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
            formato.setLenient(false);
            return formato.parse(fechaS);
        } else {
            return null;
        }
    }

    /**
     * Return the Year of a formatted string
     * @param date
     * @param delimitator default ('/')
     * @return the year of a formatted string
     */
    public static String getYear(String date, String delimitator) {
        String[] fecha = date.split(delimitator != null ? delimitator : "\\/");
        return fecha[2];
    }
}
