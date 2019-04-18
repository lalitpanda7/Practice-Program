package com.project.set_up.component;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "build")
public class Builds {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "version_id")
    private Versions version;

    @Column(name = "build_number")
    private Integer buildNumber;

    @Column(name = "release_date")
    private Date releaseDate;

    @Column(name = "updated_date")
    private Date updatedDate;
    
    @Column(name = "db_updated_date")
    private Date dbUpdatedDate;
    
    @Column(name = "notes")
    private String notes;

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

    public Versions getVersion() {
	return version;
    }

    public void setVersion(Versions version) {
	this.version = version;
    }

    public Integer getBuildNumber() {
	return buildNumber;
    }

    public void setBuildNumber(Integer buildNumber) {
	this.buildNumber = buildNumber;
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

    public Date getDbUpdatedDate() {
	return dbUpdatedDate;
    }

    public void setDbUpdatedDate(Date dbUpdatedDate) {
	this.dbUpdatedDate = dbUpdatedDate;
    }

    public String getNotes() {
	return notes;
    }

    public void setNotes(String notes) {
	this.notes = notes;
    }

}
