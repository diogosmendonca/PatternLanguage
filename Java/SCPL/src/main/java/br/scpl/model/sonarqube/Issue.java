package br.scpl.model.sonarqube;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Issue {
	
	private String engineId;
    
	private String ruleId;
	
	private String type;
	
	private String severity;
	
	private Location primaryLocation;
	
	private Integer effortMinutes;

    private List<Location> secondaryLocations;
    
    public Issue() {
    	this.secondaryLocations = new ArrayList<>();
    }

	public void setEngineId(String engineId) {
		this.engineId = engineId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = this.ruleId;
	}

	public Location getPrimaryLocation() {
		return primaryLocation;
	}

	public void setPrimaryLocation(Location primaryLocation) {
		this.primaryLocation = primaryLocation;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public void setEffortMinutes(Integer effortMinutes) {
		this.effortMinutes = effortMinutes;
	}

}
