package codingdojo;

class CustomerValidator {

    static void validateCustomerType(Customer customer, String externalId, CustomerType customerType) {
        if (customer != null && customerType != customer.getCustomerType()) {
            throw new ConflictException("Existing customer for externalCustomer " + externalId + " already exists and is not a " + customerType.toString().toLowerCase());
        }
    }

    static void validateExternalId(String customerExternalId, String externalId, String companyNumber) {
        if (customerExternalId != null && !externalId.equals(customerExternalId)) {
            throw new ConflictException("Existing customer for externalCustomer " + companyNumber + " doesn't match external id "
                    + externalId + " instead found " + customerExternalId);
        }
    }

    static CompanyCustomer validateCompanyCustomer(Customer customer, String externalId) {
        validateCustomerType(customer, externalId, CustomerType.COMPANY);
        return (CompanyCustomer) customer;
    }
}
