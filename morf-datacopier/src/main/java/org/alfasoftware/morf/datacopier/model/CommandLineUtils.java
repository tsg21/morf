package com.chpconsulting.cryo.model;

import java.util.Properties;

import org.alfasoftware.morf.dataset.DataSetConsumer;
import org.alfasoftware.morf.dataset.DataSetProducer;
import org.alfasoftware.morf.jdbc.AbstractConnectionResources;
import org.alfasoftware.morf.jdbc.ConnectionResourcesBean;
import org.alfasoftware.morf.jdbc.DatabaseType;
import org.eclipse.core.runtime.IProgressMonitor;

import com.chpconsulting.cryo.model.XmlEndPoint.XmlFileFormat;

/**
 * Utilities for the Command Line Interface (CLI) to Cryo.
 *
 * @author Copyright (c) CHP Consulting Ltd. 2012
 */
public class CommandLineUtils {

  /**
   * Creates a dataset producer (source) based on the settings in the properties file.
   *
   * @param properties the properties to read the configuration from.
   * @return a dataset producer.
   */
  public static DataSetProducer createProducer(Properties properties) {
    return createCryoEndPoint(properties, "source").asDataSetProducer();
  }


  /**
   * Creates a dataset consumer (destination) based on the settings in the properties file.
   *
   * @param properties the properties to read the configuration from.
   * @return a dataset consumer.
   */
  public static DataSetConsumer createConsumer(Properties properties) {
    return createCryoEndPoint(properties, "destination").asDataSetConsumer();
  }


  /**
   * Creates an end point from the properties using the values with a given prefi.
   *
   * @param properties the properties to read the configuration from.
   * @param prefix the prefix (e.g. source or destination).
   * @return a {@link CryoEndPoint}.
   */
  public static CryoEndPoint createCryoEndPoint(Properties properties, String prefix) {
    if (!properties.containsKey(prefix + ".type")) {
      throw new IllegalStateException("You must specify a database type in " + prefix + ".type");
    }

    String type = properties.getProperty(prefix + ".type");

    if (type.equalsIgnoreCase("database")) {
      return createDatabaseEndPoint(properties, prefix);
    }

    if (type.equalsIgnoreCase("xml")) {
      return createXMLEndPoint(properties, prefix);
    }

    throw new IllegalArgumentException("Cannot create an end point of type [" + type + "] as specified by " + prefix + ".type");
  }


  /**
   * Creates an XML end point from the properties using the values with a given prefix.
   *
   * @param properties the properties to read the configuration from.
   * @param prefix the prefix (e.g. source or destination).
   * @return an {@link XmlEndPoint}.
   */
  public static XmlEndPoint createXMLEndPoint(Properties properties, String prefix) {
    XmlEndPoint endPoint = new XmlEndPoint();

    if (!properties.containsKey(prefix + ".xml.file") && !properties.containsKey(prefix + ".xml.directory")) {
      throw new IllegalStateException("You must specify either " + prefix + ".xml.file or " + prefix + ".xml.directory");
    }

    if (properties.containsKey(prefix + ".xml.file") && properties.containsKey(prefix + ".xml.directory")) {
      throw new IllegalStateException("You must not specify both of either " + prefix + ".xml.file or " + prefix + ".xml.directory");
    }

    if (properties.containsKey(prefix + ".xml.file")) {
      endPoint.setFileFormat(XmlFileFormat.ZIP_ARCHIVE);
      endPoint.setXmlPath(properties.getProperty(prefix + ".xml.file"));
    }

    if (properties.containsKey(prefix + ".xml.directory")) {
      endPoint.setFileFormat(XmlFileFormat.DIRECTORY);
      endPoint.setXmlPath(properties.getProperty(prefix + ".xml.directory"));
    }

    return endPoint;
  }


  /**
   * Creates a database end point from the properties using the values with a given prefix.
   *
   * @param properties the properties to read the configuration from.
   * @param prefix the prefix (e.g. source or destination).
   * @return a {@link DatabaseEndPoint}.
   */
  public static DatabaseEndPoint createDatabaseEndPoint(Properties properties, String prefix) {
    DatabaseEndPoint endPoint = new DatabaseEndPoint();

    if (!properties.containsKey(prefix + ".database.url")) {
      throw new IllegalStateException("You must specify a JDBC url in " + prefix + ".database.url");
    }

    String url = properties.getProperty(prefix + ".database.url");

    AbstractConnectionResources connectionDetails = new ConnectionResourcesBean(DatabaseType.Registry.parseJdbcUrl(url));

    DatabaseType databaseType = DatabaseType.Registry.findByIdentifier(connectionDetails.getDatabaseType());

    endPoint.setConnectionDetailsFor(databaseType.identifier(), connectionDetails);

    if (properties.containsKey(prefix + ".database.username")) {
      connectionDetails.setUserName(properties.getProperty(prefix + ".database.username"));
    } else {
      throw new IllegalStateException("User name must be specified");
    }

    if (properties.containsKey(prefix + ".database.password")) {
      connectionDetails.setPassword(properties.getProperty(prefix + ".database.password"));
    }

    if (properties.containsKey(prefix + ".database.schemaName")) {
      connectionDetails.setSchemaName(properties.getProperty(prefix + ".database.schemaName"));
    }

    if (properties.containsKey(prefix + ".database.updateSchema")) {
      endPoint.setUpdateSchema(Boolean.valueOf(properties.getProperty(prefix + ".database.updateSchema")));
    }

    endPoint.setSelectedDatabaseType(databaseType.identifier());

    return endPoint;
  }


  /**
   * A progress monitor which writes progress to command line.
   *
   * @author Copyright (c) CHP Consulting Ltd. 2012
   */
  public static class ProgressMonitor implements IProgressMonitor {

    /** Progress limit. */
    private int maxProgress;

    /** Current position of progress monitor. */
    private int currentProgress;

    /** ...as dots. */
    private long numberOfDots;

    /**
     * @see org.eclipse.core.runtime.IProgressMonitor#beginTask(java.lang.String, int)
     */
    @Override
    public void beginTask(String arg0, int arg1) {
      maxProgress = arg1;
    }

    /**
     * @see org.eclipse.core.runtime.IProgressMonitor#done()
     */
    @Override
    public void done() {
      // Ignore
    }

    /**
     * @see org.eclipse.core.runtime.IProgressMonitor#internalWorked(double)
     */
    @Override
    public void internalWorked(double arg0) {
    }

    /**
     * @see org.eclipse.core.runtime.IProgressMonitor#isCanceled()
     */
    @Override
    public boolean isCanceled() {
      return false;
    }

    /**
     * @see org.eclipse.core.runtime.IProgressMonitor#setCanceled(boolean)
     */
    @Override
    public void setCanceled(boolean arg0) {
      // Do nothing
    }

    /**
     * @see org.eclipse.core.runtime.IProgressMonitor#setTaskName(java.lang.String)
     */
    @Override
    public void setTaskName(String arg0) {
      // Do nothing
    }

    /**
     * @see org.eclipse.core.runtime.IProgressMonitor#subTask(java.lang.String)
     */
    @Override
    public void subTask(String arg0) {
      // Do nothing
    }

    /**
     * @see org.eclipse.core.runtime.IProgressMonitor#worked(int)
     */
    @Override
    public void worked(int arg0) {
      currentProgress+=arg0;

      long newNumberOfDots = Math.round((double)currentProgress / maxProgress * 50);
      if (newNumberOfDots > numberOfDots) {
        numberOfDots = newNumberOfDots;
        System.out.print("\r[");
        int i = 0;
        for (; i < newNumberOfDots; i++) {
          System.out.print(".");
        }
        for (; i < 50; i++) {
          System.out.print(" ");
        }
        System.out.print("]");
      }
    }
  }
}