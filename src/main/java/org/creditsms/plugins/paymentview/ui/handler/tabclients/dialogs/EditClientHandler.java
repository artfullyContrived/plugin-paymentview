package org.creditsms.plugins.paymentview.ui.handler.tabclients.dialogs;

import java.util.ArrayList;
import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.creditsms.plugins.paymentview.data.domain.Account;
import org.creditsms.plugins.paymentview.data.domain.Client;
import org.creditsms.plugins.paymentview.data.domain.CustomField;
import org.creditsms.plugins.paymentview.data.repository.ClientDao;
import org.creditsms.plugins.paymentview.data.repository.CustomFieldDao;
import org.creditsms.plugins.paymentview.data.repository.CustomValueDao;
import org.creditsms.plugins.paymentview.ui.handler.tabclients.ClientsTabHandler;

public class EditClientHandler implements ThinletUiEventHandler {
	private static final String COMPONENT_LIST_ACCOUNTS = "fldAccounts";

	private static final String COMPONENT_TEXT_FIRST_NAME = "fldFirstName";
	private static final String COMPONENT_TEXT_OTHER_NAME = "fldOtherName";
	private static final String COMPONENT_TEXT_PHONE_NUMBER = "fldPhoneNumber";
	private static final String XML_EDIT_CLIENT = "/ui/plugins/paymentview/clients/dialogs/dlgEditClient.xml";

	private ClientDao clientDao;
	private Client client;
	private Object dialogComponent;
	private boolean editMode;

	private Object fieldFirstName;
	private Object fieldListAccounts;
	private Object fieldOtherName;
	private Object fieldPhoneNumber;
	private UiGeneratorController ui;

	private ClientsTabHandler clientsTabHandler;

	private CustomValueDao customValueDao;
	private CustomFieldDao customFieldDao;

	private Object compPanelFields;

	private int c = 0;

	private List<Object> customComponents;

	public EditClientHandler(UiGeneratorController ui, ClientDao clientDao,
			final ClientsTabHandler clientsTabHandler,
			final CustomValueDao customValueDao,
			final CustomFieldDao customFieldDao) {

		this.ui = ui;
		this.clientDao = clientDao;
		this.clientsTabHandler = clientsTabHandler;
		this.customValueDao = customValueDao;
		this.customFieldDao = customFieldDao;
		this.editMode = false;
		init();
		refresh();
	}

	public EditClientHandler(UiGeneratorController ui, Client client,
			ClientDao clientDao, final ClientsTabHandler clientsTabHandler,
			final CustomValueDao customValueDao,
			final CustomFieldDao customFieldDao) {
		this.client = client;
		this.clientsTabHandler = clientsTabHandler;
		this.customValueDao = customValueDao;
		this.customFieldDao = customFieldDao;
		this.editMode = true;
		this.clientDao = clientDao;
		this.ui = ui;
		init();
		refresh();
	}

	private Object createListItem(Account acc) {
		return ui.createListItem(acc.getAccountNumber(), acc, true);
	}

	/**
	 * @return the clientObj
	 */
	public Client getClientObj() {
		return client;
	}

	/**
	 * @return the customizeClientDialog
	 */
	public Object getDialog() {
		return dialogComponent;
	}

	public void init() {
		dialogComponent = ui.loadComponentFromFile(XML_EDIT_CLIENT, this);
		compPanelFields = ui.find(dialogComponent, "pnlFields");

		fieldFirstName = ui.find(dialogComponent, COMPONENT_TEXT_FIRST_NAME);
		fieldPhoneNumber = ui
				.find(dialogComponent, COMPONENT_TEXT_PHONE_NUMBER);
		fieldOtherName = ui.find(dialogComponent, COMPONENT_TEXT_OTHER_NAME);
		fieldListAccounts = ui.find(dialogComponent, COMPONENT_LIST_ACCOUNTS);

		System.out.println(customFieldDao);

		List<CustomField> allCustomFields = customFieldDao
				.getAllActiveUsedCustomFields();

		customComponents = new ArrayList<Object>(allCustomFields.size());

		for (CustomField cf : allCustomFields) {
			if (cf.isActive() & cf.isUsed()) {
				addField(cf.getName(), cf.getReadableName());
			}
		}

	}

	public void addField(String name, String readableName) {
		Object label = ui.createLabel(readableName);

		Object txtfield = ui.createTextfield(name, "");
		customComponents.add(txtfield);

		ui.setColspan(txtfield, 2);
		ui.setColumns(txtfield, 50);

		ui.add(compPanelFields, label);
		ui.add(compPanelFields, txtfield);
	}

	public void refresh() {
		if (editMode) {
			ui.setText(fieldFirstName, this.getClientObj().getFirstName());
			ui.setText(fieldOtherName, this.getClientObj().getOtherName());
			ui.setText(fieldPhoneNumber, this.getClientObj().getPhoneNumber());
			

			for (Account acc : this.getClientObj().getAccounts()) {
				ui.add(fieldListAccounts, createListItem(acc));
			}
		}
	}

	/** Remove the dialog from view. */
	public void removeDialog() {
		this.removeDialog(this.dialogComponent);
	}

	/** Remove a dialog from view. */
	public void removeDialog(Object dialog) {
		this.ui.removeDialog(dialog);
	}

	public void saveClient() {
		if (editMode) {
			try {
				this.client.setFirstName(ui.getText(fieldFirstName));
				this.client.setOtherName(ui.getText(fieldOtherName));
				this.client.setPhoneNumber(ui.getText(fieldPhoneNumber));
				this.clientDao.updateClient(this.client);
			} catch (DuplicateKeyException e) {
				throw new RuntimeException(e);
			}
		} else {
			String fn = ui.getText(fieldFirstName);
			String on = ui.getText(fieldOtherName);
			String phone = ui.getText(fieldPhoneNumber);
			Client c = new Client(fn, on, phone);
			try {
				this.clientDao.saveClient(c);
			} catch (DuplicateKeyException e) {
				throw new RuntimeException(e);
			}
		}
		removeDialog();
		clientsTabHandler.refresh();
	}
}
