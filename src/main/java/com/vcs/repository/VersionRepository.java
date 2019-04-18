package com.vcs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.vcs.entity.VersionEntity;

@Repository
public interface VersionRepository extends JpaRepository<VersionEntity, Long> {

	Optional<VersionEntity> findByVersionNumber(String versionNumber);
	
	List<VersionEntity> findByVersionNumberIn(List<String> versionNumber);
	
	@Query("select ve from VersionEntity ve where ve.id = (select max(id) from VersionEntity)")
	Optional<VersionEntity> findLatestVersion();
}
