package com.chpconsulting.cryo.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * Page displayed when the wizard is complete.
 *
 * @author Copyright (c) CHP Consulting Ltd. 2010
 */
public class CompletePage extends CryoWizardPage {

  /**
   * @param pageName Unique wizard page name.
   */
  public CompletePage(String pageName) {
    super(pageName);
  }


  /**
   * @see com.chpconsulting.cryo.view.CryoWizardPage#createChildControls(org.eclipse.swt.widgets.Composite)
   */
  @Override
  protected void createChildControls(Composite parent) {
    Label completionMessage = new Label(parent, SWT.NONE);
    completionMessage.setText("Data transfer completed");
  }

  /**
   * @see com.chpconsulting.cryo.view.CryoWizardPage#getIconName()
   */
  @Override
  protected String getIconName() {
    return "data_ok.ico";
  }

  /**
   * @see org.eclipse.jface.dialogs.DialogPage#getTitle()
   */
  @Override
  public String getTitle() {
    return "Wizard complete";
  }

}
