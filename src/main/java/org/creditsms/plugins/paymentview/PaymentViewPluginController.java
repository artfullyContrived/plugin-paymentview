/*
 * FrontlineSMS:Credit - http://www.creditsms.org
 * Copyright (C) - 2009, 2010
 * 
 * This file is part of FrontlineSMS:Credit
 * 
 */
package org.creditsms.plugins.paymentview;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.frontlinesms.BuildProperties;
import net.frontlinesms.FrontlineSMS;
import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.events.EventObserver;
import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.payment.PaymentService;
import net.frontlinesms.plugins.BasePluginController;
import net.frontlinesms.plugins.PluginControllerProperties;
import net.frontlinesms.plugins.PluginInitialisationException;
import net.frontlinesms.ui.ThinletUiEventHandler;
import net.frontlinesms.ui.UiGeneratorController;

import org.apache.log4j.Logger;
import org.creditsms.plugins.paymentview.analytics.TargetAnalytics;
import org.creditsms.plugins.paymentview.data.repository.AccountDao;
import org.creditsms.plugins.paymentview.data.repository.ClientDao;
import org.creditsms.plugins.paymentview.data.repository.CustomFieldDao;
import org.creditsms.plugins.paymentview.data.repository.CustomValueDao;
import org.creditsms.plugins.paymentview.data.repository.IncomingPaymentDao;
import org.creditsms.plugins.paymentview.data.repository.LogMessageDao;
import org.creditsms.plugins.paymentview.data.repository.OutgoingPaymentDao;
import org.creditsms.plugins.paymentview.data.repository.ResponseRecipientDao;
import org.creditsms.plugins.paymentview.data.repository.ServiceItemDao;
import org.creditsms.plugins.paymentview.data.repository.TargetDao;
import org.creditsms.plugins.paymentview.data.repository.ThirdPartyResponseDao;
import org.creditsms.plugins.paymentview.ui.PaymentViewThinletTabController;
import org.creditsms.plugins.paymentview.userhomepropeties.authorizationcode.AuthorizationProperties;
import org.creditsms.plugins.paymentview.utils.PvUtils;
import org.springframework.context.ApplicationContext;

/**
 * This is the base class for the FrontlineSMS:Credit PaymentView plugin. The
 * PaymentView plugin is used to process payments transacted via the connected
 * mobile phone. Processing of the payments includes mining the incoming message
 * for specific information and pushing the same to an online/external database
 * or system such as Mifos - http://www.mifos.org
 * 
 * @author Emmanuel Kala
 * @author Ian Onesmus Mukewa <ian@credit.frontlinesms.com>
 */
@PluginControllerProperties(name = "Payment View", iconPath = "/icons/creditsms.png", i18nKey = "plugins.paymentview", springConfigLocation = "classpath:org/creditsms/plugins/paymentview/paymentview-spring-hibernate.xml", hibernateConfigPath = "classpath:org/creditsms/plugins/paymentview/paymentview.hibernate.cfg.xml")
public class PaymentViewPluginController extends BasePluginController
		implements ThinletUiEventHandler, EventObserver {

//> CONSTANTS
	/** Filename and path of the XML for the PaymentView tab */

	private AccountDao accountDao;
	private ClientDao clientDao;
	private CustomValueDao customValueDao;
	private CustomFieldDao customFieldDao;
	private IncomingPaymentDao incomingPaymentDao;
	private OutgoingPaymentDao outgoingPaymentDao;
	private TargetDao targetDao;
	private ServiceItemDao serviceItemDao;
	private LogMessageDao logMessageDao;
	private ThirdPartyResponseDao thirdPartyResponseDao;
	private ResponseRecipientDao responseRecipientDao;
	
	private TargetAnalytics targetAnalytics;
	
	private PaymentViewThinletTabController paymentViewThinletTabController;
	private UiGeneratorController ui;
	
	/** Currently we will allow only one payment service to be configured TO MAKE THINGS SIMPLER */
	private PaymentService paymentService;
	private FrontlineSMS frontlineController;
	
	/** @see net.frontlinesms.plugins.PluginController#deinit() */
	public void deinit() {
		this.frontlineController.getEventBus().unregisterObserver(this);
	}
	
//> CONFIG METHODS
	/**
	 * @see net.frontlinesms.plugins.PluginController#init(FrontlineSMS,
	 *      ApplicationContext)
	 */
	public void init(FrontlineSMS frontlineController,
			ApplicationContext applicationContext)
			throws PluginInitialisationException {
		frontlineController.getEventBus().registerObserver(this);
		
		// Initialize the DAO for the domain objects
		clientDao 			= (ClientDao) applicationContext.getBean("clientDao");
		accountDao 			= (AccountDao) applicationContext.getBean("accountDao");
		customValueDao 		= (CustomValueDao) applicationContext.getBean("customValueDao");
		customFieldDao 		= (CustomFieldDao) applicationContext.getBean("customFieldDao");
		incomingPaymentDao 	= (IncomingPaymentDao) applicationContext.getBean("incomingPaymentDao");
		outgoingPaymentDao 	= (OutgoingPaymentDao) applicationContext.getBean("outgoingPaymentDao");
		serviceItemDao 		= (ServiceItemDao) applicationContext.getBean("serviceItemDao");
		targetDao 			= (TargetDao) applicationContext.getBean("targetDao");
		logMessageDao       = (LogMessageDao) applicationContext.getBean("logMessageDao");
		thirdPartyResponseDao  = (ThirdPartyResponseDao) applicationContext.getBean("thirdPartyResponseDao");
		responseRecipientDao  = (ResponseRecipientDao) applicationContext.getBean("responseRecipientDao");
		
		this.frontlineController = frontlineController;
		
		targetAnalytics = new TargetAnalytics();
		targetAnalytics.setIncomingPaymentDao(incomingPaymentDao);
		targetAnalytics.setTargetDao(targetDao);
		
		// Default authorisation code set up to password if none
		if(!AuthorizationProperties.getInstance().isAuthCodeSet()){
			AuthorizationProperties.getInstance().setAuthCode("password");
		}
		
		// If not a production build, and database is empty, add test data
		if(BuildProperties.getInstance().isSnapshot() && clientDao.getClientCount()==0) {
			try {
				DemoData.createDemoData(applicationContext);
			} catch (DuplicateKeyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/** @see net.frontlinesms.plugins.BasePluginController#initThinletTab(UiGeneratorController) */
	@Override
	public Object initThinletTab(UiGeneratorController ui) {
		paymentViewThinletTabController = new PaymentViewThinletTabController(this, ui);
		this.ui = ui;
		return paymentViewThinletTabController.getPaymentViewTab();
	}
	
//> ACCESSORS
	public AccountDao getAccountDao() {
		return accountDao;
	}
	
	public IncomingPaymentDao getIncomingPaymentDao() {
		return this.incomingPaymentDao;
	}
	
	public OutgoingPaymentDao getOutgoingPaymentDao() {
		return outgoingPaymentDao;
	}
	
	public ClientDao getClientDao() {
		return clientDao;
	}
	
	public CustomFieldDao getCustomFieldDao() {
		return customFieldDao;
	}
	
	public CustomValueDao getCustomValueDao() {
		return customValueDao;
	}

	public TargetDao getTargetDao() {
		return targetDao;
	}

	public ServiceItemDao getServiceItemDao() {
		return serviceItemDao;
	}
	
	public LogMessageDao getLogMessageDao() {
		return logMessageDao;
	}
	
	public ThirdPartyResponseDao getThirdPartyResponseDao() {
		return thirdPartyResponseDao;
	}

	public ResponseRecipientDao getResponseRecipientDao() {
		return responseRecipientDao;
	}

	
	public UiGeneratorController getUiGeneratorController() {
		return ui;
	}

	public TargetAnalytics getTargetAnalytics() {
		return targetAnalytics;
	}

	public List<PaymentService> getPaymentServices() {
		if(this.paymentService == null) return Collections.emptyList();
		else {
			return Arrays.asList(new PaymentService[] { this.paymentService });
		}
	}

	public void updateStatusBar(String message) {
		paymentViewThinletTabController.updateStatusBar(message);
	}
	
	public void clearStatusBar(String message) {
		updateStatusBar("");
	}

	public void setPaymentService(PaymentService service) {
		this.paymentService = service;
	}

	public PaymentService getPaymentService() {
		return this.paymentService;
	}

	public void notify(FrontlineEventNotification notification) {
		//Moved to Settings Tab for consistencies sake
	}

	public Logger getLogger(Class<?> clazz) {
		return PvUtils.getLogger(clazz);
	}
	
//	public FrontlineSMS getFrontlineController() { // TODO this method shouldn't really be here :)
//		return this.frontlineController;
//	}
}
