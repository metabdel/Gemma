package ubic.gemma.model;

import ubic.gemma.model.common.description.ExternalDatabase;

public class ExternalDatabaseValueObject {
    
	private String name;
    
    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static ExternalDatabaseValueObject fromEntity(ExternalDatabase ed) {
    	if (ed == null) return null;
		ExternalDatabaseValueObject vo = new ExternalDatabaseValueObject();
    	vo.setName(ed.getName());
    	return vo;
    }
    
}
