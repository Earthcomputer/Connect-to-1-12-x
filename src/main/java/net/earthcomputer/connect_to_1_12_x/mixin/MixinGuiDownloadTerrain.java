package net.earthcomputer.connect_to_1_12_x.mixin;

import net.earthcomputer.connect_to_1_12_x.CPacketKeepAlive1121;
import net.earthcomputer.connect_to_1_12_x.MultiConnectHelper;
import net.earthcomputer.connect_to_1_12_x.PacketLists;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiScreen;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(GuiDownloadTerrain.class)
public class MixinGuiDownloadTerrain extends GuiScreen {

	private int progress;

	@Override
	public void updateScreen() {
		if (MultiConnectHelper.getProtocolVersion() < PacketLists.PROTOCOL_1_12_2){
			++this.progress;

			if (this.progress % 20 == 0)
			{
				Minecraft.getMinecraft().getConnection().sendPacket(new CPacketKeepAlive1121());
			}
		}
	}
}
