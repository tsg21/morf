package com.chpconsulting.cryo.model;

import org.alfasoftware.morf.dataset.DataSetConsumer;
import org.alfasoftware.morf.dataset.DataSetProducer;

/**
 * Provides data set resources. This allows each type of data set
 * to serve as both the start point and end point in a Cryo data operation.
 *
 * @author Copyright (c) CHP Consulting Ltd. 2010
 */
public interface CryoEndPoint {

  /**
   * @return A data set producer based on the target.
   */
  public DataSetProducer asDataSetProducer();

  /**
   * @return A data set consumer based on the target.
   */
  public DataSetConsumer asDataSetConsumer();

}
