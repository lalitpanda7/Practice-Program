package com.project.set_up.dto;

import java.util.Date;

public class BuildInfoFromFile {

    private Integer buildNumber;
    private Date releaseDate;
    private String notes;

    public Integer getBuildNumber() {
	return buildNumber;
    }

    public void setBuildNumber(Integer buildNumber) {
	this.buildNumber = buildNumber;
    }

    public String getNotes() {
	return notes;
    }

    public void setNotes(String notes) {
	this.notes = notes;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }

    @Override
    public String toString() {
	return "BuildInfoFromFile [ buildNumber=" + buildNumber + ", notes=" + notes + "]";
    }

}
