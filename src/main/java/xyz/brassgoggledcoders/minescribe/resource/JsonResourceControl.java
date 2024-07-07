package xyz.brassgoggledcoders.minescribe.resource;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class JsonResourceControl extends ResourceBundle.Control {
    private static final List<String> FORMATS = List.of("json");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static final JsonResourceControl INSTANCE = new JsonResourceControl();

    @Override
    public List<String> getFormats(String baseName) {
        if (baseName != null) {
            return FORMATS;
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IOException {
        if (format.equals("json")) {
            URL url = loader.getResource(baseName + ".json");
            if (url != null) {
                URLConnection connection = url.openConnection();
                if (reload) {
                    connection.setUseCaches(false);
                }
                try (InputStream stream = connection.getInputStream()) {
                    return new JsonResourceBundle(objectMapper.readTree(stream));
                }
            } else {
                return null;
            }
        }

        throw new IllegalArgumentException("Invalid format: " + format);
    }
}
