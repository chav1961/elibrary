package chav1961.elibrary.orm;

import java.util.Properties;

import javax.swing.GroupLayout.Group;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;

import chav1961.elibrary.orm.entities.Book2Authors;
import chav1961.elibrary.orm.entities.BookAuthors;
import chav1961.elibrary.orm.entities.BookList;
import chav1961.elibrary.orm.entities.BookPublishers;
import chav1961.elibrary.orm.entities.BookSeries;


public class HibernateSession implements AutoCloseable {
	private final SessionFactory	factory;
	private long 					lastUnique = 0;

    public HibernateSession(final Properties props) {
    	this(props, Book2Authors.class, BookAuthors.class, BookList.class, BookPublishers.class, BookSeries.class);
    }	
	
    public HibernateSession(final Properties props, final Class<?>... classes) {
        final Configuration	configuration = new Configuration().addProperties(props);
        
        for(Class<?> item : classes) {
            configuration.addAnnotatedClass(item);
        }
        final StandardServiceRegistryBuilder 	builder = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
	        
        this.factory = configuration.buildSessionFactory(builder.build());
    }
    
	@Override
	public void close() throws RuntimeException {
		factory.close();
	}

    public SessionFactory getSessionFactory() {
    	if (factory.isClosed()) {
    		throw new IllegalStateException("Attempt to get session failed: session already closed");
    	}
    	else {
        	return factory;
    	}
    }

    public HibernateCriteriaBuilder getCriteriaBuilder() {
    	if (factory.isClosed()) {
    		throw new IllegalStateException("Attempt to get session failed: session already closed");
    	}
    	else {
        	return factory.getCriteriaBuilder();
    	}
    }
    
    public long getUniqueId() {
    	try(final Session	session = getSessionFactory().getCurrentSession()) {
    		synchronized(this) {
    			if ((lastUnique & 0xFF) == 0) {
    			    final Query<Long> 	query = session.createQuery( "select nextval('SYSTEMSEQ')", Long.class);
    			    
    			    lastUnique = query.uniqueResult().longValue() << 8;
    			}
    		    return lastUnique++;
    		}
    	}
    }
}
