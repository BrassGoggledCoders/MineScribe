package xyz.brassgoggledcoders.minescribe.scene.control.tree;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.scene.control.TreeItem;
import xyz.brassgoggledcoders.minescribe.collection.namedtree.NamedTree;

import java.util.Comparator;
import java.util.Objects;
import java.util.function.BiFunction;

public class NamedTreeItem<K, V, T extends Comparable<T>> extends TreeItem<T> {

    private final ObjectProperty<NamedTree<K, V>> namedTree;
    private final ObjectProperty<BiFunction<T, NamedTree<K, V>, T>> createChildFunction;

    private final ObservableList<TreeItem<T>> sourceChildren;
    private final SortedList<TreeItem<T>> sortedChildren;

    public NamedTreeItem(T value, NamedTree<K, V> namedTree, BiFunction<T, NamedTree<K, V>, T> createChildFunction) {
        super(value);
        this.namedTree = new SimpleObjectProperty<>(this, "namedTree", namedTree);
        this.namedTree.getValue()
                .children()
                .addListener(this::handleNamedTreeChildrenChange);
        this.createChildFunction = new SimpleObjectProperty<>(this, "createChildFunction", createChildFunction);
        this.namedTree.addListener(this::handleNamedTreeChange);

        this.sourceChildren = FXCollections.observableArrayList();
        this.sortedChildren = new SortedList<>(sourceChildren, this::compare);

        Bindings.bindContent(this.getChildren(), this.sortedChildren);
        this.createChildren();
    }

    public NamedTree<K, V> getNamedTree() {
        return namedTree.getValue();
    }

    private void handleNamedTreeChange(
            ObservableValue<? extends NamedTree<K, V>> observableValue,
            NamedTree<K, V> oldValue,
            NamedTree<K, V> newValue
    ) {
        this.getChildren()
                .clear();

        if (oldValue != null) {
            oldValue.children()
                    .removeListener(this::handleNamedTreeChildrenChange);
        } else if (newValue != null) {
            newValue.children()
                    .addListener(this::handleNamedTreeChildrenChange);
            this.createChildren();
        }
    }

    private void createChildren() {
        BiFunction<T, NamedTree<K, V>, T> createChildFunction = this.createChildFunction.getValue();
        if (createChildFunction != null && this.getValue() != null) {
            for (NamedTree<K, V> childNamedTree : this.getNamedTree()) {
                T newChildValue = createChildFunction.apply(this.getValue(), childNamedTree);
                if (newChildValue != null) {
                    sourceChildren.add(new NamedTreeItem<>(newChildValue, childNamedTree, createChildFunction));
                }
            }
        }
    }

    private void handleNamedTreeChildrenChange(MapChangeListener.Change<? extends K, ? extends NamedTree<K, V>> mapChange) {
        if (mapChange.wasRemoved()) {
            this.getChildren()
                    .removeIf(child -> {
                        if (child instanceof NamedTreeItem<?, ?, ?> namedTreeItem) {
                            return namedTreeItem.getNamedTree() == mapChange.getValueRemoved();
                        }
                        return false;
                    });
        } else if (mapChange.wasAdded()) {
            BiFunction<T, NamedTree<K, V>, T> createChildFunction = this.createChildFunction.getValue();
            if (createChildFunction != null && this.getValue() != null) {
                T newValue = createChildFunction.apply(this.getValue(), mapChange.getValueAdded());
                if (newValue != null) {
                    this.getChildren()
                            .add(new NamedTreeItem<>(newValue, mapChange.getValueAdded(), createChildFunction));
                }
            }
        }
    }

    private int compare(TreeItem<T> o1, TreeItem<T> o2) {
        return Objects.compare(o1.getValue(), o2.getValue(), T::compareTo);
    }
}
