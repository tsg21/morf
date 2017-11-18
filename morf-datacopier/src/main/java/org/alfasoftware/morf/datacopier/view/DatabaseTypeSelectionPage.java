package com.chpconsulting.cryo.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.alfasoftware.morf.jdbc.h2.H2;
import org.alfasoftware.morf.jdbc.mysql.MySql;
import org.alfasoftware.morf.jdbc.nuodb.NuoDB;
import org.alfasoftware.morf.jdbc.oracle.Oracle;
import org.alfasoftware.morf.jdbc.sqlserver.SqlServer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.chpconsulting.cryo.model.DatabaseEndPoint;

/**
 * Page for selecting the database vendor for a database connection.
 *
 * @author Copyright (c) CHP Consulting Ltd. 2008
 */
public class DatabaseTypeSelectionPage extends CryoWizardPage {

  /**
   * Stores the list of database types we are allowed to select from.
   */
  private final List<String> supportedDatabaseTypes = new ArrayList<>();

  /**
   * The database end point being maintained.
   */
  private final DatabaseEndPoint databaseEndPoint;

  /**
   * Creates an action selection page.
   *
   * @param pageName Unique wizard page name.
   * @param databaseEndPoint The database end point to configure.
   * @param availableTypes Supported database types that the user may select from. Ignored if em
   */
  public DatabaseTypeSelectionPage(String pageName, DatabaseEndPoint databaseEndPoint, String... availableTypes) {
    super(pageName);
    this.databaseEndPoint = databaseEndPoint;

    if (availableTypes.length > 0) {
      supportedDatabaseTypes.addAll(Arrays.asList(availableTypes));
    } else {
      // Default set
      supportedDatabaseTypes.addAll(Arrays.asList(MySql.IDENTIFIER, Oracle.IDENTIFIER, SqlServer.IDENTIFIER, H2.IDENTIFIER, NuoDB.IDENTIFIER));
    }
  }


  /**
   * @see com.chpconsulting.cryo.view.CryoWizardPage#createChildControls(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createChildControls(Composite parent) {
    Label label = createLabel(parent, "database type:");
    ((GridData) label.getLayoutData()).verticalAlignment = GridData.BEGINNING;

    Composite commandPanel = new Composite(parent, SWT.NONE);

    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth  = 0;
    commandPanel.setLayout(layout);

    for (final String databaseType : supportedDatabaseTypes) {
      Button commandButton = new Button(commandPanel, SWT.RADIO);
      commandButton.setText(Messages.getEnumerationCaption(databaseType));
      commandButton.setSelection(databaseType.equals(databaseEndPoint.getSelectedDatabaseType()));
      commandButton.addSelectionListener(new SelectionAdapter() {

        /**
         * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        @Override
        public void widgetSelected(SelectionEvent event) {
          Button button = (Button) event.getSource();
          if (button.getSelection()) {
            databaseEndPoint.changeSelectedDatabaseType(databaseType);
          }
        }
      });
    }
  }


  /**
   * @see com.chpconsulting.cryo.view.CryoWizardPage#isPageComplete()
   */
  @Override
  public boolean isPageComplete() {
    return true;
  }


  /**
   * @see org.eclipse.jface.dialogs.DialogPage#getTitle()
   */
  @Override
  public String getTitle() {
    return "Database Type Selection";
  }


  /**
   * @see org.eclipse.jface.dialogs.DialogPage#getDescription()
   */
  @Override
  public String getDescription() {
    return "Select the database type";
  }


  /**
   * @see com.chpconsulting.cryo.view.CryoWizardPage#getIconName()
   */
  @Override
  protected String getIconName() {
    return "data_unknown.ico";
  }
}
