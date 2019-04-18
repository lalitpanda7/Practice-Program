package com.vcs.dto;

import java.time.Instant;
import java.util.List;

public class VersionDTO {

	private String versionNumber;
	private Instant releaseDate;
	private Instant updatedDate;
	private String releaseNotes;
	private List<BuildDTO> builds;

	public String getVersionNumber() {
		return versionNumber;
	}
	public void setVersionNumber(String versionNumber) {
		this.versionNumber = versionNumber;
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
	public String getReleaseNotes() {
		return releaseNotes;
	}
	public void setReleaseNotes(String releaseNotes) {
		this.releaseNotes = releaseNotes;
	}
	public List<BuildDTO> getBuilds() {
		return builds;
	}
	public void setBuilds(List<BuildDTO> builds) {
		this.builds = builds;
	}

}
