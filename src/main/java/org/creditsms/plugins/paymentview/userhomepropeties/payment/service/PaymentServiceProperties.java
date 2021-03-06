package org.creditsms.plugins.paymentview.userhomepropeties.payment.service;
import net.frontlinesms.plugins.payment.service.PaymentService;
import net.frontlinesms.resources.UserHomeFilePropertySet;

public class PaymentServiceProperties extends UserHomeFilePropertySet {
	private static final String SMS_MODEM_SERIAL = "sms.modem.serial";
	private static final String PAYMENT_SERVICE_CLASS = "payment.service.class";
	private static final String PIN = "payment.service.pin";
	private static final PaymentServiceProperties INSTANCE = new PaymentServiceProperties(); 
	
	private PaymentServiceProperties() {
		super("paymentservice-settings");
	}
	
	public void setSmsModem(String val)  {
		setProperty(SMS_MODEM_SERIAL, val);
	}
	
	public String getSmsModemSerial() {
		String value = super.getProperty(SMS_MODEM_SERIAL);
		if(value != null){
			return value;
		}
		return null;
	}
	
	public void setPaymentServiceClass(Class<? extends PaymentService> val)  {
		if(val!=null){
			setProperty(PAYMENT_SERVICE_CLASS, val.getName());
		}else{
			setProperty(PAYMENT_SERVICE_CLASS, "");
		}
		
	}
	
	public PaymentService initPaymentService() {
		String value = super.getProperty(PAYMENT_SERVICE_CLASS);
		if(value != null){
			try {
				return (PaymentService) Class.forName(value).newInstance();
			} catch (Exception ex) {
				LOG.warn("Unable to load payment service specified in properties file.", ex);
			}
		}
		return null;
	}
	
	public void setPin(String val)  {
		setProperty(PIN, val);
	}
	
	public String getPin() {
		String value = super.getProperty(PIN);
		if(value != null){
			return value;
		}
		return null;
	}
	
	public static PaymentServiceProperties getInstance() {
		return INSTANCE;
	}
}
