package net.minecraft.server;

import java.util.Random;

// CraftBukkit start
import org.bukkit.event.entity.EntityInteractEvent;
import org.bukkit.craftbukkit.event.CraftEventFactory;
// CraftBukkit end

public class BlockSoil extends Block {

    protected BlockSoil(int i) {
        super(i, Material.EARTH);        
        this.b(true);  // setTickRandomly
        this.a(0.0F, 0.0F, 0.0F, 1.0F, 0.9375F, 1.0F);  // setBlockBounds
        this.k(255); // setLightOpacity
    }

    public AxisAlignedBB b(World world, int i, int j, int k) {
        return AxisAlignedBB.a().a((double) (i + 0), (double) (j + 0), (double) (k + 0), (double) (i + 1), (double) (j + 1), (double) (k + 1));
    }

    public boolean c() { // isOpaqueCube
        return false;
    }

    public boolean b() { // renderAsNormalBlock
        return false;
    }

    // updateTick()
    public void a(World world, int x, int y, int z, Random random) {
        // Almura Start -> Call Block's parent method as that fires the event
        super.a(world, x, y, z, random);
        if (!this.m(world, x, y, z) && !world.isRainingAt(x, y + 1, z) && !(world.getBiome(x, z).humidity > 0.4)) {  // isWaterNearby  // canLightningStrikeAt
            int moistureLevel = world.getData(x, y, z);

            if (moistureLevel > 0) {
             // Almura Start --> Prevent Rain Fade
                org.bukkit.block.Block block = world.getWorld().getBlockAt(x, y, z);
                if (CraftEventFactory.callBlockFadeEvent(block, Block.DIRT.id).isCancelled()) {
                    return;
                }
                // CraftBukkit end
                world.setData(x, y, z, moistureLevel - 1, 2);
            } else if (!this.k(world, x, y, z)) {
                // CraftBukkit start
                org.bukkit.block.Block block = world.getWorld().getBlockAt(x, y, z);
                if (CraftEventFactory.callBlockFadeEvent(block, Block.DIRT.id).isCancelled()) {
                    return;
                }
                // CraftBukkit end

                world.setTypeIdUpdate(x, y, z, Block.DIRT.id);
            }
        } else {
            world.setData(x, y, z, 7, 2);
        }
    }
    
    // onFallenUpon
    public void a(World world, int i, int j, int k, Entity entity, float f) {
        if (!world.isStatic && world.random.nextFloat() < f - 0.5F) {
            if (!(entity instanceof EntityHuman) && !world.getGameRules().getBoolean("mobGriefing")) {
                return;
            }

            // CraftBukkit start - Interact soil
            org.bukkit.event.Cancellable cancellable;
            if (entity instanceof EntityHuman) {
                cancellable = CraftEventFactory.callPlayerInteractEvent((EntityHuman) entity, org.bukkit.event.block.Action.PHYSICAL, i, j, k, -1, null);
            } else {
                cancellable = new EntityInteractEvent(entity.getBukkitEntity(), world.getWorld().getBlockAt(i, j, k));
                world.getServer().getPluginManager().callEvent((EntityInteractEvent) cancellable);
            }

            if (cancellable.isCancelled()) {
                return;
            }
            // CraftBukkit end

            world.setTypeIdUpdate(i, j, k, Block.DIRT.id);
        }
    }

    // isCropsNearby
    private boolean k(World world, int i, int j, int k) {
        byte b0 = 0;

        for (int l = i - b0; l <= i + b0; ++l) {
            for (int i1 = k - b0; i1 <= k + b0; ++i1) {
                int j1 = world.getTypeId(l, j + 1, i1);

                if (j1 == Block.CROPS.id || j1 == Block.MELON_STEM.id || j1 == Block.PUMPKIN_STEM.id || j1 == Block.POTATOES.id || j1 == Block.CARROTS.id) {
                    return true;
                }
            }
        }

        return false;
    }

    // isWaterNearBy
    private boolean m(World world, int i, int j, int k) {
        for (int l = i - 4; l <= i + 4; ++l) {
            for (int i1 = j; i1 <= j + 1; ++i1) {
                for (int j1 = k - 4; j1 <= k + 4; ++j1) {
                    if (world.getMaterial(l, i1, j1) == Material.WATER) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void doPhysics(World world, int i, int j, int k, int l) {
        super.doPhysics(world, i, j, k, l);
        Material material = world.getMaterial(i, j + 1, k);

        if (material.isBuildable()) {
            world.setTypeIdUpdate(i, j, k, Block.DIRT.id);
        }
    }

    public int getDropType(int i, Random random, int j) {
        return Block.DIRT.getDropType(0, random, j);
    }
}
