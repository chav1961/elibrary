package chav1961.elibrary.orm;

import java.util.Properties;

import org.flywaydb.core.Flyway;

public class Migration {
	public static final String	FLYWAY_URL = "flyway.url"; 
	public static final String	FLYWAY_USER = "flyway.user"; 
	public static final String	FLYWAY_PASSWORD = "flyway.password"; 
	public static final String	FLYWAY_SCHEMAS = "flyway.schemas"; 
	public static final String	FLYWAY_LOCATIONS = "flyway.locations";
	
	public static void migrate(final Properties dbProps) {
		if (dbProps == null) {
			throw new NullPointerException("Database properties can't be null");
		}
		else {
			final Flyway 	flyway = Flyway.configure()
							.dataSource(dbProps.getProperty(FLYWAY_URL), 
									dbProps.getProperty(FLYWAY_USER), 
									dbProps.getProperty(FLYWAY_PASSWORD))
							.locations(dbProps.getProperty(FLYWAY_LOCATIONS))
							.schemas(dbProps.getProperty(FLYWAY_SCHEMAS))
							.load();

			flyway.migrate();
		}
	}
}
