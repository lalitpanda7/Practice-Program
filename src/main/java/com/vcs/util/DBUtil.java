package com.vcs.util;

import java.util.HashMap;
import java.util.Set;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DBUtil {

	@Autowired JdbcTemplate jdbcTemplate;
	@Autowired ResourceReader resourceReader;
	
	private static final Logger LOGGER = LogManager.getLogger(DBUtil.class);

	/*This utility method reads sql files and executes sql line by line
	 * @Param : sqlFileNames - all the sql files in format directory name-filename
	 * */
	public void executeSqlFiles(Set<String> sqlFileNames) {
		sqlFileNames.forEach(file -> {
			try {
				String sql = resourceReader.getData("sql", file); //All the data are fetched
				String[] allSql = sql.split(";");
				Stream.of(allSql).forEach(s -> {
					executeSql(s); 
				});

			} catch (Exception e) {
				LOGGER.error("Error occored while executing sql file, " + file);
				System.out.println(e.getMessage());
				//throw e;
			}
		});
	}
	
	/*This utility method reads one sql file and one stored procedure file and executes them
	 * @Param : sqlFile - sql file name in format directory name-filename
	 * @Param : storedProcName - storedProcName file name in format directory name-filename
	 * */
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void executeSqlAndStoredProc(String sqlFile, String storedProcName) throws Exception {
		try {
			if(sqlFile != null) {
				executeSqlFile(sqlFile);
			}
			if(storedProcName != null) {
				executeStoredProc(storedProcName);
			}
		}catch(Exception e) {
			LOGGER.error("Error occored while executing sql file, " + sqlFile);
			System.out.println(e.getMessage());
			throw e;
		}
	}
	
	/*This utility method reads sql file and executes sql line by line
	 * @Param : sqlFile - sql file name in format directory name-filename
	 * */
	//@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void executeSqlFile(String sqlFile) throws Exception {
		
		try {
			String sql = resourceReader.getData("sql", sqlFile);
			String[] allSql = sql.split(";");
			Stream.of(allSql).forEach(s -> {
				executeSql(s); 
			});

		} catch (Exception e) {
			LOGGER.error("Error occored while executing sql file, " + sqlFile);
			System.out.println(e.getMessage());
			throw e;
		}
	}
	/*This utility method reads Stored procedure name and executes it
	 * @Param : sqlFile - sql file name in format directory name-filename
	 * @Param : storedProcName - stored procedure file path in the format directory name-file name
	 * */
	public void executeStoredProc(String storedProcName) throws Exception {
		try {
			String storedProc = resourceReader.getData("stored-procedure", storedProcName);
			
			SimpleJdbcCall jdbcCall = new SimpleJdbcCall(jdbcTemplate).withProcedureName(storedProc.trim());
				   
			SqlParameterSource in = new MapSqlParameterSource(new HashMap<String, Object>());
			jdbcCall.execute(in);
			
		}catch(Exception e) {
			System.out.println("Exception Occured");
			throw e;
		}
	}
	
	/*This utility executes one sql query using spring jdbc template
	 * @Param : sql - sql query
	 * */
	public void executeSql(String sql) {
		if(sql != null && !sql.equals("")) {
			try {
				jdbcTemplate.execute(sql);
				
			}
			catch(Exception e){
				LOGGER.error("Error occored while executing query, " + sql);
				throw e;
			}
		}
	}
}
