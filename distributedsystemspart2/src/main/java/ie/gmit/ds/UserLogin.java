package ie.gmit.ds;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name= "userLogin")
public class UserLogin {

    @NotNull
    private int userId;
    @NotBlank
    private String password;

    public UserLogin() {
        // Needed for Jackson deserialisation
        super();
    }

    public UserLogin(int userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    @XmlElement
    @JsonProperty
    public int getUserId() {
        return userId;
    }

    @XmlElement
    @JsonProperty
    public String getPassword() {
        return password;
    }

    // Setters needed for xml binding
    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
