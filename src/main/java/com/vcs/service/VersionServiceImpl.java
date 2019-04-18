package com.vcs.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vcs.dto.BuildDTO;
import com.vcs.dto.InputGenerator;
import com.vcs.dto.VersionDTO;
import com.vcs.entity.BuildEntity;
import com.vcs.entity.VersionEntity;
import com.vcs.exception.DataNotFoundException;
import com.vcs.repository.BuildRepository;
import com.vcs.repository.VersionRepository;
import com.vcs.util.DBUtil;
import com.vcs.util.ResourceReader;

/*
 * AUTHOR: Pragyan Kumar Chand
 * 
 * This class provides service 
 *  1) To manage version and build of the application
 *  2) To fetch details of latest version
 *  3) To fetch details of version by any version number
 */
@Service
public class VersionServiceImpl implements VersionService {

    @Autowired
    VersionRepository versionRepository;
    @Autowired
    BuildRepository buildRepo;
    @Autowired
    DBUtil dbUtil;
    @Autowired
    ResourceReader reader;
    private static Boolean update = true;

    // This method manages the build and version details of the application
    @Override
    @org.springframework.transaction.annotation.Transactional
    public void saveVersionAndBuild() {

	List<VersionDTO> versionInfos = InputGenerator.populateInputVersions(); // input is populated through this
										// method

	List<String> versionNumbers = versionInfos.stream().map(VersionDTO::getVersionNumber)
		.collect(Collectors.toList());

//	if (versionInfos.stream().filter(info -> info.getBuilds().isEmpty()).findAny().isPresent()) {
//	    throw new DataNotFoundException("version should contain at least one build information");
//	}

	List<VersionEntity> versions = versionRepository.findByVersionNumberIn(versionNumbers); // fetch version details
												// together
	List<VersionEntity> versionsToBeSaved = new ArrayList<>(); // this is populated to save all version entities at
								   // a go
	List<BuildEntity> buildsToBeSaved = new ArrayList<>(); // this is populated to save all build entities at a go
	Map<String, List<BuildEntity>> buildEntityWithUnsavedVersion = new HashMap<>(); // this map is populated with a
											// new version number (to be
											// saved) with its corresponding
											// builds given in input
	Set<String> sqlFileNames = new TreeSet<>(); // sql files to be executed are populated in this collection

	populateVersionAndItsBuildDetails(versionInfos, versions, versionsToBeSaved, buildsToBeSaved, sqlFileNames,
		buildEntityWithUnsavedVersion);

	List<VersionEntity> savedVersions = versionRepository.saveAll(versionsToBeSaved); // Version Entities are saved
	versionRepository.flush(); // this is used to avoid delayed query execution

	// After versions are saved, its build details are populated for new versions
	populateBuildsForNewlyCreatedVersions(buildEntityWithUnsavedVersion, savedVersions, buildsToBeSaved);

	buildRepo.saveAll(buildsToBeSaved); // all the builds are saved at a go
	buildRepo.flush();

	if (!sqlFileNames.isEmpty()) {
	    // dbUtil.executeSqlFiles(sqlFileNames); //sql files are executed
	}

    }

    /*
     * This method populates all the versions, builds to be saved and populates sql
     * files to be executed
     * 
     * @Param: versionInfos - Input version and build details by developer
     * 
     * @Param: versions - list of versions fetched from data layer from the version
     * numbers given in input.
     * 
     * @Param: versionsToBeSaved - Unsaved version details are populated here
     * 
     * @Param: buildsToBeSaved - Unsaved builds are populated here
     * 
     * @Param: sqlFileNames - list of sql files to be executed are populated here.
     * FILE_NAME_FORMAT - versionNumber-buildNumber
     * 
     * @Param: buildEntityWithUnsavedVersion - this is populated while a new version
     * gets saved to track build details for newly saved versions
     */
    void populateVersionAndItsBuildDetails(List<VersionDTO> versionInfos, List<VersionEntity> versions,
	    List<VersionEntity> versionsToBeSaved, List<BuildEntity> buildsToBeSaved, Set<String> sqlFileNames,
	    Map<String, List<BuildEntity>> buildEntityWithUnsavedVersion) {

	versionInfos.sort((v1, v2) -> v1.getVersionNumber().compareTo(v2.getVersionNumber()));

	versionInfos.stream().forEach(versionDto -> {
	    if (update) {
		Optional<VersionEntity> version = versions.stream()
			.filter(versionEntity -> versionEntity.getVersionNumber().equals(versionDto.getVersionNumber()))
			.findFirst();

		if (version.isPresent()) {
		    populateBuildsToBeSaved(versionDto, version.get(), buildsToBeSaved, sqlFileNames);
		    version.get().setUpdatedDate(Instant.now());
		} else {
		    populateVersionToBeSaved(versionDto, versionsToBeSaved, buildEntityWithUnsavedVersion,
			    sqlFileNames);
		}
	    }
	});
    }

    /*
     * This method populates all the builds for newly created version
     * 
     * @Param: buildEntityWithUnsavedVersion - this is populated while a new version
     * gets saved to track build details for newly saved versions
     * 
     * @Param: savedVersions - newly created versions
     * 
     * @Param: buildsToBeSaved - Unsaved builds are populated here
     */
    private void populateBuildsForNewlyCreatedVersions(Map<String, List<BuildEntity>> buildEntityWithUnsavedVersion,
	    List<VersionEntity> savedVersions, List<BuildEntity> buildsToBeSaved) {

	buildEntityWithUnsavedVersion.forEach((versionNumber, unsavedBuilds) -> {
	    savedVersions.stream().filter(savedVersion -> savedVersion.getVersionNumber().equals(versionNumber)) // filtering
														 // correct
														 // version
														 // from
														 // newly
														 // created
														 // version
		    .findFirst() // by comparing version number
		    .ifPresent(savedVersion -> {

			unsavedBuilds.forEach(b -> b.setVersionId(savedVersion));
			buildsToBeSaved.addAll(unsavedBuilds);

		    });
	});
    }

    /**
     * This method populates all the builds to be saved and populates sql files to
     * be executed
     * 
     * @Param: versionDTO - Input version and build details by developer
     * 
     * @Param: version - saved version entity corresponding to the input.
     * 
     * @Param: buildsToBeSaved - Unsaved builds are populated here
     * 
     * @Param: sqlFileNames - list of sql files to be executed are populated here.
     * FILE_NAME_FORMAT - versionNumber-buildNumber
     */
    private void populateBuildsToBeSaved(VersionDTO versionDTO, VersionEntity version, List<BuildEntity> buildsToBeSaved, Set<String> sqlfileNames) {

	List<Long> buildNumbersForVersion = version != null
		? version.getBuilds().stream().map(BuildEntity::getBuildNumber).collect(Collectors.toList())
		: new ArrayList<>();

	versionDTO.getBuilds().sort((b1, b2) -> b1.getBuildNumber().compareTo(b2.getBuildNumber()));

	versionDTO.getBuilds().forEach(buildDto -> {
	    if (update && !buildNumbersForVersion.contains(buildDto.getBuildNumber())) {

		// Set<String> sqlFiles =
		// populateSqlFileNamesForTheBuild(versionDTO.getVersionNumber(),
		// buildDto.getBuildNumber(), buildNumbersForVersion);
		// buildNumbersForVersion.add(buildDto.getBuildNumber());

		// executeSqlFilesForBuild(sqlFiles, buildDto, buildsToBeSaved,
		// buildNumbersForVersion, version);

		Map<Long, Map<String, String>> buildFileMap = populateFilesForForBuilds(versionDTO.getVersionNumber(),
			buildDto.getBuildNumber(), buildNumbersForVersion);
		System.out.println("---------map------------- " + buildFileMap);
		buildNumbersForVersion.add(buildDto.getBuildNumber());
		executeFilesForBuild(buildFileMap, buildDto, buildsToBeSaved, buildNumbersForVersion, version);

		if (update) {
		    BuildEntity buildEntity = new BuildEntity();
		    buildEntity.setBuildNumber(buildDto.getBuildNumber());
		    buildEntity.setDbUpdateDate(
			    buildFileMap.containsKey(buildDto.getBuildNumber()) ? Instant.now() : null);
		    buildEntity.setNotes(buildDto.getNotes());
		    buildEntity.setReleaseDate(Instant.now());
		    buildEntity.setUpdatedDate(Instant.now());
		    buildEntity.setVersionId(version);
		    if (buildDto.getIsDbUpdated()) {
			buildEntity.setDbUpdateDate(Instant.now());
		    }
		    buildsToBeSaved.add(buildEntity);
		}
	    }
	});
    }

    /*
     * This method parse the file name to get the build number
     * 
     * @Param : fileName - file name in the format versionNumber-BuildNumber to be
     * parsed
     */
    private Long getCurrentBuild(String fileName) {
	String[] ss = fileName.split("-");
	String buildNumber = ss[1];
	String[] sss = buildNumber.split("\\.");
	return Long.parseLong(sss[0]);
    }

    /*
     * This method populates all the builds to be saved and populates sql files and
     * stored procs to be executed
     * 
     * @Param : buildFileMap - it stores all the sql files and stored procs against
     * a build number
     * 
     * @Param: buildDto - Input build information by developer
     * 
     * @Param: buildsToBeSaved - consists all the builds to be persisted
     * 
     * @Param: buildNumbersForVersion - build numbers that are already persisted or
     * processed
     * 
     * @Param : version - already persisted version
     */
    private void executeFilesForBuild(Map<Long, Map<String, String>> buildFileMap, BuildDTO buildDto,
	    List<BuildEntity> buildsToBeSaved, List<Long> buildNumbersForVersion, VersionEntity version) {

	if (!buildFileMap.isEmpty()) {

	    buildFileMap.keySet().stream().sorted().forEach(buildNumber -> {
		if (update) {
		    String sqlFile = buildFileMap.get(buildNumber).get("sql");
		    String storedProcName = buildFileMap.get(buildNumber).get("sp");
		    try {
			/*
			 * if(sqlFile != null) { dbUtil.executeSqlFile(sqlFile); } if(storedProcName !=
			 * null) { dbUtil.executeStoredProc(storedProcName); }
			 */
			dbUtil.executeSqlAndStoredProc(sqlFile, storedProcName);

		    } catch (Exception e) {

			update = false;

			Long currentBuild = getCurrentBuild(sqlFile != null ? sqlFile : storedProcName);
			Long previousBuild = Long.valueOf(currentBuild.intValue() - 1);
			// if check
			if (!buildsToBeSaved.stream().filter(build -> build.getBuildNumber().equals(previousBuild))
				.findFirst().isPresent() && !buildNumbersForVersion.contains(previousBuild)) {
			    BuildEntity buildEntity = new BuildEntity();
			    buildEntity.setBuildNumber(previousBuild); // previous build number should be given
			    buildEntity.setDbUpdateDate(Instant.now());
			    buildEntity.setNotes(buildDto.getNotes());
			    buildEntity.setReleaseDate(Instant.now());
			    buildEntity.setUpdatedDate(Instant.now());
			    buildEntity.setVersionId(version);

			    buildsToBeSaved.add(buildEntity);
			}
		    }
		}
	    });

	}
    }

    /*
     * This method populates all the builds to be saved and populates sql files and
     * stored procs to be executed
     * 
     * @Param: versionNumber - Input version number
     * 
     * @Param: buildNumbersForVersion - build numbers that are currently persisted
     * 
     * @Param: buildNumber - input build number
     */
    private Map<Long, Map<String, String>> populateFilesForForBuilds(String versionNumber, Long buildNumber,
	    List<Long> buildNumbersForVersion) {

	Long maxBuild = buildNumbersForVersion.stream().max((c1, c2) -> c1.compareTo(c2)).orElseGet(() -> 0l); // getting
													       // the
													       // maximum
													       // build
													       // number,
													       // for a
													       // version
													       // currently
													       // persisted

	// fetching all the sql files present for the corresponding version directory
	Set<String> allFilesPresentForVersion = reader.listAllFiles("sql", versionNumber);
	Set<String> allStoredProcsForVersion = reader.listAllFiles("stored-procedure", versionNumber);

	Map<Long, Map<String, String>> buildFileMap = new HashMap<>();

	/*
	 * handling skipped builds. It starts checking from the LAST BUILD, if there is
	 * any sql file for the skipped builds DEFAULT build number is populated and
	 * matched with the file names present in the directory if any file names match
	 * it should be considered for execution
	 */
	for (int i = maxBuild.intValue() + 1; i <= buildNumber.intValue(); i++) {

	    Map<String, String> sqlStoredProcs = new HashMap<String, String>();

	    String fileName = i + ".sql";
	    if (allFilesPresentForVersion.contains(fileName)) {
		// sqlfileNames.add(versionNumber + "-" /* + "build" */ + i + ".sql");
		sqlStoredProcs.put("sql", versionNumber + "-" + i + ".sql");
	    }
	    if (allStoredProcsForVersion.contains(fileName)) {
		// sqlfileNames.add(versionNumber + "-" /* + "build" */ + i + ".sql");
		sqlStoredProcs.put("sp", versionNumber + "-" + i + ".sql");
	    }
	    if (!sqlStoredProcs.isEmpty()) {
		buildFileMap.put(Long.valueOf(i), sqlStoredProcs);
	    }
	}

	return buildFileMap;
    }

    // Version details are populated from dto
    /*
     * This method populates all the version to be saved and populates its builds in
     * the map for tracking and populates sql files to be executed for the build
     * 
     * @Param: versionDTO - Input version and build details by developer
     * 
     * @Param: versionsToBeSaved - Unsaved version details are populated here
     * 
     * @Param: buildEntityWithUnsavedVersion - this is populated while a new version
     * gets saved to track build details for newly saved versions
     * 
     * @Param: sqlFileNames - list of sql files to be executed are populated here.
     * FILE_NAME_FORMAT - versionNumber-buildNumber
     */
    private void populateVersionToBeSaved(VersionDTO versionDTO, List<VersionEntity> versionsToBeSaved,
	    Map<String, List<BuildEntity>> buildEntityWithUnsavedVersion, Set<String> sqlFileNames) {

	VersionEntity ve = new VersionEntity();
	ve.setVersionNumber(versionDTO.getVersionNumber());
	ve.setReleaseDate(Instant.now());
	ve.setUpdatedDate(Instant.now());
	ve.setReleaseNotes(versionDTO.getReleaseNotes());

	versionsToBeSaved.add(ve);

	List<BuildEntity> buildsToBeSaved = new ArrayList<>();
	populateBuildsToBeSaved(versionDTO, null, buildsToBeSaved, sqlFileNames);
	buildEntityWithUnsavedVersion.put(versionDTO.getVersionNumber(), buildsToBeSaved);
    }

    /*
     * This method fetches version details by a given version number
     * 
     * @Param : versionNumber
     * 
     * @Param : pageNumber : value of page number
     * 
     * @Param : pageSize : value of page size
     */
    @Override
    public VersionDTO getVersion(@NotBlank String versionNumber, int pageNumber, int pageSize) {

	Optional<VersionEntity> versionEntity = versionRepository.findByVersionNumber(versionNumber);

	return populateVersionDetails(versionEntity, pageNumber, pageSize);
    }

    /*
     * This method fetches latest version details
     * 
     * @Param : pageNumber : value of page number
     * 
     * @Param : pageSize : value of page size
     */
    @Override
    public VersionDTO getLatestVersion(int pageNumber, int pageSize) {

	Optional<VersionEntity> versionEntity = versionRepository.findLatestVersion();

	return populateVersionDetails(versionEntity, pageNumber, pageSize);
    }

    /*
     * This method populates version details
     * 
     * @Param : versionEntity - entity fetched by version number
     * 
     * @Param : pageNumber : value of page number
     * 
     * @Param : pageSize : value of page size
     */
    private VersionDTO populateVersionDetails(Optional<VersionEntity> versionEntity, int pageNumber, int pageSize) {

	VersionDTO versionDto = new VersionDTO();

	versionEntity.ifPresent(version -> {
	    versionDto.setVersionNumber(version.getVersionNumber());
	    versionDto.setReleaseDate(version.getReleaseDate());
	    versionDto.setReleaseNotes(version.getReleaseNotes());
	    versionDto.setUpdatedDate(version.getUpdatedDate());
	    versionDto.setBuilds(populateBuildsForVersion(version.getVersionNumber(), pageNumber, pageSize));
	});

	return versionDto;
    }

    /*
     * This method populates build details for a version and pages it
     * 
     * @Param : versionNumber - input version number
     * 
     * @Param : pageNumber : value of page number
     * 
     * @Param : pageSize : value of page size
     */
    private List<BuildDTO> populateBuildsForVersion(String versionNumber, int pageNumber, int pageSize) {

	Page<BuildEntity> pagedBuilds = buildRepo.getBuildsForVersion(versionNumber,
		PageRequest.of(pageNumber, pageSize, Sort.by("buildNumber").descending()));

	List<BuildDTO> builds = new ArrayList<>();

	pagedBuilds.getContent().forEach(buildEntity -> {
	    BuildDTO buildDto = new BuildDTO();
	    buildDto.setBuildNumber(buildEntity.getBuildNumber());
	    buildDto.setDbUpdateDate(buildEntity.getDbUpdateDate());
	    buildDto.setNotes(buildEntity.getNotes());
	    buildDto.setReleaseDate(buildEntity.getReleaseDate());
	    builds.add(buildDto);
	});
	return builds;
    }

}
