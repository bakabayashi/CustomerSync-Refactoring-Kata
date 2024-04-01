package codingdojo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PersonCustomerTest {

    @Mock
    ExternalCustomer externalCustomer;

    @Test
    public void shouldPopulatePersonCustomerFields() {
        //given
        PersonCustomer customer = new PersonCustomer();
        String name = "name";
        Address address = new Address("street", "city", "code");
        String preferredStore = "preferred store";
        int bonusPointsBalance = 105;

        when(externalCustomer.getName()).thenReturn(name);
        when(externalCustomer.getPostalAddress()).thenReturn(address);
        when(externalCustomer.getPreferredStore()).thenReturn(preferredStore);
        when(externalCustomer.getBonusPointsBalance()).thenReturn(bonusPointsBalance);

        //when
        customer.populateFields(externalCustomer);

        //then
        assertEquals(name, customer.getName());
        assertEquals(address, customer.getAddress());
        assertEquals(preferredStore, customer.getPreferredStore());
        assertEquals(bonusPointsBalance, customer.getBonusPointsBalance());
    }
}