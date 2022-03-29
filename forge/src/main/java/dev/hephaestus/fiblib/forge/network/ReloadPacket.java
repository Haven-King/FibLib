package dev.hephaestus.fiblib.forge.network;

import net.minecraft.network.FriendlyByteBuf;

public class ReloadPacket {
    private final int count;

    public ReloadPacket() {
        this(0);
    }

    public ReloadPacket(int size) {
        count = size;
    }

    public int getCount() {
        return count;
    }

    public static void encode(ReloadPacket msg, FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeVarInt(msg.count);
    }

    public static ReloadPacket decode(FriendlyByteBuf friendlyByteBuf) {
        return new ReloadPacket(friendlyByteBuf.getInt(0));
    }
}
