package net.frontlinesms.plugins.payment.event;

import net.frontlinesms.events.FrontlineEventNotification;
import net.frontlinesms.plugins.payment.service.PaymentStatus;

public class PaymentStatusEventNotification implements FrontlineEventNotification {
	private final PaymentStatus status;
	public PaymentStatusEventNotification(PaymentStatus status) {
		this.status = status;
	}

	public PaymentStatus getPaymentStatus() {
		return status;
	}
}