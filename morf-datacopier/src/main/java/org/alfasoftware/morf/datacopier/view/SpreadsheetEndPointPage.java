package com.chpconsulting.cryo.view;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

import com.chpconsulting.cryo.model.ExcelEndPoint;

/**
 * Wizard page that captures the information required to generate a spreadsheet.
 *
 * @author Copyright (c) CHP Consulting Ltd. 2008
 */
public class SpreadsheetEndPointPage extends CryoWizardPage {

  /**
   * Stores settings that the users enters.
   */
  private final ExcelEndPoint excelEndPoint;

  /**
   * Indicates if we are configuring a spreadsheet to read or write.
   */
  private final SourceTargetSwitch sourceTargetSwitch;

  /**
   * @param pageName Unique wizard page name.
   * @param excelEndPoint Object to configure with user settings.
   * @param sourceTargetSwitch Indicates whether we are configuring a spreadsheet to read or write.
   */
  public SpreadsheetEndPointPage(String pageName, ExcelEndPoint excelEndPoint, SourceTargetSwitch sourceTargetSwitch) {
    super(pageName);
    this.excelEndPoint = excelEndPoint;
    this.sourceTargetSwitch = sourceTargetSwitch;
  }


  /**
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createChildControls(Composite parent) {
    // Output the field for the output location
    createLabel(parent, sourceTargetSwitch == SourceTargetSwitch.SOURCE ? "spreadsheet:" : "output location:");

    final FileDialog fileDialog = new FileDialog(parent.getShell(), sourceTargetSwitch == SourceTargetSwitch.SOURCE ? SWT.OPEN : SWT.SAVE);
    fileDialog.setText(sourceTargetSwitch == SourceTargetSwitch.SOURCE ? "Select spreadsheet to read" : "Select output location");
    fileDialog.setFilterNames(new String[] {"Spreadsheets (*.xls)"});
    fileDialog.setFilterExtensions(new String[] {"*.xls"});

    // Creates a listener that shows an appropriate dialog when the user clicks the ellipsis.
    FileSelectionListener listener = new FileSelectionListener() {
      /**
       * @see com.chpconsulting.cryo.view.CryoWizardPage.FileSelectionListener#widgetSelected(org.eclipse.swt.widgets.Text, org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(Text text, SelectionEvent event) {
        fileDialog.setFileName(text.getText());
        String selectedFile = fileDialog.open();
        if (selectedFile != null) {
          text.setText(selectedFile);
        }
      }
    };

    final Text outputLocationText = createFileSelectionControl(parent, listener);
    outputLocationText.setText(StringUtils.defaultString(excelEndPoint.getFileName()));
    outputLocationText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        excelEndPoint.setFileName(outputLocationText.getText());
      }
    });
    outputLocationText.addListener(SWT.Modify, createPageCompleteListener());

    if (sourceTargetSwitch == SourceTargetSwitch.TARGET) {

      GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
      gridData.horizontalSpan = 2;

      // Force the user to specify a location for configuration options
      final FileDialog configFileDialog = new FileDialog(parent.getShell(), sourceTargetSwitch == SourceTargetSwitch.SOURCE ? SWT.OPEN : SWT.SAVE);
      configFileDialog.setText("Select configuration to read");
      configFileDialog.setFilterNames(new String[] {"Configurations (*.properties)"});
      configFileDialog.setFilterExtensions(new String[] {"*.properties"});

      // Creates a listener that shows an appropriate dialog when the user clicks the ellipsis.
      FileSelectionListener configListener = new FileSelectionListener() {
        /**
         * @see com.chpconsulting.cryo.view.CryoWizardPage.FileSelectionListener#widgetSelected(org.eclipse.swt.widgets.Text, org.eclipse.swt.events.SelectionEvent)
         */
        @Override
        public void widgetSelected(Text text, SelectionEvent event) {
          configFileDialog.setFileName(text.getText());
          String selectedFile = configFileDialog.open();
          if (selectedFile != null) {
            text.setText(selectedFile);
          }
        }
      };

      createLabel(parent, "configuration:");
      final Text configurationText = createFileSelectionControl(parent, configListener);
      configurationText.setText(StringUtils.defaultString(excelEndPoint.getConfigurationFileName()));
      configurationText.addModifyListener(new ModifyListener() {
        @Override
        public void modifyText(ModifyEvent e) {
          excelEndPoint.setConfigurationFileName(configurationText.getText());
        }
      });
      configurationText.addListener(SWT.Modify, createPageCompleteListener());
    }
  }


  /**
   * @see org.eclipse.jface.dialogs.DialogPage#getTitle()
   */
  @Override
  public String getTitle() {
    return "Generate spreadsheet";
  }


  /**
   * @see org.eclipse.jface.dialogs.DialogPage#getDescription()
   */
  @Override
  public String getDescription() {
    return "Configure the spreadsheet to generate.";
  }


  /**
   * @see com.chpconsulting.cryo.view.CryoWizardPage#getIconName()
   */
  @Override
  protected String getIconName() {
    return "data_table.ico";
  }

  /**
   * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
   */
  @Override
  public boolean isPageComplete() {
    return super.isPageComplete() && sourceTargetSwitch == SourceTargetSwitch.SOURCE ? excelEndPoint.canCreateProducer() : excelEndPoint.canCreateConsumer();
  }
}
