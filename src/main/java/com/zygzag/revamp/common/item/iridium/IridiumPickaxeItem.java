package com.zygzag.revamp.common.item.iridium;

import com.zygzag.revamp.common.item.recipe.ItemHolder;
import com.zygzag.revamp.common.item.recipe.ModRecipeType;
import com.zygzag.revamp.common.item.recipe.TransmutationRecipe;
import net.minecraft.ChatFormatting;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class IridiumPickaxeItem extends PickaxeItem implements ISocketable {
    Socket socket;
    public IridiumPickaxeItem(Tier tier, int damage, float speed, Properties properties, Socket socket) {
        super(tier, damage, speed, properties);
        this.socket = socket;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        Socket s = getSocket();
        Item i = s.i;
        MutableComponent m;
        if (s != Socket.NONE) {
            String str = s == Socket.AMETHYST || s == Socket.EMERALD || s == Socket.SKULL ? "use" : "passive";
            MutableComponent t = new TranslatableComponent("socketed.revamp").withStyle(ChatFormatting.GRAY);
            t.append(new TextComponent(": ").withStyle(ChatFormatting.GRAY));
            t.append(((MutableComponent) i.getName(i.getDefaultInstance())).withStyle(ChatFormatting.GOLD));
            text.add(t);

            Socket socket = getSocket();
            text.add(new TextComponent(""));
            if (str.equals("passive")) m = new TranslatableComponent(str + ".revamp").withStyle(ChatFormatting.GRAY);
            else m = Minecraft.getInstance().options.keyUse.getKey().getDisplayName().copy().withStyle(ChatFormatting.GRAY);
            m.append(new TextComponent( ": ").withStyle(ChatFormatting.GRAY));
            m.append(new TranslatableComponent( str + "_ability.revamp.pickaxe." + socket.name().toLowerCase()).withStyle(ChatFormatting.GOLD));
            text.add(m);
            text.add(new TranslatableComponent("description." + str + "_ability.revamp.pickaxe." + socket.name().toLowerCase()));
        }
    }

    @Override
    public Socket getSocket() {
        return socket;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() instanceof IridiumPickaxeItem item) {
            switch (item.getSocket()) {
                case DIAMOND -> {

                }
                case EMERALD -> {
                    if (!player.getCooldowns().isOnCooldown(this)) {
                        int playerX = player.getBlockX();
                        int playerY = player.getBlockY();
                        int playerZ = player.getBlockZ();
                        int n = 0;
                        for (int x = playerX - 5; x <= playerX + 5; x++) {
                            for (int y = playerY - 5; y <= playerY + 5; y++) {
                                for (int z = playerZ - 5; z <= playerZ + 5; z++) {
                                    BlockPos pos = new BlockPos(x, y, z);
                                    BlockState state = world.getBlockState(pos);
                                    if (state.is(Tags.Blocks.ORES)) {
                                        world.setBlock(pos, Blocks.AIR.defaultBlockState(), 0);
                                        if (world instanceof ServerLevel sWorld) {
                                            List<ItemStack> stacks = state.getDrops(new LootContext.Builder(sWorld).withParameter(LootContextParams.TOOL, stack).withParameter(LootContextParams.ORIGIN, new Vec3(pos.getX(), pos.getY(), pos.getZ())));
                                            for (ItemStack s : stacks) {
                                                ItemEntity i = new ItemEntity(world, playerX, playerY, playerZ, s);
                                                world.addFreshEntity(i);
                                            }
                                            n++;
                                        }
                                    }
                                }
                            }
                        }
                        if (!player.getAbilities().instabuild) {
                            player.getCooldowns().addCooldown(this, 6000);
                            damageItem(stack, n, player, (p) -> {});
                        }
                    }
                }
                case SKULL -> {
                    if (!player.getCooldowns().isOnCooldown(this)) {
                        AABB box = player.getBoundingBox().inflate(5.0);
                        List<ItemEntity> entities = world.getEntitiesOfClass(ItemEntity.class, box);
                        List<TransmutationRecipe> recipes = world.getRecipeManager().getAllRecipesFor(ModRecipeType.TRANSMUTATION);
                        for (ItemEntity itemEntity : entities) {
                            for (TransmutationRecipe recipe : recipes) {
                                ItemHolder holder = new ItemHolder(itemEntity.getItem());
                                if (recipe.matches(holder, world)) {
                                    ItemEntity newItem = new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), recipe.assemble(holder));
                                    world.addFreshEntity(newItem);
                                    if (!player.getAbilities().instabuild) stack.hurtAndBreak(newItem.getItem().getCount(), player, (it) -> {});
                                    itemEntity.kill();
                                }
                            }
                        }
                    }
                }
                case WITHER_SKULL -> {

                }
                case AMETHYST -> {

                }
            }
        }
        return InteractionResultHolder.success(stack);
    }
}