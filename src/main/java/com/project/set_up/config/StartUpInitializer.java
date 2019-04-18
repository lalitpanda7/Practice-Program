package com.project.set_up.config;

import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.project.set_up.component.Builds;
import com.project.set_up.component.Versions;
import com.project.set_up.constants.Constants;
import com.project.set_up.dto.BuildInfoFromFile;
import com.project.set_up.dto.VersionInfoFromFile;
import com.project.set_up.exception.FileReadException;
import com.project.set_up.repository.BuildsRepository;
import com.project.set_up.repository.VersionsRepository;
import com.project.set_up.utility.FileUtils;

@Component
public class StartUpInitializer {
    @Autowired
    VersionsRepository versionsRepository;
    @Autowired
    BuildsRepository buildsRepository;
    @Autowired
    MySqlDataBaseConnectionConfig start;
    @Autowired
    FileUtils fileUtils;

    private static final Logger logger = LogManager.getLogger(StartUpInitializer.class);

    /**
     * This method is for storing build info as well as executing query associated
     * with a particular version
     * 
     * @param versionId     - Id of that particular version having version number
     *                      versionNumber
     * @param versionNumber - Version number for which builds is to be executed
     * @param totalBuild    - Total no of builds for that particular version
     * @param isNewVersion  - Checks whether build has to be done for new version or
     *                      for older version
     * @throws FileReadException - This is thrown if there is any exception is there
     *                           while executing MySql scripts
     */
    private void buildInitialize(Versions version, Integer totalBuild, Boolean isNewVersion) throws FileReadException {

	List<BuildInfoFromFile> buildInfoFromFiles = new ArrayList<>();
	Integer presentValue = 0;
	try {

	    // Reading CSV file line by line and map it buildInitializeto a DTO.
	    buildInfoFromFiles = fileUtils.readFileBuildInfoByVersionNumber(version);
	} catch (FileReadException e1) {
	    logger.info(e1.getMessage());
	}

	Map<String, Builds> buildMap = new HashMap<>();
	List<Builds> builds = new ArrayList<>();

	for (BuildInfoFromFile buildInfoFromFile : buildInfoFromFiles) {
	    Builds build = new Builds();
	    build.setVersion(version);

	    // Copy properties from DTO(buildInfoFromFile) to bean(builds)
	    BeanUtils.copyProperties(buildInfoFromFile, build);
	    build.setUpdatedDate(new Date(Calendar.getInstance().getTimeInMillis()));

	    builds.add(build);

	}
	try {
	    buildsRepository.saveAll(builds);
	} catch (Exception e) {
	    logger.debug(e.getLocalizedMessage());
	}
	List<Builds> buildInfo = buildsRepository.findAllByVersionAndDbUpdatedDateIsNull(version,
		Sort.by(Direction.ASC, "buildNumber"));

	List<Builds> buildInfoDbUpdated = buildsRepository.findAllByVersionAndDbUpdatedDateIsNotNull(version,
		Sort.by(Direction.DESC, "buildNumber"));
	if (!CollectionUtils.isEmpty(buildInfo)) {
	    for (Builds build : buildInfo) {
		buildMap.put(version.getVersionNumber() + build.getBuildNumber(), build);
	    }
	}
	if (!CollectionUtils.isEmpty(buildInfoDbUpdated)) {
	    presentValue = buildInfoDbUpdated.get(0).getBuildNumber();
	}
	// Calling Method to execute query for build from previous version to present
	// version
	if (isNewVersion) {
	    queryExecutor(version, presentValue + 1, totalBuild, buildMap);
	}
    }

    /**
     * Method to call execute query for version which may not be present in build
     * info but have SQL scripts.
     * 
     * @param versionNumber   - Version for which SQL query needs to execute
     * @param previousBuildNo - Build up to which all SQL query files are executed
     * @param presentBuildNo  - Build up to which all SQL query files need to
     *                        execute
     * @throws FileReadException - This is thrown if there is any exception is there
     *                           while executing MySql scripts
     */
    private void queryExecutor(Versions version, Integer presentValue, Integer totalBuilds,
	    Map<String, Builds> buildMap) throws FileReadException {
	for (int i = presentValue; i <= totalBuilds; i++) {

	    // forming the file name by build
	    String fileName = Constants.MY_SQL_QUERY + version.getVersionNumber() + Constants.HIPEN + i + Constants.SQL;

	    String key = version.getVersionNumber() + i;
	    Builds build = buildMap.get(key);

	    // Calling execute query by passing file name
	    mySqlQueryExecutor(fileName, build, i, version);
	}
    }

    /**
     * method to execute SQL query from file
     * 
     * @param fileName name of the script which has to execute
     * @throws FileReadException
     */
    private void mySqlQueryExecutor(String fileName, Builds build, Integer buildNumber, Versions version)
	    throws FileReadException {
	Boolean isExecuted = true;
	ClassLoader classLoader = getClass().getClassLoader();
	URL resource = classLoader.getResource(fileName);
	if (resource != null && build == null) {
	    build = new Builds();
	    build.setBuildNumber(buildNumber);
	    build.setVersion(version);
	    build.setReleaseDate(version.getReleaseDate());
	    build.setUpdatedDate(new Date(Calendar.getInstance().getTimeInMillis()));
	}
	if (resource != null && build.getDbUpdatedDate() == null) {
	    try {
		start.executeScriptUsingScriptRunner(resource);
	    } catch (Exception e) {
		logger.info(e.getMessage());
		throw new FileReadException(e.getMessage());
	    }
	} else if (build != null && build.getDbUpdatedDate() == null) {
	    isExecuted = true;
	} else {
	    isExecuted = false;
	}
	if (isExecuted) {
	    build.setDbUpdatedDate(new Date(Calendar.getInstance().getTimeInMillis()));
	    try {
		buildsRepository.save(build);
	    } catch (Exception e) {
		logger.debug(e.getLocalizedMessage());
	    }
	}
    }

    /**
     * This method Initialize version info the build info as well as the query
     * associated with it
     */
    public void versionInitialize() {

	// Reading CSV file line by line
	List<VersionInfoFromFile> versionInfoFromFiles = new ArrayList<>();
	Optional<String> latestVersionNumber;
	List<String> versionNumbers = new ArrayList<>();
	try {
	    versionInfoFromFiles = fileUtils.readVersionFile();
	} catch (FileReadException e) {
	    logger.info(e.getMessage());
	}
	// change needed Use in
	List<Versions> versions = versionsRepository.findAll();
	if (!CollectionUtils.isEmpty(versions)) {
	    latestVersionNumber = sortVersionByReleaseDate(versions);
	    versions.forEach(action -> versionNumbers.add(action.getVersionNumber()));
	} else {
	    latestVersionNumber = Optional.empty();
	}

	versionInfoFromFiles.stream().forEach(versionInfoFromFile -> versionUpdate(latestVersionNumber, versionNumbers,
		versions, versionInfoFromFile));
    }

    private void versionUpdate(Optional<String> latestVersionNumber, List<String> versionNumbers,
	    List<Versions> versions, VersionInfoFromFile versionInfoFromFile) {
	Versions version = new Versions();

	// check if the version is a new version
	Boolean isNewVersion = false;

	if (CollectionUtils.isEmpty(versions) || !versionNumbers.contains(versionInfoFromFile.getVersionNumber())) {
	    BeanUtils.copyProperties(versionInfoFromFile, version);
	    version.setUpdatedDate(new Date(Calendar.getInstance().getTimeInMillis()));
	    isNewVersion = true;

	    // This method is for storing build info as well as executing query associated
	    // with a particular version
	    try {
		// needs to change version details should be saved if build is sucessfull
		versionsRepository.save(version);
		buildInitialize(version, versionInfoFromFile.getTotalbuilds(), isNewVersion);
	    } catch (FileReadException e) {
		logger.error("SQL Script execution issue. Have to correct  it and then start the Application !!!!"
			+ " \n ***********error message**********\n" + e.getMessage());
		System.exit(0);
	    }

	} else if (versionNumbers.contains(versionInfoFromFile.getVersionNumber())
		&& versionInfoFromFile.getVersionNumber().equals(latestVersionNumber.get())) {
	    version = versions.get(0);
	    BeanUtils.copyProperties(versionInfoFromFile, version);
	    version.setUpdatedDate(new Date(Calendar.getInstance().getTimeInMillis()));
	    isNewVersion = true;

	    // This method is for storing build info as well as executing query associated
	    // with a particular version
	    try {

		buildInitialize(version, versionInfoFromFile.getTotalbuilds(), isNewVersion);
	    } catch (FileReadException e) {
		logger.error("SQL Script execution issue. Have to correct  it and then start the Application !!!!"
			+ " \n ***********error message**********\n" + e.getMessage());
		System.exit(0);
	    }
	} else {
	    version = versions.stream()
		    .filter(action -> action.getVersionNumber().equals(versionInfoFromFile.getVersionNumber()))
		    .findFirst().get();
	    BeanUtils.copyProperties(versionInfoFromFile, version);
	    version.setUpdatedDate(new Date(Calendar.getInstance().getTimeInMillis()));
	    try {
		versionsRepository.save(version);
		buildInitialize(version, 0, isNewVersion);
	    } catch (Exception e) {
		logger.info("Exception while updating older version");
	    }
	    logger.info("{} version already exists ", versionInfoFromFile.getVersionNumber());
	}
    }

    private Optional<String> sortVersionByReleaseDate(List<Versions> versions) {
	versions.sort((o1, o2) -> o2.getReleaseDate().compareTo(o1.getReleaseDate()));

	return versions.stream().sorted((o1, o2) -> o2.getReleaseDate().compareTo(o1.getReleaseDate()))
		.map(Versions::getVersionNumber).findFirst();
    }
}
