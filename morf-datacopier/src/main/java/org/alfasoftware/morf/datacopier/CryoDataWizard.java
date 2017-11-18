package org.alfasoftware.morf.datacopier;

import java.awt.Button;
import java.awt.Composite;
import java.awt.Image;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletionService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorCompletionService;

import org.alfasoftware.morf.datacopier.model.CommandBean;
import org.alfasoftware.morf.datacopier.model.CryoEndPoint;
import org.alfasoftware.morf.datacopier.model.ExcelStartPoint;
import org.alfasoftware.morf.datacopier.view.CompletePage;
import org.alfasoftware.morf.datacopier.view.CryoWizardPage;
import org.alfasoftware.morf.datacopier.view.DatabaseTypeSelectionPage;
import org.alfasoftware.morf.datacopier.view.EndPointTypeSelectionPage;
import org.alfasoftware.morf.datacopier.view.H2ConnectionPage;
import org.alfasoftware.morf.datacopier.view.MySqlConnectionPage;
import org.alfasoftware.morf.datacopier.view.NuoDBConnectionPage;
import org.alfasoftware.morf.datacopier.view.OracleConnectionPage;
import org.alfasoftware.morf.datacopier.view.SourceTargetSwitch;
import org.alfasoftware.morf.datacopier.view.SpreadsheetEndPointPage;
import org.alfasoftware.morf.datacopier.view.SpreadsheetSourcePage;
import org.alfasoftware.morf.datacopier.view.SqlServerConnectionPage;
import org.alfasoftware.morf.datacopier.view.UpdateDatabaseSchemaPage;
import org.alfasoftware.morf.datacopier.view.XmlEndPointPage;
import org.alfasoftware.morf.dataset.BlanksToZeroAdapter;
import org.alfasoftware.morf.dataset.DataSetProducer;
import org.alfasoftware.morf.dataset.WithMetaDataAdapter;
import org.alfasoftware.morf.jdbc.h2.H2;
import org.alfasoftware.morf.jdbc.mysql.MySql;
import org.alfasoftware.morf.jdbc.nuodb.NuoDB;
import org.alfasoftware.morf.jdbc.oracle.Oracle;
import org.alfasoftware.morf.jdbc.sqlserver.SqlServer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Display;

/**
 * Wizard for moving data from one data source to another data source.
 *
 * <p>The wizard allows the configuration of a source and a target, each of which
 * may be one of the following:</p>
 * <p><ul>
 * <li>Database (vendor must be specified)</li>
 * <li>Xml files</li>
 * <li>Spreadsheets</li>
 * </ul></p>
 *
 * <p>For both source and target once the basic type is selected a further page
 * with details of the data source is presented. For example each database
 * vendor has a connection specific wizard page and if data is being sent to
 * an XML file then a wizard page with a file selector is shown.</p>
 *
 * @author Copyright (c) CHP Consulting Ltd. 2008
 */
public class CryoDataWizard extends Wizard {

  /**
   * Log instance.
   */
  private final static Log log = LogFactory.getLog(CryoDataWizard.class);

  /**
   * A file location in which Cryo stores user preferences.
   */
  protected final File userDefaultsFile = new File(System.getProperty("user.home") + "/cryo.prefs");

  /**
   * Stores the user entered values.
   */
  private final CommandBean commandBean = new CommandBean();


  /**
   * Start the wizard.
   *
   * <h3>Apple Mac OS X support</h3>
   * <p>On OS X, SWT needs to run on the main thread.
   * Although there is a {@code -XstartOnFirstThread} JVM argument, this
   * does not work in some versions of Oracle's Java 7 JRE via Java WebStart.
   * Therefore,  we make sure of an OS X-specific class (in both Apple- &
   * Oracle-supplied JREs) to execute our SWT initialisation on the main thread.</p>
   *
   * @param args Command line arguments to the program.
   */
  public static void main(String[] args) {

    // -- Detect OS X and load the specific class...
    //
    boolean isAppleOSX = System.getProperty("os.name").endsWith("OS X");
    if (isAppleOSX) {
      try {
        Class<?> comAppleConcurrentDispatch = Class.forName("com.apple.concurrent.Dispatch");
        Object instance = comAppleConcurrentDispatch.getMethod("getInstance").invoke(null);
        Executor executor = (Executor) comAppleConcurrentDispatch.getMethod("getNonBlockingMainQueueExecutor").invoke(instance);

        // Use a completion service so that we can wait for before returning
        CompletionService<Void> completionService = new ExecutorCompletionService<>(executor);
        completionService.submit(new Bootstrap(), null);
        completionService.take();
        return;
      } catch (ClassNotFoundException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
        log.info("Failed to load Apple class to allow SWT to execute on main dispatch thread, attempting to continue: " + e.getMessage());
      } catch (InterruptedException e) {
        log.error("InterruptedException in running Cryo bootstrap using OS X executor, attempting to continue", e);
      }
    }

    // -- If we get here, we're not OS X or the above failed - just run it directly...
    //
    new Bootstrap().run();
  }


  /**
   * Initialise {@link CryoDataWizard} and open it in a SWT {@link WizardDialog}.
   *
   * @author Copyright (c) CHP Consulting Ltd. 2013
   */
  private static class Bootstrap implements Runnable {
    @SuppressWarnings("javadoc")
    @Override public void run() {
      Display display = new Display();

      try {
        WizardDialog dialog = new WizardDialog(null, new CryoDataWizard());
        dialog.create();

        ImageData[] imageData = new ImageLoader().load(CryoDataWizard.class.getResourceAsStream("/icons/data.ico"));
        List<Image> images = new ArrayList<>();
        for (ImageData data : imageData) {
          images.add(new Image(display, data));
        }
        dialog.getShell().setImages(images.toArray(new Image[]{}));

        dialog.open();
      } catch (Exception e) {
        log.fatal("Fatal error during Cryo", e);
        MessageDialog.openError(display.getActiveShell(), "Cryo", "Fatal Error: " + e.getMessage());
      }

      display.dispose();
    }
  }


  /**
   * Creates the wizard launcher form for Cryo.
   */
  public CryoDataWizard() {
    super();
    setNeedsProgressMonitor(true);
    loadPreferences();
  }


  /**
   * @see org.eclipse.jface.wizard.Wizard#addPages()
   */
  @Override
  public void addPages() {

    // Add the end point type pages
    WizardStep.SOURCE_SELECTION.page = new EndPointTypeSelectionPage("Source", commandBean, SourceTargetSwitch.SOURCE);
    WizardStep.TARGET_SELECTION.page = new EndPointTypeSelectionPage("Target", commandBean, SourceTargetSwitch.TARGET);

    // Add the database type pages
    WizardStep.SOURCE_DATABASE_TYPE.page = new DatabaseTypeSelectionPage("sourceDatabase", commandBean.getDatabaseSource());
    WizardStep.TARGET_DATABASE_TYPE.page = new DatabaseTypeSelectionPage("targetDatabase", commandBean.getDatabaseTarget());

    // Add all the database specific command pages
    WizardStep.SOURCE_DATABASE_MYSQL.page = new MySqlConnectionPage("sourceMySql", commandBean.getDatabaseSource().connectionDetails(MySql.IDENTIFIER));
    WizardStep.SOURCE_DATABASE_ORACLE.page = new OracleConnectionPage("sourceOracle", commandBean.getDatabaseSource().connectionDetails(Oracle.IDENTIFIER));
    WizardStep.SOURCE_DATABASE_SQLSERVER.page = new SqlServerConnectionPage("sourceSqlServer", commandBean.getDatabaseSource().connectionDetails(SqlServer.IDENTIFIER));
    WizardStep.SOURCE_DATABASE_H2.page = new H2ConnectionPage("sourceH2", commandBean.getDatabaseSource().connectionDetails(H2.IDENTIFIER));
    WizardStep.SOURCE_DATABASE_NUODB.page = new NuoDBConnectionPage("sourceNuoDB", commandBean.getDatabaseSource().connectionDetails(NuoDB.IDENTIFIER));
    WizardStep.TARGET_DATABASE_MYSQL.page = new MySqlConnectionPage("targetMySql", commandBean.getDatabaseTarget().connectionDetails(MySql.IDENTIFIER));
    WizardStep.TARGET_DATABASE_ORACLE.page = new OracleConnectionPage("targetOracle", commandBean.getDatabaseTarget().connectionDetails(Oracle.IDENTIFIER));
    WizardStep.TARGET_DATABASE_SQLSERVER.page = new SqlServerConnectionPage("targetSqlServer", commandBean.getDatabaseTarget().connectionDetails(SqlServer.IDENTIFIER));
    WizardStep.TARGET_DATABASE_H2.page = new H2ConnectionPage("targetH2", commandBean.getDatabaseTarget().connectionDetails(H2.IDENTIFIER));
    WizardStep.TARGET_DATABASE_NUODB.page = new NuoDBConnectionPage("targetNuoDB", commandBean.getDatabaseTarget().connectionDetails(NuoDB.IDENTIFIER));
    WizardStep.TARGET_UPDATE_DATABASE_SCHEMA.page = new UpdateDatabaseSchemaPage("updateDatabaseSchema", commandBean.getDatabaseTarget());

    // Add the Xml pages
    WizardStep.SOURCE_XML.page = new XmlEndPointPage("sourceXml", SourceTargetSwitch.SOURCE, commandBean.getXmlSource());
    WizardStep.TARGET_XML.page = new XmlEndPointPage("targetXml", SourceTargetSwitch.TARGET, commandBean.getXmlTarget());

    // Add the Excel pages
    WizardStep.SOURCE_EXCEL.page = new SpreadsheetSourcePage("sourceExcel", commandBean.getExcelSource());
    WizardStep.TARGET_EXCEL.page = new SpreadsheetEndPointPage("targetExcel", commandBean.getExcelTarget(), SourceTargetSwitch.TARGET);

    // Add the close (final) page
    WizardStep.CLOSE.page = new CompletePage("complete");

    // Add all pages to our container
    for (WizardStep wizardPage : WizardStep.values()) {
      if (wizardPage.page != null) {
        addPage(wizardPage.page);
      }
    }
  }


  /**
   * @see org.eclipse.jface.wizard.Wizard#getNextPage(org.eclipse.jface.wizard.IWizardPage)
   */
  @Override
  public IWizardPage getNextPage(IWizardPage page) {
    WizardStep nextStep = WizardStep.forPage(page).determineNextStep(commandBean);
    return nextStep == WizardStep.CLOSE ? null : nextStep.page;
  }


  /**
   * @see org.eclipse.jface.wizard.Wizard#canFinish()
   */
  @Override
  public boolean canFinish() {
    WizardStep nextPage = WizardStep.forPage(getContainer().getCurrentPage()).determineNextStep(commandBean);
    return nextPage == WizardStep.CLOSE && getContainer().getCurrentPage().isPageComplete();
  }


  /**
   * @see org.eclipse.jface.wizard.Wizard#performFinish()
   */
  @Override
  public boolean performFinish() {
    savePreferences();

    try {
      DataSetProducer producer;
      if (getSourceEndPoint() instanceof ExcelStartPoint) {
        producer = new BlanksToZeroAdapter(
          new WithMetaDataAdapter(
            getSourceEndPoint().asDataSetProducer(),
            getTargetEndPoint().asDataSetProducer()
          )
        );
      } else {
        producer = getSourceEndPoint().asDataSetProducer();
      }

      // First two arguments are whether to fork and if the process can be cancelled.
      getContainer().run(true, true, new CryoRunnable(producer, getTargetEndPoint().asDataSetConsumer()));

    } catch (Exception e) {
      Throwable cause;
      if (e instanceof InvocationTargetException) {
        cause = e.getCause();
      } else {
        cause = e;
      }

      log.error("Error during Cryo command runnable", cause);
      MessageDialog.openError(getShell(), "Cryo", "Error performing operation:\n" + cause.getMessage());
      return false;
    }

    // Allows the wizard to progress to the close page
    getContainer().showPage(WizardStep.CLOSE.page);
    finaliseWizardButtons(getContainer().getShell());
    return false;
  }


  /**
   * @see org.eclipse.jface.wizard.Wizard#performCancel()
   */
  @Override
  public boolean performCancel() {
    return true;
  }


  /**
   * Removes the nex, back and finish buttons and converts close to cancel.
   *
   * @param control The control to scan for wizard buttons.
   */
  private void finaliseWizardButtons(Control control) {
    if (control instanceof Composite) {
      for (Control child : ((Composite) control).getChildren()) {
        finaliseWizardButtons(child);
      }
    }
    if (control instanceof Button) {
      if (((Button) control).getText().matches(".*Cancel.*")) {
        ((Button) control).setText("Close");
      }

      if (((Button) control).getText().matches(".*Next.*")) {
        control.setVisible(false);
      }

      if (((Button) control).getText().matches(".*Back.*")) {
        control.setVisible(false);
      }

      if (((Button) control).getText().matches(".*Finish.*")) {
        control.setVisible(false);
      }
    }

  }


  /**
   * @see org.eclipse.jface.wizard.Wizard#getWindowTitle()
   */
  @Override
  public String getWindowTitle() {
    return "Cryo Data Wizard";
  }


  /**
   * Load the user's last used values to populate the command bean with default values.
   */
  private void loadPreferences() {
    try {
      if (userDefaultsFile.exists()) {
        commandBean.loadFrom(userDefaultsFile);
      }
    } catch (Exception e) {
      // This is a temporary user preferences file. Assume errors will be sorted next time we have saved this file.
      log.warn("Failed to load preferences", e);
      userDefaultsFile.delete();
    }
  }


  /**
   * Save the current command bean values so they can be defaulted in next time cryo is used.
   */
  protected void savePreferences() {
    try {
      commandBean.saveTo(userDefaultsFile);
    } catch (Exception e) {
      throw new RuntimeException("Error saving user properties", e);
    }
  }


  /**
   * @return The end point which is the source of data.
   */
  private CryoEndPoint getSourceEndPoint() {
    switch (commandBean.getSourceType()) {
      case DATABASE: return commandBean.getDatabaseSource();
      case XML: return commandBean.getXmlSource();
      case EXCEL: return commandBean.getExcelSource();
      default: throw new IllegalStateException("Unsupported endpoint type [" + commandBean.getSourceType() + "]");
    }
  }


  /**
   * @return The end point which is the source of data.
   */
  private CryoEndPoint getTargetEndPoint() {
    switch (commandBean.getTargetType()) {
      case DATABASE: return commandBean.getDatabaseTarget();
      case XML: return commandBean.getXmlTarget();
      case EXCEL: return commandBean.getExcelTarget();
      default: throw new IllegalStateException("Unsupported endpoint type [" + commandBean.getSourceType() + "]");
    }
  }


  /**
   * Enumerates all the wizard pages available.
   */
  private enum WizardStep {

    /**
     * Step for selecting the type of data source from which to read a data set.
     *
     * @author Copyright (c) CHP Consulting Ltd. 2010
     */
    SOURCE_SELECTION {
      /**
       * @see com.chpconsulting.cryo.CryoDataWizard.WizardStep#determineNextStep(com.chpconsulting.cryo.model.CommandBean)
       */
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        switch (commandBean.getSourceType()) {
          case DATABASE: return SOURCE_DATABASE_TYPE;
          case XML: return SOURCE_XML;
          case EXCEL: return SOURCE_EXCEL;

          default: throw new UnsupportedOperationException("Unsupported end point type [" + commandBean.getSourceType() + "]");
        }
      }
    },

    /**
     * Step for selecting the type of databases to read from.
     *
     * @author Copyright (c) CHP Consulting Ltd. 2010
     */
    SOURCE_DATABASE_TYPE {
      /**
       * @see com.chpconsulting.cryo.CryoDataWizard.WizardStep#determineNextStep(com.chpconsulting.cryo.model.CommandBean)
       */
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        String databaseType = commandBean.getDatabaseSource().getSelectedDatabaseType();
        if (databaseType.equals(MySql.IDENTIFIER)) {
          return SOURCE_DATABASE_MYSQL;
        } else if (databaseType.equals(Oracle.IDENTIFIER)) {
          return SOURCE_DATABASE_ORACLE;
        } else if (databaseType.equals(SqlServer.IDENTIFIER)) {
          return SOURCE_DATABASE_SQLSERVER;
        } else if (databaseType.equals(H2.IDENTIFIER)) {
          return SOURCE_DATABASE_H2;
        } else if (databaseType.equals(NuoDB.IDENTIFIER)) {
          return SOURCE_DATABASE_NUODB;
        } else {
          throw new UnsupportedOperationException("Unsupported database type [" + commandBean.getDatabaseSource().getSelectedDatabaseType() + "]");
        }
      }
    },

    /**
     * Step for configuring the source database connection.
     *
     * @author Copyright (c) CHP Consulting Ltd. 2010
     */
    SOURCE_DATABASE_MYSQL {
      /**
       * @see com.chpconsulting.cryo.CryoDataWizard.WizardStep#determineNextStep(com.chpconsulting.cryo.model.CommandBean)
       */
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        return TARGET_SELECTION;
      }
    },

    /**
     * Step for configuring the source database connection.
     *
     * @author Copyright (c) CHP Consulting Ltd. 2010
     */
    SOURCE_DATABASE_SQLSERVER {
      /**
       * @see com.chpconsulting.cryo.CryoDataWizard.WizardStep#determineNextStep(com.chpconsulting.cryo.model.CommandBean)
       */
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        return TARGET_SELECTION;
      }
    },

    /**
     * Step for configuring the source database connection.
     *
     * @author Copyright (c) CHP Consulting Ltd. 2010
     */
    SOURCE_DATABASE_ORACLE {
      /**
       * @see com.chpconsulting.cryo.CryoDataWizard.WizardStep#determineNextStep(com.chpconsulting.cryo.model.CommandBean)
       */
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        return TARGET_SELECTION;
      }
    },

    /***/
    SOURCE_DATABASE_H2 {
      /**
       * @see com.chpconsulting.cryo.CryoDataWizard.WizardStep#determineNextStep(com.chpconsulting.cryo.model.CommandBean)
       */
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        return TARGET_SELECTION;
      }
    },

    /***/
    SOURCE_DATABASE_NUODB {
      /**
       * @see com.chpconsulting.cryo.CryoDataWizard.WizardStep#determineNextStep(com.chpconsulting.cryo.model.CommandBean)
       */
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        return TARGET_SELECTION;
      }
    },

    /**
     * Step for selecting an XML data source.
     *
     * @author Copyright (c) CHP Consulting Ltd. 2010
     */
    SOURCE_XML {
      /**
       * @see com.chpconsulting.cryo.CryoDataWizard.WizardStep#determineNextStep(com.chpconsulting.cryo.model.CommandBean)
       */
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        return TARGET_SELECTION;
      }
    },

    /**
     * Step for selecting an Excel data source.
     *
     * @author Copyright (c) CHP Consulting Ltd. 2010
     */
    SOURCE_EXCEL {
      /**
       * @see com.chpconsulting.cryo.CryoDataWizard.WizardStep#determineNextStep(com.chpconsulting.cryo.model.CommandBean)
       */
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        return TARGET_SELECTION;
      }
    },

    /**
     * Step for selecting the type of data set target.
     *
     * @author Copyright (c) CHP Consulting Ltd. 2010
     */
    TARGET_SELECTION{
      /**
       * @see com.chpconsulting.cryo.CryoDataWizard.WizardStep#determineNextStep(com.chpconsulting.cryo.model.CommandBean)
       */
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        switch (commandBean.getTargetType()) {
          case DATABASE: return TARGET_DATABASE_TYPE;
          case XML: return TARGET_XML;
          case EXCEL: return TARGET_EXCEL;

          default: throw new UnsupportedOperationException("Unsupported end point type [" + commandBean.getTargetType() + "]");
        }
      }
    },

    /**
     * Step for configuring the type of target database.
     *
     * @author Copyright (c) CHP Consulting Ltd. 2010
     */
    TARGET_DATABASE_TYPE {
      /**
       * @see com.chpconsulting.cryo.CryoDataWizard.WizardStep#determineNextStep(com.chpconsulting.cryo.model.CommandBean)
       */
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        String databaseType = commandBean.getDatabaseTarget().getSelectedDatabaseType();
        if (databaseType.equals(MySql.IDENTIFIER)) {
          return TARGET_DATABASE_MYSQL;
        } else if (databaseType.equals(Oracle.IDENTIFIER)) {
          return TARGET_DATABASE_ORACLE;
        } else if (databaseType.equals(SqlServer.IDENTIFIER)) {
          return TARGET_DATABASE_SQLSERVER;
        } else if (databaseType.equals(H2.IDENTIFIER)) {
          return TARGET_DATABASE_H2;
        } else if (databaseType.equals(NuoDB.IDENTIFIER)) {
          return TARGET_DATABASE_NUODB;
        } else {
          throw new UnsupportedOperationException("Unsupported database type [" + commandBean.getDatabaseTarget().getSelectedDatabaseType() + "]");
        }
      }
    },

    /**
     * MySQL details
     */
    TARGET_DATABASE_MYSQL {
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        return TARGET_UPDATE_DATABASE_SCHEMA;
      }
    },

    /**
     * Oracle details
     */
    TARGET_DATABASE_ORACLE {
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        return TARGET_UPDATE_DATABASE_SCHEMA;
      }
    },

    /**
     * SQL Server detail
     */
    TARGET_DATABASE_SQLSERVER {
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        return TARGET_UPDATE_DATABASE_SCHEMA;
      }
    },

    /**
     * H2 details
     */
    TARGET_DATABASE_H2 {
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        return TARGET_UPDATE_DATABASE_SCHEMA;
      }
    },

    /***/
    TARGET_DATABASE_NUODB {
      /**
       * @see com.chpconsulting.cryo.CryoDataWizard.WizardStep#determineNextStep(com.chpconsulting.cryo.model.CommandBean)
       */
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        return TARGET_UPDATE_DATABASE_SCHEMA;
      }
    },

    /**
     * Should the schema be updated?
     */
    TARGET_UPDATE_DATABASE_SCHEMA {
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        return CLOSE;
      }
    },

    /**
     * Possible final wizard step so no progressions.
     */
    TARGET_XML {
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        return CLOSE;
      }
    },

    /**
     * Possible final wizard step so no progressions.
     */
    TARGET_EXCEL {
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        return CLOSE;
      }
    },

    /**
     * Final visible step for the completion page.
     */
    CLOSE {
      @Override
      public WizardStep determineNextStep(CommandBean commandBean) {
        // Make this a dead end
        return CLOSE;
      }
    };

    /**
     * Stores the wizard page associated with this enumeration value.
     */
    public CryoWizardPage page;

    /**
     * @param commandBean The command bean that gives the state of user entered values.
     * @return The next page in the wizard.
     */
    public abstract  WizardStep determineNextStep(CommandBean commandBean);

    /**
     * Determines the enumerated page based on the wizard page.
     *
     * @param page The page for which the wizard step enumeration is required.
     * @return The Page instance that maps to the specified wizard page.
     */
    public static WizardStep forPage(IWizardPage page) {
      WizardStep result = null;
      for (WizardStep wizardPage : WizardStep.values()) {
        if (wizardPage.page == page) {
          result = wizardPage;
        }
      }

      if (result == null) {
        throw new IllegalStateException("Wizard on unknown page");
      }

      return result;
    }
  }

}
