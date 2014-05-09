package es.tena.foundation.util;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Strings, validators and general String utilities
 *
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class StringValidator extends StringUtil {

    static Pattern MASK_EMAIL = Pattern.compile("^[\\w\\-\\_\\+]+(\\.[\\w\\-\\_]+)*@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}$");
    static Pattern MASK_TLF = Pattern.compile("((\\+|00)?([0-9]{9}[0-9]?[0-9]?[0-9]?[0-9]?[0-9]?[0-9]?))$");
    static Pattern MASK_CP = Pattern.compile("^([1-9]{2}|[0-9][1-9]|[1-9][0-9])[0-9]{3}$");
    static Pattern MASK_ADDRESS_FORMAT = Pattern.compile("^([0-9]?[0-9]?[0-9]?[0-9][A-Za-z]?)|(S\\s*/\\s*N)$");
    static Pattern MASK_ADDRESS_SN = Pattern.compile("^S\\s*/\\s*N$");
    static Pattern MASK_ADDRESS_NUMBER = Pattern.compile("[0-9]?[0-9]?[0-9]?[0-9]");
    
    private static final Pattern cifPattern = Pattern.compile("[[A-H][J-N][P-S]UVW][0-9]{7}[0-9A-J]");
    private static final String CONTROL_SOLO_NUMEROS = "ABEH"; // Sólo admiten números como caracter de control
    private static final String CONTROL_SOLO_LETRAS = "KPQS"; // Sólo admiten letras como caracter de control
    private static final String CONTROL_NUMERO_A_LETRA = "JABCDEFGHI"; // Conversión de dígito a letra de control.

    /**
     * Comprueba Nif valido inicialmente y en caso de error lo reintenta con la
     * validacion del CIF
     *
     * @param nif
     * @throws RuntimeException
     */
    public static void checkNIFoCIF(String nif) throws RuntimeException {
        try {
            //checkCIF(nif);
            checkCIF_LeyJulio2008(nif);
        } catch (RuntimeException e) {
            //checkNIF(nif);
            checkNIF_LeyJulio2008(nif);
        }
    }

    /**
     * Comprueba que el NIF es válido de acuerdo a la ley publicada en julio de
     * 2008
     *
     * @param nif
     * @throws RuntimeException
     */
    public static void checkNIF_LeyJulio2008(String nif) throws RuntimeException {
        checkInString(nif, StringUtil.alphabetASCII + StringUtil.numbers);
        checkSpaces(nif);
        checkMaxLength(nif, 9);
        checkInicioValidoNif(nif.substring(0, 1), validNIFChars + validNIEChars + numbers);
        String letra = "" + nif.charAt(nif.length() - 1);
        String numero = nif.substring(0, nif.length() - 1);
        if (!numbers.contains(numero.substring(0, 1))) {
            // Entonces el primer carácter del NIF es una letra
            String letraInicialNif = nif.substring(0, 1);
            //if(letraInicialNif.toUpperCase().equals("Y")){
            if (!validNIEChars.contains(letraInicialNif.toUpperCase())) {
                numero = nif.substring(1, nif.length() - 1);
            } else {
                numero = validNIEChars.indexOf(letraInicialNif.toUpperCase()) + nif.substring(1, nif.length() - 1);
            }

        }

        checkInString(numero, numbers);
        checkInString(letra, alphabetASCII);
        int index = Integer.parseInt(numero) % 23;

        String[] letrasNIF = {"T", "R", "W", "A", "G", "M", "Y", "F", "P",
            "D", "X", "B", "N", "J", "Z", "S", "Q", "V", "H", "L", "C",
            "K", "E", "T"};
        if (!letra.toUpperCase().equals(letrasNIF[index])) {
            throw new RuntimeException("Letra no válida del NIF:" + numero
                    + " " + letra);
        }
    }

    /**
     * Comprueba que el CIF es válido de acuerdo a la ley publicada en julio de
     * 2008
     *
     * @param cif
     * @throws ValidationException
     */
    public static void checkCIF_LeyJulio2008(String cif) throws RuntimeException {
        try {
            if (!cifPattern.matcher(cif).matches()) {
                // No cumple el patrón
                throw new RuntimeException("El CIF no cumple el patrón:" + cif);
            }

            int parA = 0;
            for (int i = 2; i < 8; i += 2) {
                final int digito = Character.digit(cif.charAt(i), 10);
                if (digito < 0) {
                    throw new RuntimeException("El digito del CIF el < 0:" + cif + "[" + digito + "]");
                }
                parA += digito;
            }

            int nonB = 0;
            for (int i = 1; i < 9; i += 2) {
                final int digito = Character.digit(cif.charAt(i), 10);
                if (digito < 0) {
                    throw new RuntimeException("El digito del CIF el < 0:" + cif + "[" + digito + "]");
                }
                int nn = 2 * digito;
                if (nn > 9) {
                    nn = 1 + (nn - 10);
                }
                nonB += nn;
            }

            final int parcialC = parA + nonB;
            final int digitoE = parcialC % 10;
            final int digitoD = (digitoE > 0)
                    ? (10 - digitoE)
                    : 0;
            final char letraIni = cif.charAt(0);
            final char caracterFin = cif.charAt(8);

            final boolean esControlValido = // ¿el caracter de control es válido como letra?
                    (CONTROL_SOLO_NUMEROS.indexOf(letraIni) < 0 
                    && CONTROL_NUMERO_A_LETRA.charAt(digitoD) == caracterFin)
                    || // ¿el caracter de control es válido como dígito?
                    (CONTROL_SOLO_LETRAS.indexOf(letraIni) < 0
                    && digitoD == Character.digit(caracterFin, 10));
            if (!esControlValido) {
                throw new RuntimeException("El CIF no es válido:" + cif);
            }

        } catch (RuntimeException e) {
            throw new RuntimeException("El CIF no es válido:" + cif);
        }
    }

    /**
     * Comprueba que el comienzo del NIF es numérico o una letra válida
     *
     * @param ristra
     * @param cadenaPermitida
     * @throws RuntimeException
     */
    public static void checkInicioValidoNif(String ristra, String cadenaPermitida)
            throws RuntimeException {
        if (ristra != null && !ristra.equals("")) {
            for (int i = 0; i < ristra.length(); i++) {
                if (cadenaPermitida.indexOf(ristra.charAt(i)) < 0) {
                    throw new RuntimeException("El campo no es correcto ");
                }
            }
        }

    }

    /**
     * Comprueba que el NIF es valido
     *
     * @param nif
     * @throws RuntimeException
     */
    public static void checkNIF(String nif) throws RuntimeException {
        checkInString(nif, alphabetASCII + numbers);
        checkSpaces(nif);
        checkMaxLength(nif, 9);
        String letra = "" + nif.charAt(nif.length() - 1);
        String numero = nif.substring(0, nif.length() - 1);
        checkInString(numero, numbers);
        checkInString(letra, alphabetASCII);
        int index = Integer.parseInt(numero) % 23;
        String[] letrasNIF = {"T", "R", "W", "A", "G", "M", "Y", "F", "P",
            "D", "X", "B", "N", "J", "Z", "S", "Q", "V", "H", "L", "C",
            "K", "E", "T"};
        if (!letra.toUpperCase().equals(letrasNIF[index])) {
            throw new RuntimeException("Letra no válida del NIF:" + numero
                    + " " + letra);
        }
    }

    /**
     * Elimina caracteres no permitidos
     *
     * @param ristra
     * @return la ristra sin dichos caracteres
     * @throws RuntimeException
     */
    public static String deleteNotAllowedChars(String ristra)
            throws RuntimeException {
        String ristraCorregida = "";
        if (ristra != null && !ristra.equals("")) {
            for (int i = 0; i < ristra.length(); i++) {
                if ((alphabetASCII + numbers).indexOf(ristra
                        .charAt(i)) >= 0) {
                    ristraCorregida += ristra.charAt(i);
                }
            }
        }
        return ristraCorregida;
    }

    /**
     * comprueba que la ristra de entrada contiene unicamente caracteres de la
     * cadena permitida
     *
     * @param ristra
     * @param cadenaPermitida
     * @throws RuntimeException
     */
    public static void checkInString(String ristra, String cadenaPermitida) throws RuntimeException {
        if (ristra != null && !ristra.equals("")) {
            for (int i = 0; i < ristra.length(); i++) {
                if (cadenaPermitida.indexOf(ristra.charAt(i)) < 0) {
                    throw new RuntimeException("El campo no es correcto ");
                }
            }
        }
    }

    /**
     * comprueba que la ristra de entrada tiene una longitud minima mayor o
     * igual al parametro min
     *
     * @param ristra
     * @param min
     * @throws RuntimeException
     */
    public static void checkMinLength(String ristra, int min)
            throws RuntimeException {
        if (ristra == null || ristra.length() < min) {
            throw new RuntimeException("El campo no tiene la longitud mínima");
        }
    }

    /**
     * Comprueba que la ristra de entrada no supera una longitud maxima menor o
     * igual al parametro max
     *
     * @param ristra
     * @param max
     * @throws RuntimeException
     */
    public static void checkMaxLength(String ristra, int max)
            throws RuntimeException {
        if (ristra == null || ristra.length() > max) {
            throw new RuntimeException("El campo excede la longitud máxima");
        }
    }

    /**
     * Comprueba que no haya espacios en la ristra de entrada
     *
     * @param ristra
     * @throws RuntimeException
     */
    public static void checkSpaces(String ristra)
            throws RuntimeException {
        if (ristra == null || ristra.indexOf(" ") > 0) {
            throw new RuntimeException("El campo contiene espacios");
        }
    }

    /**
     * Comprueba que el CIF es valido
     *
     * @param cif
     * @throws RuntimeException
     */
    public static void checkCIF(String cif) throws RuntimeException {
        checkInString(cif, alphabetASCII + numbers);
        checkSpaces(cif);
        checkMinLength(cif, 9);
        boolean respuesta = false;
        //Tabla de caracter de control para cif extranjeros, organismos
        // estatales y locales
        char letrasCIF[] = {'J', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
        // la "J" debe estar en la ultima posición pero como
        //las tablas en java cominezan en '0' la paso a la primera posicion
        int sumap = 0;
        int sumai = 0;
        int p;
        int R;
        int dc;

        cif = cif.toUpperCase();
        try {
            sumap = Integer.parseInt(cif.substring(2, 3))
                    + Integer.parseInt(cif.substring(4, 5))
                    + Integer.parseInt(cif.substring(6, 7));
            for (int i = 1; i <= 8; i++) {
                p = 2 * Integer.parseInt(cif.substring(i, i + 1));
                if (p > 9) {
                    sumai += (p / 10) + (p % 10);
                } else {
                    sumai += p;
                }
                i++;
            }
            R = sumap + sumai;
            //R es el resultado de las sumas de los productos
            dc = R % 10;
            //calculamos el digito de control que es el modulo de
            // la suma de los productos
            dc = 10 - dc;//complemento a 10 del digito de control
            if (dc == 10) {//si el digito de control es 10 se le asigna el 0
                dc = 0;
            }

            if (Character.isLetter(cif.charAt(8))) {
                //si es un caracter el ultimo digito se compara
                //con la tabla que debe ocupar la posicion del dc
                if (letrasCIF[dc] == cif.charAt(8)) {
                    respuesta = true;
                }
            } else {
                //si no es el caracter de contro que debe coincidir con el
                // ultimo digito
                if (dc == Integer.parseInt(cif.substring(8, 9))) {
                    respuesta = true;
                }
            }
        } catch (ArithmeticException e) {
            System.out.println("Division por cero");
            respuesta = false;
        }
        if (!respuesta) {
            throw new RuntimeException("Caracter final no válido en el CIF"
                    + cif);
        }
    }


    /**
     * Comprueba que el número sigue el patrón marcado por el INE: número de
     * cuatro cifras máximo más una letra opcional
     *
     * @param number número a comprobar
     * @return true si es correcto
     */
    public static boolean isValidAddressNumber(String number) {
        if (number == null) {
            return false;
        }
        Matcher matcher = MASK_ADDRESS_FORMAT.matcher(number.trim().toUpperCase());
        return matcher.matches();
    }

    /**
     * Comprueba si está marcado como S/N
     *
     * @param number valor a comprobar
     * @return true si es un S/N
     */
    public static boolean isAddressSn(String number) {
        if (number == null) {
            return false;
        }
        Matcher matcher = MASK_ADDRESS_SN.matcher(number.trim().toUpperCase());
        return matcher.matches();
    }

    /**
     * Comprueba si un caracter es un dígito
     *
     * @param c caracter a comprobar
     * @return true si c es dígito
     */
    public static boolean isDigit(char c) {
        return numbers.contains("" + c);
    }

    /**
     * Descubre si un número (en formato INE) es impar
     *
     * @param number número a estudiar. Debe estar previamente validado que es
     * un número de cuatro cifras máximo más una letra opcional
     * @return true si es impar
     */
    public static boolean isOddAddressNumber(String number) {
        if (number == null || number.trim().equals("")) {
            return false;
        }

        number = getIneFormattedAddressNumber(number);

        char c = number.charAt(3);

        return c == '1' || c == '3' || c == '5' || c == '7' || c == '9';
    }

    /**
     * Descubre si un número (en formato INE) es par
     *
     * @param number número a estudiar. Debe estar previamente validado que es
     * un número de cuatro cifras máximo más una letra opcional
     * @return true si es par
     */
    public static boolean isEvenAddressNumber(String number) {
        if (number == null || number.trim().equals("")) {
            return false;
        }

        number = getIneFormattedAddressNumber(number);

        char c = number.charAt(3);

        return c == '0' || c == '2' || c == '4' || c == '6' || c == '8';
    }

    /**
     * Devuelve un número tal y como se especifica en los ficheros de tramo del
     * INE
     *
     * @param number cadena representando el fichero. Debe estar previamente
     * validado que es un número de cuatro cifras máximo más una letra opcional
     * @return número en formato INE (0000X, en mayúsculas)
     */
    public static String getIneFormattedAddressNumber(String number) {
        if (number == null || number.trim().equals("")) {
            return null;
        }
        if (isDigit(number.charAt(number.length() - 1))) { //no contiene letra final
            return StringUtil.lpad(number, 4, '0');
        } else {
            return StringUtil.lpad(number, 5, '0').toUpperCase();
        }
    }

    /**
     * Busca la existencia de un segmento que incluya el valor indicado.
     *
     * @param segments los segmentos en formato INE
     * aaaa-bbbb,cccc-dddd,eeee-ffff,... No es necesario que estén ordenados.
     * @param value valor a comprobar. Debe estar previamente validado que es un
     * número de cuatro cifras máximo más una letra opcional
     * @return true si el valor está dentro del rango de algún segmento.
     */
    public static boolean isAddressNumberInSegments(String segments, String value) {
        if (segments == null || segments.isEmpty()) {
            return false;
        }
        if (value == null || value.trim().equals("")) {
            return false;
        }

        value = getIneFormattedAddressNumber(value);

        String[] tramos = segments.split(",");
        for (String tramo : tramos) {
            String[] extremos = tramo.split("-");
            if (value.compareTo(extremos[0]) >= 0 && value.compareTo(extremos[1]) <= 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Corrige algunos cif que por razones aun desconocidas comienzan por numero
     * o tiene estructuras extrañas curiosamente una vez eliminados estos
     * caracteres suele ser válido el cif/nif
     *
     * @param cif
     * @return
     */
    public static String fixCIF(String cif) {
        cif = deleteNotAllowedChars(cif);
        //Algunos comienzan por 0x0 cuando deberia ser X
        if (cif.startsWith("0X0")) {
            cif = "X" + cif.substring(3);
        } else if (cif.startsWith("0X")) {
            cif = "X" + cif.substring(2);
        } //FIXME: QUITAR ELSE, ES NECESARIO SOLO PARA CORREGIR UN ERROR DE SIGES CON ESTOS CIFS
        else if (cif.startsWith("X0")) {
            cif = "X" + cif.substring(2);
        }
        Pattern mask = Pattern.compile("^[0-9]([A-Z])(.*)");
        Matcher matcher = mask.matcher(cif);
        String cifFixed = "";
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                cifFixed += matcher.group(i);
            }
        }

        return cifFixed.equals("") ? cif : cifFixed;
    }

    public static String fixCIFSinLetra(String cif) {
        cif = fixCIF(cif);

        Pattern mask = Pattern.compile("^[0-9](.*)");
        Matcher matcher = mask.matcher(cif);
        String cifFixed = "";
        while (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                cifFixed += matcher.group(i);
            }
        }
        return cifFixed.equals("") ? cif : cifFixed;
    }

    public static void checkCCC(String ccc) {
        if (ccc == null) {
            throw new RuntimeException("CCC nulo");
        }

        ccc.replaceAll(" ", "");

        if (ccc.length() != 20) {
            throw new RuntimeException("Longitud incorrecta para CCC: " + ccc);
        }

        String entidad = ccc.substring(0, 4);
        String oficina = ccc.substring(4, 8);
        String dc = ccc.substring(8, 10);
        String numero = ccc.substring(10);

        if (!dc.equals(getDigitoControlCCC(entidad, oficina, numero))) {
            throw new RuntimeException("DC incorrecto");
        }
    }

    public static boolean isValidCCC(String ccc) {
        try {
            checkCCC(ccc);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Calcula el dígito control de un CCC
     *
     * @param entidad
     * @param oficina
     * @param numero
     * @return dígito control o null si no es posible calcularlo
     */
    public static String getDigitoControlCCC(String entidad, String oficina, String numero) {
        if (entidad == null || oficina == null || numero == null) {
            return null;
        }

        if ((entidad + oficina + numero).length() != 18) {
            return null;
        }

        int pesos[] = {6, 3, 7, 9, 10, 5, 8, 4, 2, 1};

        String cadena = "";
        int nResto = 0;
        try {
            for (int i = 0; i < (entidad + oficina).length(); i++) {
                nResto += Integer.parseInt("" + (entidad + oficina).charAt(i)) * pesos[7 - i];
            }

            nResto = 11 - (nResto % 11);
            if (nResto == 11) {
                nResto = 0;
            } else if (nResto == 10) {
                nResto = 1;
            }
            cadena += nResto;

            nResto = 0;
            for (int i = 0; i < numero.length(); i++) {
                nResto += Integer.parseInt("" + numero.charAt(i)) * pesos[9 - i];
            }

            nResto = 11 - (nResto % 11);
            if (nResto == 11) {
                nResto = 0;
            } else if (nResto == 10) {
                nResto = 1;
            }
            cadena += nResto;
            return cadena;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * comprueba que la ristra de entrada contiene unicamente caracteres de la
     * cadena permitida
     *
     * @param ristra
     * @param cadenaPermitida
     * @param encoding
     * @throws ValidationException
     */
    public static void checkInString(String ristra, String cadenaPermitida, String encoding) {
        boolean ok = true;
        char fallo = 0;
        if (ristra != null && !ristra.equals("")) {
            for (int i = 0; i < ristra.length(); i++) {
                if (cadenaPermitida.indexOf(ristra.charAt(i)) < 0) {
                    ok = false;
                    fallo = ristra.charAt(i);
                }
            }
            if (!ok) {
                try {
                    byte[] bs = ristra.getBytes(encoding);
                    for (int j = 0; j < bs.length; j++) {
                        byte c = bs[j];
                        Logger.getLogger(StringValidator.class.getName()).log(Level.FINE, "\t\t Caracter erroneo en ''{3}'': [{0}]={1}; byte:[{2}]\n", new Object[]{j, (char) c, c, ristra});
                    }
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(StringValidator.class.getName()).log(Level.SEVERE, null, ex);
                }
                throw new RuntimeException("El campo no es correcto [" + fallo + "];");
            }
        }
    }

    /**
     * Devuelve true si el telefono es válido
     *
     * @param tlfno
     * @return
     */
    public static Boolean isValidTelephone(String tlfno) {
        tlfno = tlfno.replaceAll(" ", "");
        tlfno = tlfno.replaceAll("\\(", "");
        tlfno = tlfno.replaceAll("\\)", "");
        tlfno = tlfno.replaceAll("\\-", "");
        tlfno = tlfno.replaceAll("\\.", "");
        Matcher matcher = MASK_TLF.matcher(tlfno);
        return matcher.matches();
    }

    /**
     * Devuelve true si el codigo postal es válido
     *
     * @param codigoPostal
     * @return
     */
    public static Boolean isValidCP(String codigoPostal) {
        codigoPostal = lpad(codigoPostal.trim(), 5, '0');
        Matcher matcher = MASK_CP.matcher(codigoPostal);
        return matcher.matches();
    }

    /**
     * Devuelve true si el telefono corregido es válido
     *
     * @param tlfno
     * @return
     */
    public static Boolean isValidTelephoneCorrected(String tlfno) {
        return isValidTelephone(getValidTelephoneCorrected(tlfno));
    }

    /**
     * Devuelve true si el codigo postal corregido es válido
     *
     * @param codigoPostal
     * @return
     */
    public static Boolean isValidCPCorrected(String codigoPostal) {
        return isValidCP(getValidCPCorrected(codigoPostal));
    }

    /**
     * Devuelve el telefono corregido es válido
     *
     * @param tlfno
     * @return
     */
    public static String getValidTelephoneCorrected(String tlfno) {
        return removeNotNumbers(tlfno);
    }

    /**
     * Devuelve el codigo postal corregido es válido (elimina los caracteres no
     * numericos)
     *
     * @param codigoPostal
     * @return
     */
    public static String getValidCPCorrected(String codigoPostal) {
        return removeNotNumbers(codigoPostal);
    }


    /**
     * Devuelve el email corregido es válido
     *
     * @param email
     * @return
     */
    public static String getValidEmailCorrected(String email) {
        return email.trim();
    }

    /**
     * Comprueba Nif valido inicialmente y en caso de error lo reintenta con la
     * validacion del CIF
     *
     * @param id
     * @param nif a comprobar
     * @return
     */
    public static boolean isValidNifCif(String id) {
        try {
            checkNIFoCIF(id);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Comprueba que es un CIF válido segun la ley de julio 2008
     *
     * @param cif
     * @return true si es valido
     */
    public static boolean isValidCIF(String cif) {
        if (cif != null && trim(cif).length() > 0) {
            try {
                checkCIF_LeyJulio2008(cif);
                return true;
            } catch (RuntimeException e) {
                return false;
            }
        } else {
            return false;
        }
    }
}
