package xyz.brassgoggledcoders.minescribe.form

record Id(String namespace, String path) {
    Id(String id) {
        this(getNamespace(id), getPath(id))
    }

    boolean isValid() {
        return this.namespace()?.trim() && this.path()?.trim()
    }

    private static String getNamespace(String id) {
        if (id.count(":") == 1) {
            return id.split(":")[0]
        }

        throw new IllegalArgumentException("Invalid number of :, expected 1, found {}".formatted(id.count(":")))
    }

    private static String getPath(String id) {
        if (id.count(":") == 1) {
            return id.split(":")[1]
        }

        throw new IllegalArgumentException("Invalid number of :, expected 1, found {}".formatted(id.count(":")))
    }
}