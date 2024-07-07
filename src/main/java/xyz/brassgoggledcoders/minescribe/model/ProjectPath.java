package xyz.brassgoggledcoders.minescribe.model;

public record ProjectPath(
        ProjectPathAnchor anchor,
        String path
) {
    public ProjectPath(ProjectPathAnchor anchor, String path) {
        this.anchor = anchor;
        this.path = path.replace("\\", "/");
    }

    public String[] getDirectories() {
        return this.path().split("/");
    }
}
