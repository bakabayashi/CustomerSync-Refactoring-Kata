package codingdojo;

import java.util.List;

public class CustomerSync {

    private final CustomerDataAccess customerDataAccess;

    public CustomerSync(CustomerDataLayer customerDataLayer) {
        this(new CustomerDataAccess(customerDataLayer));
    }

    public CustomerSync(CustomerDataAccess db) {
        this.customerDataAccess = db;
    }

    public boolean syncWithDataLayer(ExternalCustomer externalCustomer) {

        CustomerMatches customerMatches;
        if (externalCustomer.isCompany()) {
            customerMatches = loadCompany(externalCustomer);
        } else {
            customerMatches = loadPerson(externalCustomer);
        }

        Customer customer = customerMatches.getCustomer();
        populateFields(externalCustomer, customer);

        boolean created = false;
        if (customer.getInternalId() == null) {
            customer = this.customerDataAccess.createCustomerRecord(customer);
            created = true;
        } else {
            this.customerDataAccess.updateCustomerRecord(customer);
        }
        updateContactInfo(externalCustomer, customer);

        if (customerMatches.hasDuplicates()) {
            for (Customer duplicate : customerMatches.getDuplicates()) {
                updateDuplicate(duplicate);
            }
        }

        updateRelations(externalCustomer, customer);
        updatePreferredStore(externalCustomer, customer);

        return created;
    }

    private void updateRelations(ExternalCustomer externalCustomer, Customer customer) {
        List<ShoppingList> consumerShoppingLists = externalCustomer.getShoppingLists();
        for (ShoppingList consumerShoppingList : consumerShoppingLists) {
            this.customerDataAccess.updateShoppingList(customer, consumerShoppingList);
        }
    }

    private void updateDuplicate(Customer duplicate) {
        if (duplicate.getInternalId() == null) {
            this.customerDataAccess.createCustomerRecord(duplicate);
        } else {
            this.customerDataAccess.updateCustomerRecord(duplicate);
        }
    }

    private void updatePreferredStore(ExternalCustomer externalCustomer, Customer customer) {
        customer.setPreferredStore(externalCustomer.getPreferredStore());
    }

    private void populateFields(ExternalCustomer externalCustomer, Customer customer) {
        customer.setName(externalCustomer.getName());
        if (externalCustomer.isCompany()) {
            customer.setCompanyNumber(externalCustomer.getCompanyNumber());
            customer.setCustomerType(CustomerType.COMPANY);
        } else {
            customer.setCustomerType(CustomerType.PERSON);
        }
    }

    private void updateContactInfo(ExternalCustomer externalCustomer, Customer customer) {
        customer.setAddress(externalCustomer.getPostalAddress());
    }

    public CustomerMatches loadCompany(ExternalCustomer externalCustomer) {
        return customerDataAccess.loadCompanyCustomer(externalCustomer);
    }

    public CustomerMatches loadPerson(ExternalCustomer externalCustomer) {
        return customerDataAccess.loadPersonCustomer(externalCustomer);
    }
}
