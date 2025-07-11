package net.electrisoma.testmod.registry.items.tau_cannon;

import net.createmod.catnip.platform.CatnipServices;
import net.electrisoma.testmod.network.items.tau_cannon.TauCannonClientPacket;
import net.electrisoma.testmod.network.items.tau_cannon.TauCannonServerPacket;
import net.electrisoma.testmod.registry.items.tau_cannon.rendering.TauCannonFlash;
import net.electrisoma.testmod.registry.items.tau_cannon.rendering.TauCannonItemRenderer;
import net.electrisoma.testmod.registry.items.util.ClientRenderableItem;
import net.electrisoma.testmod.registry.items.util.CustomArmPoseItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BellBlock;
import net.minecraft.world.level.block.entity.BellBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static net.electrisoma.testmod.registry.items.tau_cannon.rendering.TauCannonItemRenderer.speedMap;

public class TauCannonItem extends Item implements CustomArmPoseItem, ClientRenderableItem {
    public static final int MAX_CHARGE_TICKS = 60;
    public static final int MAX_OVERCHARGE_TICKS = 100;
    public static final float MAX_CHARGE_FORCE = 2.0f;
    public static final float MAX_CHARGE_DAMAGE = 16.0f;

    private static final float BASE_DAMAGE = 4.0f;
    private static final int COOLDOWN_TICKS = 10;

    private static final double RAY_RANGE = 200;
    private static final double RAY_MAX_BOUNCES = 5;

    public TauCannonItem(Properties properties) {
        super(properties);
    }

    @Override public BlockEntityWithoutLevelRenderer createRenderer(BlockEntityRenderDispatcher dispatcher, EntityModelSet models) {
        return new TauCannonItemRenderer();
    }
    @Override public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }
    @Nullable @Override public ArmPose getArmPose(ItemStack stack, AbstractClientPlayer player, InteractionHand hand) {
        return ArmPose.CROSSBOW_HOLD;
    }
    @Override public boolean isFoil(ItemStack stack) {
        return false;
    }
    @NotNull @Override public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }
    @Override public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }
    @Override public boolean useOnRelease(ItemStack stack) {
        return false;
    }
    @NotNull @Override public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (!(entity instanceof Player player)) return;

        int heldTicks = getUseDuration(stack, player) - remainingUseDuration;

        if (level.isClientSide && player == Minecraft.getInstance().player) {
            if (!TauCannonFlash.hasChargingLight(player))
                TauCannonFlash.addChargingLight(player);
            TauCannonFlash.updateChargingLight(player, heldTicks);

            if (heldTicks > 0 && heldTicks % 5 == 0) {
                int key = System.identityHashCode(stack);
                float velocity = speedMap.getOrDefault(key, 0f);
                float maxSpeed = 800f;
                float normSpeed = velocity / maxSpeed;
                float pitch = 1.0f + normSpeed;
                float volume = 1.0f + normSpeed;

                level.playLocalSound(player.getX(), player.getY(), player.getZ(),
                        SoundEvents.UI_BUTTON_CLICK.value(),
                        SoundSource.PLAYERS,
                        volume, pitch,
                        false
                );
            }
        }

        boolean killMeIfIOvercharge = !player.isCreative();
        if (!level.isClientSide && heldTicks > MAX_CHARGE_TICKS + MAX_OVERCHARGE_TICKS && killMeIfIOvercharge) {
            player.hurt(player.damageSources().magic(), Float.MAX_VALUE);
            level.playSound(null,
                    player.blockPosition(),
                    SoundEvents.GENERIC_EXPLODE.value(),
                    SoundSource.PLAYERS,
                    2.0f, 0.5f
            );
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;

        if (!level.isClientSide) {
            player.getCooldowns().addCooldown(this, COOLDOWN_TICKS);
        }

        if (level.isClientSide) {
            int heldTicks = getUseDuration(stack, player) - timeLeft;
            float chargeRatio = Math.min(heldTicks / (float) MAX_CHARGE_TICKS, 1.0f);

            float damage = Mth.lerp(chargeRatio * chargeRatio, BASE_DAMAGE, MAX_CHARGE_DAMAGE);
            float recoil = chargeRatio * chargeRatio * MAX_CHARGE_FORCE * 1.5f;
            int bounces = (int) (chargeRatio * RAY_MAX_BOUNCES);
            int pierces = (int) (chargeRatio * (RAY_MAX_BOUNCES * 2/3));

            TauCannonServerPacket serverPacket = new TauCannonServerPacket(damage, recoil, bounces, pierces);
            CatnipServices.NETWORK.sendToServer(serverPacket);
            TauCannonItemRenderer.startReleaseAnimation(stack);
        }
    }

    public static void sendBeamToClient(Player player, List<Vec3> beamPoints) {
        TauCannonClientPacket clientPacket = new TauCannonClientPacket(beamPoints);
        CatnipServices.NETWORK.sendToClient((ServerPlayer) player, clientPacket);
    }

    public static List<Vec3> shootRay(Level level, Player player, float damage, float recoilStrength, int maxBounces, int maxPierce) {
        Vec3 currentPos = player.getEyePosition();
        Vec3 currentDir = player.getLookAngle().normalize();

        List<Vec3> rayPoints = new ArrayList<>();
        rayPoints.add(currentPos);

        int piercesLeft = maxPierce;
        int bouncesLeft = maxBounces;

        Set<Entity> hitEntities = new HashSet<>();
        Set<BlockPos> piercedBlocks = new HashSet<>();

        while (true) {
            BlockHitResult blockHit = raycastBlock(level, currentPos, currentDir, player);

            boolean hitBlock = blockHit.getType() == HitResult.Type.BLOCK;
            Vec3 hitPos = hitBlock
                    ? blockHit.getLocation()
                    : currentPos.add(currentDir.scale(RAY_RANGE));

            damageEntities(level, player, currentPos, hitPos, damage, recoilStrength * 2, hitEntities);
            rayPoints.add(hitPos);

            if (hitBlock) {
                BlockPos hitBlockPos = blockHit.getBlockPos();
                BlockState blockState = level.getBlockState(hitBlockPos);

                // for whom the taco bell tolls
                if (blockState.getBlock() instanceof BellBlock) {
                    BlockEntity blockEntity = level.getBlockEntity(hitBlockPos);
                    if (blockEntity instanceof BellBlockEntity bellEntity) {
                        Direction ringDirection = blockState.getValue(BellBlock.FACING);
                        bellEntity.onHit(ringDirection);
                        level.playSound(null, hitBlockPos,
                                SoundEvents.BELL_BLOCK, SoundSource.BLOCKS,
                                2.0F, 1.0F);
                        level.gameEvent(player, GameEvent.BLOCK_CHANGE, hitBlockPos);
                    }
                }

                if (piercedBlocks.contains(hitBlockPos)) {
                    currentPos = currentPos.add(currentDir.scale(1.5));
                    continue;
                }

                Vec3 normal = getHitNormal(blockHit);
                boolean bounced = false;

                if (bouncesLeft > 0 && canBounce(currentDir, normal)) {
                    currentDir = reflect(currentDir, normal).normalize();
                    hitPos.add(currentDir.scale(0.01));
                    bouncesLeft--;
                    bounced = true;
                }

                if (!bounced && piercesLeft > 0) {
                    piercesLeft--;
                    piercedBlocks.add(hitBlockPos);
                    currentPos = hitPos.add(currentDir.scale(1.5));
                    continue;
                }

                if (!bounced) break;
            } else break;

            if (piercesLeft <= 0) break;
            currentPos = hitPos;
        }

        applyRecoil(player, recoilStrength);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.GENERIC_EXPLODE, player.getSoundSource(), 1.0f, 1.0f);

        return rayPoints;
    }

    private static boolean canBounce(Vec3 dir, Vec3 normal) {
        double angle = Math.toDegrees(Math.acos(Mth.clamp(dir.dot(normal), -1.0, 1.0)));
        return angle > 45 && angle < 135;
    }
    private static Vec3 reflect(Vec3 dir, Vec3 normal) {
        return dir.subtract(normal.scale(2 * dir.dot(normal)));
    }
    private static BlockHitResult raycastBlock(Level level, Vec3 origin, Vec3 direction, Player player) {
        Vec3 end = origin.add(direction.scale(RAY_RANGE));
        ClipContext context = new ClipContext(origin, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player);
        return level.clip(context);
    }
    private static Vec3 getHitNormal(BlockHitResult hit) {
        return switch (hit.getDirection()) {
            case UP -> new Vec3(0, 1, 0);
            case DOWN -> new Vec3(0, -1, 0);
            case NORTH -> new Vec3(0, 0, -1);
            case SOUTH -> new Vec3(0, 0, 1);
            case WEST -> new Vec3(-1, 0, 0);
            case EAST -> new Vec3(1, 0, 0);
        };
    }

    private static void applyRecoil(Player player, float force) {
        Vec3 recoil = player.getLookAngle().scale(-force);
        player.push(recoil.x, recoil.y * 0.5f, recoil.z);
        player.hurtMarked = true;
    }
    private static void damageEntities(Level level, Player shooter, Vec3 start, Vec3 end,
                                       float damage, float recoilStrength,
                                       Set<Entity> alreadyHit) {
        Vec3 direction = end.subtract(start).normalize();
        AABB beamAABB = new AABB(start, end).inflate(1.5);
        int hits = 0;

        for (Entity entity : level.getEntities(shooter, beamAABB, e -> e != shooter && e.isPickable() && e instanceof LivingEntity && !alreadyHit.contains(e))) {
            if (hits >= Integer.MAX_VALUE) break;

            AABB bb = entity.getBoundingBox();
            Optional<Vec3> intersection = bb.clip(start, end);
            if (intersection.isPresent()) {
                entity.hurt(shooter.damageSources().playerAttack(shooter), damage);

                Vec3 knockback = direction.scale(recoilStrength);
                entity.push(knockback.x, knockback.y * 0.25f, knockback.z);
                entity.hurtMarked = true;
                alreadyHit.add(entity);
                hits++;
            }
        }
    }
}