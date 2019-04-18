package com.project.set_up.dto;

import java.util.Date;
import java.util.List;

public class LatestVersionResponse {
    private String versionNumber;
    private Date releaseDate;
    private Date updatedDate;
    private String versionNote;
    private List<String> buildInfo;

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

    public Date getUpdatedDate() {
	return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
	this.updatedDate = updatedDate;
    }

    public String getVersionNote() {
	return versionNote;
    }

    public void setVersionNote(String versionNote) {
	this.versionNote = versionNote;
    }

    public List<String> getBuildInfo() {
	return buildInfo;
    }

    public void setBuildInfo(List<String> buildInfo) {
	this.buildInfo = buildInfo;
    }

    @Override
    public String toString() {
	return "LatestVersionResponse [versionNumber=" + versionNumber + ", releaseDate=" + releaseDate
		+ ", updatedDate=" + updatedDate + ", versionNote=" + versionNote + ", buildInfo=" + buildInfo + "]";
    }
}
