package com.project.set_up.config;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.annotation.PostConstruct;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ibatis.common.jdbc.ScriptRunner;
import com.project.set_up.constants.Constants;
import com.project.set_up.exception.FileReadException;

@Service
public class MySqlDataBaseConnectionConfig {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String userName;
    @Value("${spring.datasource.password}")
    private String password;
    private Connection connection = null;
    private static final Logger logger = LogManager.getLogger(MySqlDataBaseConnectionConfig.class);

    /**
     * setting of MySQL connection
     */
    @PostConstruct
    private void setupConnection() {
	// Connect to Database
	try {
	    this.connection = DriverManager.getConnection(url, userName, password);
	    connection.setAutoCommit(false);
	} catch (SQLException e) {
	    logger.info("Error while Connecting MySQL");
	}
    }

    /**
     * method to provide MySql connection
     * 
     * @return
     */
    public Connection getConnection() {
	return connection;
    }

    /**
     * Method to Execute MySql scripts based on filename
     * 
     * @param fileName - Name of the file containing SQL script
     * @throws IOException       - Exception while operating on file
     * @throws SQLException      - Exception while rolling back SQL query
     * @throws FileReadException - Exception while executing SQL query
     */
    void executeScriptUsingScriptRunner(URL resource) throws FileReadException, IOException, SQLException {

	// Initialize script path
	Reader reader = null;

	    try {
		ScriptRunner scriptExecutor = new ScriptRunner(connection, false, true);
		reader = new BufferedReader(new FileReader(resource.getFile()));

		// Execute script with file reader as input
		scriptExecutor.runScript(reader);
		
		connection.commit();
	    } catch (Exception e) {
		connection.rollback();
		throw new FileReadException(e.getMessage());
	    } finally {

		// Close file reader
		if (reader != null) {
		    reader.close();
		}
	    }
    }

    /**
     * Method to run MySQL script as it is from command Line
     * @param fileName - Name of the file for which the script has to run
     * @throws FileReadException - Exception while executing script file
     * @throws IOException - Exception while closing file
     */
    void executeScriptUsingScriptRunnerTest1(String fileName) throws FileReadException, IOException {

	// Initialize script path
	ClassLoader classLoader = getClass().getClassLoader();
	URL resource = classLoader.getResource(fileName);
	if (resource != null) {
	    try {
		      Runtime.getRuntime().exec
		        ("mysql --user="+userName+" --password="+password +" --database="+Constants.DATABASE +" -s < "+resource.getPath());
		    }
		    catch (Exception err) {
		      logger.info(err.getMessage());
		    }
	}else

    {
	logger.info("SQL query " + fileName + " file doesn't exist");
    }
}

}