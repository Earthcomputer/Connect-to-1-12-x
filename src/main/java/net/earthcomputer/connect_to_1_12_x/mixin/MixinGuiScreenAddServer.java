package net.earthcomputer.connect_to_1_12_x.mixin;

import net.earthcomputer.connect_to_1_12_x.IExServerData;
import net.earthcomputer.connect_to_1_12_x.PacketLists;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenAddServer;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiScreenAddServer.class)
public abstract class MixinGuiScreenAddServer extends GuiScreen {

    @Shadow @Final private ServerData serverData;

    private GuiButton versionButton;

    @Inject(method = "initGui", at = @At("TAIL"))
    public void onInitGui(CallbackInfo ci) {
        for (GuiButton button : buttonList)
            button.y += button.id == 2 ? 30 : 10;

        versionButton = addButton(new GuiButton(-1, width / 2 - 100, 136, 200, 20, "1.12"));
        refreshProtocolButton();
    }

    private void refreshProtocolButton() {
        switch (((IExServerData) serverData).getProtocolVersion()) {
            case PacketLists.PROTOCOL_1_12_1:
                versionButton.displayString = "1.12.1";
                break;
            case PacketLists.PROTOCOL_1_12_2:
                versionButton.displayString = "1.12.2";
                break;
            case PacketLists.PROTOCOL_1_12:
            default:
                versionButton.displayString = "1.12";
                break;
        }
        versionButton.displayString = I18n.format("addServer.protocolVersion", versionButton.displayString);
    }

    @Inject(method = "actionPerformed", at = @At("TAIL"))
    public void onActionPerformed(GuiButton button, CallbackInfo ci) {
        if (button.enabled && button.id == -1) {
            int protocolVersion = ((IExServerData) serverData).getProtocolVersion();
            switch (protocolVersion) {
                case PacketLists.PROTOCOL_1_12:
                    protocolVersion = PacketLists.PROTOCOL_1_12_1;
                    break;
                case PacketLists.PROTOCOL_1_12_1:
                    protocolVersion = PacketLists.PROTOCOL_1_12_2;
                    break;
                case PacketLists.PROTOCOL_1_12_2:
                default:
                    protocolVersion = PacketLists.PROTOCOL_1_12;
                    break;
            }
            ((IExServerData) serverData).setProtocolVersion(protocolVersion);
            refreshProtocolButton();
        }
    }

}
