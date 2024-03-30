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
        Customer matchByExternalId = this.customerDataLayer.findByExternalId(externalId);
        if (matchByExternalId != null) {
            validateCustomerType(matchByExternalId, externalId, CustomerType.COMPANY);
            matches.setCustomer(matchByExternalId);
            Customer matchByMasterId = this.customerDataLayer.findByMasterExternalId(externalId);
            if (matchByMasterId != null) {
                matchByMasterId.setName(externalCustomer.getName());
                matches.addDuplicate(matchByMasterId);
            }

            String customerCompanyNumber = matchByExternalId.getCompanyNumber();
            if (!companyNumber.equals(customerCompanyNumber)) {
                matchByExternalId.setMasterExternalId(null);
                matchByExternalId.setName(externalCustomer.getName());
                matches.addDuplicate(matchByExternalId);
                matches.setCustomer(createCustomer(externalCustomer));
            }
        } else {
            Customer matchByCompanyNumber = this.customerDataLayer.findByCompanyNumber(companyNumber);
            if (matchByCompanyNumber != null) {
                validateCustomerType(matchByCompanyNumber, externalId, CustomerType.COMPANY);
                matches.setCustomer(matchByCompanyNumber);
                String customerExternalId = matchByCompanyNumber.getExternalId();
                validateExternalId(customerExternalId, externalId, companyNumber);
                matchByCompanyNumber.setExternalId(externalId);
                matchByCompanyNumber.setMasterExternalId(externalId);
                Customer duplicate = new Customer();
                duplicate.setName(externalCustomer.getName());
                duplicate.setExternalId(externalCustomer.getExternalId());
                duplicate.setMasterExternalId(externalCustomer.getExternalId());
                matches.addDuplicate(duplicate);
            } else {
                matches.setCustomer(createCustomer(externalCustomer));
            }
        }

        return matches;
    }

    public CustomerMatches loadPersonCustomer(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();
        CustomerMatches matches = new CustomerMatches();
        Customer matchByPersonalNumber = this.customerDataLayer.findByExternalId(externalId);

        if(matchByPersonalNumber == null) {
            matches.setCustomer(createCustomer(externalCustomer));
        } else {
            validateCustomerType(matchByPersonalNumber, externalId, CustomerType.PERSON);
            matches.setCustomer(matchByPersonalNumber);
        }

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
