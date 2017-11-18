package com.chpconsulting.cryo.model;



/**
 * Enumerates the possible end points which can produce or receive data sets.
 *
 * @author Copyright (c) CHP Consulting Ltd. 2010
 */
public enum CryoEndPointType {

  /**
   * End points which are database.
   */
  DATABASE,

  /**
   * End points which are collections of XML files.
   */
  XML,

  /**
   * End points which are excel spread sheets.
   */
  EXCEL;

}
