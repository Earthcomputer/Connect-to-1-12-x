package net.earthcomputer.connect_to_1_12_x.mixin;

import net.earthcomputer.connect_to_1_12_x.IExServerData;
import net.earthcomputer.connect_to_1_12_x.PacketLists;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.nbt.NBTTagCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(ServerData.class)
public class MixinServerData implements IExServerData {

    private int protocolVersion = PacketLists.PROTOCOL_1_12_2;

    @Inject(method = "getServerDataFromNBTCompound", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    private static void onReadServerData(NBTTagCompound nbt, CallbackInfoReturnable<ServerData> ci, ServerData data) {
        if (nbt.hasKey("ProtocolVersion", 99))
            ((IExServerData) data).setProtocolVersion(nbt.getInteger("ProtocolVersion"));
        else
            ((IExServerData) data).setProtocolVersion(PacketLists.PROTOCOL_1_12_2);
    }

    @Inject(method = "getNBTCompound", at = @At("RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onWriteServerData(CallbackInfoReturnable<NBTTagCompound> ci, NBTTagCompound nbt) {
        nbt.setInteger("ProtocolVersion", protocolVersion);
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    public void onCopyFrom(ServerData other, CallbackInfo ci) {
        this.protocolVersion = ((IExServerData) other).getProtocolVersion();
    }

    @Override
    public int getProtocolVersion() {
        return protocolVersion;
    }

    @Override
    public void setProtocolVersion(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
}
