package codingdojo;

import static codingdojo.CustomerValidator.validateCustomerType;
import static codingdojo.CustomerValidator.validateExternalId;

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
            validateCustomerType(customer, externalId, CustomerType.COMPANY);
            Customer matchByMasterId = this.customerDataLayer.findByMasterExternalId(externalId);
            if (matchByMasterId != null) {
                matchByMasterId.setName(externalCustomer.getName());
                matches.addDuplicate(matchByMasterId);
            }

            String customerCompanyNumber = customer.getCompanyNumber();
            if (!companyNumber.equals(customerCompanyNumber)) {
                customer.setMasterExternalId(null);
                customer.setName(externalCustomer.getName());
                matches.addDuplicate(customer);
                customer = createCustomer(externalCustomer);
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
                Customer duplicate = new Customer();
                duplicate.setName(externalCustomer.getName());
                duplicate.setExternalId(externalCustomer.getExternalId());
                duplicate.setMasterExternalId(externalCustomer.getExternalId());
                matches.addDuplicate(duplicate);
            } else {
                customer = createCustomer(externalCustomer);
            }
        }

        customer.setName(externalCustomer.getName());
        customer.setCompanyNumber(externalCustomer.getCompanyNumber());
        customer.setCustomerType(CustomerType.COMPANY);
        customer.setAddress(externalCustomer.getPostalAddress());
        customer.setPreferredStore(externalCustomer.getPreferredStore());
        matches.setCustomer(customer);

        return matches;
    }

    public CustomerMatches loadPersonCustomer(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();
        CustomerMatches matches = new CustomerMatches();
        Customer customer = this.customerDataLayer.findByExternalId(externalId);

        if (customer == null) {
            customer = createCustomer(externalCustomer);
        } else {
            validateCustomerType(customer, externalId, CustomerType.PERSON);
        }

        customer.setName(externalCustomer.getName());
        customer.setCustomerType(CustomerType.PERSON);
        customer.setAddress(externalCustomer.getPostalAddress());
        customer.setPreferredStore(externalCustomer.getPreferredStore());
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

    private Customer createCustomer(ExternalCustomer externalCustomer) {
        Customer customer = new Customer();
        customer.setExternalId(externalCustomer.getExternalId());
        customer.setMasterExternalId(externalCustomer.getExternalId());
        return customer;
    }
}
