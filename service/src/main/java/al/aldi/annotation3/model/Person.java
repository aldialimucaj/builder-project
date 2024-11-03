package al.aldi.annotation3.model;

import al.aldi.annotation3.annotation.Builder;

@Builder
public class Person {
    public String firstName;
    public String lastName;
    public int age;

    // Optionally, you can define other methods here
}