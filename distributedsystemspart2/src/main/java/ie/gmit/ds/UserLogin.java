package ie.gmit.ds;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;

public class UserLogin {

    @NotNull
    private int userId;
    @NotBlank
    private String password;

    public UserLogin() {
        // Needed for Jackson deserialisation
    }

    public UserLogin(int userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    @JsonProperty
    public int getUserId() {
        return userId;
    }

    @JsonProperty
    public String getPassword() {
        return password;
    }
}
