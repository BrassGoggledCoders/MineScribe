package xyz.brassgoggledcoders.minescribe.collection.namedtree;

import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener.Change;
import javafx.collections.ObservableMap;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class NamedTree<K, V> implements Iterable<NamedTree<K, V>> {
    private final Logger LOGGER = LoggerFactory.getLogger(NamedTree.class);

    private final K key;
    private final V value;

    private final ObservableMap<K, NamedTree<K, V>> children;

    private NamedTree<K, V> parent;

    public NamedTree(K key, V value) {
        this.key = key;
        this.value = value;
        this.children = FXCollections.observableHashMap();
        this.children.addListener(this::handleChange);
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    private void handleChange(Change<? extends K, ? extends NamedTree<K, V>> change) {
        if (change.wasAdded()) {
            if (change.getValueAdded().getParent() == null) {
                change.getValueAdded()
                        .setParent(this);
            } else {
                LOGGER.error("Add child, but parent was not null");
            }
        } else if (change.wasRemoved()) {
            if (change.getValueRemoved().getParent() == this) {
                change.getValueRemoved()
                        .setParent(null);
            } else {
                LOGGER.error("Removed child, but parent did not match");
            }
        }
    }

    public void setParent(NamedTree<K, V> parent) {
        this.parent = parent;
    }

    public NamedTree<K, V> getParent() {
        return parent;
    }

    public NamedTree<K, V> addChild(K key, V value) {
        NamedTree<K, V> child = new NamedTree<>(key, value);
        this.children.put(key, child);
        return child;
    }

    public NamedTree<K, V> getChild(K key) {
        return this.children.get(key);
    }

    public ObservableMap<K, NamedTree<K, V>> children() {
        return children;
    }

    @NotNull
    @Override
    public Iterator<NamedTree<K, V>> iterator() {
        return this.children.values()
                .iterator();
    }

    public void remove(NamedTree<K, V> child) {
        this.children()
                .remove(child.getKey());
    }
}
