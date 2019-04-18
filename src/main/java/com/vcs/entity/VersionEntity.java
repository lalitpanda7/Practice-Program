package com.vcs.entity;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;

@Entity
@Table(name = "VERSION")
public class VersionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "VERSION_NUMBER")
	private String versionNumber;

	@Column(name = "RELEASE_DATE")
	private Instant releaseDate;

	@Column(name = "UPDATED_DATE")
	private Instant updatedDate;

	@Column(name = "RELEASE_NOTES")
	private String releaseNotes;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "versionId") 
	private List<BuildEntity> builds;
	
	@PostLoad
	public void init() {
		if(builds == null) {
			builds = new ArrayList<>();
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

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

	public List<BuildEntity> getBuilds() {
		return builds;
	}

	public void setBuilds(List<BuildEntity> builds) {
		this.builds = builds;
	}

}
