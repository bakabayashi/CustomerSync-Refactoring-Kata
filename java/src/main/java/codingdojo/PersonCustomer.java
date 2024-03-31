package codingdojo;

import java.util.Objects;

import lombok.Getter;

@Getter
class PersonCustomer extends Customer {

    private Integer bonusPointsBalance = 0;

    PersonCustomer() {
        this.setCustomerType(CustomerType.PERSON);
    }

    @Override
    void populateFields(ExternalCustomer externalCustomer) {
        this.setName(externalCustomer.getName());
        this.setAddress(externalCustomer.getPostalAddress());
        this.setPreferredStore(externalCustomer.getPreferredStore());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PersonCustomer)) return false;
        PersonCustomer customer = (PersonCustomer) o;
        return Objects.equals(this.getExternalId(), customer.getExternalId()) &&
                Objects.equals(this.getMasterExternalId(), customer.getMasterExternalId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getExternalId(), this.getMasterExternalId());
    }
}