package pazuzu.web.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Error")
@XmlAccessorType(XmlAccessType.NONE)
public class ErrorDto {
    @XmlElement(name = "code")
    public final String code;
    @XmlElement(name = "message")
    public final String message;

    public ErrorDto(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
