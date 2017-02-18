package com.royasoftware.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.sql.DataSource;

import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import com.royasoftware.TenantContext;

@Configuration
public class MultitenantDbConfiguration {
	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private DataSourceProperties properties;

	/**
	 * Defines the data source for the application
	 * 
	 * @return
	 */
	@Bean
	@ConfigurationProperties(prefix = "db.datasource")
	public DataSource dataSource() throws Exception {
		String pathString = "db/datasource/tenants";
		logger.info("Search tenant files in path: " + pathString);
		logger.info("Search tenant files . Resource: " + this.getClass().getResource(pathString));

		ClassLoader cl = this.getClass().getClassLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
		Resource[] resources = resolver.getResources("classpath*:" + pathString + "/*.properties");
		HashSet<File> fileVector = new HashSet<File>();
		for (Resource resource : resources) {
			fileVector.add(resource.getFile());
			logger.info("Add tenant name " + resource.getFilename());
		}
		Flyway flyway;
		// flyway.setDataSource("jdbc:mysql://localhost:3306/ryspringoaut1",
		// "root", "1qay2wsx");
		// flyway.setLocation("main.resources.db.migration")
		// flyway.migrate();
		// flyway.setDataSource("jdbc:mysql://localhost:3306/todospring2",
		// "root", "1qay2wsx");
		// flyway.setLocation("main.resources.db.migration")
		// flyway.migrate();

		// Path path =
		// Paths.get(this.getClass().getResource(pathString).toURI());
		// logger.info("Found tenant files number: "+path);
		// File[] files = path.toFile().listFiles();
		// logger.info("Found tenant files number: "+files.length);
		Map<Object, Object> resolvedDataSources = new HashMap<>();

		//Important! Repair default db. 
//		flyway = new Flyway();
//		flyway.setDataSource(properties.getUrl(),properties.getUsername(),properties.getPassword());
//		flyway.setLocations("db.migration");
//		flyway.repair();

		for (File propertyFile : fileVector) {
			Properties tenantProperties = new Properties();
			DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(this.getClass().getClassLoader());
			try {
				tenantProperties.load(new FileInputStream(propertyFile));
				String tenantId = tenantProperties.getProperty("name");
				logger.info("tenantId: " + tenantId);
				flyway = new Flyway();
//				flyway.setValidateOnMigrate(false);
				if (tenantProperties.getProperty("datasource.url") != null)
					flyway.setDataSource(tenantProperties.getProperty("datasource.url"),
							tenantProperties.getProperty("datasource.username"),
							tenantProperties.getProperty("datasource.password"));
				else
					flyway.setDataSource("jdbc:mysql://localhost:3306/" + tenantId + "?autoReconnect=true&useSSL=false",
							"root", "1qay2wsx");
				flyway.setLocations(tenantProperties.getProperty("flyway.locations"));
				//Important! Repair dbs.
//				flyway.repair();
				flyway.migrate();

				if (tenantProperties.getProperty("datasource.url") != null)
					dataSourceBuilder.driverClassName(properties.getDriverClassName())
							.url(tenantProperties.getProperty("datasource.url"))
							.username(tenantProperties.getProperty("datasource.username"))
							.password(tenantProperties.getProperty("datasource.password"));
				else
					dataSourceBuilder.driverClassName(properties.getDriverClassName())
							.url("jdbc:mysql://localhost:3306/" + tenantId + "?autoReconnect=true&useSSL=false")
							.username("root").password("1qay2wsx");

				if (properties.getType() != null) {
					dataSourceBuilder.type(properties.getType());
				}
				resolvedDataSources.put(tenantId, dataSourceBuilder.build());
			} catch (IOException e) {
				// Ooops, tenant could not be loaded. This is bad.
				// Stop the application!
				e.printStackTrace();
				return null;
			}
		}

		TenantContext.setValidTenants(resolvedDataSources.keySet());
		// Create the final multi-tenant source.
		// It needs a default database to connect to.
		// Make sure that the default database is actually an empty tenant
		// database.
		// Don't use that for a regular tenant if you want things to be safe!
		MultitenantDataSource dataSource = new MultitenantDataSource();
		dataSource.setDefaultTargetDataSource(defaultDataSource());
		dataSource.setTargetDataSources(resolvedDataSources);
		// Call this to finalize the initialization of the data source.
		dataSource.afterPropertiesSet();
		return dataSource;
	}

	/**
	 * Creates the default data source for the application
	 * 
	 * @return
	 */
	private DataSource defaultDataSource() {
		DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(this.getClass().getClassLoader())
				.driverClassName(properties.getDriverClassName()).url(properties.getUrl())
				.username(properties.getUsername()).password(properties.getPassword());
		if (properties.getType() != null) {
			dataSourceBuilder.type(properties.getType());
		}
		return dataSourceBuilder.build();
	}
}