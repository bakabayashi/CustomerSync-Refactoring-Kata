package codingdojo;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomerDataAccess {

    private final CustomerDataLayer customerDataLayer;

    Customer updateCustomerRecord(Customer customer) {
        return customerDataLayer.updateCustomerRecord(customer);
    }

    Customer createCustomerRecord(Customer customer) {
        return customerDataLayer.createCustomerRecord(customer);
    }

    boolean mergeCustomer(Customer customer) {
        boolean shouldCreate = customer.getInternalId() == null;

        if (shouldCreate) {
            customerDataLayer.createCustomerRecord(customer);
        } else {
            customerDataLayer.updateCustomerRecord(customer);
        }

        return shouldCreate;
    }

    Customer findByExternalId(String externalId) {
        return customerDataLayer.findByExternalId(externalId);
    }

    Customer findByMasterExternalId(String externalId) {
        return customerDataLayer.findByMasterExternalId(externalId);
    }

    Customer findByCompanyNumber(String companyNumber) {
        return customerDataLayer.findByCompanyNumber(companyNumber);
    }

    void updateShoppingList(Customer customer, List<ShoppingList> consumerShoppingList) {
        consumerShoppingList.forEach(
                shoppingList -> {
                    customer.addShoppingList(shoppingList);
                    customerDataLayer.updateShoppingList(shoppingList);
                    customerDataLayer.updateCustomerRecord(customer);
                }
        );
    }
}