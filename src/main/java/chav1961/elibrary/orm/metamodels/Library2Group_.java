package chav1961.elibrary.orm.metamodels;

import java.sql.Date;
import java.util.UUID;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import chav1961.elibrary.orm.entities.Library2Group;

@StaticMetamodel( Library2Group.class )
public class Library2Group_ {
	public static volatile SingularAttribute<Library2Group, UUID> 		groupId;
	public static volatile SingularAttribute<Library2Group, UUID> 		libraryId;
	public static volatile SingularAttribute<Library2Group, String>		description;
	public static volatile SingularAttribute<Library2Group, String[]>	tags;
	public static volatile SingularAttribute<Library2Group, Date> 		added;
	public static volatile SingularAttribute<Library2Group, Boolean> 	isLocked;
}
