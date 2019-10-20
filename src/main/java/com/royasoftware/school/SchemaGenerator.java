package com.royasoftware.school;


import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.boot.registry.internal.StandardServiceRegistryImpl;
import org.hibernate.boot.spi.MetadataImplementor;
import org.hibernate.cfg.Configuration;
import org.hibernate.internal.SessionImpl;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.schema.TargetType;

/**
 * @author john.thompson
 *
 */
public class SchemaGenerator
{
  private Configuration cfg;
 
  public SchemaGenerator(String packageName) throws Exception
  {
    cfg = new Configuration();
    cfg.setProperty("hibernate.hbm2ddl.auto","none");
  
    for(Class<Object> clazz : getClasses(packageName))
    {
      cfg.addAnnotatedClass(clazz);
    }
  }
 
  /**
   * Method that actually creates the file.  
   * @param dbDialect to use
   */
  private void generate()
  {
//    cfg.setProperty("hibernate.dialect", dialect.getDialectClass());
  
//    SchemaUpdate export = new SchemaUpdate(cfg);
//    SchemaExport export = new SchemaExport(cfg);
//    export.setDelimiter(";");
//    export.setOutputFile("ddl_" + dialect.name().toLowerCase() + ".sql");
//    export.execute(false, false);
    
    try {
    	
    	ServiceRegistry serviceRegistry = (StandardServiceRegistryImpl) new StandardServiceRegistryBuilder()
        .configure("hibernate.cfg.xml")
        .build();
    	
//        Configuration configuration = new Configuration().configure();
//        configuration.configure("hibernate.cfg.xml");
//        configuration.setNamingStrategy(ImprovedNamingStrategy.INSTANCE);
//        Dialect dialect = Dialect.getDialect(configuration.getProperties());

//        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().
//        		 applySettings(configuration.getProperties()).build(); 

        		 // Create session factory instance
//        SessionFactory sessionFactory = configuration.buildSessionFactory(serviceRegistry);

        
//        Connection connection = ((SessionImpl)sessionFactory.openSession()).connection();
//        Connection connection = dataSource.getConnection();
//        DatabaseMetadata meta = new DatabaseMetadata(connection, dialect);
//        MetadataSources meta = new MetadataSources(serviceRegistry);

    	 MetadataImplementor metadata = (MetadataImplementor) new MetadataSources(serviceRegistry).buildMetadata();
//         SchemaExport schemaExport = new SchemaExport(metadata);
    	 
//         SchemaUpdate schemaExport = new SchemaUpdate(metadata);
         new SchemaUpdate()
        		 .setDelimiter(";").setOutputFile("V__upd.sql")
			.execute( EnumSet.of( TargetType.SCRIPT ), metadata, serviceRegistry );
         
//        SchemaExport export = new SchemaExport(
//                (MetadataImplementor) meta.buildMetadata()
//        );
        

//        schemaExport.setDelimiter(";");
//        schemaExport.setOutputFile("V__upd.sql");
        
//        schemaExport.setFormat(true);
//        schemaExport.execute(true, false, false, false);
//        schemaExport.execute(true, false);
//        schemaExport.create(true, true);
        
        ( (StandardServiceRegistryImpl) serviceRegistry ).destroy();
//        String[] createSQL = configuration.generateSchemaUpdateScript(dialect, meta);
//        
//        FileWriter fw = new FileWriter("V__upd.sql");
//
//        System.out.println("write to file");
//        for (int i = 0; i < createSQL.length; i++) {
//          fw.write(createSQL[i] + ";\n");
//        }
//        fw.close();
        System.out.println("Finished");
    } catch (Exception e) {
        System.out.println("Database unreachable.");
        e.printStackTrace();
    }finally{
        System.exit(0);
    }
    
  }
 
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception
  {
    SchemaGenerator gen = new SchemaGenerator("com.royasoftware.school.model");
    gen.generate();
//    gen.generate(Dialect.ORACLE);
//    gen.generate(Dialect.HSQL);
  }

  /**
   * Utility method used to fetch Class list based on a package name.
   * @param packageName (should be the package containing your annotated beans.
   */
  private List<Class> getClasses(String packageName) throws Exception
  {
    List<Class> classes = new ArrayList<Class>();
    File directory = null;
    try 
    {
      ClassLoader cld = Thread.currentThread().getContextClassLoader();
      if (cld == null) {
        throw new ClassNotFoundException("Can't get Class loader.");
      }
      String path = packageName.replace('.', '/');
      URL resource = cld.getResource(path);
      if (resource == null) {
        throw new ClassNotFoundException("No resource for " + path);
      }
      directory = new File(resource.getFile());
    } catch (NullPointerException x) {
      throw new ClassNotFoundException(packageName + " (" + directory
          + ") does not appear to be a valid package");
    }
    if (directory.exists()) {
      String[] files = directory.list();
      for (int i = 0; i < files.length; i++) {
        if (files[i].endsWith(".class")) {
          // removes the .class extension
          classes.add(Class.forName(packageName + '.'
              + files[i].substring(0, files[i].length() - 6)));
        }
      }
    } else {
      throw new ClassNotFoundException(packageName+ " is not a valid package");
    }
  
    return classes;
  }
 
  /**
   * Holds the classnames of hibernate dialects for easy reference.
   */
//  private static enum Dialect 
//  {
//    ORACLE("org.hibernate.dialect.Oracle10gDialect"),
//    MYSQL("org.hibernate.dialect.MySQLDialect"),
//    HSQL("org.hibernate.dialect.HSQLDialect");
//  
//    private String dialectClass;
//    private Dialect(String dialectClass)
//    {
//      this.dialectClass = dialectClass;
//    }
//    public String getDialectClass()
//    {
//      return dialectClass;
//    }
//  }
}
