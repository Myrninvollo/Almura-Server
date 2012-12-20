package net.minecraft.server;

public class ItemMinecart extends Item {

    public int a;

    public ItemMinecart(int i, int j) {
        super(i);
        this.maxStackSize = 1;
        this.a = j;
        this.a(CreativeModeTab.e);
    }

    public boolean interactWith(ItemStack itemstack, EntityHuman entityhuman, World world, int i, int j, int k, int l, float f, float f1, float f2) {
        int i1 = world.getTypeId(i, j, k);

        if (BlockMinecartTrack.e(i1)) {
            if (!world.isStatic) {
                // CraftBukkit start - Minecarts
                org.bukkit.event.player.PlayerInteractEvent event = org.bukkit.craftbukkit.event.CraftEventFactory.callPlayerInteractEvent(entityhuman, org.bukkit.event.block.Action.RIGHT_CLICK_BLOCK, i, j, k, l, itemstack);

                if (event.isCancelled()) {
                    return false;
                }
                // CraftBukkit end

                world.addEntity(new EntityMinecart(world, (double) ((float) i + 0.5F), (double) ((float) j + 0.5F), (double) ((float) k + 0.5F), this.a));
            }

            --itemstack.count;
            return true;
        } else {
            return false;
        }
    }
}
