package net.earthcomputer.connect_to_1_12_x;

import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;
import java.util.List;

public class CPacketRecipePlacement implements Packet<INetHandlerPlayServer>
{
    private int containerId;
    private short uid;
    private List<CPacketRecipePlacement.ItemMove> moveItemsFromGrid;
    private List<CPacketRecipePlacement.ItemMove> moveItemsToGrid;

    public CPacketRecipePlacement(int p_i47425_1_, List<CPacketRecipePlacement.ItemMove> p_i47425_2_, List<CPacketRecipePlacement.ItemMove> p_i47425_3_, short p_i47425_4_)
    {
        this.containerId = p_i47425_1_;
        this.uid = p_i47425_4_;
        this.moveItemsFromGrid = p_i47425_2_;
        this.moveItemsToGrid = p_i47425_3_;
    }

    public void readPacketData(PacketBuffer buf) throws IOException
    {
        throw new UnsupportedOperationException("This should never be called!");
    }

    public void writePacketData(PacketBuffer buf) throws IOException
    {
        buf.writeByte(this.containerId);
        buf.writeShort(this.uid);
        this.writeMoveItems(buf, this.moveItemsFromGrid);
        this.writeMoveItems(buf, this.moveItemsToGrid);
    }

    private void writeMoveItems(PacketBuffer buffer, List<CPacketRecipePlacement.ItemMove> p_192612_2_)
    {
        buffer.writeShort(p_192612_2_.size());

        for (CPacketRecipePlacement.ItemMove cpacketrecipeplacement$itemmove : p_192612_2_)
        {
            buffer.writeItemStack(cpacketrecipeplacement$itemmove.stack);
            buffer.writeByte(cpacketrecipeplacement$itemmove.srcSlot);
            buffer.writeByte(cpacketrecipeplacement$itemmove.destSlot);
        }
    }

    public void processPacket(INetHandlerPlayServer handler)
    {
        throw new UnsupportedOperationException("This should never be called!");
    }

    public static class ItemMove
    {
        public ItemStack stack;
        public int srcSlot;
        public int destSlot;

        public ItemMove(ItemStack p_i47401_1_, int p_i47401_2_, int p_i47401_3_)
        {
            this.stack = p_i47401_1_.copy();
            this.srcSlot = p_i47401_2_;
            this.destSlot = p_i47401_3_;
        }
    }
}
