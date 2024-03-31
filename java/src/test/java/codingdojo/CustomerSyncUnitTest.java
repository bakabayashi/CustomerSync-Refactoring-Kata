package codingdojo;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        sync.syncWithDataLayer(externalCustomer);

        //then
        verify(externalCustomer, times(2)).getCompanyNumber();
    }

    @Test
    public void shouldExecuteSynchronizeForPersonCustomer() {
        //given
        CustomerSync sync = new CustomerSync(customerDataAccess);
        when(externalCustomer.isCompany()).thenReturn(false);

        Customer mockedCustomer = Mockito.mock(Customer.class);
        when(customerDataAccess.createCustomerRecord(any(Customer.class))).thenReturn(mockedCustomer);

        //when
        sync.syncWithDataLayer(externalCustomer);

        //then
        verify(externalCustomer, never()).getCompanyNumber();
    }
}