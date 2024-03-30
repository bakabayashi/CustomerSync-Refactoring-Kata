package codingdojo;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
public class ExternalCustomer {

    private Address address;

    @Getter
    private String name;

    @Getter
    private String preferredStore;

    @Getter
    private List<ShoppingList> shoppingLists;

    @Getter
    private String externalId;

    @Getter
    private String companyNumber;

    public boolean isCompany() {
        return companyNumber != null;
    }

    public Address getPostalAddress() {
        return address;
    }

}
