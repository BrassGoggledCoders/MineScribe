package xyz.brassgoggledcoders.minescribe.editor.service.page;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import xyz.brassgoggledcoders.minescribe.editor.Application;
import xyz.brassgoggledcoders.minescribe.editor.service.fxml.IFXMLService;

import java.lang.ref.WeakReference;

@Singleton
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
                    Node node = fxmlService.load(Application.class.getResource(pageName + ".fxml"));
                    if (node != null) {
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
