package xyz.brassgoggledcoders.minescribe.core.validation;

import org.graalvm.polyglot.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.Closeable;

public class ScriptHandler implements Closeable {
    private static final ScriptHandler INSTANCE = new ScriptHandler();
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptHandler.class);

    private final Engine engine;
    private final Context context;

    public ScriptHandler() {
        this.engine = Engine.newBuilder()
                .build();
        this.context = buildContext(engine);
    }

    private Context buildContext(Engine engine) {
        SLF4JBridgeHandler bridgeHandler = new SLF4JBridgeHandler();
        return Context.newBuilder("js")
                .engine(engine)
                .logHandler(bridgeHandler)
                .allowHostClassLookup(s -> true)
                .allowHostAccess(HostAccess.ALL)
                .build();
    }


    public void runScript(String fileContents) {
        try {
            Source source = Source.create("js", fileContents);

            this.context.parse(source)
                    .executeVoid();
        } catch (PolyglotException polyglotException) {
            LOGGER.error("Failed to run Script", polyglotException);
        }

    }

    public void putBinding(String name, Object value) {
        this.context.getBindings("js")
                .putMember(name, value);
    }

    @Override
    public void close() {
        this.engine.close();
        this.context.close();
    }

    public static ScriptHandler getInstance() {
        return INSTANCE;
    }
}
