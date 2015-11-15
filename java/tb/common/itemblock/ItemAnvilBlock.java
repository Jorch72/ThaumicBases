package tb.common.itemblock;

import net.minecraft.block.Block;
import net.minecraft.item.ItemMultiTexture;

public class ItemAnvilBlock extends ItemMultiTexture
{
    public ItemAnvilBlock(Block block)
    {
        super(block, block, new String[] {"intact", "slightlyDamaged", "veryDamaged"});
    }

    public int getMetadata(int damage)
    {
        return damage % 3;
    }
}