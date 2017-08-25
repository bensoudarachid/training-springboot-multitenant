package com.royasoftware.repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

//import org.apache.tomcat.jdbc.pool.PoolProperties;
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
	// @ Configuration Properties(prefixx = "db.datasource")
	@ConfigurationProperties("spring.datasource")
	public MultitenantDataSource dataSource() throws Exception {
//		PoolProperties p = new PoolProperties();
//        p.setJmxEnabled(true);
//        p.setTestWhileIdle(false);
//        p.setTestOnBorrow(true);
//        p.setValidationQuery("SELECT 1");
//        p.setTestOnReturn(false);
//        p.setValidationInterval(30000);
//        p.setTimeBetweenEvictionRunsMillis(30000);
//        p.setMaxActive(100);
//        p.setInitialSize(10);
//        p.setMaxWait(10000);
//        p.setRemoveAbandonedTimeout(60);
//        p.setMinEvictableIdleTimeMillis(30000);
//        p.setMinIdle(10);
//        p.setLogAbandoned(true);
//        p.setRemoveAbandoned(true);

		
		String pathString = "db/datasource/tenants";
		logger.info("Search tenant files in path: " + pathString);
		logger.info("Search tenant files . Resource: " + this.getClass().getResource(pathString));

		ClassLoader cl = this.getClass().getClassLoader();
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
		Resource[] resources = resolver.getResources("classpath*:" + pathString + "/*.properties");
//		HashSet<File> fileVector = new HashSet<File>();
//		for (Resource resource : resources) {
//			fileVector.add(resource.getFile());
//			logger.info("Add tenant name " + resource.getFilename());
//		}
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

		//!!!!IMPORTANT!!!!! Repair default db. 
//		flyway = new Flyway();
//		flyway.setDataSource(properties.getUrl(),properties.getUsername(),properties.getPassword());
//		flyway.setLocations("db.migration");
//		flyway.repair();
		DataSource ds = null;
		for (Resource propertyFile : resources) {
			Properties tenantProperties = new Properties();
			DataSourceBuilder dataSourceBuilder = new DataSourceBuilder(this.getClass().getClassLoader());
			try {
				tenantProperties.load(propertyFile.getInputStream()); 
				String tenantId = tenantProperties.getProperty("name");
				logger.info("tenantId: " + tenantId +" propertyFile"+ propertyFile.getFilename());
				flyway = new Flyway();
//				flyway.setValidateOnMigrate(false);
				if (tenantProperties.getProperty("datasource.url") != null)
					flyway.setDataSource(resolveEnvVars(tenantProperties.getProperty("datasource.url")),
							tenantProperties.getProperty("datasource.username"),
							tenantProperties.getProperty("datasource.password"));
				else
					flyway.setDataSource("jdbc:mysql://"+System.getenv("MYSQL_HOST")+":3306/" + tenantId + "?autoReconnect=true&useSSL=false",
							"root", "1qay2wsx");
				flyway.setLocations(tenantProperties.getProperty("flyway.locations"));
				//!!!!IMPORTANT!!!!! Repair dbs.
//				flyway.repair();
				try {
					flyway.migrate();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if (tenantProperties.getProperty("datasource.url") != null)
					dataSourceBuilder.driverClassName(properties.getDriverClassName())
							.url(resolveEnvVars(tenantProperties.getProperty("datasource.url")))
							.username(tenantProperties.getProperty("datasource.username"))
							.password(tenantProperties.getProperty("datasource.password"));
				else
					dataSourceBuilder.driverClassName(properties.getDriverClassName())
							.url("jdbc:mysql://"+System.getenv("MYSQL_HOST")+":3306/" + tenantId + "?autoReconnect=true&useSSL=false") //autoReconnect=true&
							.username("root").password("1qay2wsx");

				logger.info("------->properties.getType()="+properties.getType());
				if (properties.getType() != null) {
					dataSourceBuilder.type(properties.getType());
				}
				ds = dataSourceBuilder.build();
				setDataSourcePoolProps(ds);
//				((org.apache.tomcat.jdbc.pool.DataSource)ds).setTestOnBorrow(true);
//				((org.apache.tomcat.jdbc.pool.DataSource)ds).setTestWhileIdle(true);     
////				((org.apache.tomcat.jdbc.pool.DataSource)ds).setTimeBetweenEvictionRunsMillis(5000);
//				((org.apache.tomcat.jdbc.pool.DataSource)ds).setValidationQuery("SELECT 1");
//				((org.apache.tomcat.jdbc.pool.DataSource)ds).setMinIdle(2);
//				((org.apache.tomcat.jdbc.pool.DataSource)ds).setMaxIdle(8);
				resolvedDataSources.put(tenantId, ds);
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
	private String resolveEnvVars(String input)
	{
	    if (null == input)
	    {
	        return null;
	    }
	    // match ${ENV_VAR_NAME} or $ENV_VAR_NAME
	    Pattern p = Pattern.compile("\\$\\{(\\w+)\\}|\\$(\\w+)");
	    Matcher m = p.matcher(input); // get a matcher object
	    StringBuffer sb = new StringBuffer();
	    while(m.find()){
	        String envVarName = null == m.group(1) ? m.group(2) : m.group(1);
	        String envVarValue = System.getenv(envVarName);
	        m.appendReplacement(sb, null == envVarValue ? "" : envVarValue);
	    }
	    m.appendTail(sb);
	    return sb.toString();
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
		logger.info("------->properties.getType() default="+properties.getType());
		if (properties.getType() != null) {
			dataSourceBuilder.type(properties.getType());
		}
		DataSource ds = dataSourceBuilder.build();
		setDataSourcePoolProps(ds);
		return ds;
	}
	
	private void setDataSourcePoolProps(DataSource ds) {
//		((org.apache.tomcat.jdbc.pool.DataSource)ds).setSuspectTimeout(2000);
		((org.apache.tomcat.jdbc.pool.DataSource)ds).setValidationInterval(20000);
		((org.apache.tomcat.jdbc.pool.DataSource)ds).setTestOnBorrow(true);
		((org.apache.tomcat.jdbc.pool.DataSource)ds).setTestWhileIdle(true);     
//		((org.apache.tomcat.jdbc.pool.DataSource)ds).setTimeBetweenEvictionRunsMillis(2000);
		((org.apache.tomcat.jdbc.pool.DataSource)ds).setValidationQuery("SELECT 1");
//		((org.apache.tomcat.jdbc.pool.DataSource)ds).setMinIdle(2);
//		((org.apache.tomcat.jdbc.pool.DataSource)ds).setMaxIdle(8);
	}
	
}