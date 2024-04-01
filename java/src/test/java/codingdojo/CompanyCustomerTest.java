package codingdojo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CompanyCustomerTest {

    @Mock
    ExternalCustomer externalCustomer;

    @Test
    public void shouldPopulatePersonCustomerFields() {
        //given
        CompanyCustomer customer = new CompanyCustomer();
        String name = "name";
        Address address = new Address("street", "city", "code");
        String preferredStore = "preferred store";
        String companyNumber = "1232-4567";

        when(externalCustomer.getCompanyNumber()).thenReturn(companyNumber);
        when(externalCustomer.getName()).thenReturn(name);
        when(externalCustomer.getPostalAddress()).thenReturn(address);
        when(externalCustomer.getPreferredStore()).thenReturn(preferredStore);

        //when
        customer.populateFields(externalCustomer);

        //then
        assertEquals(companyNumber, customer.getCompanyNumber());
        assertEquals(name, customer.getName());
        assertEquals(address, customer.getAddress());
        assertEquals(preferredStore, customer.getPreferredStore());
    }
}