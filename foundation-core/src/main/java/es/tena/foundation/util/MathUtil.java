/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.tena.foundation.util;

import java.math.BigDecimal;
import java.util.Random;

/**
 *
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class MathUtil {

    /**
     * * Round a double value to a specified number of decimal places.
     *
     * @param val the value to be rounded.
     * @param places the number of decimal places to round to.
     * @return val rounded to places decimal places.
     */
    public static double round(double val, int places) {
        long factor = (long) Math.pow(10, places);

        // Shift the decimal the correct number of places to the right.
        val = val * factor;

        // Round to the nearest integer.
        long tmp = Math.round(val);

        // Shift the decimal the correct number of places back to the left.
        return (double) tmp / factor;
    }

    /**
     * Round a float value to a specified number of decimal places.
     *
     * @param val the value to be rounded.
     * @param places the number of decimal places to round to.
     * @return val rounded to places decimal places.
     */
    public static float round(float val, int places) {
        return (float) round((double) val, places);
    }

    

    /**
     * Checks if the number provided is null or zero
     *
     * @param object
     * @return boolean
     */
    public static boolean isNullOrZero(Number object) {

        Number numero = 0;
        if (object instanceof Long) {
            numero = 0L;
        } else {
            if (object instanceof Float) {
                numero = 0f;
            } else {
                if (object instanceof Double) {
                    numero = 0d;
                }
            }
        }

        if (object instanceof BigDecimal) {
            BigDecimal numeroBigDecimal = (BigDecimal) object;
            return ((numeroBigDecimal == null || numeroBigDecimal.compareTo(BigDecimal.ZERO) == 0));
        } else {
            return ((object == null || object.equals(numero)));
        }
    }
    
    

    /**
     * tardamos en comer entre 1 y 2 horas
     * @return
     */
    public static int getDelayAlmuerzo(){
        Random r = new Random();
        return 3600000 + r.nextInt(3600000);
    }


    /**
     * tardamos entre 15 y 20 minutos en desayunar
     * @return
     */
    public static int getDelayCafelito(){
        Random r = new Random();
        return 900000 + r.nextInt(600000);
    }
    /**
     * tardamos entre 15 y 30 minutos en desayunar
     * @return
     */
    public static int getDelayDesayuno(){
        Random r = new Random();
        return 900000 + r.nextInt(900000);
    }

    /**
     * Devolvemos un aleatorio entre las 10 y las 11
     * @return
     */
    public static int getHoraCafelito(){
        Random r = new Random();
        return 16 + r.nextInt(1);
    }

    /**
     * Devolvemos un aleatorio entre las 00 y las 59
     * @return
     */
    public static int getMinutoCafelito(){
        Random r = new Random();
        return r.nextInt(59);
    }

    /**
     * Devolvemos un aleatorio entre las 10 y las 11
     * @return
     */
    public static int getHoraDesayuno(){
        Random r = new Random();
        return 10 + r.nextInt(1);
    }

    /**
     * Devolvemos un aleatorio entre las 00 y las 59
     * @return
     */
    public static int getMinutoDesayuno(){
        Random r = new Random();
        return r.nextInt(59);
    }

}
