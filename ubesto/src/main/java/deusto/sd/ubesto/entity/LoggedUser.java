package deusto.sd.ubesto.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "logged_users")
public class LoggedUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="type")
    private String user_type;
    @Column(name="userid")
    private Long userid;
    @Column(name="token")
    private String token;
    
    public LoggedUser(Long id, String user_type, Long userid, String token) {
        this.id = id;
        this.user_type = user_type;
        this.userid = userid;
        this.token = token;
    }

    public LoggedUser( String user_type, Long userid, String token) {
        this.user_type = user_type;
        this.userid = userid;
        this.token = token;
    }

    public LoggedUser() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public Long getuserid() {
        return userid;
    }

    public void setuserid(Long userid) {
        this.userid = userid;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
    
    
    
}
