package tb.common.enchantment;

import java.util.Iterator;
import java.util.LinkedHashMap;

import DummyCore.Utils.MiscUtils;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tb.common.entity.EntityAspectOrb;
import tb.init.TBEnchant;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectHelper;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.entities.IEldritchMob;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.items.ItemGenericEssentiaContainer;
import thaumcraft.api.items.ItemsTC;
import thaumcraft.common.config.Config;
import thaumcraft.common.lib.aura.EntityAuraNode;

public class EnchantmentHandler {
	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
	public void itemExpire(ItemExpireEvent event)
	{
		if(event.entityItem != null && event.entityItem.getEntityItem() != null && !event.entityItem.worldObj.isRemote)
		{
			int x = MathHelper.floor_double(event.entityItem.posX);
			int y = MathHelper.floor_double(event.entityItem.posY);
			int z = MathHelper.floor_double(event.entityItem.posZ);
			World w = event.entityItem.worldObj;
			ItemStack is = event.entityItem.getEntityItem();
			
			EntityAuraNode node = (EntityAuraNode) MiscUtils.getClosestEntity(event.entityItem.worldObj.getEntitiesWithinAABB(EntityAuraNode.class, AxisAlignedBB.fromBounds(x, y, z, x, y, z).expand(8, 8, 8)),x,y,z);
			if(node != null && node.getNodeType() == 4)
			{
				if(is.getItem() instanceof ItemSword)
				{
					if(EnchantmentHelper.getEnchantmentLevel(TBEnchant.tainted.effectId, is) <= 0)
					{
						LinkedHashMap<Integer,Integer> lhm = (LinkedHashMap<Integer,Integer>)EnchantmentHelper.getEnchantments(is);
						boolean canApply = true;
						if(!lhm.isEmpty())
						{
							Iterator<Integer> $i = lhm.keySet().iterator();
							while($i.hasNext())
							{
								int i = $i.next();
								if(i < MiscUtils.enchantmentList().length && MiscUtils.enchantmentList()[i] != null)
								{
									Enchantment ench = MiscUtils.enchantmentList()[i];
									if(!ench.canApplyTogether(TBEnchant.tainted))
									{
										canApply = false;
										break;
									}
								}
							}
						}
						if(canApply)
						{
							is.addEnchantment(TBEnchant.tainted, 1+w.rand.nextInt(3));
							event.extraLife = 1000;
							event.setCanceled(true);
						}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onMobDeath(LivingDeathEvent event)
	{
		if(event.entityLiving != null && !event.entityLiving.worldObj.isRemote && event.source != null && event.source.getSourceOfDamage() != null && event.source.getSourceOfDamage() instanceof EntityPlayer)
		{
			EntityPlayer player = EntityPlayer.class.cast(event.source.getSourceOfDamage());
			EntityLivingBase dyingMob = event.entityLiving;
			ItemStack currentItem = player.getCurrentEquippedItem();
			if(currentItem != null)
			{
				if(EnchantmentHelper.getEnchantmentLevel(TBEnchant.elderKnowledge.effectId, currentItem) > 0)
				{
					int enchLevel = EnchantmentHelper.getEnchantmentLevel(TBEnchant.elderKnowledge.effectId, currentItem);
					if(player.worldObj.rand.nextInt(Math.max(1, 7-enchLevel)) == 0)
					{
						AspectList al = AspectHelper.getEntityAspects(dyingMob);
						if(al != null)
							for(int i = 0; i < al.size(); ++i)
							{
								EntityXPOrb xp = new EntityXPOrb(player.worldObj,player.posX,player.posY,player.posZ,3+player.worldObj.rand.nextInt(2+enchLevel*2));
								if(!player.worldObj.isRemote)
									player.worldObj.spawnEntityInWorld(xp);
								if(player.worldObj.rand.nextBoolean())
									break;
							}
					}
				}
				if(EnchantmentHelper.getEnchantmentLevel(TBEnchant.magicTouch.effectId, currentItem) > 0)
				{
					int enchLevel = EnchantmentHelper.getEnchantmentLevel(TBEnchant.magicTouch.effectId, currentItem);
					AspectList aspectsCompound = AspectHelper.getEntityAspects(dyingMob);
					if ((aspectsCompound != null) && (aspectsCompound.size() > 0)) 
					{
						AspectList aspects = AspectHelper.reduceToPrimals(aspectsCompound);
						for(int i = 0; i < enchLevel; ++i)
						{
							for (Aspect aspect : aspects.getAspects())
								if (event.entityLiving.worldObj.rand.nextBoolean()) 
								{
									EntityAspectOrb orb = new EntityAspectOrb(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, aspect, 1 + event.entityLiving.worldObj.rand.nextInt(aspects.getAmount(aspect)));
									event.entityLiving.worldObj.spawnEntityInWorld(orb);
								}
							
							if(player.worldObj.rand.nextBoolean())
								break;
						}
					}
				}
				if(EnchantmentHelper.getEnchantmentLevel(TBEnchant.vaporising.effectId, currentItem) > 0)
				{
					int enchLevel = EnchantmentHelper.getEnchantmentLevel(TBEnchant.vaporising.effectId, currentItem);
					if(event.entity.worldObj.rand.nextInt(Math.max(1, 5 - enchLevel)) == 0)
					{
						AspectList aspects = AspectHelper.getEntityAspects(event.entityLiving);
						if ((aspects != null) && (aspects.size() > 0))
							for (Aspect aspect : aspects.getAspects())
							{
								if (!event.entity.worldObj.rand.nextBoolean()) 
								{
									int size = 1 + event.entity.worldObj.rand.nextInt(aspects.getAmount(aspect));
									size = Math.max(1, size / 2);
									ItemStack stack = new ItemStack(ItemsTC.crystalEssence, size, 0);
									((ItemGenericEssentiaContainer)stack.getItem()).setAspects(stack, new AspectList().add(aspect, 1));
									EntityItem cEs = new EntityItem(event.entity.worldObj, event.entityLiving.posX, event.entityLiving.posY + event.entityLiving.getEyeHeight(), event.entityLiving.posZ, stack);
									event.entity.worldObj.spawnEntityInWorld(cEs);
								}
								if(event.entity.worldObj.rand.nextInt(2+enchLevel) == 0)
									break;
							}
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public void onMobDamage(LivingHurtEvent event)
	{
		if(event.entityLiving != null && !event.entityLiving.worldObj.isRemote && event.source != null && event.source.getSourceOfDamage() != null && event.source.getSourceOfDamage() instanceof EntityPlayer)
		{
			EntityPlayer player = EntityPlayer.class.cast(event.source.getSourceOfDamage());
			EntityLivingBase mob = event.entityLiving;
			ItemStack currentItem = player.getCurrentEquippedItem();
			if(currentItem != null)
			{
				if(mob instanceof EntityEnderman || mob instanceof IEldritchMob)
				{
					if(EnchantmentHelper.getEnchantmentLevel(TBEnchant.eldritchBane.effectId, currentItem) > 0)
					{
						int enchLevel = EnchantmentHelper.getEnchantmentLevel(TBEnchant.eldritchBane.effectId, currentItem);
						event.ammount += enchLevel*5F;
					}
				}
				if(EnchantmentHelper.getEnchantmentLevel(TBEnchant.tainted.effectId, currentItem) > 0)
				{
					int enchLevel = EnchantmentHelper.getEnchantmentLevel(TBEnchant.tainted.effectId, currentItem);
					if(!(mob instanceof ITaintedMob))
					{
						event.ammount += enchLevel*3F;
						if(player.worldObj.rand.nextInt(Math.max(1,4-enchLevel)) == 0)
						{
							mob.addPotionEffect(new PotionEffect(Config.potionTaintPoisonID,200,enchLevel-1,true,false));
						}
					}
				}
			}
		}
	}
}
