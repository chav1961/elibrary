package chav1961.elibrary.orm;

import java.util.Properties;

import javax.swing.GroupLayout.Group;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.criteria.HibernateCriteriaBuilder;

import chav1961.elibrary.orm.entities.Group2User;
import chav1961.elibrary.orm.entities.Library;
import chav1961.elibrary.orm.entities.Library2Group;
import chav1961.elibrary.orm.entities.Library2Scheme;
import chav1961.elibrary.orm.entities.Project;
import chav1961.elibrary.orm.entities.Project2Group;
import chav1961.elibrary.orm.entities.QuantumMatrix;
import chav1961.elibrary.orm.entities.QuantumScheme;
import chav1961.elibrary.orm.entities.User;

//https://javarush.com/groups/posts/hibernate-java
public class HibernateSession implements AutoCloseable {
	private final SessionFactory	factory;

    public HibernateSession(final Properties props) {
    	this(props, Group.class, Group2User.class, Library.class, Library2Group.class, Library2Scheme.class, 
    			    Project.class, Project2Group.class, QuantumMatrix.class, QuantumScheme.class, User.class);
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
}
