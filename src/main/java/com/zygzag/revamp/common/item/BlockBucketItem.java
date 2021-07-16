package com.zygzag.revamp.common.item;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class BlockBucketItem extends Item {

    public BlockState block;

    public BlockBucketItem(Properties prop, BlockState block) {
        super(prop);
        this.block = block;
    }

    public BlockBucketItem(Properties prop, Block block) {
        this(prop, block.defaultBlockState());
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack itemstack = player.getItemInHand(hand);
        BlockRayTraceResult result = getPlayerPOVHitResult(world, player, RayTraceContext.FluidMode.NONE);
        if (result.getType() == RayTraceResult.Type.BLOCK) {
            BlockRayTraceResult blockraytraceresult = result;
            BlockPos blockpos = blockraytraceresult.getBlockPos();
            Direction direction = blockraytraceresult.getDirection();
            BlockPos blockpos1 = blockpos.relative(direction);
            if (world.mayInteract(player, blockpos) && player.mayUseItemAt(blockpos1, direction, itemstack) && world.getBlockState(blockpos1).is(Blocks.AIR)) {
                player.setItemInHand(hand, Items.BUCKET.getDefaultInstance());
                world.setBlock(blockpos1, this.block, 0);
                return ActionResult.consume(itemstack);
            }
            return ActionResult.pass(itemstack);
        }
        return ActionResult.pass(itemstack);
    }
}
