package xyz.brassgoggledcoders.minescribe.service.scripting;

import groovy.lang.GroovyShell;
import groovy.lang.Script;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

@Service
public class GroovyService {

    private final GroovyShell groovyShell;

    public GroovyService() {
        this.groovyShell = new GroovyShell(this.getClass().getClassLoader());
    }

    public Script parse(URI uri) {
        try {
            return this.groovyShell.parse(uri);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
