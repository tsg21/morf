package com.chpconsulting.cryo.model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.thoughtworks.xstream.XStream;



/**
 * Bean that holds all user maintainable settings for Cryo.
 *
 * <p>This bean provides a persistence mechanism between sessions.</p>
 *
 * @author Copyright (c) CHP Consulting Ltd. 2010
 */
public class CommandBean {

  /**
   * Database connection details for source.
   */
  private final DatabaseEndPoint databaseSource = new DatabaseEndPoint();

  /**
   * Database connection details for target.
   */
  private final DatabaseEndPoint databaseTarget = new DatabaseEndPoint();

  /**
   * Xml file details for source.
   */
  private final XmlEndPoint xmlSource = new XmlEndPoint();

  /**
   * Xml file details for target.
   */
  private final XmlEndPoint xmlTarget = new XmlEndPoint();

  /**
   * Excel file details for source.
   */
  private final ExcelStartPoint excelSource = new ExcelStartPoint();

  /**
   * Excel file details for target.
   */
  private final ExcelEndPoint excelTarget = new ExcelEndPoint();

  /**
   * Type of data source to be used.
   */
  private CryoEndPointType sourceType = CryoEndPointType.DATABASE;

  /**
   * Type of data target to be used.
   */
  private CryoEndPointType targetType = CryoEndPointType.DATABASE;

  /**
   * @return the sourceType
   */
  public CryoEndPointType getSourceType() {
    return sourceType;
  }

  /**
   * @param sourceType the sourceType to set
   */
  public void setSourceType(CryoEndPointType sourceType) {
    this.sourceType = sourceType;
  }

  /**
   * @return the targetType
   */
  public CryoEndPointType getTargetType() {
    return targetType;
  }

  /**
   * @param targetType the targetType to set
   */
  public void setTargetType(CryoEndPointType targetType) {
    this.targetType = targetType;
  }

  /**
   * @return the databaseSource
   */
  public DatabaseEndPoint getDatabaseSource() {
    return databaseSource;
  }

  /**
   * @return the databaseTarget
   */
  public DatabaseEndPoint getDatabaseTarget() {
    return databaseTarget;
  }

  /**
   * @return the xmlSource
   */
  public XmlEndPoint getXmlSource() {
    return xmlSource;
  }

  /**
   * @return the xmlTarget
   */
  public XmlEndPoint getXmlTarget() {
    return xmlTarget;
  }

  /**
   * @return the excelSource
   */
  public ExcelStartPoint getExcelSource() {
    return excelSource;
  }

  /**
   * @return the excelTarget
   */
  public ExcelEndPoint getExcelTarget() {
    return excelTarget;
  }


  /**
   * Load this configuration.
   *
   * @param fromFile The source file
   * @throws IOException On exception
   */
  public void loadFrom(File fromFile) throws IOException {
    FileReader fileReader = new FileReader(fromFile);
    try {
      new XStream().fromXML(fileReader, this);
    } finally {
      fileReader.close();
    }
  }


  /**
   * Save this configuration.
   *
   * @param toFile The destination file
   * @throws IOException On exception
   */
  public void saveTo(File toFile) throws IOException {
    FileWriter fileWriter = new FileWriter(toFile);
    try {
      new XStream().toXML(this, fileWriter);
    } finally {
      fileWriter.close();
    }
  }
}
