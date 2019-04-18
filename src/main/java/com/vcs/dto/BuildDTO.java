package com.vcs.dto;

import java.time.Instant;
import java.util.Set;

public class BuildDTO {

	private Long buildNumber;
	private Instant releaseDate;
	private Instant updatedDate;
	private Instant dbUpdateDate;
	private String notes;
	private Boolean isDbUpdated;
	private Set<String> sqlFilesToExecute;// not implemented

	public Long getBuildNumber() {
		return buildNumber;
	}
	public void setBuildNumber(Long buildNumber) {
		this.buildNumber = buildNumber;
	}
	public Instant getReleaseDate() {
		return releaseDate;
	}
	public void setReleaseDate(Instant releaseDate) {
		this.releaseDate = releaseDate;
	}
	public Instant getUpdatedDate() {
		return updatedDate;
	}
	public void setUpdatedDate(Instant updatedDate) {
		this.updatedDate = updatedDate;
	}
	public Instant getDbUpdateDate() {
		return dbUpdateDate;
	}
	public void setDbUpdateDate(Instant dbUpdateDate) {
		this.dbUpdateDate = dbUpdateDate;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
	public Boolean getIsDbUpdated() {
		return isDbUpdated;
	}
	public void setIsDbUpdated(Boolean isDbUpdated) {
		this.isDbUpdated = isDbUpdated;
	}
	public Set<String> getSqlFilesToExecute() {
		return sqlFilesToExecute;
	}
	public void setSqlFilesToExecute(Set<String> sqlFilesToExecute) {
		this.sqlFilesToExecute = sqlFilesToExecute;
	}

}
