package com.vcs.service;

import javax.validation.constraints.NotBlank;

import com.vcs.dto.VersionDTO;

public interface VersionService {

	void saveVersionAndBuild();

	VersionDTO getVersion(@NotBlank String versionNumber, int pageNumber, int pageSize);

	VersionDTO getLatestVersion(int pageNumber, int pageSize);

}
