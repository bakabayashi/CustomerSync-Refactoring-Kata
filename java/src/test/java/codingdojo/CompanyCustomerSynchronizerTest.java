package codingdojo;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompanyCustomerSynchronizerTest {

    @Mock
    CustomerDataLayer customerDataLayer;

    @Mock
    ExternalCustomer externalCustomer;

    @Mock
    CompanyCustomer customer;

    @Test
    public void shouldSynchronizeExistingCompanyUserByExternalId() {
        //given
        String externalId = "12345";
        String companyNumber = "1234-4321";

        when(externalCustomer.getExternalId()).thenReturn(externalId);
        when(externalCustomer.getCompanyNumber()).thenReturn(companyNumber);
        ArrayList<ShoppingList> shoppingLists = new ArrayList<>();
        ShoppingList shoppingList = new ShoppingList();
        shoppingLists.add(shoppingList);
        when(externalCustomer.getShoppingLists()).thenReturn(shoppingLists);

        when(customer.getCompanyNumber()).thenReturn(companyNumber);
        when(customer.getCustomerType()).thenReturn(CustomerType.COMPANY);
        when(customer.getInternalId()).thenReturn("1");
        when(customerDataLayer.findByExternalId(externalId)).thenReturn(customer);

        CustomerDataAccess customerDataAccess = new CustomerDataAccess(customerDataLayer);
        CompanyCustomerSynchronizer synchronizer = new CompanyCustomerSynchronizer(customerDataAccess);

        //when
        boolean newlyCreated = synchronizer.synchronizeData(externalCustomer);

        //then
        assertFalse(newlyCreated);
        verify(customerDataLayer, never()).createCustomerRecord(customer);
        verify(customerDataLayer, times(2)).updateCustomerRecord(customer);
        verify(customerDataLayer).updateShoppingList(shoppingList);
    }

    @Test
    public void shouldSynchronizeExistingCompanyUserByExternalIdAndUpdateDuplicateByMasterExternalId() {
        //given
        String externalId = "12345";
        String companyNumber = "1234-4321";
        String companyName = "name";

        when(externalCustomer.getExternalId()).thenReturn(externalId);
        when(externalCustomer.getCompanyNumber()).thenReturn(companyNumber);
        when(externalCustomer.getName()).thenReturn(companyName);
        ArrayList<ShoppingList> shoppingLists = new ArrayList<>();
        ShoppingList shoppingList = new ShoppingList();
        shoppingLists.add(shoppingList);
        when(externalCustomer.getShoppingLists()).thenReturn(shoppingLists);

        when(customer.getCompanyNumber()).thenReturn(companyNumber);
        when(customer.getCustomerType()).thenReturn(CustomerType.COMPANY);
        when(customer.getInternalId()).thenReturn("1");
        when(customerDataLayer.findByExternalId(externalId)).thenReturn(customer);

        CompanyCustomer anotherCompanyCustomer = mock(CompanyCustomer.class);
        when(customerDataLayer.findByMasterExternalId(externalId)).thenReturn(anotherCompanyCustomer);

        CustomerDataAccess customerDataAccess = new CustomerDataAccess(customerDataLayer);
        CompanyCustomerSynchronizer synchronizer = new CompanyCustomerSynchronizer(customerDataAccess);

        //when
        boolean newlyCreated = synchronizer.synchronizeData(externalCustomer);

        //then
        assertFalse(newlyCreated);
        verify(customerDataLayer, never()).createCustomerRecord(customer);
        verify(customerDataLayer, times(2)).updateCustomerRecord(customer);
        verify(customerDataLayer).updateCustomerRecord(anotherCompanyCustomer);
        verify(anotherCompanyCustomer).setName(companyName);
        verify(customerDataLayer).updateShoppingList(shoppingList);
    }

    @Test
    public void shouldSynchronizeExistingCompanyUserByExternalIdAndUpdateWrongCompanyNumber() {
        //given
        String externalId = "12345";
        String companyNumber = "1234-4321";
        String companyName = "name";

        when(externalCustomer.getExternalId()).thenReturn(externalId);
        when(externalCustomer.getCompanyNumber()).thenReturn(companyNumber);
        when(externalCustomer.getName()).thenReturn(companyName);
        ArrayList<ShoppingList> shoppingLists = new ArrayList<>();
        ShoppingList shoppingList = new ShoppingList();
        shoppingLists.add(shoppingList);
        when(externalCustomer.getShoppingLists()).thenReturn(shoppingLists);

        String anotherCompanyNumber = "0001-1000";
        when(customer.getCompanyNumber()).thenReturn(anotherCompanyNumber);
        when(customer.getCustomerType()).thenReturn(CustomerType.COMPANY);
        when(customerDataLayer.findByExternalId(externalId)).thenReturn(customer);

        CustomerDataAccess customerDataAccess = new CustomerDataAccess(customerDataLayer);
        CompanyCustomerSynchronizer synchronizer = new CompanyCustomerSynchronizer(customerDataAccess);

        //when
        boolean newlyCreated = synchronizer.synchronizeData(externalCustomer);

        //then
        assertTrue(newlyCreated);
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDataLayer).createCustomerRecord(customerCaptor.capture());
        assertEquals(externalId, customerCaptor.getValue().getExternalId());

        verify(customer).setMasterExternalId(null);
        verify(customer).setName(companyName);
        verify(customerDataLayer).updateCustomerRecord(customer);
        verify(customerDataLayer).updateShoppingList(shoppingList);
    }

    @Test
    public void shouldSynchronizeExistingCompanyUserByCompanyNumber() {
        //given
        String externalId = "12345";
        String companyNumber = "1234-4321";

        when(externalCustomer.getExternalId()).thenReturn(externalId);
        when(externalCustomer.getCompanyNumber()).thenReturn(companyNumber);
        ArrayList<ShoppingList> shoppingLists = new ArrayList<>();
        ShoppingList shoppingList = new ShoppingList();
        shoppingLists.add(shoppingList);
        when(externalCustomer.getShoppingLists()).thenReturn(shoppingLists);

        when(customer.getCustomerType()).thenReturn(CustomerType.COMPANY);
        when(customer.getExternalId()).thenReturn(externalId);
        when(customer.getInternalId()).thenReturn("1");
        when(customerDataLayer.findByCompanyNumber(companyNumber)).thenReturn(customer);

        CustomerDataAccess customerDataAccess = new CustomerDataAccess(customerDataLayer);
        CompanyCustomerSynchronizer synchronizer = new CompanyCustomerSynchronizer(customerDataAccess);

        //when
        boolean newlyCreated = synchronizer.synchronizeData(externalCustomer);

        //then
        assertFalse(newlyCreated);
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDataLayer).createCustomerRecord(customerCaptor.capture());
        assertEquals(externalId, customerCaptor.getValue().getExternalId());
        verify(customerDataLayer, times(2)).updateCustomerRecord(customer);
    }

    @Test
    public void shouldSynchronizeNonExistingCompanyUser() {
        //given
        String externalId = "12345";
        String companyNumber = "1234-4321";

        when(externalCustomer.getExternalId()).thenReturn(externalId);
        when(externalCustomer.getCompanyNumber()).thenReturn(companyNumber);
        ArrayList<ShoppingList> shoppingLists = new ArrayList<>();
        ShoppingList shoppingList = new ShoppingList();
        shoppingLists.add(shoppingList);
        when(externalCustomer.getShoppingLists()).thenReturn(shoppingLists);

        CustomerDataAccess customerDataAccess = new CustomerDataAccess(customerDataLayer);
        CompanyCustomerSynchronizer synchronizer = new CompanyCustomerSynchronizer(customerDataAccess);

        //when
        boolean newlyCreated = synchronizer.synchronizeData(externalCustomer);

        //then
        assertTrue(newlyCreated);
        ArgumentCaptor<Customer> customerCaptor = ArgumentCaptor.forClass(Customer.class);
        verify(customerDataLayer).createCustomerRecord(customerCaptor.capture());
        assertEquals(externalId, customerCaptor.getValue().getExternalId());
        verify(customerDataLayer).updateCustomerRecord(any(Customer.class));
        verify(customerDataLayer).updateShoppingList(shoppingList);
    }
}