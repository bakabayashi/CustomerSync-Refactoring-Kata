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

        if (externalCustomer.isCompany()) {
            return customerDataAccess.loadCompanyCustomer(externalCustomer);
        } else {
            return customerDataAccess.loadPersonCustomer(externalCustomer);
        }
    }
}
