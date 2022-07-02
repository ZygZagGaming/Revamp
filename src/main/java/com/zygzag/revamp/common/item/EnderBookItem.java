package com.zygzag.revamp.common.item;

import com.zygzag.revamp.common.Revamp;
import com.zygzag.revamp.common.capability.Document;
import com.zygzag.revamp.common.registry.Registry;
import com.zygzag.revamp.util.ClientUtils;
import com.zygzag.revamp.util.GeneralUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class EnderBookItem extends Item {
    public EnderBookItem(Item.Properties properties) {
        super(properties);
    }

    public InteractionResult useOn(UseOnContext ctx) {
        Level level = ctx.getLevel();
        BlockPos blockpos = ctx.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        /*if (blockstate.is(Blocks.LECTERN)) {
            return LecternBlock.tryPlaceBook(ctx.getPlayer(), level, blockpos, blockstate, ctx.getItemInHand()) ? InteractionResult.sidedSuccess(level.isClientSide) : InteractionResult.PASS;
        } else {*/
            return InteractionResult.PASS;
        //}
    }

    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        if (world.isClientSide) {
            ClientUtils.openEnderBookScreen(player, itemstack, hand);
        } else {
            GeneralUtil.ifCapability(world, Revamp.SERVER_LEVEL_ENDER_BOOK_CAPABILITY, (handler) -> {
                CompoundTag tag = itemstack.getOrCreateTag();
                if (!tag.contains("id")) {
                    tag.putInt("id", handler.documents.size());
                    tag.putInt("generation", 0);
                    handler.documents.add(new Document());
                }
            });
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.sidedSuccess(itemstack, world.isClientSide());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> text, TooltipFlag flag) {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains("id")) {
            int id = tag.getInt("id");
            text.add(Component.literal("Id: #" + id).withStyle(ChatFormatting.DARK_GRAY));
            text.add((Component.translatable("book.generation." + tag.getInt("generation"))).withStyle(ChatFormatting.GRAY));
        }
    }

    @Override
    public boolean hasCraftingRemainingItem() {
        return true;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        return itemStack.copy();
    }
}
