package kg.tarantula.tea.entity;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Food {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "food_gen")
    @SequenceGenerator(name = "food_gen", allocationSize = 1)
    private int id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "foods_ingredients",
    joinColumns = @JoinColumn(name = "food_id"),
    inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private List<Ingredient> ingredientList = new ArrayList<>();
}
