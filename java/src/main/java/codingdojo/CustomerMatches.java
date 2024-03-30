package codingdojo;

import java.util.ArrayList;
import java.util.Collection;

import lombok.Getter;
import lombok.Setter;

@Getter
public class CustomerMatches {
    private Collection<Customer> duplicates = new ArrayList<>();

    @Setter
    private String matchTerm;

    @Setter
    private Customer customer;

    public boolean hasDuplicates() {
        return !duplicates.isEmpty();
    }

    public void addDuplicate(Customer duplicate) {
        duplicates.add(duplicate);
    }

}
