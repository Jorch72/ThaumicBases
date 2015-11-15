package tb.common.item;

import java.util.List;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import DummyCore.Client.Icon;
import DummyCore.Client.IconRegister;
import DummyCore.Utils.IOldItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.items.IRepairable;
import thaumcraft.api.items.IRunicArmor;
import thaumcraft.api.items.IVisDiscountGear;

public class ItemBloodyArmor extends ItemArmor implements IRepairable, IVisDiscountGear, IRunicArmor, IOldItem{

	int aType;
	Icon icon;
	String textureName;
	
	public ItemBloodyArmor(ArmorMaterial mat,int aType) {
		super(mat, 0, aType);
		this.aType = aType;
	}
	
	public ItemBloodyArmor setTextureName(String s)
	{
		textureName = s;
		return this;
	}
	
    public String getArmorTexture(ItemStack stack, Entity entity, int slot, String type)
    {
    	return slot == 2 ? "thaumicbases:textures/items/armor/bloody/bloody_2.png" : "thaumicbases:textures/items/armor/bloody/bloody_1.png";
    }

	@Override
	public int getVisDiscount(ItemStack stack, EntityPlayer player,
			Aspect aspect) {
		return discount[aType];
	}
	
	static final int[] discount = new int[]{5,4,3,2};

	@Override
	public int getRunicCharge(ItemStack itemstack) {
		return 0;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4)
	{
		list.add(EnumChatFormatting.DARK_PURPLE + StatCollector.translateToLocal("tc.visdiscount") + ": " + getVisDiscount(stack, player, null) + "%");
		super.addInformation(stack, player, list, par4);
	}
	
    @SuppressWarnings({ "rawtypes", "unchecked" })
	public Multimap getAttributeModifiers(ItemStack stack)
    {
    	HashMultimap map = HashMultimap.create();
    	
    	switch(aType)
    	{
	    	case 1:
	    	{
	    		map.put(SharedMonsterAttributes.maxHealth.getAttributeUnlocalizedName(), new AttributeModifier(UUID.fromString("96042c45-dfe3-4366-b93b-84663c4d828d"), "maxHealth", 0.2F, 2));
	    		break;
	    	}
	    	case 2:
	    	{
	    		map.put(SharedMonsterAttributes.knockbackResistance.getAttributeUnlocalizedName(), new AttributeModifier(UUID.fromString("e4e1d8b2-87f2-44f5-8f24-e1876060a04c"), "knockback", 0.5F, 2));
	    		break;
	    	}
	    	case 3:
	    	{
	    		map.put(SharedMonsterAttributes.movementSpeed.getAttributeUnlocalizedName(), new AttributeModifier(UUID.fromString("f6d1384c-74c3-4cce-9a80-11b91dbd4ff4"), "moveSpeed", 0.1F, 2));
	    		break;
	    	}
    	}
    	
    	return map;
    }

	@Override
	public Icon getIconFromDamage(int meta) {
		return icon;
	}

	@Override
	public Icon getIconFromItemStack(ItemStack stk) {
		return getIconFromDamage(stk.getMetadata());
	}

	@Override
	public void registerIcons(IconRegister reg) {
		icon = reg.registerItemIcon(textureName);
	}

	@Override
	public int getRenderPasses(ItemStack stk) {
		return 0;
	}

	@Override
	public Icon getIconFromItemStackAndRenderPass(ItemStack stk, int pass) {
		return getIconFromItemStack(stk);
	}

	@Override
	public boolean recreateIcon(ItemStack stk) {
		return false;
	}

	@Override
	public boolean render3D(ItemStack stk) {
		return false;
	}
	
}
