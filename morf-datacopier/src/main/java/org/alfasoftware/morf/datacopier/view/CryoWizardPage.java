package com.chpconsulting.cryo.view;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

/**
 * Provides functionality for Cryo wizard pages to generate required UI elements.
 *
 * @author Copyright (c) CHP Consulting Ltd. 2008
 */
public abstract class CryoWizardPage extends WizardPage {

  /**
   * Width hint supplied to fields created by this wizard page.
   */
  protected static final int DEFAULT_FIELD_WIDTH = 200;

  /**
   * Width hint supplied to fields created by this wizard page.
   */
  protected static final int USER_CREDENTIALS_FIELD_WIDTH = 100;

  /**
   * Creates a Cryp wizard page.
   *
   * @param pageName Unique wizard page name.
   */
  public CryoWizardPage(String pageName) {
    super(pageName);

    ImageData[] data = (new ImageLoader()).load(getClass().getResourceAsStream("/icons/" + getIconName()));
    setImageDescriptor(ImageDescriptor.createFromImageData(data[data.length - 1]));
  }


  /**
   * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createControl(Composite parent) {
    Composite control = new Composite(parent, SWT.NONE);
    GridLayout layout    = new GridLayout(2, false);
    layout.marginLeft  = 60;
    layout.marginRight = 60;
    layout.marginTop   = 15;
    control.setLayout(layout);
    createChildControls(control);
    setControl(control);
  }


  /**
   * Create any child controls required on this wizard page.
   *
   * @param parent The composite in which child controls should be created.
   */
  protected abstract void createChildControls(Composite parent);


  /**
   * @return the icon name used by the wizard page.
   */
  protected abstract String getIconName();


  /**
   * Create a standard Cryo wizard label.
   *
   * @param parent The parent container to hold the label.
   * @param labelText The text required in the label.
   * @return SWT label in the standard Cryo wizard style.
   */
  protected Label createLabel(Composite parent, String labelText) {
    Label label = new Label(parent, SWT.NONE);
    label.setText(labelText);
    label.setLayoutData(new GridData(110, SWT.DEFAULT));
    return label;
  }


  /**
   * @return a listener that updates the page complete status.
   */
  protected Listener createPageCompleteListener() {
    return new Listener() {
      @Override
      public void handleEvent(Event event) {
        setPageComplete(isPageComplete());
      }
    };
  }


  /**
   * Creates and returns a formatted composite containing a text field with a trailing
   * button. By default the button is labelled with an ellipsis but no event handlers
   * are assigned to either control.
   *
   * @param parent Composite to contain the controls.
   * @param listeners Listeners to attach to the button.
   * @return An SWT composite containing an edit and a linked button.
   */
  protected Text createFileSelectionControl(Composite parent, FileSelectionListener... listeners) {
    final Composite container = new Composite(parent, SWT.NONE);
    GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
    gridData.widthHint = DEFAULT_FIELD_WIDTH;
    container.setLayoutData(gridData);

    // Divide the composite into two pieces
    GridLayout controlLayout = new GridLayout(2, false);
    controlLayout.marginHeight = 0;
    controlLayout.marginWidth = 0;
    container.setLayout(controlLayout);

    // Text edit for the file name
    final Text text = new Text(container, SWT.SINGLE | SWT.BORDER);
    text.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

    Button button = new Button(container, SWT.PUSH);
    button.setText(Dialog.ELLIPSIS);
    for (final FileSelectionListener listener : listeners) {
      button.addSelectionListener(new SelectionAdapter() {
        /**
         * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        @Override
        public void widgetSelected(SelectionEvent event) {
          listener.widgetSelected(text, event);
        }
      });
    }

    return text;
  }


  /**
   * Creates and returns a formatted composite containing a text field with a trailing
   * button. By default the button is labelled with an ellipsis but no event handlers
   * are assigned to either control.
   *
   * @param parent Composite to contain the controls.
   * @param listeners Listeners to attach to the button.
   * @return An SWT composite containing an edit and a linked button.
   */
  protected Text createFolderSelectionControl(Composite parent, FolderSelectionListener... listeners) {
    final Composite container = new Composite(parent, SWT.NONE);
    GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL);
    gridData.widthHint = DEFAULT_FIELD_WIDTH;
    container.setLayoutData(gridData);

    // Divide the composite into two pieces
    GridLayout controlLayout = new GridLayout(2, false);
    controlLayout.marginHeight = 0;
    controlLayout.marginWidth = 0;
    container.setLayout(controlLayout);

    // Text edit for the file name
    final Text text = new Text(container, SWT.SINGLE | SWT.BORDER);
    text.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));

    Button button = new Button(container, SWT.PUSH);
    button.setText(Dialog.ELLIPSIS);
    for (final FolderSelectionListener listener : listeners) {
      button.addSelectionListener(new SelectionAdapter() {
        /**
         * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        @Override
        public void widgetSelected(SelectionEvent event) {
          listener.widgetSelected(text, event);
        }
      });
    }

    return text;
  }


  /**
   * Listener interface employed when the file selection button is pressed.
   *
   * @author Copyright (c) CHP Consulting Ltd. 2010
   */
  protected static interface FileSelectionListener {

    /**
     * @param text The text being maintained.
     * @param event Standard SWT event object.
     */
    public void widgetSelected(Text text, SelectionEvent event);
  }


  /**
   * Listener interface employed when the folder selection button is pressed.
   *
   * @author Copyright (c) CHP Consulting Ltd. 2010
   */
  protected static interface FolderSelectionListener {

    /**
     * @param text The text being maintained.
     * @param event Standard SWT event object.
     */
    public void widgetSelected(Text text, SelectionEvent event);
  }
}
