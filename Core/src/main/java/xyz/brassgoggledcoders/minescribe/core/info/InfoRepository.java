package xyz.brassgoggledcoders.minescribe.core.info;

import java.util.HashMap;
import java.util.Map;

public class InfoRepository {
    private static final InfoRepository INSTANCE = new InfoRepository();
    private final Map<InfoKey<?>, Object> storedInfo;

    public InfoRepository() {
        this.storedInfo = new HashMap<>();
    }

    public <T> void setValue(InfoKey<T> key, T value) {
        this.storedInfo.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T getValue(InfoKey<T> key) {
        return (T)this.storedInfo.get(key);
    }

    public static InfoRepository getInstance() {
        return INSTANCE;
    }
}
