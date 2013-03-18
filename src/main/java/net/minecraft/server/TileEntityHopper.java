package net.minecraft.server;

import java.util.List;

// CraftBukkit start
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.inventory.InventoryPickupItemEvent;
// CraftBukkit end

public class TileEntityHopper extends TileEntity implements IHopper {

    private ItemStack[] a = new ItemStack[5];
    private String b;
    private int c = -1;

    // CraftBukkit start
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();
    private int maxStack = MAX_STACK;

    public ItemStack[] getContents() {
        return this.a;
    }

    public void onOpen(CraftHumanEntity who) {
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public void setMaxStackSize(int size) {
        maxStack = size;
    }
    // CraftBukkit end


    public TileEntityHopper() {}

    public void a(NBTTagCompound nbttagcompound) {
        super.a(nbttagcompound);
        NBTTagList nbttaglist = nbttagcompound.getList("Items");

        this.a = new ItemStack[this.getSize()];
        if (nbttagcompound.hasKey("CustomName")) {
            this.b = nbttagcompound.getString("CustomName");
        }

        this.c = nbttagcompound.getInt("TransferCooldown");

        for (int i = 0; i < nbttaglist.size(); ++i) {
            NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttaglist.get(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.a.length) {
                this.a[b0] = ItemStack.createStack(nbttagcompound1);
            }
        }
    }

    public void b(NBTTagCompound nbttagcompound) {
        super.b(nbttagcompound);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.a.length; ++i) {
            if (this.a[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();

                nbttagcompound1.setByte("Slot", (byte) i);
                this.a[i].save(nbttagcompound1);
                nbttaglist.add(nbttagcompound1);
            }
        }

        nbttagcompound.set("Items", nbttaglist);
        nbttagcompound.setInt("TransferCooldown", this.c);
        if (this.c()) {
            nbttagcompound.setString("CustomName", this.b);
        }
    }

    public void update() {
        super.update();
    }

    public int getSize() {
        return this.a.length;
    }

    public ItemStack getItem(int i) {
        return this.a[i];
    }

    public ItemStack splitStack(int i, int j) {
        if (this.a[i] != null) {
            ItemStack itemstack;

            if (this.a[i].count <= j) {
                itemstack = this.a[i];
                this.a[i] = null;
                return itemstack;
            } else {
                itemstack = this.a[i].a(j);
                if (this.a[i].count == 0) {
                    this.a[i] = null;
                }

                return itemstack;
            }
        } else {
            return null;
        }
    }

    public ItemStack splitWithoutUpdate(int i) {
        if (this.a[i] != null) {
            ItemStack itemstack = this.a[i];

            this.a[i] = null;
            return itemstack;
        } else {
            return null;
        }
    }

    public void setItem(int i, ItemStack itemstack) {
        this.a[i] = itemstack;
        if (itemstack != null && itemstack.count > this.getMaxStackSize()) {
            itemstack.count = this.getMaxStackSize();
        }
    }

    public String getName() {
        return this.c() ? this.b : "container.hopper";
    }

    public boolean c() {
        return this.b != null && this.b.length() > 0;
    }

    public void a(String s) {
        this.b = s;
    }

    public int getMaxStackSize() {
        return 64;
    }

    public boolean a(EntityHuman entityhuman) {
        return this.world.getTileEntity(this.x, this.y, this.z) != this ? false : entityhuman.e((double) this.x + 0.5D, (double) this.y + 0.5D, (double) this.z + 0.5D) <= 64.0D;
    }

    public void startOpen() {}

    public void g() {}

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    public void h() {
        if (this.world != null && !this.world.isStatic) {
            --this.c;
            if (!this.l()) {
                this.c(0);
                this.j();
            }
        }
    }

    public boolean j() {
        if (this.world != null && !this.world.isStatic) {
            if (!this.l() && BlockHopper.d(this.p())) {
                boolean flag = this.u() | a((IHopper) this);

                if (flag) {
                    this.c(8);
                    this.update();
                    return true;
                }
            }

            return false;
        } else {
            return false;
        }
    }

    private boolean u() {
        int i = a(this, -1);
        boolean flag = false;

        if (i > -1) {
            IInventory iinventory = this.v();

            if (iinventory != null) {
                ItemStack itemstack = this.getItem(i).cloneItemStack();
                ItemStack itemstack1 = a(iinventory, this.splitStack(i, 1), Facing.OPPOSITE_FACING[BlockHopper.c(this.p())]);

                if (itemstack1 != null && itemstack1.count != 0) {
                    this.setItem(i, itemstack);
                } else {
                    flag |= true;
                    iinventory.update();
                }
            }
        }

        return flag;
    }

    public static boolean a(IHopper ihopper) {
        boolean flag = false;
        IInventory iinventory = b(ihopper);

        if (iinventory != null) {
            byte b0 = 0;
            int i = 0;
            int j = iinventory.getSize();

            if (iinventory instanceof IWorldInventory && b0 > -1) {
                IWorldInventory iworldinventory = (IWorldInventory) iinventory;

                i = iworldinventory.c(b0);
                j = Math.min(j, i + iworldinventory.d(b0));
            }

            for (int k = i; k < j && !flag; ++k) {
                ItemStack itemstack = iinventory.getItem(k);

                if (itemstack != null) {
                    ItemStack itemstack1 = itemstack.cloneItemStack();
                    ItemStack itemstack2 = a(ihopper, iinventory.splitStack(k, 1), -1);

                    if (itemstack2 != null && itemstack2.count != 0) {
                        iinventory.setItem(k, itemstack1);
                    } else {
                        flag |= true;
                        iinventory.update();
                    }
                }
            }
        } else {
            EntityItem entityitem = a(ihopper.getWorld(), ihopper.aA(), ihopper.aB() + 1.0D, ihopper.aC());

            if (entityitem != null) {
                flag |= a((IInventory) ihopper, entityitem);
            }
        }

        return flag;
    }

    public static boolean a(IInventory iinventory, EntityItem entityitem) {
        boolean flag = false;

        if (entityitem == null) {
            return false;
        } else {
            // CraftBukkit start
            InventoryPickupItemEvent event = new InventoryPickupItemEvent(iinventory.getOwner().getInventory(), (org.bukkit.entity.Item) entityitem.getBukkitEntity());
            entityitem.world.getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return false;
            }
            // CraftBukkit end

            ItemStack itemstack = entityitem.getItemStack().cloneItemStack();
            ItemStack itemstack1 = a(iinventory, itemstack, -1);

            if (itemstack1 != null && itemstack1.count != 0) {
                entityitem.setItemStack(itemstack1);
            } else {
                flag = true;
                entityitem.die();
            }

            return flag;
        }
    }

    public static int a(IInventory iinventory, int i) {
        int j = 0;
        int k = iinventory.getSize();

        if (iinventory instanceof IWorldInventory && i > -1) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory;

            j = iworldinventory.c(i);
            k = Math.min(k, j + iworldinventory.d(i));
        }

        for (int l = j; l < k; ++l) {
            if (iinventory.getItem(l) != null) {
                return l;
            }
        }

        return -1;
    }

    public static ItemStack a(IInventory iinventory, ItemStack itemstack, int i) {
        int j = 0;
        int k = iinventory.getSize();

        if (iinventory instanceof IWorldInventory && i > -1) {
            IWorldInventory iworldinventory = (IWorldInventory) iinventory;

            j = iworldinventory.c(i);
            k = Math.min(k, j + iworldinventory.d(i));
        }

        for (int l = j; l < k && itemstack != null && itemstack.count > 0; ++l) {
            ItemStack itemstack1 = iinventory.getItem(l);

            if (iinventory.b(l, itemstack)) {
                boolean flag = false;

                if (itemstack1 == null) {
                    iinventory.setItem(l, itemstack);
                    itemstack = null;
                    flag = true;
                } else if (a(itemstack1, itemstack)) {
                    int i1 = itemstack.getMaxStackSize() - itemstack1.count;
                    int j1 = Math.min(itemstack.count, i1);

                    itemstack.count -= j1;
                    itemstack1.count += j1;
                    flag = j1 > 0;
                }

                if (flag) {
                    if (iinventory instanceof TileEntityHopper) {
                        ((TileEntityHopper) iinventory).c(8);
                    }

                    iinventory.update();
                }
            }
        }

        if (itemstack != null && itemstack.count == 0) {
            itemstack = null;
        }

        return itemstack;
    }

    private IInventory v() {
        int i = BlockHopper.c(this.p());

        return b(this.getWorld(), (double) (this.x + Facing.b[i]), (double) (this.y + Facing.c[i]), (double) (this.z + Facing.d[i]));
    }

    public static IInventory b(IHopper ihopper) {
        return b(ihopper.getWorld(), ihopper.aA(), ihopper.aB() + 1.0D, ihopper.aC());
    }

    public static EntityItem a(World world, double d0, double d1, double d2) {
        List list = world.a(EntityItem.class, AxisAlignedBB.a().a(d0, d1, d2, d0 + 1.0D, d1 + 1.0D, d2 + 1.0D), IEntitySelector.a);

        return list.size() > 0 ? (EntityItem) list.get(0) : null;
    }

    public static IInventory b(World world, double d0, double d1, double d2) {
        IInventory iinventory = null;
        int i = MathHelper.floor(d0);
        int j = MathHelper.floor(d1);
        int k = MathHelper.floor(d2);

        if (iinventory == null) {
            TileEntity tileentity = world.getTileEntity(i, j, k);

            if (tileentity != null && tileentity instanceof IInventory) {
                iinventory = (IInventory) tileentity;
                if (iinventory instanceof TileEntityChest) {
                    int l = world.getTypeId(i, j, k);
                    Block block = Block.byId[l];

                    if (block instanceof BlockChest) {
                        iinventory = ((BlockChest) block).g_(world, i, j, k);
                    }
                }
            }
        }

        if (iinventory == null) {
            List list = world.getEntities((Entity) null, AxisAlignedBB.a().a(d0, d1, d2, d0 + 1.0D, d1 + 1.0D, d2 + 1.0D), IEntitySelector.b);

            if (list != null && list.size() > 0) {
                iinventory = (IInventory) list.get(world.random.nextInt(list.size()));
            }
        }

        return iinventory;
    }

    private static boolean a(ItemStack itemstack, ItemStack itemstack1) {
        return itemstack.id != itemstack1.id ? false : (itemstack.getData() != itemstack1.getData() ? false : (itemstack.count > itemstack.getMaxStackSize() ? false : ItemStack.equals(itemstack, itemstack1)));
    }

    public double aA() {
        return (double) this.x;
    }

    public double aB() {
        return (double) this.y;
    }

    public double aC() {
        return (double) this.z;
    }

    public void c(int i) {
        this.c = i;
    }

    public boolean l() {
        return this.c > 0;
    }
}
