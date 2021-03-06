package net.minecraft.server;

import java.util.Random;

// Almura start
import org.bukkit.Bukkit;

import com.almuramc.event.block.CropPlantEvent;
import com.almuramc.event.block.CropPopEvent;
// Almura end

public class BlockFlower extends Block {

    protected BlockFlower(int i, Material material) {
        super(i, material);
        this.b(true);
        float f = 0.2F;

        this.a(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 3.0F, 0.5F + f);
        this.a(CreativeModeTab.c);
    }

    protected BlockFlower(int i) {
        this(i, Material.PLANT);
    }

    public boolean canPlace(World world, int i, int j, int k) {
        return super.canPlace(world, i, j, k) && this.g_(world.getTypeId(i, j - 1, k));
    }

    protected boolean g_(int i) {
        // Almura start
        CropPlantEvent event = new CropPlantEvent();
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (!event.isCustom()) {
            return i == Block.GRASS.id || i == Block.DIRT.id || i == Block.SOIL.id;
        } else {
            return event.canPlant();
        }
        // Almura end
    }

    public void doPhysics(World world, int i, int j, int k, int l) {
        super.doPhysics(world, i, j, k, l);
        this.e(world, i, j, k);
    }

    public void a(World world, int i, int j, int k, Random random) {
        //Almura Start -> Call super to fire BlockTickEvent
        super.a(world, i, j, k, random);
        this.e(world, i, j, k);
    }

    protected final void e(World world, int i, int j, int k) {
        if (!this.f(world, i, j, k)) {
            // Almura start
            CropPopEvent event = new CropPopEvent(world.getWorld().getBlockAt(i,j,k));
            Bukkit.getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()) {
                this.c(world, i, j, k, world.getData(i, j, k), 0);
                world.setTypeIdAndData(i, j, k, 0, 0, 2);
            }
            // Almura end
        }
    }

    public boolean f(World world, int i, int j, int k) {
        return (world.m(i, j, k) >= 8 || world.l(i, j, k)) && this.g_(world.getTypeId(i, j - 1, k));
    }

    public AxisAlignedBB b(World world, int i, int j, int k) {
        return null;
    }

    public boolean c() {
        return false;
    }

    public boolean b() {
        return false;
    }

    public int d() {
        return 1;
    }
}
