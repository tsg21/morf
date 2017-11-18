package com.chpconsulting.cryo.view;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.chpconsulting.cryo.model.ExcelStartPoint;

/**
 * Wizard page that captures the information required to generate a spreadsheet.
 *
 * @author Copyright (c) CHP Consulting Ltd. 2008
 */
public class SpreadsheetSourcePage extends CryoWizardPage {

  /**
   * Stores settings that the users enters.
   */
  private final ExcelStartPoint excelStartPoint;

  /**
   * @param pageName Unique wizard page name.
   * @param excelStartPoint Object to configure with user settings.
   */
  public SpreadsheetSourcePage(String pageName, ExcelStartPoint excelStartPoint) {
    super(pageName);
    this.excelStartPoint = excelStartPoint;
  }


  /**
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createChildControls(Composite parent) {
    // Output the field for the output location
    createLabel(parent, "source folder:");

    final DirectoryDialog folderDialog = new DirectoryDialog(parent.getShell(), SWT.OPEN);
    folderDialog.setMessage("Select folder containing spreadsheets");

    // Creates a listener that shows an appropriate dialog when the user clicks the ellipsis.
    FolderSelectionListener listener = new FolderSelectionListener() {
      /**
       * @see com.chpconsulting.cryo.view.CryoWizardPage.FileSelectionListener#widgetSelected(org.eclipse.swt.widgets.Text, org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(Text text, SelectionEvent event) {
        folderDialog.setText(text.getText());
        String selectedFile = folderDialog.open();
        if (selectedFile != null) {
          text.setText(selectedFile);
        }
      }
    };

    final Text outputLocationText = createFolderSelectionControl(parent, listener);
    outputLocationText.setText(StringUtils.defaultString(excelStartPoint.getFolderName()));
    outputLocationText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        excelStartPoint.setFolderName(outputLocationText.getText());
      }
    });
    outputLocationText.addListener(SWT.Modify, createPageCompleteListener());

    // FIXME - We need to support translations properly. Currently they don't download to spreadsheets
    // and when you upload all translations are deleted for every table then just uploaded for those
    // in the current spreadsheet.
    Label label = new Label(parent, SWT.NONE);
    label.setText("Translation upload is currently disabled pending further improvements");
    GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
    gridData.horizontalSpan = 2;
    label.setLayoutData(gridData);
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
    return super.isPageComplete() && excelStartPoint.canCreateProducer();
  }
}
