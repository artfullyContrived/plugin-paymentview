package net.frontlinesms.payment.safaricom;

import static org.mockito.Mockito.mock;
import net.frontlinesms.events.EventBus;

public class MpesaPayBillServiceTest extends
		MpesaPaymentServiceTest<MpesaPayBillService> {
	
	@Override
	protected MpesaPayBillService createNewTestClass() {
		return new MpesaPayBillService(mock(EventBus.class));
	}

	public void testIncomingPayBillProcessing() throws Exception {
		testIncomingPaymentProcessing("BH45UU225 Confirmed.\n"
						+ "on 5/4/11 at 2:45 PM\n"
						+ "Ksh950 received from BORIS BECKER 254723908002.\n"
						+ "Account Number 0700000021\n"
						+ "New Utility balance is Ksh50,802\n"
						+ "Time: 05/04/2011 14:45:34",
				PHONENUMBER_2, ACCOUNTNUMBER_2_1, "950", "BH45UU225",
				"BORIS BECKER", "14:45 5 Apr 2011");
		
		//testing mpessa paybill message with paidby in small caps  
		testIncomingPaymentProcessing("BH45UU225 Confirmed.\n"
						+ "on 5/4/11 at 2:45 PM\n"
						+ "Ksh950 received from ian mbogua kuburi 254723908002.\n"
						+ "Account Number 0700000021\n"
						+ "New Utility balance is Ksh50,802\n"
						+ "Time: 05/04/2011 14:45:34",
				PHONENUMBER_2, ACCOUNTNUMBER_2_1, "950", "BH45UU225",
				"ian mbogua kuburi", "14:45 5 Apr 2011");
		
		// Check the payment time is processed rather than the balance time
		testIncomingPaymentProcessing("BHT57U225 Confirmed.\n"
						+ "on 5/4/11 at 1:45 PM\n"
						+ "Ksh123 received from ELLY ASAKHULU 254723908002.\n"
						+ "Account Number 0700000022\n"
						+ "New Utility balance is Ksh50,802\n"
						+ "Time: 05/04/2011 16:45:34",
				PHONENUMBER_2, ACCOUNTNUMBER_2_2, "123", "BHT57U225",
				"ELLY ASAKHULU", "13:45 5 Apr 2011");
	}

	@Override
	String[] getValidMessagesText() {
		return new String[] {
				"Real text from a MPESA Paybill confirmation",
				"BH45UU225 Confirmed.\n"
						+ "on 5/4/11 at 2:45 PM\n"
						+ "Ksh950 received from BORIS BECKER 254723908002.\n"
						+ "Account Number 0700000021\n"
						+ "New Utility balance is Ksh50,802\n"
						+ "Time: 05/04/2011 14:45:34",
						
				"Test in case someone has only one name",
				"BHT57U225 Confirmed.\n"
						+ "on 5/4/11 at 1:45 PM\n"
						+ "Ksh123 received from ELLY 254723908002.\n"
						+ "Account Number 0700000022\n"
						+ "New Utility balance is Ksh50,802\n"
						+ "Time: 05/04/2011 16:45:34",
						
				"Test in case someone with three names",
				"BHT57U225 Confirmed.\n"
						+ "on 5/4/11 at 1:45 PM\n"
						+ "Ksh123 received from ELLY Y Tu 254723908002.\n"
						+ "Account Number 0700000022\n"
						+ "New Utility balance is Ksh50,802\n"
						+ "Time: 05/04/2011 16:45:34",
						
				"Test in case confirmation codes are made longer",
				"BHT57U225XXX Confirmed.\n"
						+ "on 5/4/11 at 1:45 PM\n"
						+ "Ksh123 received from ELLY 254723908002.\n"
						+ "Account Number 0700000022\n"
						+ "New Utility balance is Ksh50,802\n"
						+ "Time: 05/04/2011 16:45:34",
		};
	}
	
	@Override
	String[] getInvalidMessagesText() {
		return new String[] {
				"No newline after 'Confirmed.'",
				"BH45UU225 Confirmed."
						+ "on 5/4/11 at 2:45 PM\n"
						+ "Ksh950 received from BORIS BECKER 254723908002.\n"
						+ "Account Number 0700000021\n"
						+ "New Utility balance is Ksh50,802\n"
						+ "Time: 05/04/2011 14:45:34",
						
				"American Christmas - day and month exchanged in date format",
				"BHT57U225 Confirmed.\n"
						+ "on 5/4/11 at 1:45 PM\n"
						+ "Ksh123 received from ELLY ASAKHULU 254723908002.\n"
						+ "Account Number 0700000022\n"
						+ "New Utility balance is Ksh50,802\n"
						+ "Time: 12/25/2011 16:45:34",
						
				"29 undecimber",
				"BHT57U225XXX Confirmed.\n"
						+ "on 29/13/11 at 1:45 PM\n"
						+ "Ksh123 received from ELLY 254723908002.\n"
						+ "Account Number 0700000022\n"
						+ "New Utility balance is Ksh50,802\n"
						+ "Time: 29/13/2011 16:45:34",
		};
	}
}