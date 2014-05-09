package es.tena.foundation.persistence;

import javax.persistence.PersistenceException;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

/**
 * Class used to manage Hibernate instances
 *
 * @author francisco.tena@gmail.com
 */
public abstract class HibernateUtil {

    // private final static Class<?> mappedClasses[];
    public final static ThreadLocal<Session> threadSession = new ThreadLocal<>();
    private final static Logger log = Logger.getLogger(HibernateUtil.class);
    private final static SessionFactory SESSION_FACTORY;
    private final static AnnotationConfiguration cfg = new AnnotationConfiguration();
    private static final ThreadLocal<Transaction> threadTransaction = new ThreadLocal<>();

    private HibernateUtil() {
    }

    /**
     * Getting configuration using annotations
     * @return 
     */
/*    public static synchronized Configuration getCfg() {
        if (cfg == null) {
            AnnotationConfiguration annoCfg = new AnnotationConfiguration();
            for (Class<?> c : mappedClasses) {
                annoCfg = annoCfg.addAnnotatedClass(c);
            }
            cfg = annoCfg.configure();
        }
        return cfg;
    }
*/

    static {
        try {
            log.debug("HibernateUtil.static - loading config");
            SESSION_FACTORY = getCfg().configure().buildSessionFactory();
            log.debug("HibernateUtil.static - end");
        } catch (HibernateException ex) {
            throw new RuntimeException("Exception building SessionFactory: "
                    + ex.getMessage(), ex);
        }
    }

    public static AnnotationConfiguration getCfg() {
        return cfg;
    }

    /**
     * Gets a cleared session
     *
     * @return
     * @throws HibernateException
     */
    public static Session initSession() throws HibernateException {
        Session s = getSession();
        s.clear();
        return s;
    }

    /**
     * Gets the session for the current thread. Consumers must return the session by using {@link #closeSession() closeSession()}.
     * @return Hibernate Session for the current thread.
     * @throws HibernateException 
     */
    public static Session getSession() throws HibernateException {
        Session s = threadSession.get();
        if (s == null) {
            s = SESSION_FACTORY.openSession();
            threadSession.set(s);
        }
        return s;
    }

    /**
     * close the current session. Must be called after {@link #getSession() currentSession()}.
     * @throws PersistenceException 
     */
    public static void closeSession() throws PersistenceException {
        try {
            Session s = (Session) threadSession.get();
            threadSession.set(null);
            if (s != null && s.isOpen()) {
                log.debug("Closing Session of this thread.");
                s.close();
            }
        } catch (HibernateException ex) {
            throw new PersistenceException(ex.getMessage());
        }
    }

    /**
     * Commit and close the transaction if it's possible, otherwise it rollback the transaction.
     *
     * @param tx
     */
    public static void closeTransaction(Transaction tx) {
        try {
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new RuntimeException(e.getMessage());
        } finally {
            HibernateUtil.closeSession();
        }
    }

    
    /**
     * Creates a new transaction for the current thread getting a cleared session
     * @throws PersistenceException 
     */
    public static void beginTransaction() throws PersistenceException {
        Transaction tx = (Transaction) threadTransaction.get();
        try {
            if (tx == null) {
                log.debug("Starting new database transaction in this thread.");
                tx = initSession().beginTransaction();
                threadTransaction.set(tx);
            }
        } catch (HibernateException ex) {
            throw new PersistenceException(ex.getMessage());
        }
    }

    
    /**
     * Commits the current thread transaction
     * @throws PersistenceException 
     */
    public static void commitTransaction() throws PersistenceException {
        Transaction tx = (Transaction) threadTransaction.get();
        try {
            if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
                log.debug("Committing database transaction of this thread.");
                tx.commit();
            }
            threadTransaction.set(null);
        } catch (HibernateException ex) {
            rollbackTransaction();
            throw new PersistenceException(ex.getMessage());
        } finally {
            closeSession();
        }
    }

    /**
     * Rollback the current thread transaction
     * @throws PersistenceException 
     */
    public static void rollbackTransaction() throws PersistenceException {
        Transaction tx = (Transaction) threadTransaction.get();
        try {
            threadTransaction.set(null);
            if (tx != null && !tx.wasCommitted() && !tx.wasRolledBack()) {
                log.debug("Tyring to rollback database transaction of this thread.");
                tx.rollback();
            }
        } catch (HibernateException ex) {
            throw new PersistenceException(ex.getMessage());
        } finally {
            closeSession();
        }
    }

    
    /**
     * Disconnect and return the session for the current thread
     * @return disconnected session
     * @throws PersistenceException 
     */
    public static Session disconnectSession() throws PersistenceException {

        Session session = getSession();
        try {
            threadSession.set(null);
            if (session.isConnected() && session.isOpen()) {
                session.disconnect();
            }
        } catch (HibernateException ex) {
            throw new PersistenceException(ex.getMessage());
        }
        return session;
    }

    /**
     * Recreates the database schema <b>DROPING</b> the existing.
     */
    public static void recreateSchema() {

        SchemaExport schemaTool = new SchemaExport(getCfg());
        schemaTool.drop(true, true);
        schemaTool.create(true, true);
    }

    /**
     * Shows the SQL DDL script that generates the schema
     */
    public static void sqlSchema() {

        SchemaExport schemaTool = new SchemaExport(getCfg());
        schemaTool.create(true, false);
    }

    /**
     * Updates the current schema
     */
    public static void updateSchema() {
        SchemaUpdate schemaUpdate = new SchemaUpdate(getCfg());
        schemaUpdate.execute(true, true);
    }

}
