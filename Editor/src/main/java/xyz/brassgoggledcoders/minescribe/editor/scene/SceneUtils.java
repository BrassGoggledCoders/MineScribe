package xyz.brassgoggledcoders.minescribe.editor.scene;

import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;

public class SceneUtils {
    public static void setAnchors(Node node) {
        AnchorPane.setTopAnchor(node, 0D);
        AnchorPane.setBottomAnchor(node, 0D);
        AnchorPane.setLeftAnchor(node, 0D);
        AnchorPane.setRightAnchor(node, 0D);
    }

    @SuppressWarnings("unused")
    public static ColumnConstraints createConstraintsForPercent(double percent) {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(percent);
        return columnConstraints;
    }

    public static boolean hasToolTip(Node node) {
        return node.getProperties().get("javafx.scene.control.Tooltip") != null;
    }
}
