package xyz.brassgoggledcoders.minescribe.api.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.LiteralContents;
import xyz.brassgoggledcoders.minescribe.core.text.FancyText;
import xyz.brassgoggledcoders.minescribe.core.text.LiteralFancyText;

public class ConvertingUtil {
    public static FancyText convert(Component component) {
        if (component.getContents() instanceof LiteralContents literalContents) {
            return FancyText.literal(literalContents.text());
        }

        throw new UnsupportedOperationException("Only literal for now");
    }

    public static Component convert(FancyText fancyText) {
        if (fancyText instanceof LiteralFancyText literalContents) {
            return Component.literal(literalContents.getText());
        }

        throw new UnsupportedOperationException("Only literal for now");
    }
}
