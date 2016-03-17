package pazuzu.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ErrorDto {
    @JsonProperty("code")
    public final String code;
    @JsonProperty("message")
    public final String message;

    public ErrorDto(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
