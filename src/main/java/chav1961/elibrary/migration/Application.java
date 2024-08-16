package chav1961.elibrary.migration;

import java.io.IOException;
import java.util.Properties;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.Location;

/*
 * flyway.url=jdbc:h2:mem:DATABASE
 * flyway.user=databaseUser
 * flyway.password=databasePassword
 * flyway.schemas=app-db
 * flyway.locations=filesystem:db/migration
 * 
 * */
public class Application {
	public static final String	FLYWAY_URL = "flyway.url"; 
	public static final String	FLYWAY_USER = "flyway.user"; 
	public static final String	FLYWAY_PASSWORD = "flyway.password"; 
	public static final String	FLYWAY_SCHEMAS = "flyway.schemas"; 
	public static final String	FLYWAY_LOCATIONS = "flyway.locations"; 
	
	public static void main(final String[] args) {
		final Properties	props = new Properties();
		
		try{
			props.load(System.in);
		
			final Flyway 	flyway = Flyway.configure()
								.dataSource(props.getProperty(FLYWAY_URL), 
											props.getProperty(FLYWAY_USER), 
											props.getProperty(FLYWAY_PASSWORD))
								.locations(props.getProperty(FLYWAY_LOCATIONS))
								.schemas(props.getProperty(FLYWAY_SCHEMAS))
								.load();

			flyway.migrate();
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(128);
		}
	}

}
