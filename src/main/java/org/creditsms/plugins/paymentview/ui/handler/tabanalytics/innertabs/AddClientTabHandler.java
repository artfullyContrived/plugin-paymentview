package org.creditsms.plugins.paymentview.ui.handler.tabanalytics.innertabs;

import net.frontlinesms.ui.UiGeneratorController;
import net.frontlinesms.ui.handler.BaseTabHandler;

import org.creditsms.plugins.paymentview.data.repository.ClientDao;
import org.creditsms.plugins.paymentview.ui.PaymentViewThinletTabController;
import org.creditsms.plugins.paymentview.ui.handler.tabanalytics.innertabs.steps.addclient.SelectTargetSavingsHandler;

public class AddClientTabHandler extends BaseTabHandler {

	private static final String TAB_CREATE_DASHBOARD = "tab_createDashboard";

	
	private Object createDashboardTab;
	private static Object currentPanel;

	private ClientDao clientDao;


	private PaymentViewThinletTabController paymentViewThinletTabController;

	public AddClientTabHandler(UiGeneratorController ui, Object tabAnalytics, final PaymentViewThinletTabController paymentViewThinletTabController) {
		super(ui);
		this.paymentViewThinletTabController = paymentViewThinletTabController;
		this.clientDao = paymentViewThinletTabController.getClientDao();
		createDashboardTab = ui.find(tabAnalytics, TAB_CREATE_DASHBOARD);
		this.init();
	}

	public void deinit() {
	}

	@Override
	protected Object initialiseTab() {
		setCurrentStepPanel(new SelectTargetSavingsHandler(ui, clientDao, this)
				.getPanelComponent());
		return createDashboardTab;
	}

	public void refresh() {
		paymentViewThinletTabController.refresh();
	}

	public void setCurrentStepPanel(Object panel) {
		if (currentPanel != null)
			ui.remove(currentPanel);

		ui.add(createDashboardTab, panel);
		currentPanel = panel;
		this.refresh();
	}
}