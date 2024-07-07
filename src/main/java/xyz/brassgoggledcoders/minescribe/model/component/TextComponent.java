package xyz.brassgoggledcoders.minescribe.model.component;

public record TextComponent(
    ITextContent textContent
) {
    public String getText() {
        return this.textContent.getText();
    }
}
