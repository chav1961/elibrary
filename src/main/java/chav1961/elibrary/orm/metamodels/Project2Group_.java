package chav1961.elibrary.orm.metamodels;

import java.sql.Date;
import java.util.UUID;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import chav1961.elibrary.orm.entities.Project2Group;

@StaticMetamodel( Project2Group.class )
public class Project2Group_ {
	public static volatile SingularAttribute<Project2Group, UUID> 		groupId;
	public static volatile SingularAttribute<Project2Group, UUID> 		projectId;
	public static volatile SingularAttribute<Project2Group, String>		description;
	public static volatile SingularAttribute<Project2Group, Date>		added;
	public static volatile SingularAttribute<Project2Group, Boolean>	isLocked;
	public static volatile SingularAttribute<Project2Group, Boolean>	readOnly;
}
