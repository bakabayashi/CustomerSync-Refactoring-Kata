package codingdojo;

import static codingdojo.CustomerValidator.validateCustomerType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PersonCustomerSynchronizer implements CustomerSynchronizer {

    private final CustomerDataAccess customerDataAccess;

    @Override
    public boolean synchronizeData(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();
        Customer customer = customerDataAccess.findByExternalId(externalId);
        boolean shouldCreate = (customer == null);

        if (shouldCreate) {
            customer = customerDataAccess.createCustomerRecord(
                    CustomerFactory.create(externalCustomer)
            );
        } else {
            validateCustomerType(customer, externalId, CustomerType.PERSON);
        }

        customer.populateFields(externalCustomer);
        customerDataAccess.updateShoppingList(customer, externalCustomer.getShoppingLists());

        return shouldCreate;
    }
}