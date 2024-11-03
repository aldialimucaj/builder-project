package al.aldi.annotation3;

import al.aldi.annotation3.model.Person;
import al.aldi.annotation3.model.PersonBuilder;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        Person name = new PersonBuilder().setFirstName("Name").build();
        System.out.println(name);
    }
}
