package xyz.brassgoggledcoders.minescribe.editor.event.page;

import javafx.event.EventType;

public class RequestPageEvent extends PageEvent {
    public static final EventType<RequestPageEvent> REQUEST_PAGE_EVENT_TYPE = new EventType<>(EVENT_TYPE, "request_page");

    private final String pageName;
    public RequestPageEvent(String pageName) {
        super(REQUEST_PAGE_EVENT_TYPE);
        this.pageName = pageName;
    }

    public String getPageName() {
        return pageName;
    }
}
