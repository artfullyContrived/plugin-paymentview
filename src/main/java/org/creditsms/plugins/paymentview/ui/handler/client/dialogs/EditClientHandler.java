package org.creditsms.plugins.paymentview.ui.handler.client.dialogs;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.creditsms.plugins.paymentview.data.domain.Account;
import org.creditsms.plugins.paymentview.data.domain.Client;
import org.creditsms.plugins.paymentview.data.repository.ClientDao;

public class EditClientHandler implements ThinletUiEventHandler {
	private static final String XML_EDIT_CLIENT = "/ui/plugins/paymentview/clients/dialogs/dlgEditClient.xml";
	
	private static final String COMPONENT_TEXT_FIRST_NAME = "fldFirstName";
	private static final String COMPONENT_TEXT_PHONE_NUMBER = "fldPhoneNumber";
	private static final String COMPONENT_TEXT_OTHER_NAME = "fldOtherName";
	private static final String COMPONENT_LIST_ACCOUNTS = "fldAccounts";

	private UiGeneratorController ui;	
	private Object dialogComponent;
	private Client clientObj;
	private ClientDao clientDao;

	private Object fieldPhoneNumber;
	private Object fieldOtherName;
	private Object fieldFirstName;
	private Object fieldListAccounts;
	private boolean editMode; 

	public EditClientHandler(UiGeneratorController ui) {
		this.ui = ui;
		this.editMode = false;
		init();
		refresh();
	}
	
	public EditClientHandler(UiGeneratorController ui, Client clientObj,
			ClientDao clientDao) {
		this.editMode = true;
		this.setClientObj(clientObj);
		this.clientDao = clientDao;
		this.ui = ui;
		init();
		refresh();
	}

	public void refresh() {
		if (editMode){
			ui.setText(fieldFirstName, this.getClientObj().getFirstName());
			ui.setText(this.fieldOtherName, this.getClientObj().getOtherName());
			ui.setText(fieldPhoneNumber, this.getClientObj().getPhoneNumber());
			
			for (Account acc : this.getClientObj().getAccounts()) {  
				ui.add(fieldListAccounts, createListItem(acc));  
			}
		}
	}

	private Object createListItem(Account acc) {
		return ui.createListItem(Long.toString(acc.getAccountNumber()), acc, true);		
	}	

	public void init() {
		dialogComponent = ui.loadComponentFromFile(XML_EDIT_CLIENT, this);

		fieldFirstName = ui.find(dialogComponent, COMPONENT_TEXT_FIRST_NAME);
		fieldPhoneNumber = ui.find(dialogComponent, COMPONENT_TEXT_PHONE_NUMBER);
		fieldOtherName = ui.find(dialogComponent, COMPONENT_TEXT_OTHER_NAME);
		fieldListAccounts = ui.find(dialogComponent, COMPONENT_LIST_ACCOUNTS); 
	}

	/**
	 * @return the customizeClientDialog
	 */
	public Object getDialog() {
		return dialogComponent;
	}

	public void saveClient() throws DuplicateKeyException {
		this.clientDao.saveClient(getClientObj());  
	}

	/** Remove the dialog from view. */
	public void removeDialog() {
		this.removeDialog(this.dialogComponent);
	}

	/** Remove a dialog from view. */
	public void removeDialog(Object dialog) {
		this.ui.removeDialog(dialog);
	}

	/**
	 * @param clientObj
	 * the clientObj to set
	 */
	public void setClientObj(Client clientObj) {
		this.clientObj = clientObj;
	}

	/**
	 * @return the clientObj
	 */
	public Client getClientObj() {
		return clientObj;
	}
}