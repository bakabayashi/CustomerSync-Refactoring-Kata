package codingdojo;

import static codingdojo.CustomerValidator.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class CompanyCustomerSynchronizer implements CustomerSynchronizer {

    private final CustomerDataAccess customerDataAccess;

    @Override
    public boolean synchronizeData(ExternalCustomer externalCustomer) {
        Customer customer = this.customerDataAccess.findByExternalId(externalCustomer.getExternalId());

        if (customer == null) {
            Customer customerByCompanyNumber = this.customerDataAccess.findByCompanyNumber(externalCustomer.getCompanyNumber());

            if (customerByCompanyNumber == null) {
                customer = CustomerFactory.create(externalCustomer);
            } else {
                customer = synchronizeByCompanyNumber(externalCustomer, customerByCompanyNumber);
            }
        } else {
            customer = synchronizeByExternalId(externalCustomer, customer);
        }

        customer.populateFields(externalCustomer);

        boolean created = customerDataAccess.mergeCustomer(customer);
        customerDataAccess.updateShoppingList(customer, externalCustomer.getShoppingLists());

        return created;
    }

    private Customer synchronizeByExternalId(ExternalCustomer externalCustomer, Customer customer) {
        final String externalId = externalCustomer.getExternalId();
        final String companyNumber = externalCustomer.getCompanyNumber();

        CompanyCustomer companyCustomer = validateCompanyCustomer(customer, externalId);
        checkForMasterExternalIdDuplicate(externalCustomer, externalId);

        return checkForCompanyNumberDuplicate(externalCustomer, companyCustomer, companyNumber);
    }

    private Customer checkForCompanyNumberDuplicate(ExternalCustomer externalCustomer, CompanyCustomer companyCustomer, String companyNumber) {
        String customerCompanyNumber = companyCustomer.getCompanyNumber();

        if (!companyNumber.equals(customerCompanyNumber)) {
            companyCustomer.setMasterExternalId(null);
            companyCustomer.setName(externalCustomer.getName());
            customerDataAccess.updateCustomerRecord(companyCustomer);
            return CustomerFactory.create(externalCustomer);
        }

        return companyCustomer;
    }

    private void checkForMasterExternalIdDuplicate(ExternalCustomer externalCustomer, String externalId) {
        Customer customerByMasterExternalId = this.customerDataAccess.findByMasterExternalId(externalId);
        if (customerByMasterExternalId != null) {
            customerByMasterExternalId.setName(externalCustomer.getName());
            customerDataAccess.updateCustomerRecord(customerByMasterExternalId);
        }
    }

    private Customer synchronizeByCompanyNumber(ExternalCustomer externalCustomer, Customer customerByCompanyNumber) {
        final String externalId = externalCustomer.getExternalId();
        final String companyNumber = externalCustomer.getCompanyNumber();

        validateCustomerType(customerByCompanyNumber, externalId, CustomerType.COMPANY);

        String customerExternalId = customerByCompanyNumber.getExternalId();
        validateExternalId(customerExternalId, externalId, companyNumber);

        customerByCompanyNumber.setExternalId(externalId);
        customerByCompanyNumber.setMasterExternalId(externalId);

        Customer duplicate = CustomerFactory.create(externalCustomer);
        customerDataAccess.createCustomerRecord(duplicate);

        return customerByCompanyNumber;
    }

}