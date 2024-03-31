package codingdojo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerDataAccessTest {

    @Mock
    CustomerDataLayer customerDataLayer;

    @Test
    public void shouldUpdateCustomerRecord() {
        //given
        CustomerDataAccess customerDataAccess = new CustomerDataAccess(customerDataLayer);
        Customer customer = mock(Customer.class);

        //when
        customerDataAccess.updateCustomerRecord(customer);

        //then
        verify(customerDataLayer).updateCustomerRecord(customer);
    }
    @Test
    public void shouldCreateCustomerRecord() {
        //given
        CustomerDataAccess customerDataAccess = new CustomerDataAccess(customerDataLayer);
        Customer customer = mock(Customer.class);

        //when
        customerDataAccess.createCustomerRecord(customer);

        //then
        verify(customerDataLayer).createCustomerRecord(customer);
    }

    @Test
    public void shouldCreateCustomerRecordWhileMerging() {
        //given
        CustomerDataAccess customerDataAccess = new CustomerDataAccess(customerDataLayer);
        Customer customer = mock(Customer.class);

        //when
        boolean created = customerDataAccess.mergeCustomer(customer);

        //then
        assertTrue(created);
        verify(customerDataLayer).createCustomerRecord(customer);
    }

    @Test
    public void shouldNotCreateCustomerRecordWhileMergingExistingCustomer() {
        //given
        CustomerDataAccess customerDataAccess = new CustomerDataAccess(customerDataLayer);
        Customer customer = mock(Customer.class);
        when(customer.getInternalId()).thenReturn("45612");

        //when
        boolean created = customerDataAccess.mergeCustomer(customer);

        //then
        assertFalse(created);
        verify(customerDataLayer).updateCustomerRecord(customer);
    }

    @Test
    public void shouldFindByExternalId() {
        //given
        CustomerDataAccess customerDataAccess = new CustomerDataAccess(customerDataLayer);
        String externalId = "45612";

        //when
        customerDataAccess.findByExternalId(externalId);

        //then
        verify(customerDataLayer).findByExternalId(externalId);
    }

    @Test
    public void shouldFindByMasterExternalId() {
        //given
        CustomerDataAccess customerDataAccess = new CustomerDataAccess(customerDataLayer);
        String masterExternalId = "45612";

        //when
        customerDataAccess.findByMasterExternalId(masterExternalId);

        //then
        verify(customerDataLayer).findByMasterExternalId(masterExternalId);
    }

    @Test
    public void shouldFindByCompanyNumber() {
        //given
        CustomerDataAccess customerDataAccess = new CustomerDataAccess(customerDataLayer);
        String companyNumber = "0000-1234";

        //when
        customerDataAccess.findByCompanyNumber(companyNumber);

        //then
        verify(customerDataLayer).findByCompanyNumber(companyNumber);
    }

    @Test
    public void shouldUpdateShoppingLists() {
        //given
        CustomerDataAccess customerDataAccess = new CustomerDataAccess(customerDataLayer);
        Customer customer = mock(Customer.class);
        List<ShoppingList> shoppingLists = new ArrayList<>();
        ShoppingList fruits = new ShoppingList("apple", "banana");
        shoppingLists.add(fruits);
        ShoppingList cars = new ShoppingList("Ferrari", "Fiat");
        shoppingLists.add(cars);

        //when
        customerDataAccess.updateShoppingList(customer, shoppingLists);

        //then
        verify(customer).addShoppingList(fruits);
        verify(customerDataLayer).updateShoppingList(fruits);
        verify(customer).addShoppingList(cars);
        verify(customerDataLayer).updateShoppingList(cars);
        verify(customerDataLayer, times(2)).updateCustomerRecord(customer);
    }
}