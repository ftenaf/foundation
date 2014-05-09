package es.tena.foundation.persistence;

import org.hibernate.SessionFactory;

/**
 * 
 * @author francisco.tena@gmail.com
 */
public class SchemaUpdater {

    /**
     * We use this session factory to create our sessions
     */
    public static SessionFactory sessionFactory;

    public static void main(String[] args) {
        initialization();
    }

    /**
     * Loads the Hibernate configuration information, sets up the database and
     * the Hibernate session factory.
     */
    public static void initialization() {
        System.out.println("initialization");
        try {
            HibernateUtil.updateSchema();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
