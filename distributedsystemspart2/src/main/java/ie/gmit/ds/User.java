package ie.gmit.ds;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name= "user")
public class User {

    @NotNull
    private int userId;
    @NotBlank
    private String userName;
    @NotBlank
    private String email;
    @NotBlank
    private String hashedPassword;
    @NotBlank
    private String salt;

    public User() {
        // Needed for Jackson deserialisation
        super();
    }

    // Used in get requests
    public User(int userId, String userName, String email, String hashedPassword, String salt) {
        this.userId = userId;
        this.userName = userName;
        this.email = email;
        this.hashedPassword = hashedPassword;
        this.salt = salt;
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
    public String getHashedPassword() {
        return hashedPassword;
    }

    @XmlElement
    @JsonProperty
    public String getSalt() {
        return salt;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    // Setters needed for xml binding
    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }
}
