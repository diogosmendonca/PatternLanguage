package br.scpl.model.sonarqube;

import java.util.ArrayList;
import java.util.List;

public class Issue {
	
	private String engineId;
    
	private String ruleId;
	
	private String type;
	
	private String severity;
	
	private Location primaryLocation;
	
	private Integer effortMinutes;

    private List<Location> secondaryLocations;
    
    private String alertComment;
    
    public Issue() {
    	this.secondaryLocations = new ArrayList<>();
    }

	public void setEngineId(String engineId) {
		this.engineId = engineId;
	}

	public void setRuleId(String ruleId) {
		this.ruleId = ruleId;
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

	public List<Location> getSecondaryLocations() {
		return secondaryLocations;
	}

	public String getAlertComment() {
		return alertComment;
	}

	public void setAlertComment(String alertComment) {
		this.alertComment = alertComment;
	}
	
}
