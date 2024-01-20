package xyz.brassgoggledcoders.minescribe.editor.javascript;

import com.google.inject.Inject;

@SuppressWarnings("unused")
public class MineScribeJSHelper {
    public final MineScribeJSFieldHelper fieldHelper;
    public final MineScribeJSValidationHelper validationHelper;

    public final MineScribeJSValidationRegistry validationRegistry;

    @Inject
    public MineScribeJSHelper(MineScribeJSValidationRegistry validationRegistry) {
        this.fieldHelper = new MineScribeJSFieldHelper();
        this.validationHelper = new MineScribeJSValidationHelper();
        this.validationRegistry = validationRegistry;
    }
}
