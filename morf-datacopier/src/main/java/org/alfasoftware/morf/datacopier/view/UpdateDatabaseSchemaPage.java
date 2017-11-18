package com.chpconsulting.cryo.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import com.chpconsulting.cryo.model.DatabaseEndPoint;

/**
 * Page to configure whether the schema should be updated
 *
 * @author Copyright (c) CHP Consulting Ltd. 2010
 */
public class UpdateDatabaseSchemaPage extends CryoWizardPage {

  /**
   * The target database endpoint
   */
  private final DatabaseEndPoint databaseEndPoint;

  /**
   * @param pageName The page name
   * @param databaseEndPoint The target database endpoint
   */
  public UpdateDatabaseSchemaPage(String pageName, DatabaseEndPoint databaseEndPoint) {
    super(pageName);
    this.databaseEndPoint = databaseEndPoint;
  }


  /**
   * @see com.chpconsulting.cryo.view.CryoWizardPage#createChildControls(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected void createChildControls(Composite parent) {
    createUpdateSchemaField(parent);
  }


  /**
   * @see com.chpconsulting.cryo.view.CryoWizardPage#getIconName()
   */
  @Override
  protected String getIconName() {
    return "data_connection.ico";
  }

  /**
   * @see org.eclipse.jface.dialogs.DialogPage#getTitle()
   */
  @Override
  public String getTitle() {
    return "Update Schema";
  }

  /**
   * @see org.eclipse.jface.dialogs.DialogPage#getDescription()
   */
  @Override
  public String getDescription() {
    return "Update the target database schema to match the source data set?";
  }

  /**
   * @param parent The composite on which to put the field
   */
  private void createUpdateSchemaField(Composite parent) {
    createLabel(parent, "update schema:");

    final Button updateSchema = new Button(parent, SWT.CHECK);
    updateSchema.setLayoutData(new GridData(USER_CREDENTIALS_FIELD_WIDTH, SWT.DEFAULT));
    updateSchema.setSelection(databaseEndPoint.isUpdateSchema());
    updateSchema.addSelectionListener(new SelectionListener() {

      @Override
      public void widgetDefaultSelected(SelectionEvent arg0) {
        // not used
      }

      @Override
      public void widgetSelected(SelectionEvent arg0) {
        databaseEndPoint.setUpdateSchema(updateSchema.getSelection());
      }
    });
  }

}
