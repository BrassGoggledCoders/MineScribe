package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import com.google.common.base.Suppliers;
import com.google.gson.JsonElement;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Labeled;
import javafx.scene.control.Tooltip;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import xyz.brassgoggledcoders.minescribe.core.validation.FieldValidation;
import xyz.brassgoggledcoders.minescribe.core.validation.Validation;
import xyz.brassgoggledcoders.minescribe.core.validation.ValidationResult;
import xyz.brassgoggledcoders.minescribe.editor.event.field.FieldInfo;
import xyz.brassgoggledcoders.minescribe.editor.event.field.FieldMessagesEvent;
import xyz.brassgoggledcoders.minescribe.editor.message.FieldMessage;
import xyz.brassgoggledcoders.minescribe.editor.message.MessageType;
import xyz.brassgoggledcoders.minescribe.editor.scene.SceneUtils;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.FieldContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IHelpTextContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.ILabeledContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IValueContent;
import xyz.brassgoggledcoders.minescribe.editor.util.ButtonUtils;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class FieldControl<C extends FieldControl<C, P, V>, P extends ReadOnlyProperty<V>, V>
        extends FieldContent<C> implements IValueContent<C, P, V>, ILabeledContent<C>, IHelpTextContent {
    private final BooleanProperty valid;
    private final BooleanProperty changed;
    private final BooleanProperty required;

    private final ObjectProperty<Label> label;
    private final StringProperty helpText;

    private final ObjectProperty<JsonElement> persistedValue;
    private final SetProperty<FieldMessage> messages;
    private final Set<Function<Object, ValidationResult>> validations;
    private final Supplier<Tooltip> supplierValidationTooltip;
    private final Supplier<Button> resetButton;
    private final Supplier<Button> helpButton;
    private final FieldInfo fieldInfo;

    private InputGroup inputGroup = null;

    protected FieldControl() {
        this.valid = new SimpleBooleanProperty(true);
        this.changed = new SimpleBooleanProperty(false);
        this.required = new SimpleBooleanProperty(false);
        this.label = new SimpleObjectProperty<>();
        this.persistedValue = new SimpleObjectProperty<>();
        this.helpText = new SimpleStringProperty();

        this.messages = new SimpleSetProperty<>(FXCollections.observableSet());
        this.messages.addListener((SetChangeListener<? super FieldMessage>) c -> handleValidationChange());
        this.messages.addListener((observable, oldValue, newValue) -> handleValidationChange());

        this.validations = new HashSet<>();
        this.supplierValidationTooltip = Suppliers.memoize(this::creatValidationToolTip);

        this.resetButton = Suppliers.memoize(this::createResetButton);
        this.helpButton = Suppliers.memoize(this::createHelpButton);

        StringProperty fieldInfoName = new SimpleStringProperty();
        fieldInfoName.bind(this.label.map(Labeled::getText));
        this.fieldInfo = new FieldInfo(
                this.getUniqueId(),
                fieldInfoName
        );
    }

    @Override
    public FieldInfo getFieldInfo() {
        return fieldInfo;
    }

    @Override
    public void finishSetup() {
        super.finishSetup();
        this.bindFields();
        this.valueProperty()
                .addListener((observable, oldValue, newValue) -> this.onChanged());
    }

    protected void onChanged() {
        this.checkValid(this.valueProperty().getValue());
        if (!this.changedProperty().isBound()) {
            changedProperty()
                    .set(true);
        }
    }


    protected void bindFields() {
        this.valid.bind(Bindings.isEmpty(this.messages));
    }

    private void checkValid(V newValue) {
        List<FieldMessage> newErrors = new ArrayList<>();
        if (this.required.get() || this.fulfillsRequired(newValue)) {
            for (Function<Object, ValidationResult> fieldValidation : this.validations) {
                ValidationResult result = fieldValidation.apply(newValue);
                if (!result.isValid()) {
                    newErrors.add(new FieldMessage(
                            this.fieldInfo,
                            MessageType.ERROR,
                            result.getMessage()
                    ));
                }
            }
        }

        newErrors.addAll(this.additionalChecks(newValue));

        Set<FieldMessage> removed = new HashSet<>();
        Iterator<FieldMessage> messageIterator = this.messages.iterator();
        while (messageIterator.hasNext()) {
            FieldMessage mineScribeMessage = messageIterator.next();
            if (!newErrors.contains(mineScribeMessage)) {
                messageIterator.remove();
                removed.add(mineScribeMessage);
            }
        }
        Set<FieldMessage> added = new HashSet<>();
        for (FieldMessage fieldMessage : newErrors) {
            if (this.messages.add(fieldMessage)) {
                added.add(fieldMessage);
            }
        }

        if (!removed.isEmpty() || !added.isEmpty()) {
            this.getNode()
                    .fireEvent(new FieldMessagesEvent(this.getFieldInfo(), added, removed, this.messages));
        }
    }

    protected Set<FieldMessage> additionalChecks(V newValue) {
        if (this.required.get() && !fulfillsRequired(newValue)) {
            return Collections.singleton(new FieldMessage(
                    this.getFieldInfo(),
                    MessageType.ERROR,
                    "Value is required"
            ));
        }
        return Collections.emptySet();
    }

    @SuppressWarnings("unchecked")
    public C withLabel(Label label) {
        this.label.set(label);
        return (C) this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public C withRequired(boolean required) {
        this.required.set(required);
        return (C) this;
    }

    @Override
    @Nullable
    public Label getLabel() {
        return label.get();
    }

    @Override
    public @Nullable String getHelpText() {
        return this.helpText.get();
    }

    @Override
    public void setHelpText(String helpText) {
        this.helpText.set(helpText);
    }

    @Override
    public BooleanProperty changedProperty() {
        return changed;
    }

    @Override
    public BooleanProperty validProperty() {
        return valid;
    }

    @Override
    public void load(JsonElement jsonElement) {
        this.persistedValue.set(jsonElement);
        this.loadControl(jsonElement);
        if (!this.changedProperty().isBound()) {
            this.changedProperty().set(false);
        }
    }

    @Override
    public JsonElement save() {
        return this.persistedValue.get();
    }

    @Override
    public void persist() {
        this.validate();
        if (this.validProperty().get()) {
            JsonElement saved = this.saveControl();
            this.persistedValue.set(saved);
            if (!this.changedProperty().isBound()) {
                this.changedProperty().set(false);
            }
        }
    }

    @Override
    public void reset() {
        this.loadControl(this.persistedValue.get());
        if (!this.changedProperty().isBound()) {
            this.changedProperty().set(false);
        }
    }

    @Override
    public SetProperty<FieldMessage> messagesProperty() {
        return this.messages;
    }

    public BooleanProperty requiredProperty() {
        return this.required;
    }

    @Override
    @SuppressWarnings("unchecked")
    public C withValidations(List<Validation<?>> validations) {
        this.validations.clear();
        for (Validation<?> validation : validations) {
            if (validation instanceof FieldValidation fieldValidation) {
                this.validations.add(fieldValidation);
            }
        }
        return (C) this;
    }

    @Override
    public boolean hasValidations() {
        return !this.validations.isEmpty();
    }

    protected abstract JsonElement saveControl();

    protected abstract void loadControl(JsonElement jsonElement);

    public abstract boolean fulfillsRequired(V value);

    @Override
    public void validate() {
        this.checkValid(this.valueProperty().getValue());
    }

    private Tooltip creatValidationToolTip() {
        if (this.hasValidations()) {
            Tooltip validationTooltip = new Tooltip();

            validationTooltip.textProperty().bind(this.messagesProperty()
                    .map(errorSet -> errorSet.stream()
                            .map(FieldMessage::message)
                            .filter(Objects::nonNull)
                            .reduce((stringA, stringB) -> stringA + System.lineSeparator() + stringB)
                            .orElse("")
                    )
            );
            return validationTooltip;
        }
        return null;
    }

    private void handleValidationChange() {
        if (!this.messagesProperty().isEmpty()) {
            if (!SceneUtils.hasToolTip(this.getNode())) {
                Tooltip.install(this.getNode(), this.supplierValidationTooltip.get());
            }
            this.setPseudoClass(Styles.STATE_DANGER, true);
        } else {
            if (SceneUtils.hasToolTip(this.getNode())) {
                Tooltip.uninstall(this.getNode(), this.supplierValidationTooltip.get());
            }
            this.setPseudoClass(Styles.STATE_DANGER, false);
        }
    }

    protected Button getResetButton() {
        return this.resetButton.get();
    }

    private Button createResetButton() {
        Button resetButton = ButtonUtils.createIconButton(Feather.REFRESH_CW, "Reset");
        resetButton.onActionProperty()
                .set(event -> reset());
        return resetButton;
    }

    @NotNull
    protected Button getHelpButton() {
        return this.helpButton.get();
    }

    private Button createHelpButton() {
        FontIcon helpIcon = new FontIcon(Feather.HELP_CIRCLE);
        Button helpButton = new Button("&nbsp;", helpIcon);
        helpButton.getStyleClass().add(Styles.BUTTON_ICON);
        helpButton.visibleProperty().bind(Bindings.isNotEmpty(this.helpText));
        Tooltip tooltip = new Tooltip();
        tooltip.textProperty().bind(this.helpText);
        helpButton.setTooltip(tooltip);
        helpButton.onActionProperty()
                .set(action -> {
                    Point2D p = helpButton.localToScreen(helpButton.getHeight() - 5, helpButton.getWidth() - 5);
                    tooltip.show(helpButton, p.getX(), p.getY());

                    PauseTransition pt = new PauseTransition(Duration.millis(5000));
                    pt.setOnFinished(e -> tooltip.hide());
                    pt.play();
                });
        return helpButton;
    }

    protected InputGroup createInputGroup(Node node) {
        this.inputGroup = new InputGroup(node, this.getResetButton());
        this.helpText.addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                this.inputGroup.getChildren().remove(this.getHelpButton());
            } else if (!this.inputGroup.getChildren().contains(this.getHelpButton())) {
                this.inputGroup.getChildren().add(0, this.getHelpButton());
            }
        });
        return this.inputGroup;
    }
}
