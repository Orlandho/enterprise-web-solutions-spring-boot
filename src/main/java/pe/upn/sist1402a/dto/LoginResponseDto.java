package pe.upn.sist1402a.dto;

public class LoginResponseDto {

    private String token;
    private String tokenType;
    private String username;
    private Long expiresInMs;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String token, String tokenType, String username, Long expiresInMs) {
        this.token = token;
        this.tokenType = tokenType;
        this.username = username;
        this.expiresInMs = expiresInMs;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Long getExpiresInMs() {
        return expiresInMs;
    }

    public void setExpiresInMs(Long expiresInMs) {
        this.expiresInMs = expiresInMs;
    }
}
