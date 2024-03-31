package codingdojo;

import static codingdojo.CustomerValidator.*;

import java.util.List;

public class CustomerDataAccess {

    private final CustomerDataLayer customerDataLayer;

    public CustomerDataAccess(CustomerDataLayer customerDataLayer) {
        this.customerDataLayer = customerDataLayer;
    }

    public boolean loadCompanyCustomer(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();
        final String companyNumber = externalCustomer.getCompanyNumber();
        Customer customer = this.customerDataLayer.findByExternalId(externalId);

        if (customer != null) {
            CompanyCustomer companyCustomer = validateCompanyCustomer(customer, externalId);
            Customer matchByMasterId = this.customerDataLayer.findByMasterExternalId(externalId);
            if (matchByMasterId != null) {
                matchByMasterId.setName(externalCustomer.getName());
                updateCustomerRecord(matchByMasterId);
            }

            String customerCompanyNumber = companyCustomer.getCompanyNumber();
            if (!companyNumber.equals(customerCompanyNumber)) {
                companyCustomer.setMasterExternalId(null);
                companyCustomer.setName(externalCustomer.getName());
                updateCustomerRecord(companyCustomer);
                customer = CustomerFactory.create(externalCustomer);
            }
        } else {
            customer = this.customerDataLayer.findByCompanyNumber(companyNumber);
            if (customer != null) {
                validateCustomerType(customer, externalId, CustomerType.COMPANY);
                updateCustomerRecord(customer);
                String customerExternalId = customer.getExternalId();
                validateExternalId(customerExternalId, externalId, companyNumber);
                customer.setExternalId(externalId);
                customer.setMasterExternalId(externalId);
                Customer duplicate = CustomerFactory.create(externalCustomer);
                createCustomerRecord(duplicate);
            } else {
                customer = CustomerFactory.create(externalCustomer);
            }
        }

        customer.populateFields(externalCustomer);
        updateShoppingList(customer, externalCustomer.getShoppingLists());

        return mergeCustomer(customer);
    }

    public boolean loadPersonCustomer(ExternalCustomer externalCustomer) {
        final String externalId = externalCustomer.getExternalId();
        Customer customer = this.customerDataLayer.findByExternalId(externalId);
        boolean shouldCreate = (customer == null);

        if (shouldCreate) {
            customer = createCustomerRecord(
                    CustomerFactory.create(externalCustomer)
            );
        } else {
            validateCustomerType(customer, externalId, CustomerType.PERSON);
        }

        customer.populateFields(externalCustomer);
        updateShoppingList(customer, externalCustomer.getShoppingLists());

        return shouldCreate;
    }

    public Customer updateCustomerRecord(Customer customer) {
        return customerDataLayer.updateCustomerRecord(customer);
    }

    public Customer createCustomerRecord(Customer customer) {
        return customerDataLayer.createCustomerRecord(customer);
    }

    public boolean mergeCustomer(Customer customer) {
        boolean shouldCreate = customer.getInternalId() == null;

        if (shouldCreate) {
            this.customerDataLayer.createCustomerRecord(customer);
        } else {
            this.customerDataLayer.updateCustomerRecord(customer);
        }

        return shouldCreate;
    }

    public void updateShoppingList(Customer customer, List<ShoppingList> consumerShoppingList) {
        consumerShoppingList.forEach(
                shoppingList -> {
                    customer.addShoppingList(shoppingList);
                    customerDataLayer.updateShoppingList(shoppingList);
                    customerDataLayer.updateCustomerRecord(customer);
                }
        );
    }
}
