package codingdojo;

import static codingdojo.CustomerValidator.*;

public class CustomerDataAccess {

    private final CustomerDataLayer customerDataLayer;

    public CustomerDataAccess(CustomerDataLayer customerDataLayer) {
        this.customerDataLayer = customerDataLayer;
    }

    public CustomerMatches loadCompanyCustomer(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();
        final String companyNumber = externalCustomer.getCompanyNumber();
        CustomerMatches matches = new CustomerMatches();
        Customer customer = this.customerDataLayer.findByExternalId(externalId);

        if (customer != null) {
            CompanyCustomer companyCustomer = validateCompanyCustomer(customer, externalId);
            Customer matchByMasterId = this.customerDataLayer.findByMasterExternalId(externalId);
            if (matchByMasterId != null) {
                matchByMasterId.setName(externalCustomer.getName());
                matches.addDuplicate(matchByMasterId);
            }

            String customerCompanyNumber = companyCustomer.getCompanyNumber();
            if (!companyNumber.equals(customerCompanyNumber)) {
                companyCustomer.setMasterExternalId(null);
                companyCustomer.setName(externalCustomer.getName());
                matches.addDuplicate(companyCustomer);
                customer = CustomerFactory.create(externalCustomer);
            }
        } else {
            customer = this.customerDataLayer.findByCompanyNumber(companyNumber);
            if (customer != null) {
                validateCustomerType(customer, externalId, CustomerType.COMPANY);
                matches.setCustomer(customer);
                String customerExternalId = customer.getExternalId();
                validateExternalId(customerExternalId, externalId, companyNumber);
                customer.setExternalId(externalId);
                customer.setMasterExternalId(externalId);
                Customer duplicate = CustomerFactory.create(externalCustomer);
                matches.addDuplicate(duplicate);
            } else {
                customer = CustomerFactory.create(externalCustomer);
            }
        }

        customer.populateFields(externalCustomer);
        matches.setCustomer(customer);

        return matches;
    }

    public CustomerMatches loadPersonCustomer(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();
        CustomerMatches matches = new CustomerMatches();
        Customer customer = this.customerDataLayer.findByExternalId(externalId);

        if (customer == null) {
            customer = CustomerFactory.create(externalCustomer);
        } else {
            validateCustomerType(customer, externalId, CustomerType.PERSON);
        }

        customer.populateFields(externalCustomer);
        matches.setCustomer(customer);

        return matches;
    }

    public Customer updateCustomerRecord(Customer customer) {
        return customerDataLayer.updateCustomerRecord(customer);
    }

    public Customer createCustomerRecord(Customer customer) {
        return customerDataLayer.createCustomerRecord(customer);
    }

    public void updateShoppingList(Customer customer, ShoppingList consumerShoppingList) {
        customer.addShoppingList(consumerShoppingList);
        customerDataLayer.updateShoppingList(consumerShoppingList);
        customerDataLayer.updateCustomerRecord(customer);
    }
}
