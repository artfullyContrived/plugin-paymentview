package net.frontlinesms.payment.safaricom;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import net.frontlinesms.data.domain.FrontlineMessage;

public class BalanceDispatcher {
	private Queue<MpesaPaymentService> queue = new LinkedList<MpesaPaymentService>();
	private List<FrontlineMessage> ignoredBalanceMessageList = new ArrayList<FrontlineMessage>();;
	
	private static BalanceDispatcher INSTANCE = new BalanceDispatcher();
	public static BalanceDispatcher getInstance() {return INSTANCE;}
	private BalanceDispatcher(){}
	
	public void queuePaymentService(MpesaPaymentService paymentService) {
		queue.add(paymentService);
	}
	
	public void notify(FrontlineMessage message) {
		if (!ignoredBalanceMessageList.contains(message)){
			MpesaPaymentService ps = queue.poll();
			if (ps != null) {
				ps.finaliseBalanceProcessing(message);
				addToIgnoredBalanceMessageList(message);
			}
		}
	}
	
	public void addToIgnoredBalanceMessageList(FrontlineMessage message) {
		if (ignoredBalanceMessageList.contains(message)) return;
		ignoredBalanceMessageList.add(message);
	}
	
	public boolean remove(MpesaPaymentService paymentService) {
		return queue.remove(paymentService);
	}
}