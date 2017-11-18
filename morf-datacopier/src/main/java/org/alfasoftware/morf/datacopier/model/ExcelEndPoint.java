package com.chpconsulting.cryo.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.chpconsulting.cryo.excel.TableConfigurationProvider;
import org.alfasoftware.morf.dataset.DataSetConsumer;
import org.alfasoftware.morf.dataset.DataSetProducer;
import org.alfasoftware.morf.excel.SpreadsheetDataSetConsumer;
import com.google.common.base.Optional;

/**
 * Defines an end point that can produce or consumer Excel data sets.
 *
 * <p>This class exists to provide a mechanism for storing user entered settings.</p>
 *
 * @author Copyright (c) CHP Consulting Ltd. 2010
 */
public class ExcelEndPoint implements CryoEndPoint {

  /**
   * Holds the spread sheet file name.
   */
  private String fileName;

  /**
   * Holds the spread sheet configuration file name.
   */
  private String configurationFileName;

  /**
   * Retained so existing config xml works
   */
  @Deprecated
  public boolean includeJavadoc;

  /**
   * Retained so existing config xml works
   */
  @Deprecated
  public String javadocUrl;

  /**
   * Retained so existing config xml works
   */
  @Deprecated
  public String sourceUrl;


  /**
   * @see com.chpconsulting.cryo.model.CryoEndPoint#asDataSetConsumer()
   */
  @Override
  public DataSetConsumer asDataSetConsumer() {
    try {
      return new SpreadsheetDataSetConsumer(
        new FileOutputStream(fileName),
        Optional.of(new TableConfigurationProvider().load(new FileInputStream(configurationFileName)))
      );
    } catch (FileNotFoundException e) {
      throw new RuntimeException("Error opening spreadsheet file stream", e);
    }
  }


  /**
   * @see com.chpconsulting.cryo.model.CryoEndPoint#asDataSetProducer()
   */
  @Override
  public DataSetProducer asDataSetProducer() {
    throw new UnsupportedOperationException("Data sets cannot be read from spreadsheets");
  }


  /**
   * @return the fileName
   */
  public String getFileName() {
    return fileName;
  }


  /**
   * @param fileName the fileName to set
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }


  /**
   * @return the configurationFileName
   */
  public String getConfigurationFileName() {
    return configurationFileName;
  }


  /**
   * @param configurationFileName the configurationFileName to set
   */
  public void setConfigurationFileName(String configurationFileName) {
    this.configurationFileName = configurationFileName;
  }


  /**
   * @return True if enough configuration has been provided to create a data set consumer.
   */
  public boolean canCreateConsumer() {
    return fileName != null && new File(fileName).getParentFile().exists();
  }

  /**
   * @return True if enough configuration has been provided to create a data set producer.
   */
  public boolean canCreateProducer() {
    return fileName != null && new File(fileName).exists();
  }
}
