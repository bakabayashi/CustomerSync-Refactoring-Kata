package codingdojo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerFactoryTest {

    @Mock
    ExternalCustomer externalCustomer;

    @Test
    public void shouldCreateCompanyCustomer() {
        //given
        String externalId = "12345";

        when(externalCustomer.isCompany()).thenReturn(true);
        when(externalCustomer.getExternalId()).thenReturn(externalId);

        //when
        Customer customer = CustomerFactory.create(externalCustomer);

        //then
        assertTrue(customer instanceof CompanyCustomer);
        assertEquals(externalId, customer.getExternalId());
        assertEquals(externalId, customer.getMasterExternalId());
    }

    @Test
    public void shouldCreatePersonCustomer() {
        //given
        String externalId = "12345";

        when(externalCustomer.isCompany()).thenReturn(false);
        when(externalCustomer.getExternalId()).thenReturn(externalId);

        //when
        Customer customer = CustomerFactory.create(externalCustomer);

        //then
        assertTrue(customer instanceof PersonCustomer);
        assertEquals(externalId, customer.getExternalId());
        assertEquals(externalId, customer.getMasterExternalId());
    }
}