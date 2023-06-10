package my.project.application_user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUser {

    @Id
    @SequenceGenerator(name = "application_user_id_sequence", sequenceName = "application_user_id_sequence")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "application_user_id_sequence")
    private Long id;
    private String name;
    private String surname;
    private String email;

    public ApplicationUser(String name, String surname, String email) {
        this.name = name;
        this.surname = surname;
        this.email = email;
    }
}
