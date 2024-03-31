package codingdojo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CustomerValidatorTest {

    @Test
    public void shouldThrowExceptionWhenWrongCustomerType() {
        //given
        Customer customer = new CompanyCustomer();
        customer.setCustomerType(CustomerType.COMPANY);
        String externalId = "45671";

        //when
        //then
        String message = assertThrows(
                ConflictException.class,
                () -> CustomerValidator.validateCustomerType(customer, externalId, CustomerType.PERSON)
        ).getMessage();
        assertEquals("Existing customer for externalCustomer " + externalId + " already exists and is not a person", message);
    }

    @Test
    public void shouldNotThrowExceptionWhenCorrectCustomerType() {
        //given
        Customer customer = new CompanyCustomer();
        customer.setCustomerType(CustomerType.PERSON);
        String externalId = "45671";

        //when
        //then
        assertDoesNotThrow(
                () -> CustomerValidator.validateCustomerType(customer, externalId, CustomerType.PERSON)
        );
    }

    @Test
    public void shouldNotThrowExceptionWhenCorrectCompanyCustomerTypeAndReturnCompanyCustomer() {
        //given
        Customer customer = new CompanyCustomer();
        customer.setCustomerType(CustomerType.COMPANY);
        String externalId = "45671";

        //when
        Customer companyCustomer = CustomerValidator.validateCompanyCustomer(customer, externalId);

        //then
        assertNotNull(companyCustomer);
        assertTrue(companyCustomer instanceof CompanyCustomer);
        assertEquals(externalId, customer.getInternalId());
    }

    @Test
    public void shouldThrowExceptionWhenExternalIdDiffersFromCustomerExternalId() {
        //given
        String externalId = "45671";
        String customerExternalId = "45671";
        String companyNumber = "0000-1111";

        //when
        //then
        String message = assertThrows(
                ConflictException.class,
                () -> CustomerValidator.validateExternalId(customerExternalId, externalId, companyNumber)
        ).getMessage();
        assertEquals("Existing customer for externalCustomer " + companyNumber + " doesn't match external id "
                + externalId + " instead found " + customerExternalId, message);
    }

    @Test
    public void shouldNotThrowExceptionWhenExternalIdEqualToCustomerExternalId() {
        //given
        String externalId = "45671";
        String customerExternalId = "45671";
        String companyNumber = "0000-1111";

        //when
        //then
        assertDoesNotThrow(
                () -> CustomerValidator.validateExternalId(customerExternalId, externalId, companyNumber)
        );
    }
}