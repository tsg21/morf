package com.chpconsulting.cryo.view;

import org.alfasoftware.morf.jdbc.AbstractConnectionResources;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard page for configuring a database connection to a H2 database.
 *
 * @author Copyright (c) CHP Consulting Ltd. 2009
 */
public class H2ConnectionPage extends DatabaseConnectionPage {

  /**
   * Creates a connection details page backed by the bean class <var>connectionDetails</var>
   * that is specific to MySQL databases.
   *
   * @param pageName The unique wizard page name.
   * @param connectionDetails Database connection details to bind to.
   */
  public H2ConnectionPage(String pageName, AbstractConnectionResources connectionDetails) {
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

    {
      final DirectoryDialog directoryDialog = new DirectoryDialog(parent.getShell());

      // Creates a listener that shows an appropriate dialog when the user clicks the ellipsis.
      FileSelectionListener listener = new FileSelectionListener() {
        /**
         * @see com.chpconsulting.cryo.view.CryoWizardPage.FileSelectionListener#widgetSelected(org.eclipse.swt.widgets.Text, org.eclipse.swt.events.SelectionEvent)
         */
        @Override
        public void widgetSelected(Text text, SelectionEvent event) {
          String selectedFile;
          directoryDialog.setFilterPath(text.getText());
          selectedFile = directoryDialog.open();

          if (selectedFile != null) {
            text.setText(selectedFile);
          }
        }
      };

      // Output the field for the output location
      createLabel(parent, "local directory:");
      final Text outputLocationText = createFileSelectionControl(parent, listener);
      outputLocationText.setText(StringUtils.defaultString(connectionDetails.getInstanceName()));
      outputLocationText.addModifyListener(new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
          connectionDetails.setInstanceName(outputLocationText.getText());
        }
      });
      outputLocationText.addListener(SWT.Modify, createPageCompleteListener());
      outputLocationText.addListener(SWT.Modify, createJdbcUrlUpdateListener(jdbcUrl));
    }
    {
      createLabel(parent, "database name:");
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
      database.addListener(SWT.Modify, createPageCompleteListener());
    }

    createUserNameField(parent);
    createPasswordField(parent);
    super.createChildControls(parent);
  }


  /**
   * @see org.eclipse.jface.dialogs.DialogPage#getDescription()
   */
  @Override
  public String getDescription() {
    return "Configure the H2 database connection";
  }

  /**
   * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
   */
  @Override
  public boolean isPageComplete() {
    return hasDatabaseName() && hasUserDetails() && hasInstanceName();
  }
}
