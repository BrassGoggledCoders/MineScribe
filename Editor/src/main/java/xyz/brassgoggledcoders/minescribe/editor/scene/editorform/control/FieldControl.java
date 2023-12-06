package xyz.brassgoggledcoders.minescribe.editor.scene.editorform.control;

import atlantafx.base.layout.InputGroup;
import atlantafx.base.theme.Styles;
import com.google.common.base.Suppliers;
import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
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
import xyz.brassgoggledcoders.minescribe.editor.message.MessageType;
import xyz.brassgoggledcoders.minescribe.editor.message.MineScribeMessage;
import xyz.brassgoggledcoders.minescribe.editor.scene.SceneUtils;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.FieldContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IHelpTextContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.ILabeledContent;
import xyz.brassgoggledcoders.minescribe.editor.scene.editorform.content.IValueContent;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public abstract class FieldControl<C extends FieldControl<C, P, V>, P extends ReadOnlyProperty<V>, V>
        extends FieldContent<C> implements IValueContent<C, P, V>, ILabeledContent<C>, IHelpTextContent {
    private final BooleanProperty valid;
    private final BooleanProperty changed;
    private final BooleanProperty required;

    private final ObjectProperty<Label> label;
    private final ObservableValue<String> labelString;
    private final StringProperty helpText;

    private final ObjectProperty<JsonElement> persistedValue;
    private final SetProperty<MineScribeMessage> messages;
    private final Set<Function<Object, ValidationResult>> validations;
    private final Supplier<Tooltip> supplierValidationTooltip;
    private final Supplier<Button> resetButton;
    private final Supplier<Button> helpButton;

    private InputGroup inputGroup = null;

    protected FieldControl() {
        this.valid = new SimpleBooleanProperty(true);
        this.changed = new SimpleBooleanProperty(false);
        this.required = new SimpleBooleanProperty(false);
        this.label = new SimpleObjectProperty<>();
        this.labelString = this.label.map(Labeled::getText);
        this.persistedValue = new SimpleObjectProperty<>();
        this.helpText = new SimpleStringProperty();

        this.messages = new SimpleSetProperty<>(FXCollections.observableSet());
        this.messages.addListener((SetChangeListener<? super MineScribeMessage>) c -> handleValidationChange());
        this.messages.addListener((observable, oldValue, newValue) -> handleValidationChange());

        this.validations = new HashSet<>();
        this.supplierValidationTooltip = Suppliers.memoize(this::creatValidationToolTip);

        this.resetButton = Suppliers.memoize(this::createResetButton);
        this.helpButton = Suppliers.memoize(this::createHelpButton);
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

    protected void checkValid(V newValue) {
        List<MineScribeMessage> newErrors = new ArrayList<>();
        for (Function<Object, ValidationResult> fieldValidation : this.validations) {
            ValidationResult result = fieldValidation.apply(newValue);
            if (!result.isValid()) {
                newErrors.add(new MineScribeMessage(
                        MessageType.ERROR,
                        null,
                        this.labelString.getValue(),
                        result.getMessage()
                ));
            }
        }
        this.messages.get().removeIf(Predicate.not(newErrors::contains));
        this.messages.get().addAll(newErrors);
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
        this.validations.add(this::checkRequires);
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
    public SetProperty<MineScribeMessage> messagesProperty() {
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
        if (this.required.get()) {
            this.validations.add(this::checkRequires);
        }
        for (Function<V, ValidationResult> defaultValidation : getDefaultValidations()) {
            Function<Object, Either<V, ValidationResult>> casted = this::castObject;
            this.validations.add(casted.andThen(either -> either.mapLeft(defaultValidation))
                    .andThen(either -> either.left()
                            .or(either::right)
                            .orElseGet(ValidationResult::valid)
                    )
            );
        }
        return (C) this;
    }

    protected abstract Either<V, ValidationResult> castObject(Object value);

    public Either<V, ValidationResult> castObjectWithClass(Object o, Class<V> vClass) {
        if (vClass.isInstance(o)) {
            return Either.left(vClass.cast(o));
        } else {
            return Either.right(ValidationResult.error("Invalid Value Type"));
        }
    }

    protected Set<Function<V, ValidationResult>> getDefaultValidations() {
        return Collections.emptySet();
    }

    private ValidationResult checkRequires(Object value) {
        Either<V, ValidationResult> either = this.castObject(value);

        return either.mapLeft(castValue -> {
                    if (fulfillsRequired(castValue)) {
                        return ValidationResult.valid();
                    } else {
                        return ValidationResult.error("Field is Required");
                    }
                })
                .left()
                .or(either::right)
                .orElseGet(ValidationResult::valid);
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

    protected String getLabelString() {
        return this.labelString.getValue();
    }

    private Tooltip creatValidationToolTip() {
        if (this.hasValidations()) {
            Tooltip validationTooltip = new Tooltip();

            validationTooltip.textProperty().bind(this.messagesProperty()
                    .map(errorSet -> errorSet.stream()
                            .map(MineScribeMessage::messageProperty)
                            .map(StringExpression::getValue)
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
        FontIcon resetIcon = new FontIcon(Feather.REPEAT);
        Button resetButton = new Button("&nbsp;", resetIcon);
        resetButton.getStyleClass().add(Styles.BUTTON_ICON);
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
