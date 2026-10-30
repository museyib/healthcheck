package llc.devlab.app.config;

public class Constants {

    public static final String URL_REGEX =
            "^(https?://)"
                    + "((localhost)"
                    + "|(([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,})"
                    + "|(\\d{1,3}(\\.\\d{1,3}){3}))"
                    + "(:\\d{1,5})?"
                    + "(/[a-zA-Z0-9._~:/?#\\[\\]@!$&'()*+,;=%-]*)?$";
}
