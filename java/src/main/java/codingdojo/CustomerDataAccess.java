package codingdojo;

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
            verify(matchByExternalId, externalId, CustomerType.COMPANY);
            matches.setCustomer(matchByExternalId);
            Customer matchByMasterId = this.customerDataLayer.findByMasterExternalId(externalId);
            if (matchByMasterId != null)
                matches.addDuplicate(matchByMasterId);

            String customerCompanyNumber = matches.getCustomer().getCompanyNumber();
            if (!companyNumber.equals(customerCompanyNumber)) {
                matches.getCustomer().setMasterExternalId(null);
                matches.addDuplicate(matches.getCustomer());
                matches.setCustomer(null);
                matches.setMatchTerm(null);
            }
        } else {
            Customer matchByCompanyNumber = this.customerDataLayer.findByCompanyNumber(companyNumber);
            verify(matchByCompanyNumber, externalId, CustomerType.COMPANY);
            if (matchByCompanyNumber != null) {
                matches.setCustomer(matchByCompanyNumber);
                String customerExternalId = matches.getCustomer().getExternalId();
                if (customerExternalId != null && !externalId.equals(customerExternalId)) {
                    throw new ConflictException("Existing customer for externalCustomer " + companyNumber + " doesn't match external id " + externalId + " instead found "
                            + customerExternalId);
                }
                Customer customer = matches.getCustomer();
                customer.setExternalId(externalId);
                customer.setMasterExternalId(externalId);
                matches.addDuplicate(null);
            }
        }

        return matches;
    }

    private void verify(Customer customer, String externalId, CustomerType customerType) {
        if (customer != null && !(customerType == customer.getCustomerType())) {
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a company");
        }
    }

    public CustomerMatches loadPersonCustomer(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();
        CustomerMatches matches = new CustomerMatches();
        Customer matchByPersonalNumber = this.customerDataLayer.findByExternalId(externalId);
        verify(matchByPersonalNumber, externalId, CustomerType.PERSON);
        matches.setCustomer(matchByPersonalNumber);
        if (matchByPersonalNumber == null) {
            if (matches.getCustomer() != null) {
                Customer customer = matches.getCustomer();
                customer.setExternalId(externalId);
                customer.setMasterExternalId(externalId);
            }
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
}
