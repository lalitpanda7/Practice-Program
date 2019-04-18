package com.project.set_up.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;

import com.project.set_up.component.Versions;

public interface VersionsRepository
	extends JpaRepository<Versions, Integer>, JpaRepositoryImplementation<Versions, Integer> {
    public List<Versions> findByVersionNumber(String versionNumber);

}
