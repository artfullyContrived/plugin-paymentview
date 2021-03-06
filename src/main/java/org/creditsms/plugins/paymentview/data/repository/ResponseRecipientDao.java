package org.creditsms.plugins.paymentview.data.repository;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;

import org.creditsms.plugins.paymentview.data.domain.Client;
import org.creditsms.plugins.paymentview.data.domain.ResponseRecipient;

public interface ResponseRecipientDao {
	/**
	 * Retrieves the ResponseRecipient List with
	 * 
	 * @param thirdPartyId
	 **/
	public List<ResponseRecipient> getResponseRecipientByThirdPartyResponseId(long thirdPartyId);

	/**
	 * Retrieves the ResponseRecipient with
	 * 
	 * @param thirdPartyId
	 * @param recipientId
	 **/
	public ResponseRecipient getResponseRecipientByTpResponseAndRecipient(
			long thirdPartyResponseId, Client recipientId);
	
	/**
	 * Retrieves all the ThirdPartyResponses in the system
	 *
	 **/
	public List<ResponseRecipient> getAllResponseRecipients();
	
	/**
	 * Deletes a ResponseRecipient from the system
	 * 
	 * @param responseRecipient
	 */
	public void deleteResponseRecipient(ResponseRecipient responseRecipient);
	
	/**
	 * updates a ResponseRecipient to the system
	 * 
	 * @param responseRecipient
	 * @throws DuplicateKeyException 
	 */
	public void updateResponseRecipient(ResponseRecipient responseRecipient) throws DuplicateKeyException;
	
	/**
	 * Saves a ResponseRecipient to the system
	 * 
	 * @param responseRecipient
	 */
	public void saveResponseRecipient(ResponseRecipient responseRecipient) throws DuplicateKeyException;
}
