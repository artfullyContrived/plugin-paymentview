package org.creditsms.plugins.paymentview.ui.handler.analytics.dialogs;

import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

public class CreateAlertHandler implements ThinletUiEventHandler {
	private static final String XML_CREATE_ALERT = "/ui/plugins/paymentview/analytics/dialogs/dlgCreateAlert.xml";

	private UiGeneratorController ui;
	private Object dialogComponent;

	private Object compPanelFields;

	public CreateAlertHandler(UiGeneratorController ui) {
		this.ui = ui;
		init();
		refresh();
	}

	private void refresh() {		
	}

	private void init() {
		dialogComponent = ui.loadComponentFromFile(XML_CREATE_ALERT,
				this);
	}

	/**
	 * @return the customizeClientDialog
	 */
	public Object getDialog() {
		return dialogComponent;
	}

	/** Remove the dialog from view. */
	public void removeDialog() {
		this.removeDialog(this.dialogComponent);
	}

	/** Remove a dialog from view. */
	public void removeDialog(Object dialog) {
		this.ui.removeDialog(dialog);
	}

	public void addField() {
		Object label = ui.createLabel("Field"); 
		ui.add(compPanelFields, label);
		Object txtfield = ui.createTextfield("", "");
		ui.add(compPanelFields, txtfield);
		this.refresh();
	}

	public void removeField() {
		// TODO Auto-generated method stub

	}

}
