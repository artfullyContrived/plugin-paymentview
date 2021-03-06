package org.creditsms.plugins.paymentview.data.repository.hibernate;

import java.util.List;

import net.frontlinesms.data.DuplicateKeyException;
import net.frontlinesms.junit.HibernateTestCase;

import org.creditsms.plugins.paymentview.data.domain.Client;
import org.creditsms.plugins.paymentview.data.domain.CustomField;
import org.creditsms.plugins.paymentview.data.domain.CustomValue;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Roy
 */
public class CustomClientDataIntergrationTest extends HibernateTestCase {
	@Autowired                     
	HibernateCustomValueDao hibernateOtherClientDetailsDao;
	@Autowired
	HibernateClientDao hibernateClientDao;
	@Autowired                     
	HibernateCustomFieldDao hibernateCustomFieldDao;
	
	public void testSetup() {
		assertNotNull(hibernateOtherClientDetailsDao);
		assertNotNull(hibernateClientDao);
		assertNotNull(hibernateCustomFieldDao);
	}
	
	public void testSave() throws DuplicateKeyException {
		assertEmptyDatabase();
		createAndSaveCustomField("Location", 1);
		CustomValue ocd = createOtherClientdetails("Kariobangi South", getCustomField());
		hibernateOtherClientDetailsDao.saveCustomValue(ocd);
		assertEquals(1, hibernateOtherClientDetailsDao.getAllCustomValues().size());
	}
	
	public void testGetOtherClientDetailsById() throws DuplicateKeyException{
		assertEmptyDatabase();
		createAndSaveCustomField("Location", 1);
		createAndSaveOtherClientDetails("Kariobangi", null, 1, getCustomField());
		long gId = getOtherClientDetails().getId();
		assertEquals("Kariobangi",hibernateOtherClientDetailsDao.getCustomValueById(gId).getStrValue());
	}
	
	public void testGetOtherClientDetailsByClientId() throws DuplicateKeyException{
		assertEmptyDatabase();
		createAndSaveCustomField("Location", 1);
		Client c1 = createAndSaveClient("0721000000", "Waweru Nguru", 1);
		createAndSaveOtherClientDetails("Kamkunji", c1, 1, getCustomField());
		long clientId = getOtherClientDetails().getClient().getId();
		
		assertEquals("Kamkunji",hibernateOtherClientDetailsDao.getCustomValuesByClientId(clientId).get(0).getStrValue());
	}
	
	public void testDeleteAccount() throws DuplicateKeyException{
		assertEmptyDatabase();
		createAndSaveCustomField("Location", 1);
		createAndSaveOtherClientDetails("Mkuru Kwa Jenga", null, 1, getCustomField());
		hibernateOtherClientDetailsDao.deleteCustomValue(getOtherClientDetails());
		assertEquals(0, hibernateOtherClientDetailsDao.getAllCustomValues().size());
	}
	
	private void createAndSaveOtherClientDetails(String cfName, Client client, int expectedPaymentCount, CustomField cf) throws DuplicateKeyException{
		hibernateOtherClientDetailsDao.saveCustomValue(createOtherClientdetails(cfName, client, cf));
		assertEquals(1, hibernateOtherClientDetailsDao.getAllCustomValues().size());
	}
	
	private CustomValue getOtherClientDetails(){
		assertEquals(1, hibernateOtherClientDetailsDao.getAllCustomValues().size());
		List<CustomValue> lstCustomValue = hibernateOtherClientDetailsDao.getAllCustomValues();
		return(this.hibernateOtherClientDetailsDao.getCustomValueById(lstCustomValue.get(0).getId()));
	}
	
	
	private Client createAndSaveClient(String phnNumber, String firstName, int expectedAccountCount) throws DuplicateKeyException{
		Client c = new Client();
		c.setPhoneNumber(phnNumber);
		c.setFirstName(firstName);
		hibernateClientDao.saveClient(c);
		assertEquals(expectedAccountCount, hibernateClientDao.getAllClients().size());
		return c;
	}
	
	private CustomValue createOtherClientdetails(String strVal, CustomField cf){
		CustomValue ocd = new CustomValue();
		ocd.setStrValue(strVal);
		ocd.setCustomField(cf);
		return ocd;
	}
	
	private CustomValue createOtherClientdetails(String strVal, Client client, CustomField cf){
		CustomValue ocd = new CustomValue();
		ocd.setStrValue(strVal);
		ocd.setCustomField(cf);
		if(client!=null) ocd.setClient(client);
		return ocd;
	}
	
	private void createAndSaveCustomField(String strName, int expectedCustomFieldCount) throws DuplicateKeyException{
        CustomField cf = createCustomField(strName);
        hibernateCustomFieldDao.saveCustomField(cf);
        assertEquals(expectedCustomFieldCount, hibernateCustomFieldDao.getCustomFieldCount());
	}
	
	private CustomField createCustomField(String strName){
		CustomField cf = new CustomField();
		cf.setReadableName(strName);
		return cf;
	}
	
	private CustomField getCustomField(){
		return hibernateCustomFieldDao.getAllCustomFields().get(0);
	}
	
	private void assertEmptyDatabase(){
		assertEquals(0, hibernateOtherClientDetailsDao.getAllCustomValues().size());
	}
}
