package xyz.brassgoggledcoders.minescribe.form.util

import jakarta.annotation.Nullable

record MineScribePath(
        MineScribePathType pathType,
        String path
) {
    @Nullable
    InputStream getInputStream() throws IOException {
        return switch (this.pathType()) {
            case MineScribePathType.RESOURCE ->
                yield this.class.getResourceAsStream(this.path())
            default ->
                throw new IllegalArgumentException("Invalid pathType: " + this.pathType())
        }
    }
}