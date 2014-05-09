package es.tena.foundation.util;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Francisco Tena <francisco.tena@gmail.com>
 */
public class SQLUtil {

    enum TipoSQL {

        UPDATE, SELECT, INSERT
    };

    /**
     *
     * @param param
     * @return
     */
    public static String toStringSQL(String param) {
        if (param == null) {
            return "'" + "" + "'";
        } else {
            return "'" + sqlQuote(param) + "'";
        }
    }

    public static String getSQLString(String value) {
        if (value != null && !value.toLowerCase().equals("null")) {
            return "'" + replace(value) + "'";
        } else {
            return null;
        }
    }

    //corregimos los & y las ' para que se ejecuten correctamente los sql
    public static String replace(String s) {
        if (s != null) {
            String s1 = s.replaceAll("'", "''");
            //        s1 = s1.replaceAll("\\", "\\\\");
            s1 = s1.replace("&", "'||'&'||'");
            return s1;
        } else {
            return s;
        }
    }

    /**
     * Converts a date to a Oracle String to_date
     *
     * @param fechaD
     * @return
     */
    public static String dateTo_Date(Date fechaD) {

        String fechaS = "TO_DATE('";
        fechaS += DateUtil.dateToStringWithSeconds(fechaD);
        fechaS += "', 'DD/MM/YYYY HH24:MI:SS')";

        return fechaS;
    }

    /**
     * Gets the resultSet size
     *
     * @param resultSet
     * @return
     */
    public static int getResultSetSize(ResultSet resultSet) {
        int size = -1;
        try {
            resultSet.last();
            size = resultSet.getRow();
            resultSet.beforeFirst();
        } catch (java.sql.SQLException e) {
            return size;
        }

        return size;
    }

    /**
     * Fix the scape character in a SQL parameter
     *
     * @param str parameter to be fixed
     * @return
     */
    public static String sqlQuote(String str) {
//		Codec ORACLE_CODEC = new OracleCodec();
//		ESAPI.encoder().encodeForSQL( ORACLE_CODEC, );
        StringBuilder sb = new StringBuilder();
        if (str == null || str.length() == 0
                || (!str.contains("\'") && !str.contains("&"))
                || (str.toUpperCase().contains("TO_DATE"))) {
            return str;
        } else {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) == '\'') {
                    sb.append('\'');
                }
                sb.append(str.charAt(i));
            }
        }
        return sb.toString();
    }

    /**
     * Gets all the parameter in the SQL statement provided
     *
     * @param sql
     * @return
     */
    public static String[] getParameters(String sql) {
        List<String> parametrosOut = new ArrayList<>();
        try {
            String[] parametros;
            parametros = sql.split(",");
            for (int i = 0; i < parametros.length; i++) {
                if (parametros[i].toUpperCase().contains("TO_DATE")) {
                    parametrosOut.add(parametros[i] + "," + parametros[i + 1]);
                    i++;
                } else {
                    parametrosOut.add(parametros[i]);
                }
            }
        } catch (Exception e) {
            return null;
        }
        String[] out = new String[parametrosOut.size()];
        parametrosOut.toArray(out);
        return out;
    }

    @Deprecated
    public static String getSQLNormalized(String sql) {
        // copia del inicio del sql hasta los parametros
        String sqlIni;
        String sqlFinal = "";
        String sqlFin = "";
        TipoSQL tipoSQL = TipoSQL.SELECT;

        // copia de sql en mayusculas
        String sqlAux = sql.toUpperCase();
        // contador hasta donde estan los parametros
        int ini = sqlAux.length();
        int fin = sqlAux.length();

        String keyword = "";

        if (sqlAux.startsWith("INSERT")) {
            tipoSQL = TipoSQL.INSERT;
            keyword = "VALUES";
        } else if (sqlAux.startsWith("UPDATE")) {
            tipoSQL = TipoSQL.UPDATE;
            keyword = "SET";
            fin = sql.toUpperCase().indexOf("WHERE");
        } else if (sqlAux.startsWith("SELECT")) {
            tipoSQL = TipoSQL.SELECT;
            keyword = "WHERE";
        }

        // copiamos desde el final de la palabra clave
        ini = sqlAux.indexOf(keyword) + keyword.length() + 1;

        // copiamos el inicio y el fin del sql sin modificar mayusculas
        sqlIni = sql.substring(0, ini);
        sqlFin = sql.substring(fin - 1);

        // almacenamos el resto para interpretarlo y corregir errores
        sqlAux = sql.substring(ini, fin);

        // obtenemos los parametros unicamente de la ristra cortada
        if (sqlAux.trim().startsWith("(")) {
            int posParentesisAbre = sqlAux.indexOf('(');
            int posParentesisCierra = sqlAux.lastIndexOf(')');
            sqlAux = sqlAux.substring(posParentesisAbre + 1,
                    posParentesisCierra);
        }
        // Si es un INSERT o un UPDATE comprobamos los parametros que se van a
        // insertar
        if (tipoSQL.equals(TipoSQL.INSERT) || tipoSQL.equals(TipoSQL.UPDATE)) {
            String[] parametros = SQLUtil.getParameters(sqlAux);
            for (int j = 0; j < parametros.length; j++) {
                if (tipoSQL.equals(TipoSQL.INSERT)) {
                    // si el valor comienza por una comilla simple lo revisamos
                    deleteQuotes(parametros[j]);
                    parametros[j] = SQLUtil.sqlQuote(parametros[j]);
                    // entrecomillamos el valor retocado
                    sqlFinal += "\"" + parametros[j] + "\", ";
                } else if (tipoSQL.equals(TipoSQL.UPDATE)) {
                    // la sintaxis del update es diferente asi que buscamos el
                    // "="
                    int igual = parametros[j].indexOf("=");
                    String valor = parametros[j].substring(igual + 1,
                            parametros[j].length()).trim();
                    // si el valor comienza por una comilla simple lo revisamos
                    if (valor != null && valor.length() > 0
                            && valor.startsWith("\'")) {
                        valor = valor.substring(1, valor.lastIndexOf("\'"));
                    }
                    parametros[j] = parametros[j].substring(0, igual + 1)
                            + "\"" + SQLUtil.sqlQuote(valor) + "\"";
                    sqlFinal += "" + parametros[j] + ", ";
                }
            }
        } else {
            // TODO: no funciona con selects
            if (tipoSQL.equals(TipoSQL.SELECT)) {
                sqlFinal += SQLUtil.sqlQuote(sqlAux) + ", ";
            }
        }
        if (tipoSQL.equals(TipoSQL.INSERT)) {
            sqlFinal = sqlIni + "("
                    + sqlFinal.substring(0, sqlFinal.length() - 2) + ")"
                    + sqlFin;
        } else {
            sqlFinal = sqlIni + sqlFinal.substring(0, sqlFinal.length() - 2)
                    + sqlFin;
        }

        return sqlFinal;
    }

    /**
     * Delete single quotes from the string provided
     * @param parameter
     * @return 
     */
    public static String deleteQuotes(String parameter) {
        if (parameter != null
                && parameter.trim().length() > 0
                && parameter.trim().startsWith("\'")) {
            parameter = parameter.trim().substring(1, parameter.trim().length() - 1);
        }
        return parameter;
    }

    /**
     * gets the TO_DATE Oracle function from a date (dd/mm/yyyy hh24:mi:ss) 
     * @param fecha
     * @return 
     */
    public static String getToDate(Date fecha) {
        if (fecha != null) {
            return "to_date('" + StringUtil.sdfecha.format(fecha) + "', 'dd/mm/yyyy hh24:mi:ss')";
        } else {
            return "null";
        }
    }
}
