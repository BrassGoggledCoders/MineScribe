package xyz.brassgoggledcoders.minescribe.core.netty.packet;

import io.netty.buffer.ByteBuf;
import xyz.brassgoggledcoders.minescribe.core.netty.NettyUtil;
import xyz.brassgoggledcoders.minescribe.core.packinfo.PackTypeInfo;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public record InstanceDataResponse(
        List<PackTypeInfo> packTypes,
        Map<String, Path> packRepositories
) {
    public void encode(ByteBuf byteBuf) {
        NettyUtil.writeCollection(byteBuf, this.packTypes(), (listByteBuf, value) -> value.encode(listByteBuf));
        NettyUtil.writeMap(
                byteBuf,
                this.packRepositories(),
                NettyUtil::writeUtf,
                (valueByteBuf, path) -> NettyUtil.writeUtf(valueByteBuf, path.toString())
        );
    }

    public static InstanceDataResponse decode(ByteBuf byteBuf) {
        return new InstanceDataResponse(
                NettyUtil.readList(byteBuf, PackTypeInfo::decode),
                NettyUtil.readMap(
                        byteBuf,
                        NettyUtil::readUtf,
                        valueByteBuf -> Path.of(NettyUtil.readUtf(valueByteBuf))
                )
        );
    }
}
