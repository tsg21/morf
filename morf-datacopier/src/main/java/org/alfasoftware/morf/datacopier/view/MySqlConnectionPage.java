package com.chpconsulting.cryo.view;

import org.alfasoftware.morf.jdbc.AbstractConnectionResources;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard page for configuring a database connection to a MySQL database.
 *
 * @author Copyright (c) CHP Consulting Ltd. 2009
 */
public class MySqlConnectionPage extends DatabaseConnectionPage {

  /**
   * Creates a connection details page backed by the bean class <var>connectionDetails</var>
   * that is specific to MySQL databases.
   *
   * @param pageName The unique wizard page name.
   * @param connectionDetails Database connection details to bind to.
   */
  public MySqlConnectionPage(String pageName, AbstractConnectionResources connectionDetails) {
    super(pageName, connectionDetails);
  }


  /**
   * Creates fields on the specified composite to allow the user to enter a JDBC URL.
   *
   * @see com.chpconsulting.cryo.view.CryoWizardPage#createChildControls(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected void createChildControls(Composite parent) {
    Text jdbcUrl = createJdbcUrlField(parent);
    createHostNameField(parent).addListener(SWT.Modify, createJdbcUrlUpdateListener(jdbcUrl));
    createPortField(parent).addListener(SWT.Modify, createJdbcUrlUpdateListener(jdbcUrl));

    // Database name is specific to MySql (other implementations use different names)
    createLabel(parent, "database:");
    final Text database = new Text(parent, SWT.SINGLE | SWT.BORDER);
    database.setLayoutData(new GridData(USER_CREDENTIALS_FIELD_WIDTH, SWT.DEFAULT));
    database.setText(StringUtils.defaultString(connectionDetails.getDatabaseName()));
    database.addModifyListener(new ModifyListener(){
      @Override
      public void modifyText(ModifyEvent e) {
        connectionDetails.setDatabaseName(database.getText());
      }
    });
    database.addListener(SWT.Modify, createJdbcUrlUpdateListener(jdbcUrl));

    createUserNameField(parent);
    createPasswordField(parent);
    super.createChildControls(parent);
  }


  /**
   * @see org.eclipse.jface.dialogs.DialogPage#getDescription()
   */
  @Override
  public String getDescription() {
    return "Configure the MySQL database connection";
  }

  /**
   * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
   */
  @Override
  public boolean isPageComplete() {
    return hasHostName() && hasDatabaseName() && hasUserDetails();
  }
}
