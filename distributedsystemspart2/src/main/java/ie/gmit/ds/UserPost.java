package ie.gmit.ds;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserPost {

    private int userId;
    private String userName;
    private String email;
    private String password;

    public UserPost() {
        // Needed for Jackson deserialisation
    }

    // Used in post request
    public UserPost(int userId, String userName, String email, String password) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
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
    public String getPassword() {
        return password;
    }
}
