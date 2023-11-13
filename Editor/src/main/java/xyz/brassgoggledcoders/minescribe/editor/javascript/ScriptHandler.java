package xyz.brassgoggledcoders.minescribe.editor.javascript;

import com.google.common.base.Suppliers;
import org.graalvm.polyglot.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.Closeable;
import java.util.function.Supplier;

public class ScriptHandler implements Closeable {
    private static final Supplier<ScriptHandler> INSTANCE = Suppliers.memoize(ScriptHandler::new);
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptHandler.class);

    private final Engine engine;
    private final Context context;
    private final SLF4JBridgeHandler handler;

    public ScriptHandler() {
        this.engine = Engine.newBuilder()
                .build();
        this.handler = new SLF4JBridgeHandler();
        this.context = this.buildContext(engine);
        MineScribeJSHelper helper = new MineScribeJSHelper();
        this.context.getBindings("js")
                .putMember("minescribe", helper);
        this.context.getBindings("js")
                .putMember("validationHelper", helper.validationHelper);
    }

    private Context buildContext(Engine engine) {
        return Context.newBuilder("js")
                .engine(engine)
                .logHandler(handler)
                .allowHostClassLookup(s -> true)
                .allowHostAccess(HostAccess.ALL)
                .build();
    }


    public void runScript(String fileName, String fileContents) {
        try {
            Source source = Source.create("js", fileContents);

            this.context.parse(source)
                    .executeVoid();
        } catch (PolyglotException polyglotException) {
            LOGGER.error("Failed to run Script {}", fileName, polyglotException);
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
        return INSTANCE.get();
    }
}
