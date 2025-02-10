package xyz.brassgoggledcoders.minescribe.form.fieldgenerator


import javafx.scene.Node

import java.util.function.Consumer
import java.util.function.Supplier

class FieldContainer {
    Node node
    Consumer<Object> readValue
    Supplier<Object> writeValue

    FieldContainer(Node node) {
        this.node = node
    }

    FieldContainer(Node node, Consumer<Object> readValue, Supplier<Object> writeValue) {
        this.node = node
        this.readValue = readValue
        this.writeValue = writeValue
    }
}
