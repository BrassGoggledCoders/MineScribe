package xyz.brassgoggledcoders.minescribe.editor.javascript;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.graalvm.polyglot.*;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.Closeable;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class ScriptHandler implements Closeable {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptHandler.class);

    private final Engine engine;
    private final Context context;
    private final SLF4JBridgeHandler handler;
    private final AtomicReference<Path> currentScript;

    @Inject
    public ScriptHandler(MineScribeJSHelper mineScribeJSHelper) {
        this.engine = Engine.newBuilder()
                .build();
        this.handler = new SLF4JBridgeHandler();
        this.context = this.buildContext(engine);
        this.context.getBindings("js")
                .putMember("minescribe", mineScribeJSHelper);
        this.context.getBindings("js")
                .putMember("validationHelper", mineScribeJSHelper.validationHelper);
        this.currentScript = new AtomicReference<>();
    }

    private Context buildContext(Engine engine) {
        return Context.newBuilder("js")
                .engine(engine)
                .logHandler(handler)
                .allowHostClassLookup(s -> true)
                .allowHostAccess(HostAccess.ALL)
                .build();
    }

    @Nullable
    public Path getCurrentScript() {
        return this.currentScript.get();
    }


    public void runScript(Path filePath, String fileContents) {
        try {
            Source source = Source.create("js", fileContents);
            this.currentScript.set(filePath);
            this.context.parse(source)
                    .executeVoid();
            this.currentScript.set(null);
        } catch (PolyglotException polyglotException) {
            LOGGER.error("Failed to run Script {}", filePath, polyglotException);
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
}
