package br.scpl.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Issue {
	
	private String engineId;
    
	private String ruleId;
	
	private Location primaryLocation;
	
	private String type;
	
	private String severity;
	
	private Integer effortMinutes;

    private List<Location> secondaryLocations;

	public String getEngineId() {
		return engineId;
	}

	public void setEngineId(String engineId) {
		this.engineId = engineId;
	}

	public String getRuleId() {
		return ruleId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = this.ruleId == null ? ruleId : this.ruleId;
	}

	public Location getPrimaryLocation() {
		return primaryLocation;
	}

	public void setPrimaryLocation(Location primaryLocation) {
		this.primaryLocation = primaryLocation;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSeverity() {
		return severity;
	}

	public void setSeverity(String severity) {
		this.severity = severity;
	}

	public Integer getEffortMinutes() {
		return effortMinutes;
	}

	public void setEffortMinutes(Integer effortMinutes) {
		this.effortMinutes = effortMinutes;
	}

	public List<Location> getSecondaryLocations() {
		return secondaryLocations;
	}

	public void setSecondaryLocations(List<Location> secondaryLocations) {
		this.secondaryLocations = secondaryLocations;
	}
    
}
