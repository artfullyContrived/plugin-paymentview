package org.creditsms.plugins.paymentview.ui.handler.client.dialogs;

import java.lang.reflect.Field;

import javax.persistence.Column;

import org.creditsms.plugins.paymentview.data.domain.Client;

import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

public class CustomizeClientDBHandler implements ThinletUiEventHandler {
	private static final String XML_CUSTOMIZE_CLIENT_DB = "/ui/plugins/paymentview/clients/dialogs/dlgCustomizeClient.xml";

	private static final String COMPONENT_PNL_FIELDS = "pnlFields";

	private UiGeneratorController ui;
	private Object dialogComponent;

	private Object compPanelFields;

	public CustomizeClientDBHandler(UiGeneratorController ui) {
		this.ui = ui;
		init();
		refresh();
	}

	private void refresh() {		
	}

	private void init() {
		dialogComponent = ui.loadComponentFromFile(XML_CUSTOMIZE_CLIENT_DB,
				this);
		compPanelFields = ui.find(dialogComponent, COMPONENT_PNL_FIELDS);
		
		Object label;
		Object txtfield;
		String name;
		int c = 0;
		for (Field field : Client.class.getDeclaredFields()) {
			if (field.isAnnotationPresent(Column.class)) {
				name = field.getName();
				label = ui.createLabel("Field "+ ++c); 
				txtfield = ui.createTextfield("fld"+name, name);
				ui.setColspan(txtfield, 2);
				ui.setColumns(txtfield, 50);				
				ui.add(compPanelFields, label);
				ui.add(compPanelFields, txtfield);				
			}			
		}		
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

	public void showOtherFieldDialog() {
		ui.add(new OtherFieldHandler(ui, this).getDialog());
	}
	
	public void addField() {
		
	}

}