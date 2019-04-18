package com.vcs.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.vcs.entity.BuildEntity;

@Repository
public interface BuildRepository extends JpaRepository<BuildEntity, Long> {

	@Query("select build from BuildEntity build where build.versionId.versionNumber = :versionNumber")
	Page<BuildEntity> getBuildsForVersion(@Param("versionNumber") String versionNumber, Pageable pageable);
}
