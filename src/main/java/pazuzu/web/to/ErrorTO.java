package pazuzu.web.to;

/**
 * Created by cseidel on 23/02/16.
 */
public class ErrorTO {
    public static final int INVALID_FEATURE_REQUESTED = 1;

    public final int errorcode;
    public final String msg;

    public ErrorTO(int errorcode, String msg){
        this.errorcode = errorcode;
        this.msg = msg;
    }

}
