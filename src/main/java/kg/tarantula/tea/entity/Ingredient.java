package kg.tarantula.tea.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ing_gen")
    @SequenceGenerator(name = "ing_gen", allocationSize = 1)
    private int id;

    private String name;
    private double price;
    private String photoUrl;
}
