package xyz.brassgoggledcoders.minescribe.editor;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

public class SceneUtils {
    public static void setAnchors(Node node) {
        AnchorPane.setTopAnchor(node, 0D);
        AnchorPane.setBottomAnchor(node, 0D);
        AnchorPane.setLeftAnchor(node, 0D);
        AnchorPane.setRightAnchor(node, 0D);
    }
}
