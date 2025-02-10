package xyz.brassgoggledcoders.minescribe.form.util

enum MineScribePathType {
    FILE("file"),
    DATA("data"),
    RESOURCE("resource")

    final String protocol

    MineScribePathType(String protocol) {
        this.protocol = protocol;
    }
}