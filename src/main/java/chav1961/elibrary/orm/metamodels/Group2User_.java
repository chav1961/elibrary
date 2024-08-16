package chav1961.elibrary.orm.metamodels;

import java.sql.Date;
import java.util.UUID;

import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import chav1961.elibrary.orm.entities.Group2User;

@StaticMetamodel( Group2User.class )
public class Group2User_ {
	public static volatile SingularAttribute<Group2User, UUID> 		groupId;
	public static volatile SingularAttribute<Group2User, UUID> 		userId;
	public static volatile SingularAttribute<Group2User, Date> 		added;
	public static volatile SingularAttribute<Group2User, Boolean>	isLocked;
}
