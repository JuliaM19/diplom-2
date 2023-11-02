package praktikum.model;

public class AuthorizationRequest {

    private String email;
    private String password;

    public AuthorizationRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public AuthorizationRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
