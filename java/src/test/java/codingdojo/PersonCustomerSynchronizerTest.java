package codingdojo;

import static codingdojo.CustomerType.PERSON;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonCustomerSynchronizerTest {

    @Mock
    CustomerDataAccess customerDataAccess;

    @Mock
    ExternalCustomer externalCustomer;

    @Mock
    Customer customer;

    @Test
    public void shouldSynchronizeExistingPersonCustomer() {
        //given
        PersonCustomerSynchronizer synchronizer = new PersonCustomerSynchronizer(customerDataAccess);
        String externalId = "12345";
        when(externalCustomer.getExternalId()).thenReturn(externalId);
        when(customerDataAccess.findByExternalId(externalId)).thenReturn(customer);
        when(customer.getCustomerType()).thenReturn(PERSON);

        //when
        boolean newlyCreated = synchronizer.synchronizeData(externalCustomer);

        //then
        assertFalse(newlyCreated);
        verify(customerDataAccess, never()).createCustomerRecord(customer);
        verify(customer).populateFields(externalCustomer);
        verify(customerDataAccess).updateShoppingList(customer, new ArrayList<>());
    }

    @Test
    public void shouldSynchronizeNonExistingPersonCustomer() {
        //given
        PersonCustomerSynchronizer synchronizer = new PersonCustomerSynchronizer(customerDataAccess);
        String externalId = "12345";
        when(externalCustomer.getExternalId()).thenReturn(externalId);
        when(customerDataAccess.createCustomerRecord(any(Customer.class))).thenReturn(customer);

        //when
        boolean newlyCreated = synchronizer.synchronizeData(externalCustomer);

        //then
        assertTrue(newlyCreated);
        verify(customer).populateFields(externalCustomer);
        verify(customerDataAccess).updateShoppingList(customer, new ArrayList<>());
    }
}