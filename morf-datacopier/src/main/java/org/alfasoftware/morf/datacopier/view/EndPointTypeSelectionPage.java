package com.chpconsulting.cryo.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import com.chpconsulting.cryo.model.CommandBean;
import com.chpconsulting.cryo.model.CryoEndPointType;

/**
 * Select the type .
 *
 * @author Copyright (c) CHP Consulting Ltd. 2008
 */
public class EndPointTypeSelectionPage extends CryoWizardPage {

  /**
   * Stores the list of availabel end points to choose from.
   */
  private final List<CryoEndPointType> availableEndPoints = new ArrayList<CryoEndPointType>();

  /**
   * The bean to update.
   */
  private final CommandBean commandBean;

  /**
   * Indicates if we are maintaining the source or target value.
   */
  private final SourceTargetSwitch sourceTargetSwitch;

  /**
   * Creates an action selection page.
   *
   * @param pageName Identifier within the wizard.
   * @param commandBean Bean to update.
   * @param sourceTargetSwitch Indicates whether the source or target is being maintained
   * @param endPoints Available end points the user can choose from. Ignored if empty.
   */
  public EndPointTypeSelectionPage(String pageName, CommandBean commandBean, SourceTargetSwitch sourceTargetSwitch, CryoEndPointType... endPoints) {
    super(pageName);
    this.commandBean = commandBean;
    this.sourceTargetSwitch = sourceTargetSwitch;
    this.availableEndPoints.addAll(Arrays.asList(endPoints.length > 0 ? endPoints : CryoEndPointType.values()));
  }


  /**
   * @see com.chpconsulting.cryo.view.CryoWizardPage#createChildControls(org.eclipse.swt.widgets.Composite)
   */
  @Override
  public void createChildControls(Composite parent) {
    Label label = createLabel(parent, "data " + sourceTargetSwitch.toString().toLowerCase() + " type:");
    ((GridData) label.getLayoutData()).verticalAlignment = GridData.BEGINNING;

    Composite commandPanel = new Composite(parent, SWT.NONE);

    GridLayout layout = new GridLayout(1, false);
    layout.marginHeight = 0;
    layout.marginWidth  = 0;
    commandPanel.setLayout(layout);

    for (final CryoEndPointType endPoint : availableEndPoints) {
      Button commandButton = new Button(commandPanel, SWT.RADIO);
      commandButton.setText(Messages.getEnumerationCaption(endPoint));
      commandButton.setSelection(endPoint == getEndPointType());
      commandButton.addSelectionListener(new SelectionAdapter() {

        /**
         * @see org.eclipse.swt.events.SelectionAdapter#widgetSelected(org.eclipse.swt.events.SelectionEvent)
         */
        @Override
        public void widgetSelected(SelectionEvent event) {
          Button button = (Button) event.getSource();
          if (button.getSelection()) {
            setEndPointType(endPoint);
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
    return "Data " + (sourceTargetSwitch == SourceTargetSwitch.SOURCE ? "Source" : "Target") + " Selection";
  }


  /**
   * @see org.eclipse.jface.dialogs.DialogPage#getDescription()
   */
  @Override
  public String getDescription() {
    return "Select the type of data source to connect to";
  }


  /**
   * @return the endPointType
   */
  private CryoEndPointType getEndPointType() {
    return sourceTargetSwitch == SourceTargetSwitch.SOURCE ? commandBean.getSourceType() : commandBean.getTargetType();
  }


  /**
   * @param endPointType End point type to set.
   */
  private void setEndPointType(CryoEndPointType endPointType) {
    if (sourceTargetSwitch == SourceTargetSwitch.SOURCE) {
      commandBean.setSourceType(endPointType);
    } else {
      commandBean.setTargetType(endPointType);
    }
  }


  /**
   * @see com.chpconsulting.cryo.view.CryoWizardPage#getIconName()
   */
  @Override
  protected String getIconName() {
    return  getName().matches(".*Source.*") ? "data_out.ico" : "data_into.ico";
  }
}
