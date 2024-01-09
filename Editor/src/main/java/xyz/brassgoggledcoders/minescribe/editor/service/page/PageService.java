package xyz.brassgoggledcoders.minescribe.editor.service.page;

import com.google.inject.Inject;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import xyz.brassgoggledcoders.minescribe.editor.Application;
import xyz.brassgoggledcoders.minescribe.editor.service.fxml.IFXMLService;
import xyz.brassgoggledcoders.minescribe.editor.service.fxml.IFXMLService.LoadResult;

import java.lang.ref.WeakReference;

public class PageService implements IPageService {
    private final IFXMLService fxmlService;

    private WeakReference<AnchorPane> pagePaneRef;

    @Inject
    public PageService(IFXMLService fxmlService) {
        this.fxmlService = fxmlService;
        this.pagePaneRef = new WeakReference<>(null);
    }

    @Override
    public void setPage(String pageName) {
        Platform.runLater(() -> {
            AnchorPane pagePane = pagePaneRef.get();
            if (pagePane != null) {
                boolean notCurrentPage = pagePane.getChildren()
                        .stream()
                        .noneMatch(node -> node.getId().equals(pageName));

                if (notCurrentPage) {
                    LoadResult<Node, Object> loadResult = fxmlService.load(Application.class.getResource(pageName + ".fxml"));
                    if (loadResult != null) {
                        Node node = loadResult.node();
                        pagePane.getChildren().clear();
                        AnchorPane.setTopAnchor(node, 0D);
                        AnchorPane.setBottomAnchor(node, 0D);
                        AnchorPane.setLeftAnchor(node, 0D);
                        AnchorPane.setRightAnchor(node, 0D);
                        pagePane.getChildren().add(node);
                    }
                }
            }
        });

    }

    @Override
    public void setPagePane(AnchorPane pagePane) {
        this.pagePaneRef = new WeakReference<>(pagePane);
    }
}
