package xyz.brassgoggledcoders.minescribe.core.packinfo;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.nio.file.Path;
import java.util.Optional;

public record MineScribePackType(
        String label,
        String name,
        Path folder,
        int version,
        Optional<String> versionKey
) {
    public static final Codec<MineScribePackType> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("label").forGetter(MineScribePackType::label),
            Codec.STRING.fieldOf("name").forGetter(MineScribePackType::name),
            Codec.STRING.fieldOf("path").xmap(Path::of, Path::toString).forGetter(MineScribePackType::folder),
            Codec.INT.fieldOf("version").forGetter(MineScribePackType::version),
            Codec.STRING.optionalFieldOf("versionKey").forGetter(MineScribePackType::versionKey)
    ).apply(instance, MineScribePackType::new));

    @Override
    public String toString() {
        return "%s (./%s)".formatted(toTitleCase(this.label()), folder.toString());
    }

    public static String toTitleCase(String phrase) {

        // convert the string to an array
        char[] phraseChars = phrase.toCharArray();
        if (phraseChars.length > 0) {
            phraseChars[0] = Character.toTitleCase(phraseChars[0]);
        }

        for (int i = 0; i < phraseChars.length - 1; i++) {
            if (Character.isWhitespace(phraseChars[i])) {
                phraseChars[i + 1] = Character.toUpperCase(phraseChars[i + 1]);
            }
        }

        // convert the array to string
        return String.valueOf(phraseChars);
    }
}
