package xyz.brassgoggledcoders.minescribe.editor.scene.editortree;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.input.*;
import org.controlsfx.dialog.ExceptionDialog;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import xyz.brassgoggledcoders.minescribe.editor.file.FileHandler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class EditorTreeCell extends TreeCell<EditorItem> {
    private final ObjectProperty<EventHandler<MouseEvent>> clickHandlerProperty;

    public EditorTreeCell() {
        this.clickHandlerProperty = new SimpleObjectProperty<>();
        this.createDragHandlers();
    }

    @Override
    protected void updateItem(EditorItem item, boolean empty) {
        EditorItem previousItem = this.getItem();
        if (previousItem != null) {
            if (previousItem.getCssClass() != null) {
                this.getStyleClass()
                        .remove(previousItem.getCssClass());
            }
        }
        super.updateItem(item, empty);
        if (item != null) {
            this.setText(item.getName());
            this.setContextMenu(item.createContextMenu(this));
            if (item.isDirectory()) {
                this.setGraphic(new FontIcon(Feather.FOLDER));
            } else {
                this.setGraphic(new FontIcon(Feather.FILE));
                ClickHandler clickHandler = new ClickHandler(this);
                this.clickHandlerProperty.set(clickHandler);
                this.setEventHandler(MouseEvent.MOUSE_CLICKED, clickHandler);
            }
            if (item.getCssClass() != null) {
                this.getStyleClass()
                        .add(item.getCssClass());
            }
        } else {
            this.setText(null);
            this.setContextMenu(null);
            this.setGraphic(null);
            if (this.clickHandlerProperty.get() != null) {
                this.removeEventHandler(MouseEvent.MOUSE_CLICKED, this.clickHandlerProperty.get());
                this.clickHandlerProperty.set(null);
            }
        }
    }

    private void createDragHandlers() {
        this.setOnDragDetected(this::dragDetected);
        this.setOnDragEntered(this::dragEntered);
        this.setOnDragExited(this::dragExited);
        this.setOnDragOver(this::dragOver);
        this.setOnDragDropped(this::dragDropped);
    }

    private void dragDetected(MouseEvent mouseEvent) {
        Dragboard dragboard = this.startDragAndDrop(TransferMode.MOVE);

        if (this.getItem() != null) {
            ClipboardContent content = new ClipboardContent();
            content.putFiles(Collections.singletonList(this.getItem().getFile()));
            dragboard.setContent(content);
        }

        mouseEvent.consume();
    }

    private void dragEntered(DragEvent dragEvent) {
        this.setStyle("-fx-background-color: -color-accent-subtle");
    }

    private void dragExited(DragEvent dragEvent) {
        this.setStyle("");
    }

    private void dragOver(DragEvent dragEvent) {
        if (dragEvent.getDragboard().hasFiles()) {
            dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        }
        dragEvent.consume();
    }

    private void dragDropped(DragEvent dragEvent) {
        Dragboard dragboard = dragEvent.getDragboard();
        if (dragboard.hasFiles()) {
            List<Path> paths = dragboard.getFiles()
                    .stream()
                    .map(File::toPath)
                    .toList();

            EditorItem editorItem = this.getItem();
            if (editorItem != null) {
                Path parentPath = null;
                if (editorItem.isDirectory()) {
                    parentPath = editorItem.getPath();
                } else {
                    TreeItem<EditorItem> parentItem = FileHandler.getInstance()
                            .getClosestNode(editorItem.getPath().getParent(), false);

                    if (parentItem != null && parentItem.getValue() != null) {
                        parentPath = parentItem.getValue()
                                .getPath();
                    }
                }
                if (parentPath != null) {
                    for (Path path : paths) {
                        try {
                            Files.move(path, parentPath.resolve(path.getFileName()));
                        } catch (IOException e) {
                            ExceptionDialog exceptionDialog = new ExceptionDialog(e);
                            exceptionDialog.showAndWait();
                        }
                    }
                }
            }
        }
        dragEvent.setDropCompleted(true);
        dragEvent.consume();
    }

    private record ClickHandler(TreeCell<EditorItem> treeCell) implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            if (event.getClickCount() > 1 && event.getButton().equals(MouseButton.PRIMARY)) {
                this.treeCell()
                        .getItem()
                        .onDoubleClick(this.treeCell());
            }
        }
    }
}
