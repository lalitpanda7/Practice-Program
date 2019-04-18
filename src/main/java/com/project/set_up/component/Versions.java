package com.project.set_up.component;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "version")
public class Versions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    
    @Column(name = "version_number")
    private String versionNumber;
    
    @Column(name = "release_date")
    private Date releaseDate;
    
    @Column(name = "updated_date")
    private Date updatedDate;
    
    @Column(name = "notes")
    private String notes;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "version")
    List<Builds> builds;

    public Integer getId() {
	return id;
    }

    public void setId(Integer id) {
	this.id = id;
    }

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

    public String getNotes() {
	return notes;
    }

    public void setNotes(String notes) {
	this.notes = notes;
    }

    public List<Builds> getBuilds() {
        return builds;
    }

    public void setBuilds(List<Builds> builds) {
        this.builds = builds;
    }

    @Override
    public String toString() {
	return "Versions [id=" + id + ", versionNumber=" + versionNumber + ", releaseDate=" + releaseDate
		+ ", updatedDate=" + updatedDate + ", notes=" + notes + "]";
    }

}
