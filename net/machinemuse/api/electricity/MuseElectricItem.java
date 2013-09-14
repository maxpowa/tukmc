package net.machinemuse.api.electricity;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;

public abstract interface MuseElectricItem
{
  public abstract double getCurrentEnergy(ItemStack paramItemStack);

  public abstract double getMaxEnergy(ItemStack paramItemStack);
  
}