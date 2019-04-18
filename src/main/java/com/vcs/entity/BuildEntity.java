package com.vcs.entity;

import java.time.Instant;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "BUILD")
public class BuildEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "VERSION_ID") 
	private VersionEntity versionId;

	@Column(name = "BUILD_NUMBER")
	private Long buildNumber;

	@Column(name = "RELEASE_DATE")
	private Instant releaseDate;

	@Column(name = "UPDATED_DATE")
	private Instant updatedDate;

	@Column(name = "DB_UPDATE_DATE")
	private Instant dbUpdateDate;

	@Column(name = "NOTES")
	private String notes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public VersionEntity getVersionId() { 
		return versionId; 
	}

	public void setVersionId(VersionEntity versionId) { 
		this.versionId = versionId; 
	}


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

}
