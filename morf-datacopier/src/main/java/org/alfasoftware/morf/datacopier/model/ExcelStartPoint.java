package com.chpconsulting.cryo.model;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import org.alfasoftware.morf.dataset.DataSetConsumer;
import org.alfasoftware.morf.dataset.DataSetProducer;
import org.alfasoftware.morf.excel.SpreadsheetDataSetProducer;

/**
 * Defines an end point that can produce or consumer Excel data sets.
 *
 * <p>This class exists to provide a mechanism for storing user entered settings.</p>
 *
 * @author Copyright (c) CHP Consulting Ltd. 2010
 */
public class ExcelStartPoint implements CryoEndPoint {

  /**
   * Holds the spread sheet folder name.
   */
  private String folderName;


  /**
   * @see com.chpconsulting.cryo.model.CryoEndPoint#asDataSetConsumer()
   */
  @Override
  public DataSetConsumer asDataSetConsumer() {
    throw new RuntimeException("Cannot be used as a consumer");
  }


  /**
   * @see com.chpconsulting.cryo.model.CryoEndPoint#asDataSetProducer()
   */
  @Override
  public DataSetProducer asDataSetProducer() {
    File folder = new File(folderName);
    File[] files = folder.listFiles((FileFilter)new WildcardFileFilter("*.xls",IOCase.INSENSITIVE));
    InputStream[] streams = new InputStream[files.length];
    System.out.println("Processing the following Excel files:");
    SpreadsheetDataSetProducer producer;
    try {
      for (int i = 0; i < files.length; i++) {
        System.out.println(files[i].getPath());
        streams[i] = new FileInputStream(files[i]);
      }
      try {
        producer = new SpreadsheetDataSetProducer(streams);
      } finally {
        for (int i = 0; i < streams.length; i++) {
          streams[i].close();
        }
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return producer;
  }


  /**
   * @return True if enought configuration has been provided to create a data set consumer.
   */
  public boolean canCreateConsumer() {
    return false;
  }

  /**
   * @return True if enought configuration has been provided to create a data set producer.
   */
  public boolean canCreateProducer() {
    return folderName != null && new File(folderName).exists();
  }

  /**
   * @return the folderName
   */
  public String getFolderName() {
    return folderName;
  }

  /**
   * @param folderName the folderName to set
   */
  public void setFolderName(String folderName) {
    this.folderName = folderName;
  }
}
