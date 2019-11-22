package ie.gmit.ds;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class User {

    @NotNull
    private int userId;
    private String userName;
    private String email;
    private String hashedPassword;
    private String salt;

    public User() {
        // Needed for Jackson deserialisation
    }

    // Used in get requests
    public User(int userId, String userName, String email, String hashedPassword, String salt) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
    }

    @JsonProperty
    public int getUserId() {
        return userId;
    }

    @JsonProperty
    public String getUserName() {
        return userName;
    }

    @JsonProperty
    public String getEmail() {
        return email;
    }

    @JsonProperty
    public String getHashedPassword() {
        return hashedPassword;
    }

    @JsonProperty
    public String getSalt() {
        return salt;
    }
}
