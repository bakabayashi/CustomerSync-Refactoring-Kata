package codingdojo;

import static codingdojo.CustomerValidator.*;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CompanyCustomerSynchronizer implements CustomerSynchronizer {

    private final CustomerDataAccess customerDataAccess;

    @Override
    public boolean synchronizeData(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();
        final String companyNumber = externalCustomer.getCompanyNumber();
        Customer customer = customerDataAccess.findByExternalId(externalId);

        if (customer != null) {
            CompanyCustomer companyCustomer = validateCompanyCustomer(customer, externalId);
            Customer matchByMasterId = customerDataAccess.findByMasterExternalId(externalId);
            if (matchByMasterId != null) {
                matchByMasterId.setName(externalCustomer.getName());
                customerDataAccess.updateCustomerRecord(matchByMasterId);
            }

            String customerCompanyNumber = companyCustomer.getCompanyNumber();
            if (!companyNumber.equals(customerCompanyNumber)) {
                companyCustomer.setMasterExternalId(null);
                companyCustomer.setName(externalCustomer.getName());
                customerDataAccess.updateCustomerRecord(companyCustomer);
                customer = CustomerFactory.create(externalCustomer);
            }
        } else {
            customer = customerDataAccess.findByCompanyNumber(companyNumber);
            if (customer != null) {
                validateCustomerType(customer, externalId, CustomerType.COMPANY);
                customerDataAccess.updateCustomerRecord(customer);
                String customerExternalId = customer.getExternalId();
                validateExternalId(customerExternalId, externalId, companyNumber);
                customer.setExternalId(externalId);
                customer.setMasterExternalId(externalId);
                Customer duplicate = CustomerFactory.create(externalCustomer);
                customerDataAccess.createCustomerRecord(duplicate);
            } else {
                customer = CustomerFactory.create(externalCustomer);
            }
        }

        customer.populateFields(externalCustomer);
        customerDataAccess.updateShoppingList(customer, externalCustomer.getShoppingLists());

        return customerDataAccess.mergeCustomer(customer);
    }

}