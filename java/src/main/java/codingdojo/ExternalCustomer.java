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

    @Getter
    private Integer bonusPointsBalance;

    public Address getPostalAddress() {
        return address;
    }
    public boolean isCompany() {
        return companyNumber != null;
    }

}
