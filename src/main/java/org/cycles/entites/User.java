package org.cycles.entites;




import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String username;
    private String password;
    private String usertype;
    private String name;
    private String surname;
    private String phone;
    private String address;
    @OneToMany(mappedBy = "user",cascade = {CascadeType.ALL},fetch = FetchType.EAGER)
    @JsonManagedReference
    private List<Product> products;

}
