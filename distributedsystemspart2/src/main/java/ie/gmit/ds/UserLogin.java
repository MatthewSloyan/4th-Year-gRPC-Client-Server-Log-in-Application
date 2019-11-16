package ie.gmit.ds;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserLogin {
    private int userId;
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
