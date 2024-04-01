package codingdojo;

import static codingdojo.CustomerValidator.validateCustomerType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class PersonCustomerSynchronizer implements CustomerSynchronizer {

    private final CustomerDataAccess customerDataAccess;

    @Override
    public boolean synchronizeData(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();
        Customer customer = customerDataAccess.findByExternalId(externalId);

        if (customer == null) {
            customer = CustomerFactory.create(externalCustomer);
        } else {
            validateCustomerType(customer, externalId, CustomerType.PERSON);
        }

        customer.populateFields(externalCustomer);
        boolean created = customerDataAccess.mergeCustomer(customer);
        customerDataAccess.updateShoppingList(customer, externalCustomer.getShoppingLists());

        return created;
    }
}