package codingdojo;

import java.util.Objects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompanyCustomer extends Customer {
    private String companyNumber;

    CompanyCustomer() {
        this.setCustomerType(CustomerType.COMPANY);
    }

    @Override
    public void populateFields(ExternalCustomer externalCustomer) {
        this.setCompanyNumber(externalCustomer.getCompanyNumber());
        this.setName(externalCustomer.getName());
        this.setAddress(externalCustomer.getPostalAddress());
        this.setPreferredStore(externalCustomer.getPreferredStore());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CompanyCustomer)) return false;
        CompanyCustomer customer = (CompanyCustomer) o;
        return Objects.equals(this.getExternalId(), customer.getExternalId()) &&
                Objects.equals(this.getMasterExternalId(), customer.getMasterExternalId()) &&
                Objects.equals(companyNumber, customer.getCompanyNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getExternalId(), this.getMasterExternalId(), companyNumber);
    }
}