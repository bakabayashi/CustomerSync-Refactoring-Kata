package codingdojo;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerSyncUnitTest {

    @Mock
    CustomerDataAccess customerDataAccess;

    @Mock
    ExternalCustomer externalCustomer;

    @Test
    public void shouldExecuteSynchronizeForCompanyCustomer() {
        //given
        CustomerSync sync = new CustomerSync(customerDataAccess);
        when(externalCustomer.isCompany()).thenReturn(true);
        when(externalCustomer.getCompanyNumber()).thenReturn("12345");

        //when
        boolean created = sync.syncWithDataLayer(externalCustomer);

        //then
        assertFalse(created);
        verify(externalCustomer, times(2)).getCompanyNumber();
    }

    @Test
    public void shouldExecuteSynchronizeForPersonCustomer() {
        //given
        CustomerSync sync = new CustomerSync(customerDataAccess);
        when(externalCustomer.isCompany()).thenReturn(false);
        when(customerDataAccess.mergeCustomer(any(Customer.class))).thenReturn(true);

        //when
        boolean created = sync.syncWithDataLayer(externalCustomer);

        //then
        assertTrue(created);
        verify(externalCustomer, never()).getCompanyNumber();
    }
}