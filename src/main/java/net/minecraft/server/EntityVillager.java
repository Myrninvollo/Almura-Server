package net.minecraft.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

public class EntityVillager extends EntityAgeable implements IMerchant, NPC {
    private int profession;
    private boolean br;
    private boolean bs;
    Village village;
    private EntityHuman tradingPlayer;
    private MerchantRecipeList bu;
    private int bv;
    private boolean bw;
    private int riches;
    private String by;
    private boolean bz;
    private float bA;
    private static final Map bB = new HashMap();
    private static final Map bC = new HashMap();

    public EntityVillager(World paramWorld) {
        this(paramWorld, 0);
    }

    public EntityVillager(World paramWorld, int paramInt) {
        super(paramWorld);
        setProfession(paramInt);
        a(0.6F, 1.8F);

        getNavigation().b(true);
        getNavigation().a(true);

        this.goalSelector.a(0, new PathfinderGoalFloat(this));
        this.goalSelector.a(1, new PathfinderGoalAvoidPlayer(this, EntityZombie.class, 8.0F, 0.6D, 0.6D));
        this.goalSelector.a(1, new PathfinderGoalTradeWithPlayer(this));
        this.goalSelector.a(1, new PathfinderGoalLookAtTradingPlayer(this));
        this.goalSelector.a(2, new PathfinderGoalMoveIndoors(this));
        this.goalSelector.a(3, new PathfinderGoalRestrictOpenDoor(this));
        this.goalSelector.a(4, new PathfinderGoalOpenDoor(this, true));
        this.goalSelector.a(5, new PathfinderGoalMoveTowardsRestriction(this, 0.6D));
        this.goalSelector.a(6, new PathfinderGoalMakeLove(this));
        this.goalSelector.a(7, new PathfinderGoalTakeFlower(this));
        this.goalSelector.a(8, new PathfinderGoalPlay(this, 0.32D));
        this.goalSelector.a(9, new PathfinderGoalInteract(this, EntityHuman.class, 3.0F, 1.0F));
        this.goalSelector.a(9, new PathfinderGoalInteract(this, EntityVillager.class, 5.0F, 0.02F));
        this.goalSelector.a(9, new PathfinderGoalRandomStroll(this, 0.6D));
        this.goalSelector.a(10, new PathfinderGoalLookAtPlayer(this, EntityInsentient.class, 8.0F));
    }

    protected void az() {
        super.az();
        getAttributeInstance(GenericAttributes.d).setValue(0.5D);
    }

    public boolean bf() {
        return true;
    }

    protected void bk() {
        Object localObject;
        if (--this.profession <= 0) {
            this.world.villages.a(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ));
            this.profession = (70 + this.random.nextInt(50));

            this.village = this.world.villages.getClosestVillage(MathHelper.floor(this.locX), MathHelper.floor(this.locY), MathHelper.floor(this.locZ), 32);
            if (this.village == null) { bR();
            } else {
                localObject = this.village.getCenter();
                b(((ChunkCoordinates)localObject).x, ((ChunkCoordinates)localObject).y, ((ChunkCoordinates)localObject).z, (int)(this.village.getSize() * 0.6F));

                if (this.bz) {
                    this.bz = false;
                    this.village.b(5);
                }
            }
        }
        if ((!bW()) && (this.bv > 0)) {
            this.bv -= 1;
            if (this.bv <= 0) {
                if (this.bw)
                {
                    if (this.bu.size() > 1) {
                        for (localObject = this.bu.iterator(); ((Iterator)localObject).hasNext(); ) { MerchantRecipe localMerchantRecipe = (MerchantRecipe)((Iterator)localObject).next();
                        if (localMerchantRecipe.g()) {
                            localMerchantRecipe.a(this.random.nextInt(6) + this.random.nextInt(6) + 2);
                        }
                        }
                    }

                    q(1);
                    this.bw = false;

                    if ((this.village != null) && (this.by != null)) {
                        this.world.broadcastEntityEffect(this, (byte) 14);
                        this.village.a(this.by, 1);
                    }
                }
                addEffect(new MobEffect(MobEffectList.REGENERATION.id, 200, 0));
            }
        }

        super.bk();
    }

    public boolean a(EntityHuman paramEntityHuman)
    {
        ItemStack localItemStack = paramEntityHuman.inventory.getItemInHand();
        int i = (localItemStack != null) && (localItemStack.id == Item.MONSTER_EGG.id) ? 1 : 0;

        if ((i == 0) && (isAlive()) && (!bW()) && (!isBaby())) {
            if (!this.world.isStatic)
            {
                a_(paramEntityHuman);
                paramEntityHuman.openTrade(this, getCustomName());
            }
            return true;
        }
        return super.a(paramEntityHuman);
    }

    protected void a()
    {
        super.a();
        this.datawatcher.a(16, Integer.valueOf(0));
    }

    public void b(NBTTagCompound paramNBTTagCompound)
    {
        super.b(paramNBTTagCompound);
        paramNBTTagCompound.setInt("Profession", getProfession());
        paramNBTTagCompound.setInt("Riches", this.riches);
        if (this.bu != null)
            paramNBTTagCompound.setCompound("Offers", this.bu.a());
    }

    public void a(NBTTagCompound paramNBTTagCompound)
    {
        super.a(paramNBTTagCompound);
        setProfession(paramNBTTagCompound.getInt("Profession"));
        this.riches = paramNBTTagCompound.getInt("Riches");
        if (paramNBTTagCompound.hasKey("Offers")) {
            NBTTagCompound localNBTTagCompound = paramNBTTagCompound.getCompound("Offers");
            this.bu = new MerchantRecipeList(localNBTTagCompound);
        }
    }

    protected boolean isTypeNotPersistent()
    {
        return false;
    }

    protected String r()
    {
        if (bW()) {
            return "mob.villager.haggle";
        }
        return "mob.villager.idle";
    }

    protected String aO()
    {
        return "mob.villager.hit";
    }

    protected String aP()
    {
        return "mob.villager.death";
    }

    public void setProfession(int paramInt) {
        this.datawatcher.watch(16, Integer.valueOf(paramInt));
    }

    public int getProfession() {
        return this.datawatcher.getInt(16);
    }

    public boolean bU() {
        return this.br;
    }

    public void i(boolean paramBoolean) {
        this.br = paramBoolean;
    }

    public void j(boolean paramBoolean) {
        this.bs = paramBoolean;
    }

    public boolean bV() {
        return this.bs;
    }

    public void b(EntityLiving paramEntityLiving)
    {
        super.b(paramEntityLiving);
        if ((this.village != null) && (paramEntityLiving != null)) {
            this.village.a(paramEntityLiving);

            if ((paramEntityLiving instanceof EntityHuman)) {
                int i = -1;
                if (isBaby()) {
                    i = -3;
                }
                this.village.a(((EntityHuman)paramEntityLiving).getName(), i);
                if (isAlive())
                    this.world.broadcastEntityEffect(this, (byte) 13);
            }
        }
    }

    public void die(DamageSource paramDamageSource)
    {
        if (this.village != null) {
            Entity localEntity = paramDamageSource.getEntity();
            if (localEntity != null) {
                if ((localEntity instanceof EntityHuman))
                    this.village.a(((EntityHuman)localEntity).getName(), -2);
                else if ((localEntity instanceof IMonster))
                    this.village.h();
            }
            else if (localEntity == null)
            {
                EntityHuman localEntityHuman = this.world.findNearbyPlayer(this, 16.0D);
                if (localEntityHuman != null) {
                    this.village.h();
                }
            }
        }

        super.die(paramDamageSource);
    }

    public void a_(EntityHuman paramEntityHuman)
    {
        this.tradingPlayer = paramEntityHuman;
    }

    public EntityHuman m_()
    {
        return this.tradingPlayer;
    }

    public boolean bW() {
        return this.tradingPlayer != null;
    }

    public void a(MerchantRecipe paramMerchantRecipe)
    {
        paramMerchantRecipe.f();
        this.a_ = (-o());
        makeSound("mob.villager.yes", ba(), bb());

        if (paramMerchantRecipe.a((MerchantRecipe)this.bu.get(this.bu.size() - 1))) {
            this.bv = 40;
            this.bw = true;
            if (this.tradingPlayer != null)
                this.by = this.tradingPlayer.getName();
            else {
                this.by = null;
            }
        }
        if (paramMerchantRecipe.getBuyItem1().id == Item.EMERALD.id)
            this.riches += paramMerchantRecipe.getBuyItem1().count;
    }

    public void a_(ItemStack paramItemStack)
    {
        if ((!this.world.isStatic) && (this.a_ > -o() + 20)) {
            this.a_ = (-o());
            if (paramItemStack != null)
                makeSound("mob.villager.yes", ba(), bb());
            else
                makeSound("mob.villager.no", ba(), bb());
        }
    }

    public MerchantRecipeList getOffers(EntityHuman paramEntityHuman)
    {
        if (this.bu == null) {
            q(1);
        }
        return this.bu;
    }

    private float p(float paramFloat)
    {
        float f = paramFloat + this.bA;
        if (f > 0.9F) {
            return 0.9F - (f - 0.9F);
        }
        return f;
    }

    @SuppressWarnings("unchecked")
    private void q(int paramInt)
    {
        if (this.bu != null)
            this.bA = (MathHelper.c(this.bu.size()) * 0.2F);
        else {
            this.bA = 0.0F;
        }

        MerchantRecipeList localMerchantRecipeList = new MerchantRecipeList();
        Object localObject1;
        int m;
        System.out.println("Profession: " + getProfession());
        switch (getProfession()) {        
        case 0:
            //a(localMerchantRecipeList, Item.WHEAT.id, this.random, p(0.9F));
            //a(localMerchantRecipeList, Block.WOOL.id, this.random, p(0.5F));
            //a(localMerchantRecipeList, Item.RAW_CHICKEN.id, this.random, p(0.5F));
            //a(localMerchantRecipeList, Item.COOKED_FISH.id, this.random, p(0.4F));
            //b(localMerchantRecipeList, Item.BREAD.id, this.random, p(0.9F));
            //b(localMerchantRecipeList, Item.MELON.id, this.random, p(0.3F));
            //b(localMerchantRecipeList, Item.APPLE.id, this.random, p(0.3F));
            //b(localMerchantRecipeList, Item.COOKIE.id, this.random, p(0.3F));
            //b(localMerchantRecipeList, Item.SHEARS.id, this.random, p(0.3F));
            //b(localMerchantRecipeList, Item.FLINT_AND_STEEL.id, this.random, p(0.3F));
            b(localMerchantRecipeList, Item.COOKED_CHICKEN.id, this.random, p(0.3F));
            //b(localMerchantRecipeList, Item.ARROW.id, this.random, p(0.5F));
            if (this.random.nextFloat() >= p(0.5F)) break;
            localMerchantRecipeList.add(new MerchantRecipe(new ItemStack(Block.GRAVEL, 10), new ItemStack(Item.EMERALD), new ItemStack(Item.FLINT.id, 4 + this.random.nextInt(2), 0))); break;
        case 4:
            a(localMerchantRecipeList, Item.COAL.id, this.random, p(0.7F));
            a(localMerchantRecipeList, Item.PORK.id, this.random, p(0.5F));
            a(localMerchantRecipeList, Item.RAW_BEEF.id, this.random, p(0.5F));
            b(localMerchantRecipeList, Item.SADDLE.id, this.random, p(0.1F));
            b(localMerchantRecipeList, Item.LEATHER_CHESTPLATE.id, this.random, p(0.3F));
            b(localMerchantRecipeList, Item.LEATHER_BOOTS.id, this.random, p(0.3F));
            b(localMerchantRecipeList, Item.LEATHER_HELMET.id, this.random, p(0.3F));
            b(localMerchantRecipeList, Item.LEATHER_LEGGINGS.id, this.random, p(0.3F));
            b(localMerchantRecipeList, Item.GRILLED_PORK.id, this.random, p(0.3F));
            b(localMerchantRecipeList, Item.COOKED_BEEF.id, this.random, p(0.3F));
            break;
        case 3:
            a(localMerchantRecipeList, Item.COAL.id, this.random, p(0.7F));
            a(localMerchantRecipeList, Item.IRON_INGOT.id, this.random, p(0.5F));
            a(localMerchantRecipeList, Item.GOLD_INGOT.id, this.random, p(0.5F));
            a(localMerchantRecipeList, Item.DIAMOND.id, this.random, p(0.5F));

            b(localMerchantRecipeList, Item.IRON_SWORD.id, this.random, p(0.5F));
            b(localMerchantRecipeList, Item.DIAMOND_SWORD.id, this.random, p(0.5F));
            b(localMerchantRecipeList, Item.IRON_AXE.id, this.random, p(0.3F));
            b(localMerchantRecipeList, Item.DIAMOND_AXE.id, this.random, p(0.3F));
            b(localMerchantRecipeList, Item.IRON_PICKAXE.id, this.random, p(0.5F));
            b(localMerchantRecipeList, Item.DIAMOND_PICKAXE.id, this.random, p(0.5F));
            b(localMerchantRecipeList, Item.IRON_SPADE.id, this.random, p(0.2F));
            b(localMerchantRecipeList, Item.DIAMOND_SPADE.id, this.random, p(0.2F));
            b(localMerchantRecipeList, Item.IRON_HOE.id, this.random, p(0.2F));
            b(localMerchantRecipeList, Item.DIAMOND_HOE.id, this.random, p(0.2F));
            b(localMerchantRecipeList, Item.IRON_BOOTS.id, this.random, p(0.2F));
            b(localMerchantRecipeList, Item.DIAMOND_BOOTS.id, this.random, p(0.2F));
            b(localMerchantRecipeList, Item.IRON_HELMET.id, this.random, p(0.2F));
            b(localMerchantRecipeList, Item.DIAMOND_HELMET.id, this.random, p(0.2F));
            b(localMerchantRecipeList, Item.IRON_CHESTPLATE.id, this.random, p(0.2F));
            b(localMerchantRecipeList, Item.DIAMOND_CHESTPLATE.id, this.random, p(0.2F));
            b(localMerchantRecipeList, Item.IRON_LEGGINGS.id, this.random, p(0.2F));
            b(localMerchantRecipeList, Item.DIAMOND_LEGGINGS.id, this.random, p(0.2F));
            b(localMerchantRecipeList, Item.CHAINMAIL_BOOTS.id, this.random, p(0.1F));
            b(localMerchantRecipeList, Item.CHAINMAIL_HELMET.id, this.random, p(0.1F));
            b(localMerchantRecipeList, Item.CHAINMAIL_CHESTPLATE.id, this.random, p(0.1F));
            b(localMerchantRecipeList, Item.CHAINMAIL_LEGGINGS.id, this.random, p(0.1F));
            break;
        case 1:
            a(localMerchantRecipeList, Item.PAPER.id, this.random, p(0.8F));
            a(localMerchantRecipeList, Item.BOOK.id, this.random, p(0.8F));
            a(localMerchantRecipeList, Item.WRITTEN_BOOK.id, this.random, p(0.3F));
            b(localMerchantRecipeList, Block.BOOKSHELF.id, this.random, p(0.8F));
            b(localMerchantRecipeList, Block.GLASS.id, this.random, p(0.2F));
            b(localMerchantRecipeList, Item.COMPASS.id, this.random, p(0.2F));
            b(localMerchantRecipeList, Item.WATCH.id, this.random, p(0.2F));

            if (this.random.nextFloat() >= p(0.07F)) break;
            localObject1 = Enchantment.c[this.random.nextInt(Enchantment.c.length)];
            int j = MathHelper.nextInt(this.random, ((Enchantment)localObject1).getStartLevel(), ((Enchantment)localObject1).getMaxLevel());
            ItemStack localItemStack = Item.ENCHANTED_BOOK.a(new EnchantmentInstance((Enchantment)localObject1, j));
            m = 2 + this.random.nextInt(5 + j * 10) + 3 * j;

            localMerchantRecipeList.add(new MerchantRecipe(new ItemStack(Item.BOOK), new ItemStack(Item.EMERALD, m), localItemStack));
            break;
        case 2:
            b(localMerchantRecipeList, Item.EYE_OF_ENDER.id, this.random, p(0.3F));
            b(localMerchantRecipeList, Item.EXP_BOTTLE.id, this.random, p(0.2F));
            b(localMerchantRecipeList, Item.REDSTONE.id, this.random, p(0.4F));
            b(localMerchantRecipeList, Block.GLOWSTONE.id, this.random, p(0.3F));

            int []localObject2 = new int[] { Item.IRON_SWORD.id, Item.DIAMOND_SWORD.id, Item.IRON_CHESTPLATE.id, Item.DIAMOND_CHESTPLATE.id, Item.IRON_AXE.id, Item.DIAMOND_AXE.id, Item.IRON_PICKAXE.id, Item.DIAMOND_PICKAXE.id };

            for (int n : localObject2) {
                if (this.random.nextFloat() < p(0.05F)) {
                    localMerchantRecipeList.add(new MerchantRecipe(new ItemStack(n, 1, 0), new ItemStack(Item.EMERALD, 2 + this.random.nextInt(3), 0), EnchantmentManager.a(this.random, new ItemStack(n, 1, 0), 5 + this.random.nextInt(15))));
                }

            }

        }

        if (localMerchantRecipeList.isEmpty()) {
            a(localMerchantRecipeList, Item.GOLD_INGOT.id, this.random, 1.0F);
        }

        Collections.shuffle(localMerchantRecipeList);

        if (this.bu == null) {
            this.bu = new MerchantRecipeList();
        }
        for (int i = 0; (i < paramInt) && (i < localMerchantRecipeList.size()); i++)
            this.bu.a((MerchantRecipe)localMerchantRecipeList.get(i));
    }

    @SuppressWarnings("unchecked")
    private static void a(MerchantRecipeList paramMerchantRecipeList, int paramInt, Random paramRandom, float paramFloat)
    {
        if (paramRandom.nextFloat() < paramFloat)
            paramMerchantRecipeList.add(new MerchantRecipe(a(paramInt, paramRandom), Item.EMERALD));
    }

    private static ItemStack a(int paramInt, Random paramRandom)
    {
        return new ItemStack(paramInt, b(paramInt, paramRandom), 0);
    }

    private static int b(int paramInt, Random paramRandom) {
        Tuple localTuple = (Tuple)bB.get(Integer.valueOf(paramInt));
        if (localTuple == null) {
            return 1;
        }
        if (((Integer)localTuple.a()).intValue() >= ((Integer)localTuple.b()).intValue()) {
            return ((Integer)localTuple.a()).intValue();
        }
        return ((Integer)localTuple.a()).intValue() + paramRandom.nextInt(((Integer)localTuple.b()).intValue() - ((Integer)localTuple.a()).intValue());
    }

    @SuppressWarnings("unchecked")
    private static void b(MerchantRecipeList paramMerchantRecipeList, int paramInt, Random paramRandom, float paramFloat)
    {
        if (paramRandom.nextFloat() < paramFloat) {
            int i = c(paramInt, paramRandom);
            ItemStack localItemStack1;
            ItemStack localItemStack2;
            if (i < 0) {
                localItemStack1 = new ItemStack(Item.EMERALD.id, 1, 0);
                localItemStack2 = new ItemStack(paramInt, -i, 0);
            } else {
                localItemStack1 = new ItemStack(Item.EMERALD.id, i, 0);
                localItemStack2 = new ItemStack(paramInt, 1, 0);
            }
            paramMerchantRecipeList.add(new MerchantRecipe(localItemStack1, localItemStack2));
        }
    }

    private static int c(int paramInt, Random paramRandom) {
        Tuple localTuple = (Tuple)bC.get(Integer.valueOf(paramInt));
        if (localTuple == null) {
            return 1;
        }
        if (((Integer)localTuple.a()).intValue() >= ((Integer)localTuple.b()).intValue()) {
            return ((Integer)localTuple.a()).intValue();
        }
        return ((Integer)localTuple.a()).intValue() + paramRandom.nextInt(((Integer)localTuple.b()).intValue() - ((Integer)localTuple.a()).intValue());
    }

    public GroupDataEntity a(GroupDataEntity paramGroupDataEntity) {
        paramGroupDataEntity = super.a(paramGroupDataEntity);
        setProfession(this.world.random.nextInt(5));
        return paramGroupDataEntity;
    }

    public void bX() {
        this.bz = true;
    }

    public EntityVillager b(EntityAgeable paramEntityAgeable) {
        EntityVillager localEntityVillager = new EntityVillager(this.world);
        localEntityVillager.a((EntityAgeable)null);
        return localEntityVillager;
    }

    public boolean bG() {
        return false;
    }

    static {
        bB.put(Integer.valueOf(Item.COAL.id), new Tuple(Integer.valueOf(16), Integer.valueOf(24)));
        bB.put(Integer.valueOf(Item.IRON_INGOT.id), new Tuple(Integer.valueOf(8), Integer.valueOf(10)));
        bB.put(Integer.valueOf(Item.GOLD_INGOT.id), new Tuple(Integer.valueOf(8), Integer.valueOf(10)));
        bB.put(Integer.valueOf(Item.DIAMOND.id), new Tuple(Integer.valueOf(4), Integer.valueOf(6)));
        bB.put(Integer.valueOf(Item.PAPER.id), new Tuple(Integer.valueOf(24), Integer.valueOf(36)));
        bB.put(Integer.valueOf(Item.BOOK.id), new Tuple(Integer.valueOf(11), Integer.valueOf(13)));
        bB.put(Integer.valueOf(Item.WRITTEN_BOOK.id), new Tuple(Integer.valueOf(1), Integer.valueOf(1)));
        bB.put(Integer.valueOf(Item.ENDER_PEARL.id), new Tuple(Integer.valueOf(3), Integer.valueOf(4)));
        bB.put(Integer.valueOf(Item.EYE_OF_ENDER.id), new Tuple(Integer.valueOf(2), Integer.valueOf(3)));
        bB.put(Integer.valueOf(Item.PORK.id), new Tuple(Integer.valueOf(14), Integer.valueOf(18)));
        bB.put(Integer.valueOf(Item.RAW_BEEF.id), new Tuple(Integer.valueOf(14), Integer.valueOf(18)));
        bB.put(Integer.valueOf(Item.RAW_CHICKEN.id), new Tuple(Integer.valueOf(14), Integer.valueOf(18)));
        bB.put(Integer.valueOf(Item.COOKED_FISH.id), new Tuple(Integer.valueOf(9), Integer.valueOf(13)));
        bB.put(Integer.valueOf(Item.SEEDS.id), new Tuple(Integer.valueOf(34), Integer.valueOf(48)));
        bB.put(Integer.valueOf(Item.MELON_SEEDS.id), new Tuple(Integer.valueOf(30), Integer.valueOf(38)));
        bB.put(Integer.valueOf(Item.PUMPKIN_SEEDS.id), new Tuple(Integer.valueOf(30), Integer.valueOf(38)));
        bB.put(Integer.valueOf(Item.WHEAT.id), new Tuple(Integer.valueOf(18), Integer.valueOf(22)));
        bB.put(Integer.valueOf(Block.WOOL.id), new Tuple(Integer.valueOf(14), Integer.valueOf(22)));
        bB.put(Integer.valueOf(Item.ROTTEN_FLESH.id), new Tuple(Integer.valueOf(36), Integer.valueOf(64)));

        bC.put(Integer.valueOf(Item.FLINT_AND_STEEL.id), new Tuple(Integer.valueOf(3), Integer.valueOf(4)));
        bC.put(Integer.valueOf(Item.SHEARS.id), new Tuple(Integer.valueOf(3), Integer.valueOf(4)));
        bC.put(Integer.valueOf(Item.IRON_SWORD.id), new Tuple(Integer.valueOf(7), Integer.valueOf(11)));
        bC.put(Integer.valueOf(Item.DIAMOND_SWORD.id), new Tuple(Integer.valueOf(12), Integer.valueOf(14)));
        bC.put(Integer.valueOf(Item.IRON_AXE.id), new Tuple(Integer.valueOf(6), Integer.valueOf(8)));
        bC.put(Integer.valueOf(Item.DIAMOND_AXE.id), new Tuple(Integer.valueOf(9), Integer.valueOf(12)));
        bC.put(Integer.valueOf(Item.IRON_PICKAXE.id), new Tuple(Integer.valueOf(7), Integer.valueOf(9)));
        bC.put(Integer.valueOf(Item.DIAMOND_PICKAXE.id), new Tuple(Integer.valueOf(10), Integer.valueOf(12)));
        bC.put(Integer.valueOf(Item.IRON_SPADE.id), new Tuple(Integer.valueOf(4), Integer.valueOf(6)));
        bC.put(Integer.valueOf(Item.DIAMOND_SPADE.id), new Tuple(Integer.valueOf(7), Integer.valueOf(8)));
        bC.put(Integer.valueOf(Item.IRON_HOE.id), new Tuple(Integer.valueOf(4), Integer.valueOf(6)));
        bC.put(Integer.valueOf(Item.DIAMOND_HOE.id), new Tuple(Integer.valueOf(7), Integer.valueOf(8)));
        bC.put(Integer.valueOf(Item.IRON_BOOTS.id), new Tuple(Integer.valueOf(4), Integer.valueOf(6)));
        bC.put(Integer.valueOf(Item.DIAMOND_BOOTS.id), new Tuple(Integer.valueOf(7), Integer.valueOf(8)));
        bC.put(Integer.valueOf(Item.IRON_HELMET.id), new Tuple(Integer.valueOf(4), Integer.valueOf(6)));
        bC.put(Integer.valueOf(Item.DIAMOND_HELMET.id), new Tuple(Integer.valueOf(7), Integer.valueOf(8)));
        bC.put(Integer.valueOf(Item.IRON_CHESTPLATE.id), new Tuple(Integer.valueOf(10), Integer.valueOf(14)));
        bC.put(Integer.valueOf(Item.DIAMOND_CHESTPLATE.id), new Tuple(Integer.valueOf(16), Integer.valueOf(19)));
        bC.put(Integer.valueOf(Item.IRON_LEGGINGS.id), new Tuple(Integer.valueOf(8), Integer.valueOf(10)));
        bC.put(Integer.valueOf(Item.DIAMOND_LEGGINGS.id), new Tuple(Integer.valueOf(11), Integer.valueOf(14)));
        bC.put(Integer.valueOf(Item.CHAINMAIL_BOOTS.id), new Tuple(Integer.valueOf(5), Integer.valueOf(7)));
        bC.put(Integer.valueOf(Item.CHAINMAIL_HELMET.id), new Tuple(Integer.valueOf(5), Integer.valueOf(7)));
        bC.put(Integer.valueOf(Item.CHAINMAIL_CHESTPLATE.id), new Tuple(Integer.valueOf(11), Integer.valueOf(15)));
        bC.put(Integer.valueOf(Item.CHAINMAIL_LEGGINGS.id), new Tuple(Integer.valueOf(9), Integer.valueOf(11)));
        bC.put(Integer.valueOf(Item.BREAD.id), new Tuple(Integer.valueOf(-4), Integer.valueOf(-2)));
        bC.put(Integer.valueOf(Item.MELON.id), new Tuple(Integer.valueOf(-8), Integer.valueOf(-4)));
        bC.put(Integer.valueOf(Item.APPLE.id), new Tuple(Integer.valueOf(-8), Integer.valueOf(-4)));
        bC.put(Integer.valueOf(Item.COOKIE.id), new Tuple(Integer.valueOf(-10), Integer.valueOf(-7)));
        bC.put(Integer.valueOf(Block.GLASS.id), new Tuple(Integer.valueOf(-5), Integer.valueOf(-3)));
        bC.put(Integer.valueOf(Block.BOOKSHELF.id), new Tuple(Integer.valueOf(3), Integer.valueOf(4)));
        bC.put(Integer.valueOf(Item.LEATHER_CHESTPLATE.id), new Tuple(Integer.valueOf(4), Integer.valueOf(5)));
        bC.put(Integer.valueOf(Item.LEATHER_BOOTS.id), new Tuple(Integer.valueOf(2), Integer.valueOf(4)));
        bC.put(Integer.valueOf(Item.LEATHER_HELMET.id), new Tuple(Integer.valueOf(2), Integer.valueOf(4)));
        bC.put(Integer.valueOf(Item.LEATHER_LEGGINGS.id), new Tuple(Integer.valueOf(2), Integer.valueOf(4)));
        bC.put(Integer.valueOf(Item.SADDLE.id), new Tuple(Integer.valueOf(6), Integer.valueOf(8)));
        bC.put(Integer.valueOf(Item.EXP_BOTTLE.id), new Tuple(Integer.valueOf(-4), Integer.valueOf(-1)));
        bC.put(Integer.valueOf(Item.REDSTONE.id), new Tuple(Integer.valueOf(-4), Integer.valueOf(-1)));
        bC.put(Integer.valueOf(Item.COMPASS.id), new Tuple(Integer.valueOf(10), Integer.valueOf(12)));
        bC.put(Integer.valueOf(Item.WATCH.id), new Tuple(Integer.valueOf(10), Integer.valueOf(12)));
        bC.put(Integer.valueOf(Block.GLOWSTONE.id), new Tuple(Integer.valueOf(-3), Integer.valueOf(-1)));
        bC.put(Integer.valueOf(Item.GRILLED_PORK.id), new Tuple(Integer.valueOf(-7), Integer.valueOf(-5)));
        bC.put(Integer.valueOf(Item.COOKED_BEEF.id), new Tuple(Integer.valueOf(-7), Integer.valueOf(-5)));
        bC.put(Integer.valueOf(Item.COOKED_CHICKEN.id), new Tuple(Integer.valueOf(-8), Integer.valueOf(-6)));
        bC.put(Integer.valueOf(Item.EYE_OF_ENDER.id), new Tuple(Integer.valueOf(7), Integer.valueOf(11)));
        bC.put(Integer.valueOf(Item.ARROW.id), new Tuple(Integer.valueOf(-12), Integer.valueOf(-8)));
    }

    @Override
    public EntityAgeable createChild(EntityAgeable entityageable) {
        // TODO Auto-generated method stub
        return null;
    }
}