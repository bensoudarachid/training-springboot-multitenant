package com.royasoftware;


import java.io.File;
import java.io.FileWriter;
import java.net.URL;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.ImprovedNamingStrategy;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.service.ServiceRegistryBuilder;
import org.hibernate.tool.hbm2ddl.DatabaseMetadata;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

/**
 * @author john.thompson
 *
 */
public class SchemaGenerator
{
  private AnnotationConfiguration cfg;
 
  public SchemaGenerator(String packageName) throws Exception
  {
    cfg = new AnnotationConfiguration();
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
        Configuration configuration = new Configuration();
        configuration.configure("hibernate.cfg.xml");
        configuration.setNamingStrategy(ImprovedNamingStrategy.INSTANCE);
        Dialect dialect = Dialect.getDialect(configuration.getProperties()); 
        ServiceRegistryBuilder serviceRegistryBuilder = new ServiceRegistryBuilder().applySettings(configuration
            .getProperties());
        SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor)configuration
            .buildSessionFactory(serviceRegistryBuilder.buildServiceRegistry());
        Connection connection = sessionFactory.getConnectionProvider().getConnection();
//        Connection connection = dataSource.getConnection();
        DatabaseMetadata meta = new DatabaseMetadata(connection, dialect);
        String[] createSQL = configuration.generateSchemaUpdateScript(dialect, meta);
        
        FileWriter fw = new FileWriter("V__upd.sql");

        System.out.println("write to file");
        for (int i = 0; i < createSQL.length; i++) {
          fw.write(createSQL[i] + ";\n");
        }
        fw.close();
        System.out.println("Finished");
        System.exit(0);
    } catch (Exception e) {
        System.out.println("Database unreachable.");
        e.printStackTrace();
    }
    
  }
 
  /**
   * @param args
   */
  public static void main(String[] args) throws Exception
  {
    SchemaGenerator gen = new SchemaGenerator("com.royasoftware.model");
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
      throw new ClassNotFoundException(packageName
          + " is not a valid package");
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
