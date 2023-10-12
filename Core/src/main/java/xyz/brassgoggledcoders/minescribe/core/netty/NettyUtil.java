package xyz.brassgoggledcoders.minescribe.core.netty;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class NettyUtil {
    public static void writeUtf(ByteBuf byteBuf, String pString) {
        writeUtf(byteBuf, pString, 100);
    }

    public static void writeUtf(ByteBuf byteBuf, String pString, int pMaxLength) {
        if (pString.length() > pMaxLength) {
            throw new EncoderException("String too big (was " + pString.length() + " characters, max " + pMaxLength + ")");
        } else {
            byte[] abyte = pString.getBytes(StandardCharsets.UTF_8);
            int i = getMaxEncodedUtfLength(pMaxLength);
            if (abyte.length > i) {
                throw new EncoderException("String too big (was " + abyte.length + " bytes encoded, max " + i + ")");
            } else {
                byteBuf.writeInt(abyte.length);
                byteBuf.writeBytes(abyte);
            }
        }
    }

    private static int getMaxEncodedUtfLength(int maxLength) {
        return maxLength * 3;
    }

    public static String readUtf(ByteBuf byteBuf) {
        return readUtf(byteBuf, 32767);
    }

    /**
     * Reads a string with a maximum length from this buffer.
     *
     * @see #writeUtf
     */
    public static String readUtf(ByteBuf byteBuf, int pMaxLength) {
        int i = getMaxEncodedUtfLength(pMaxLength);
        int j = byteBuf.readInt();
        if (j > i) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + i + ")");
        } else if (j < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            String s = byteBuf.toString(byteBuf.readerIndex(), j, StandardCharsets.UTF_8);
            byteBuf.readerIndex(byteBuf.readerIndex() + j);
            if (s.length() > pMaxLength) {
                throw new DecoderException("The received string length is longer than maximum allowed (" + s.length() + " > " + pMaxLength + ")");
            } else {
                return s;
            }
        }
    }

    public static <K, V> void writeMap(ByteBuf byteBuf, Map<K, V> map, BiConsumer<ByteBuf, K> writeKey, BiConsumer<ByteBuf, V> writeValue) {
        byteBuf.writeInt(map.size());
        for (Map.Entry<K, V> entry : map.entrySet()) {
            writeKey.accept(byteBuf, entry.getKey());
            writeValue.accept(byteBuf, entry.getValue());
        }
    }

    public static <K, V> Map<K, V> readMap(ByteBuf byteBuf, Function<ByteBuf, K> readKey, Function<ByteBuf, V> readValue) {
        int mapLength = byteBuf.readInt();
        Map<K, V> map = new HashMap<>();
        for (int i = 0; i < mapLength; i++) {
            map.put(readKey.apply(byteBuf), readValue.apply(byteBuf));
        }
        return map;
    }

    public static <C extends Collection<V>, V> void writeCollection(ByteBuf byteBuf, C collection, BiConsumer<ByteBuf, V> writeValue) {
        byteBuf.writeInt(collection.size());
        for (V value : collection) {
            writeValue.accept(byteBuf, value);
        }
    }

    public static <C extends Collection<V>, V> C readCollection(ByteBuf byteBuf, Supplier<C> cSupplier, Function<ByteBuf, V> valueReader) {
        int size = byteBuf.readInt();
        C collection = cSupplier.get();
        for (int x = 0; x < size; x++) {
            collection.add(valueReader.apply(byteBuf));
        }
        return collection;
    }

    public static <V> List<V> readList(ByteBuf byteBuf, Function<ByteBuf, V> valueReader) {
        return readCollection(byteBuf, ArrayList::new, valueReader);
    }

    public static void writeUUID(ByteBuf byteBuf, UUID uuid) {
        byteBuf.writeLong(uuid.getMostSignificantBits());
        byteBuf.writeLong(uuid.getLeastSignificantBits());
    }

    public static UUID readUUID(ByteBuf byteBuf) {
        return new UUID(
                byteBuf.readLong(),
                byteBuf.readLong()
        );
    }
}
