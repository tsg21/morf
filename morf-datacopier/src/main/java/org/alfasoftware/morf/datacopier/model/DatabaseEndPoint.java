package com.chpconsulting.cryo.model;

import java.util.HashMap;
import java.util.Map;

import org.alfasoftware.morf.dataset.DataSetConsumer;
import org.alfasoftware.morf.dataset.DataSetProducer;
import org.alfasoftware.morf.jdbc.AbstractConnectionResources;
import org.alfasoftware.morf.jdbc.ConnectionResourcesBean;
import org.alfasoftware.morf.jdbc.DatabaseDataSetConsumer;
import org.alfasoftware.morf.jdbc.DatabaseDataSetProducer;
import org.alfasoftware.morf.jdbc.SchemaModificationAdapter;
import org.alfasoftware.morf.jdbc.SqlScriptExecutorProvider;
import org.alfasoftware.morf.jdbc.mysql.MySql;

import com.google.inject.util.Providers;

/**
 * Bean class that stores all the parameters requried to connect to accepted when running cryo.
 * <p>
 * The {@linkplain DatabaseEndPoint} is used when running Cryo from the command line
 * and when running the Cryo wizard.
 * </p>
 *
 * @author Copyright (c) CHP Consulting Ltd. 2009
 */
public class DatabaseEndPoint implements CryoEndPoint {

  /**
   * The currently active database type.
   */
  private String selectedDatabaseType = MySql.IDENTIFIER;

  /**
   * Data base connection details used to create producers and consumers.
   */
  private final Map<String, AbstractConnectionResources> connectionDetails = new HashMap<>();

  /**
   * Indicates whether the database schema should be updated when an import is run
   */
  private boolean updateSchema = true;


  /**
   * @see com.chpconsulting.cryo.model.CryoEndPoint#asDataSetConsumer()
   */
  @Override
  public DataSetConsumer asDataSetConsumer() {
    final AbstractConnectionResources connectionResources = connectionDetails.get(selectedDatabaseType);
    SqlScriptExecutorProvider sqlScriptExecutorProvider = new SqlScriptExecutorProvider(connectionResources.getDataSource(), Providers.of(connectionResources.sqlDialect()));

    DatabaseDataSetConsumer databaseDataSetConsumer = new DatabaseDataSetConsumer(connectionResources, sqlScriptExecutorProvider);
    if (updateSchema) {
      return new SchemaModificationAdapter(databaseDataSetConsumer);
    } else {
      return databaseDataSetConsumer;
    }
  }

  /**
   * @see com.chpconsulting.cryo.model.CryoEndPoint#asDataSetProducer()
   */
  @Override
  public DataSetProducer asDataSetProducer() {
    return new DatabaseDataSetProducer(connectionDetails.get(selectedDatabaseType));
  }

  /**
   * @return The currently selected database conneciton details bean.
   */
  public AbstractConnectionResources selectedConnectionDetails() {
    return connectionDetails(selectedDatabaseType);
  }

  /**
   * @return the selectedDatabaseType
   */
  public String getSelectedDatabaseType() {
    return selectedDatabaseType;
  }

  /**
   * @param newSelectedDatabaseType the selectedDatabaseType to set
   */
  public void changeSelectedDatabaseType(String newSelectedDatabaseType) {
    if (newSelectedDatabaseType == null) {
      throw new IllegalArgumentException("Cannot select a null database type");
    }

    this.selectedDatabaseType = newSelectedDatabaseType;
  }

  /**
   * Get connection details for a specific database type.
   *
   * @param type The database type for which details are required.
   * @return A Connection details bean for the database type.
   */
  public AbstractConnectionResources connectionDetails(String type) {
    AbstractConnectionResources connectionDetailsForType = this.connectionDetails.get(type);
    if (connectionDetailsForType == null) {
      connectionDetailsForType = new ConnectionResourcesBean();
      connectionDetailsForType.setDatabaseType(type);
      this.connectionDetails.put(type, connectionDetailsForType);
    }
    return connectionDetailsForType;
  }


  /**
   * Sets the connection details for a specified database type.
   *
   * @param type the type of database.
   * @param connectionDetails the connection details to use.
   */
  public void setConnectionDetailsFor(String type, AbstractConnectionResources connectionDetails) {
    this.connectionDetails.put(type, connectionDetails);
  }


  /**
   * @return whether the database schema should be updated when an import is run.
   */
  public boolean isUpdateSchema() {
    return updateSchema;
  }


  /**
   * @param updateSchema set whether the database schema should be updated when an import is run.
   */
  public void setUpdateSchema(boolean updateSchema) {
    this.updateSchema = updateSchema;
  }


  /**
   * @param selectedDatabaseType the selectedDatabaseType to set
   */
  public void setSelectedDatabaseType(String selectedDatabaseType) {
    this.selectedDatabaseType = selectedDatabaseType;
  }
}
