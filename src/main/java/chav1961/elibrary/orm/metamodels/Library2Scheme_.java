package chav1961.elibrary.orm.metamodels;

import java.net.URI;
import java.sql.Date;
import java.util.UUID;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import chav1961.elibrary.orm.entities.Library2Scheme;

@StaticMetamodel( Library2Scheme.class )
public class Library2Scheme_ {
	public static volatile SingularAttribute<Library2Scheme, UUID> 		libraryId;
	public static volatile SingularAttribute<Library2Scheme, UUID> 		schemeId;
	public static volatile SingularAttribute<Library2Scheme, String>	itemName;
	public static volatile SingularAttribute<Library2Scheme, String>	description;
	public static volatile SingularAttribute<Library2Scheme, String[]>	tags;
	public static volatile SingularAttribute<Library2Scheme, URI> 		icon;
	public static volatile SingularAttribute<Library2Scheme, Date> 		added;
	public static volatile SingularAttribute<Library2Scheme, String>	parameters;
	public static volatile SingularAttribute<Library2Scheme, String>	matrixRules;
	public static volatile SingularAttribute<Library2Scheme, String>	schemeRules;
}
