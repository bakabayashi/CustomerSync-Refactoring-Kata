package codingdojo;

public class CustomerFactory {

    static Customer create(ExternalCustomer externalCustomer) {
        Customer customer;

        if (externalCustomer.isCompany()) {
            customer = new CompanyCustomer();
        } else {
            customer = new PersonCustomer();
        }

        customer.setExternalId(externalCustomer.getExternalId());
        customer.setMasterExternalId(externalCustomer.getExternalId());

        return customer;
    }
}