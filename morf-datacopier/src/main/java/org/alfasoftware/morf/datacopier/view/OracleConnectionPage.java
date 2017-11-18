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
 * Wizard page for configuring a database connection to an Oracle database.
 *
 * @author Copyright (c) CHP Consulting Ltd. 2009
 */
public class OracleConnectionPage extends DatabaseConnectionPage {

  /**
   * Creates a connection details page backed by the bean class <var>connectionDetails</var>
   * that is specific to Oracle databases.
   *
   * @param pageName The unique wizard page name.
   * @param connectionDetails Database connection details to bind to.
   */
  public OracleConnectionPage(String pageName, AbstractConnectionResources connectionDetails) {
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

    // Instance name
    createLabel(parent, "instance:");
    final Text instance = new Text(parent, SWT.SINGLE | SWT.BORDER);
    instance.setLayoutData(new GridData(DEFAULT_FIELD_WIDTH, SWT.DEFAULT));
    instance.setText(StringUtils.defaultString(connectionDetails.getInstanceName()));
    instance.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        connectionDetails.setInstanceName(instance.getText());
      }
    });
    instance.addListener(SWT.Modify, createJdbcUrlUpdateListener(jdbcUrl));

    createSchemaField(parent);
    createUserNameField(parent);
    createPasswordField(parent);

    super.createChildControls(parent);
  }


  /**
   * @see org.eclipse.jface.dialogs.DialogPage#getDescription()
   */
  @Override
  public String getDescription() {
    return "Configure the Oracle database connection";
  }

  /**
   * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
   */
  @Override
  public boolean isPageComplete() {
    return hasHostName() && hasInstanceName() && hasUserDetails();
  }
}
