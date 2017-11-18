package com.chpconsulting.cryo.model;

import java.io.File;
import java.net.MalformedURLException;

import org.alfasoftware.morf.dataset.DataSetConsumer;
import org.alfasoftware.morf.dataset.DataSetProducer;
import org.alfasoftware.morf.xml.XmlDataSetConsumer;
import org.alfasoftware.morf.xml.XmlDataSetProducer;

/**
 * Defines an end point that can produce or consumer XML data sets.
 *
 * <p>This class exists to provide a mechanism for storing user entered settings.</p>
 *
 * @author Copyright (c) CHP Consulting Ltd. 2010
 */
public class XmlEndPoint implements CryoEndPoint {

  /**
   * Enumerates the mechanisms available to store data set XML on disk.
   *
   * @author Copyright (c) CHP Consulting Ltd. 2010
   */
  public static enum XmlFileFormat {

    /**
     * Indicates the entire data set is stored in a single zip archive.
     */
    ZIP_ARCHIVE,

    /**
     * Indicates the data set is stored in a directory with one file per database table.
     */
    DIRECTORY;

  }

  /**
   * Indicates the file format used to store the XML.
   */
  private XmlFileFormat fileFormat = XmlFileFormat.ZIP_ARCHIVE;

  /**
   * Holds the path to the xml directory or file.
   */
  private String xmlPath;

  /**
   * @see com.chpconsulting.cryo.model.CryoEndPoint#asDataSetConsumer()
   */
  @Override
  public DataSetConsumer asDataSetConsumer() {
    return new XmlDataSetConsumer(new File(xmlPath));
  }

  /**
   * @see com.chpconsulting.cryo.model.CryoEndPoint#asDataSetProducer()
   */
  @Override
  public DataSetProducer asDataSetProducer() {
    try {
      return new XmlDataSetProducer(new File(xmlPath).toURI().toURL());
    } catch (MalformedURLException e) {
      throw new RuntimeException("Error creating XML data set producer", e);
    }
  }

  /**
   * @return the fileFormat
   */
  public XmlFileFormat getFileFormat() {
    return fileFormat;
  }

  /**
   * @param fileFormat the fileFormat to set
   */
  public void setFileFormat(XmlFileFormat fileFormat) {
    this.fileFormat = fileFormat;
  }

  /**
   * @return the xmlPath
   */
  public String getXmlPath() {
    return xmlPath;
  }

  /**
   * @param xmlPath the xmlPath to set
   */
  public void setXmlPath(String xmlPath) {
    this.xmlPath = xmlPath;
  }


  /**
   * @return True if a data set consumer can be created based on current property values.
   */
  public boolean canCreateConsumer() {
    if (xmlPath != null) {
      File xmlFile = new File(xmlPath);
      if (fileFormat == XmlFileFormat.ZIP_ARCHIVE) {
        // We can create the file so just need the directory to exist
        xmlFile = xmlFile.getParentFile();
      }
      return xmlFile.exists() && xmlFile.isDirectory();
    } else {
      return false;
    }
  }


  /**
   * @return True if a data set producer can be created based on current property values.
   */
  public boolean canCreateProducer() {
    if (xmlPath != null) {
      File xmlFile = new File(xmlPath);
      return xmlFile.exists() && xmlFile.isDirectory() == (fileFormat == XmlFileFormat.DIRECTORY);
    } else {
      return false;
    }
  }
}
