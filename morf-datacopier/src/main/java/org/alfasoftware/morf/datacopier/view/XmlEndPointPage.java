package com.chpconsulting.cryo.view;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import com.chpconsulting.cryo.model.XmlEndPoint;
import com.chpconsulting.cryo.model.XmlEndPoint.XmlFileFormat;

/**
 * Wizard page that captures the information required to perform a Cryo data extract.
 *
 * @author Copyright (c) CHP Consulting Ltd. 2008
 */
public class XmlEndPointPage extends CryoWizardPage {

  /**
   * Stores settings that the users enters.
   */
  private final XmlEndPoint xmlEndPoint;

  /**
   * Indicates if we are configuring the source or target.
   */
  private final SourceTargetSwitch sourceTargetSwitch;

  /**
   * @param pageName Unique wizard page name.
   * @param sourceTargetSwitch Flag indicating if we are configuring the source or target.
   * @param xmlEndPoint The bean holding all user entered settings.
   */
  public XmlEndPointPage(String pageName, SourceTargetSwitch sourceTargetSwitch, XmlEndPoint xmlEndPoint) {
    super(pageName);
    this.xmlEndPoint = xmlEndPoint;
    this.sourceTargetSwitch = sourceTargetSwitch;
  }


  /**
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createChildControls(Composite parent) {
    Label label = createLabel(parent, "XML file type:");
    ((GridData) label.getLayoutData()).verticalAlignment = GridData.BEGINNING;

    Composite fileFormatPanel = new Composite(parent, SWT.NONE);
    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth  = 0;
    fileFormatPanel.setLayout(layout);

    // Radio buttons for the XML file format
    for (final XmlFileFormat fileFormat : XmlFileFormat.values()) {
      Button commandButton = new Button(fileFormatPanel, SWT.RADIO);
      commandButton.setText(Messages.getEnumerationCaption(fileFormat));
      commandButton.setSelection(fileFormat == xmlEndPoint.getFileFormat());
      commandButton.addSelectionListener(new SelectionAdapter() {
        /**
         * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        @Override
        public void widgetSelected(SelectionEvent event) {
          Button button = (Button) event.getSource();
          if (button.getSelection()) {
            xmlEndPoint.setFileFormat(fileFormat);
          }
        }
      });
    }

    // Dialogs
    final FileDialog zipFileDialog = new FileDialog(parent.getShell(), sourceTargetSwitch == SourceTargetSwitch.SOURCE ? SWT.OPEN : SWT.SAVE);
    zipFileDialog.setText(sourceTargetSwitch == SourceTargetSwitch.SOURCE ? "Select archive to read" : "Select output location");
    zipFileDialog.setFilterNames(new String[] {"Zip Archives (*.zip)"});
    zipFileDialog.setFilterExtensions(new String[] {"*.zip"});

    final DirectoryDialog directoryDialog = new DirectoryDialog(parent.getShell());
    zipFileDialog.setText(sourceTargetSwitch == SourceTargetSwitch.SOURCE ? "Select directory to read" : "Select output location");

    // Creates a listener that shows an appropriate dialog when the user clicks the ellipsis.
    FileSelectionListener listener = new FileSelectionListener() {
      /**
       * @see com.chpconsulting.cryo.view.CryoWizardPage.FileSelectionListener#widgetSelected(org.eclipse.swt.widgets.Text, org.eclipse.swt.events.SelectionEvent)
       */
      @Override
      public void widgetSelected(Text text, SelectionEvent event) {
        String selectedFile;
        if (xmlEndPoint.getFileFormat() == XmlFileFormat.DIRECTORY) {
          directoryDialog.setFilterPath(text.getText());
          selectedFile = directoryDialog.open();
        } else {
          zipFileDialog.setFileName(text.getText());
          selectedFile = zipFileDialog.open();
        }

        if (selectedFile != null) {
          text.setText(selectedFile);
        }
      }
    };

    // Output the field for the output location
    createLabel(parent, sourceTargetSwitch == SourceTargetSwitch.SOURCE ? "source archive:" : "output location:");
    final Text outputLocationText = createFileSelectionControl(parent, listener);
    outputLocationText.setText(StringUtils.defaultString(xmlEndPoint.getXmlPath()));
    outputLocationText.addModifyListener(new ModifyListener() {
      @Override
      public void modifyText(ModifyEvent e) {
        xmlEndPoint.setXmlPath(outputLocationText.getText());
      }
    });
    outputLocationText.addListener(SWT.Modify, createPageCompleteListener());
  }


  /**
   * @see org.eclipse.jface.wizard.WizardPage#isPageComplete()
   */
  @Override
  public boolean isPageComplete() {
    return super.isPageComplete() && sourceTargetSwitch == SourceTargetSwitch.SOURCE ? xmlEndPoint.canCreateProducer() : xmlEndPoint.canCreateConsumer();
  }


  /**
   * @see org.eclipse.jface.dialogs.DialogPage#getTitle()
   */
  @Override
  public String getTitle() {
    switch(sourceTargetSwitch) {
      case SOURCE: return "XML Source Data";
      case TARGET: return "XML Extract";
      default : return "";
    }
  }


  /**
   * @see org.eclipse.jface.dialogs.DialogPage#getDescription()
   */
  @Override
  public String getDescription() {
    switch(sourceTargetSwitch) {
      case SOURCE: return "Select the source extract location.";
      case TARGET: return "Select the target location for the extract.";
      default : return "";
    }
  }


  /**
   * @see com.chpconsulting.cryo.view.CryoWizardPage#getIconName()
   */
  @Override
  protected String getIconName() {
    return "data_disk.ico";
  }
}
