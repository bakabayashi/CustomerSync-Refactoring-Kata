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
            customerMatches = customerDataAccess.loadCompanyCustomer(externalCustomer);
        } else {
            customerMatches = customerDataAccess.loadPersonCustomer(externalCustomer);
        }

        Customer customer = customerMatches.getCustomer();

        boolean created = false;
        if (customer.getInternalId() == null) {
            customer = this.customerDataAccess.createCustomerRecord(customer);
            created = true;
        } else {
            this.customerDataAccess.updateCustomerRecord(customer);
        }

        if (customerMatches.hasDuplicates()) {
            for (Customer duplicate : customerMatches.getDuplicates()) {
                updateDuplicate(duplicate);
            }
        }

        updateRelations(externalCustomer, customer);

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
}
