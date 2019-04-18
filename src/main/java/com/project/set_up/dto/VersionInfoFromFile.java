package com.project.set_up.dto;

import java.util.Date;

public class VersionInfoFromFile {

    private String versionNumber;
    private Date releaseDate;
    private String notes;
    private Integer totalbuilds;

    public String getVersionNumber() {
	return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
	this.versionNumber = versionNumber;
    }

    public Date getReleaseDate() {
	return releaseDate;
    }

    public void setReleaseDate(Date releaseDate) {
	this.releaseDate = releaseDate;
    }

    public String getNotes() {
	return notes;
    }

    public void setNotes(String notes) {
	this.notes = notes;
    }

    public Integer getTotalbuilds() {
	return totalbuilds;
    }

    public void setTotalbuilds(Integer totalbuilds) {
	this.totalbuilds = totalbuilds;
    }

    @Override
    public String toString() {
	return "VersionInfoFromFile [versionNumber=" + versionNumber + ", releaseDate=" + releaseDate + ", notes="
		+ notes + ", totalbuilds=" + totalbuilds + "]";
    }

}
