package chav1961.elibrary.admin.db;

import chav1961.purelib.sql.interfaces.InstanceManager;
import chav1961.purelib.ui.interfaces.FormManager;

public interface DedicatedORMInterface<Cl, Inst extends InstanceManager<Long, Cl>> extends ORMInterface<Cl, Inst> {
	FormManager<Long, Cl> getDedicatedFormManager();
}
