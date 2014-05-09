package es.tena.foundation.util;

import es.tena.foundation.util.SQLUtil;
import static org.junit.Assert.fail;

import java.util.Date;

import es.tena.foundation.util.SQLUtil.TipoSQL;
import org.junit.Ignore;
import org.junit.Test;

public class TestSQLUtil {

	

	@Test
	@Ignore
	public void testToStringSQL() {
		fail("Not yet implemented");
	}

	@Test
	@Ignore
	public void testFechaToOracle() {
		fail("Not yet implemented");
	}

	@Test
	public void testSqlQuote() {
		TipoSQL tipoSQL = TipoSQL.UPDATE;
		Date date = new Date();
		String sql =  "UPDATE PublicacionReal SET " 
			+ "autores='Roger's father'"
			+ ", origen='Roger & Emma'"
			+ ", fecha="+ SQLUtil.dateTo_Date(date)
			+ ", origen2='Roger's & Emma'"
			+ " WHERE id=2";
		
		sql = "INSERT INTO PublicacionReal (id, costeEdicion, origen, idpubprev, origen2) " 
			+ "VALUES (1" 
			+ ",'Roger's father'"
			+ "," + SQLUtil.dateTo_Date(date)
			+ ",'Roger & Emma'"
			+ ",'Roger's & Emma')";
			
		sql = "SELECT to_number( to_char(to_date('1','J') "
				+ " (date1 - date2), 'J') - 1)  days, "
				+ " to_char(to_date('00:00:00','HH24:MI:SS') (date1 - date2), 'HH24:MI:SS') time"
				+ " FROM dates";

		sql = "SELECT name FROM emp " 
				+ " WHERE id LIKE '% ESCAPE \'";
		
		//copia de sql en mayusculas
		String sqlAux = sql.toUpperCase();
		//copia del inicio del sql hasta los parametros
		String sqlIni;
		String sqlFinal = "";
		String sqlFin = "";
		// contador hasta donde estan los parametros
		try {
			int ini = sqlAux.length();
			int fin = sqlAux.length();
			String keyword = "";
			if (sqlAux.startsWith("INSERT")){
				tipoSQL = TipoSQL.INSERT;
				keyword = "VALUES";
			}else if (sqlAux.startsWith("UPDATE")){
				tipoSQL = TipoSQL.UPDATE;
				keyword = "SET";
				fin = sql.toUpperCase().indexOf("WHERE");
			}else if (sqlAux.startsWith("SELECT")){
				tipoSQL = TipoSQL.SELECT;
				keyword = "WHERE";
			}
			
			//copiamos desde el final de la palabra clave
			ini = sqlAux.indexOf(keyword) + keyword.length()+1;
			
			//copiamos el inicio y el fin del sql sin modificar mayusculas
			sqlIni = sql.substring(0, ini);
			sqlFin = sql.substring(fin-1);
			
			//almacenamos el resto para interpretarlo y corregir errores
			sqlAux = sql.substring(ini, fin);
			
			//obtenemos los parametros unicamente de la ristra cortada
			if (sqlAux.trim().startsWith("(")){
				int posParentesisAbre = sqlAux.indexOf('(');
				int posParentesisCierra = sqlAux.lastIndexOf(')');
				sqlAux = sqlAux.substring(posParentesisAbre+1, posParentesisCierra);
			}
			// Si es un INSERT o un UPDATE comprobamos los parametros que se van a insertar
			if (tipoSQL.equals(TipoSQL.INSERT) || tipoSQL.equals(TipoSQL.UPDATE)){
				String[] parametros = SQLUtil.getParameters(sqlAux);
				for (int j = 0; j < parametros.length; j++) {
					if (tipoSQL.equals(TipoSQL.INSERT)){
						// si el valor comienza por una comilla simple lo revisamos
						if (parametros[j] != null && parametros[j].trim().length() > 0 
								&& parametros[j].trim().startsWith("\'")) {
							parametros[j] = parametros[j].trim().substring(1, parametros[j].trim().length()-1);
						}
						parametros[j] = SQLUtil.sqlQuote(parametros[j]);
						//entrecomillamos el valor retocado
						sqlFinal += "\""+ parametros[j] +"\", ";
					}else if (tipoSQL.equals(TipoSQL.UPDATE)){
						// la sintaxis del update es diferente asi que buscamos el "="
						int igual = parametros[j].indexOf("=");
						String valor = parametros[j].substring(igual+1, parametros[j].length()).trim();
						// si el valor comienza por una comilla simple lo revisamos
						if (valor != null && valor.length() > 0 
								&& valor.startsWith("\'")) {
							valor = valor.substring(1, valor.lastIndexOf("\'"));
						}
						parametros[j] = parametros[j].substring(0, igual+1) + "\""+ SQLUtil.sqlQuote(valor) + "\"";
						sqlFinal += ""+ parametros[j] +", ";
					}
				}
			}else if (tipoSQL.equals(TipoSQL.SELECT)){
				sqlFinal += SQLUtil.sqlQuote(sqlAux) +", ";
			}
			if (tipoSQL.equals(TipoSQL.INSERT)){
				sqlFinal = sqlIni + "(" + sqlFinal.substring(0, sqlFinal.length()-2) + ")"+ sqlFin;
			}else{
				sqlFinal = sqlIni + sqlFinal.substring(0, sqlFinal.length()-2) + sqlFin;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println(sqlFinal);
	}

	@Test
	@Ignore
	public void testGetParameters() {
		Date date = new Date();
		String sql = "UPDATE PublicacionReal SET " 
			+ " autores='Roger's father'"
			+ ", origen='Roger & Emma'"
			+ ", fecha="+ SQLUtil.dateTo_Date(date)
			+ ", origen2='Roger's & Emma'"
			+ " WHERE id=2";
		
		
		
		sql = "INSERT INTO PublicacionReal (id, costeEdicion, origen, idpubprev, origen2) " 
				+ "VALUES (1," 
				+ ",'Roger's father'"
				+ "," + SQLUtil.dateTo_Date(date)
				+ ",'Roger & Emma'"
				+ "'Roger's & Emma')";
		
		fail("Not yet implemented");
	}

}
