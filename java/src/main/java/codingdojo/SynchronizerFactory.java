package codingdojo;

class SynchronizerFactory {

    static CustomerSynchronizer create(CustomerDataAccess customerDataAccess, boolean isCompany) {
        if (isCompany) {
            return new CompanyCustomerSynchronizer(customerDataAccess);
        } else {
            return new PersonCustomerSynchronizer(customerDataAccess);
        }
    }
}
