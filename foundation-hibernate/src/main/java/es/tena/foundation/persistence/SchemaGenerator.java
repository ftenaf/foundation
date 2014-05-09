package es.tena.foundation.persistence;

/**
 * 
* @author francisco.tena@gmail.com
 */
public class SchemaGenerator {

	/**
         * Recreates the schema droping the existing
	 * @param args
	 */
	public static void main(String[] args) {
		HibernateUtil.recreateSchema();

	}
	
        /**
         * Shows SQL DDL script to generate the schema
         * @param args 
         */
	public static void showSql(String[] args){
		HibernateUtil.sqlSchema();
	}

}
