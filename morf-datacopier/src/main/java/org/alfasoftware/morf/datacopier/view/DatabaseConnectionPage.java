package org.alfasoftware.morf.datacopier.view;

import org.alfasoftware.morf.jdbc.AbstractConnectionResources;
import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Wizard page for configuring a database connection.
 *
 * @author Copyright (c) CHP Consulting Ltd. 2009
 */
public abstract class DatabaseConnectionPage extends CryoWizardPage {

  /**
   * Stores settings that the users enters.
   */
  protected final AbstractConnectionResources connectionDetails;


  /**
   * Creates a connection details page backed by the bean class <var>connectionDetails</var>.
   *
   * @param pageName Unique name for the wizard page.
   * @param connectionDetails Database connection details to configure.
   */
  public DatabaseConnectionPage(String pageName, AbstractConnectionResources connectionDetails) {
    super(pageName);
    this.connectionDetails = connectionDetails;
  }


  /**
   * Creates a field that displays the Jdbc URL.
   *
   * @param parent The composite to own the created control.
   * @return A SWT control (read only) for the jdbc URL.
   */
  protected Text createJdbcUrlField(Composite parent) {
    // Read only jdbc url (edited by changing other fields)
    createLabel(parent, "JDBC url:");
    final Text jdbc = new Text(parent, SWT.SINGLE | SWT.BORDER);
    jdbc.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
    jdbc.setText(connectionDetails.getJdbcUrl());
    jdbc.setEditable(false);
    return jdbc;
  }


  /**
   * Creates a listener that will trigger an update to the jdbc URL field.
   *
   * @param jdbcUrl The jdbc URL text control to update.
   * @return A SQT listener that will update the <var>jdbcUrl</var>.
   */
  protected Listener createJdbcUrlUpdateListener(final Text jdbcUrl) {
    return new Listener() {
      /**
       * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
       */
      @Override
      public void handleEvent(Event event) {
        jdbcUrl.setText(connectionDetails.getJdbcUrl());
      }
    };
  }


  /**
   * Creates a host name field.
   *
   * @param parent The composite to own the created control.
   * @return a SWT text control for the host name.
   */
  protected Text createHostNameField(Composite parent) {
    createLabel(parent, "host:");
    final Text host = new Text(parent, SWT.SINGLE | SWT.BORDER);
    host.setLayoutData(new GridData(DEFAULT_FIELD_WIDTH, SWT.DEFAULT));
    host.setText(StringUtils.defaultString(connectionDetails.getHostName()));
    host.addModifyListener(new ModifyListener(){
      @Override
      public void modifyText(ModifyEvent e) {
        connectionDetails.setHostName(host.getText());
      }
    });
    host.addListener(SWT.Modify, createPageCompleteListener());
    return host;
  }


  /**
   * Creates a TCP/IP port name field.
   *
   * @param parent The composite to own the created control.
   * @return a SWT text control for the port.
   */
  protected Text createPortField(Composite parent) {
    createLabel(parent, "port:");
    final Text port = new Text(parent, SWT.SINGLE | SWT.BORDER);
    port.setLayoutData(new GridData(DEFAULT_FIELD_WIDTH, SWT.DEFAULT));
    port.setText(Integer.toString(connectionDetails.getPort()));
    // only digits
    port.addListener(SWT.Verify, new Listener () {
      @Override
      public void handleEvent (Event e) {
        String string = e.text;
        char[] chars = new char[string.length()];
        string.getChars (0, chars.length, chars, 0);
        for (int i=0; i<chars.length; i++) {
          if (!('0' <= chars[i] && chars[i] <= '9')) {
            e.doit = false;
            return;
          }
        }
      }
    });
    port.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        connectionDetails.setPort(Integer.parseInt(port.getText()));
      }
    });
    port.addListener(SWT.Modify, createPageCompleteListener());
    return port;
  }


  /**
   * Creates a user name field.
   *
   * @param parent The composite to own the created control.
   * @return a SWT text control for the user name.
   */
  protected Text createUserNameField(Composite parent) {
    createLabel(parent, "user name:");
    final Text userName = new Text(parent, SWT.SINGLE | SWT.BORDER);
    userName.setLayoutData(new GridData(USER_CREDENTIALS_FIELD_WIDTH, SWT.DEFAULT));
    userName.setText(StringUtils.defaultString(connectionDetails.getUserName()));
    userName.addModifyListener(new ModifyListener(){
      @Override
      public void modifyText(ModifyEvent e) {
        connectionDetails.setUserName(userName.getText());
      }
    });
    userName.addListener(SWT.Modify, createPageCompleteListener());
    return userName;
  }


  /**
   * Creates a password field.
   *
   * @param parent The composite to own the created control.
   * @return a SWT text control for the passowrd.
   */
  protected Text createPasswordField(Composite parent) {
    createLabel(parent, "password:");
    final Text password = new Text(parent, SWT.SINGLE | SWT.BORDER);
    password.setEchoChar('*');
    password.setLayoutData(new GridData(USER_CREDENTIALS_FIELD_WIDTH, SWT.DEFAULT));
    password.addModifyListener(new ModifyListener(){
      @Override
      public void modifyText(ModifyEvent e) {
        connectionDetails.setPassword(password.getText());
      }
    });
    password.addListener(SWT.Modify, createPageCompleteListener());
    return password;
  }


  /**
   * @param parent The composite on which to put the field
   */
  protected void createSchemaField(Composite parent) {
    createLabel(parent, "schema:");
    final Text schema = new Text(parent, SWT.SINGLE | SWT.BORDER);
    schema.setLayoutData(new GridData(USER_CREDENTIALS_FIELD_WIDTH, SWT.DEFAULT));
    schema.setText(StringUtils.defaultString(connectionDetails.getSchemaName()));
    schema.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        connectionDetails.setSchemaName(schema.getText().trim());
      }
    });
    schema.addListener(SWT.Modify, createPageCompleteListener());
  }


  /**
   * {@inheritDoc}
   * @see com.chpconsulting.cryo.view.CryoWizardPage#createChildControls(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected void createChildControls(Composite parent) {
    // Nothing to do
  }


  /**
   * @see org.eclipse.jface.dialogs.DialogPage#getTitle()
   */
  @Override
  public final String getTitle() {
    return "Database Connection";
  }


  /**
   * @see com.chpconsulting.cryo.view.CryoWizardPage#getIconName()
   */
  @Override
  protected final String getIconName() {
    return "data_connection.ico";
  }


  /**
   * @return True if the user logon details have been entered.
   */
  protected boolean hasUserDetails() {
    return connectionDetails.getUserName() != null && !connectionDetails.getUserName().equals("") &&
        connectionDetails.getPassword() != null && !connectionDetails.getPassword().equals("");
  }

  /**
   * @return True if a host name has been entered.
   */
  protected boolean hasHostName() {
    return connectionDetails.getHostName() != null && !connectionDetails.getHostName().equals("");
  }

  /**
   * @return True if a database name has been entered.
   */
  protected boolean hasDatabaseName() {
    return connectionDetails.getDatabaseName() != null && !connectionDetails.getDatabaseName().equals("");
  }

  /**
   * @return True if an instance name has been entered.
   */
  protected boolean hasInstanceName() {
    return connectionDetails.getInstanceName() != null && !connectionDetails.getInstanceName().equals("");
  }

  /**
   * @return True if a schema name has been entered.
   */
  protected boolean hasSchemaName() {
    return connectionDetails.getSchemaName() != null && !connectionDetails.getSchemaName().equals("");
  }
}
