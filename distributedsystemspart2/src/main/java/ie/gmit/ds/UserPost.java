package ie.gmit.ds;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name= "userPost")
public class UserPost {

    @NotNull
    private int userId;
    @NotBlank
    private String userName;
    @NotBlank
    private String email;
    @NotBlank
    private String password;

    public UserPost() {
        // Needed for Jackson deserialisation
        super();
    }

    // Used in post request
    public UserPost(int userId, String userName, String email, String password) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.password = password;
    }

    @XmlElement
    @JsonProperty
    public int getUserId() {
        return userId;
    }

    @XmlElement
    @JsonProperty
    public String getUserName() {
        return userName;
    }

    @XmlElement
    @JsonProperty
    public String getEmail() {
        return email;
    }

    @XmlElement
    @JsonProperty
    public String getPassword() {
        return password;
    }

    // Setters needed for xml binding
    // This does however take away the fact that it's immutable but it doesn't seem to work without them.
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
