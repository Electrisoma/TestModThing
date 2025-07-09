package net.electrisoma.testmod.registry.items.tau_cannon;

import net.electrisoma.testmod.registry.items.util.CustomArmPoseItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TauCannonItem extends Item implements CustomArmPoseItem {
    public static final float BASE_DAMAGE = 4.0f;
    private static final int COOLDOWN_TICKS = 10;
    private static final int MAX_CHARGE_TICKS = 40;
    private static final float MAX_CHARGE_FORCE = 2.0f;
    private static final float MAX_CHARGE_DAMAGE = 12.0f;
    private static final double RAY_RANGE = 100.0;
    private static final double MIN_BOUNCE_ANGLE = 15.0;
    private static final double MAX_BOUNCE_ANGLE = 75.0;

    private static final Map<UUID, Integer> playerLastShotTick = new HashMap<>();
    public static final Map<UUID, List<Vec3>> activeBeams = new HashMap<>();
    public static final Map<UUID, Integer> beamTimers = new HashMap<>();

    private static final DustParticleOptions ORANGE_BEAM_PARTICLE =
            new DustParticleOptions(new Vec3(1.0f, 0.5f, 0.0f).toVector3f(), 1.5f);

    public TauCannonItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return false;
    }

    @Nullable
    @Override
    public ArmPose getArmPose(ItemStack stack, AbstractClientPlayer player, InteractionHand hand) {
        System.out.println("Player holding item in hand: " + hand);
        return ArmPose.CROSSBOW_HOLD;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return false;
    }

    @NotNull
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @NotNull
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        if (!(entity instanceof Player player)) return;

        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player) return;

        int useDuration = getUseDuration(stack, entity);
        int heldTicks = useDuration - remainingUseDuration;

        if (level.isClientSide && player == mc.player) {
            if (!TauCannonFlash.hasChargingLight(player))
                TauCannonFlash.addChargingLight(player);
            TauCannonFlash.updateChargingLight(player, heldTicks);
        }

        if (heldTicks > 0 && heldTicks % 5 == 0) {
            float ratio = Math.min(heldTicks / (float) MAX_CHARGE_TICKS, 1.0f);
            float pitch = 1.0f + ratio;

            level.playLocalSound(player.getX(), player.getY(), player.getZ(),
                    SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS,
                    2f, pitch, false);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;
        if (level.isClientSide && player == Minecraft.getInstance().player)
            TauCannonFlash.removeChargingLight(player);

        int currentTick = (int) level.getGameTime();
        UUID playerId = player.getUUID();
        Integer lastShotTick = playerLastShotTick.get(playerId);

        if (lastShotTick != null && currentTick - lastShotTick < COOLDOWN_TICKS)
            return;

        int chargeTime = getUseDuration(stack, entity) - timeLeft;
        float chargeRatio = Mth.clamp(chargeTime / (float) MAX_CHARGE_TICKS, 0.0f, 1.0f);

        float damage = Mth.lerp(chargeRatio, BASE_DAMAGE, MAX_CHARGE_DAMAGE);
        float recoil = chargeRatio * MAX_CHARGE_FORCE;

        int maxBounces = Mth.floor(chargeRatio * 3);
        int maxPierce = Mth.floor(chargeRatio * 2);

        shootRay(level, player, damage, recoil, maxBounces, maxPierce);

        playerLastShotTick.put(playerId, currentTick);
    }

    public static void shootRay(Level level, Player player, float damage, float recoilStrength, int maxBounces, int maxPierce) {
        Vec3 currentPos = player.getEyePosition();
        Vec3 currentDir = player.getLookAngle().normalize();

        if (level.isClientSide) {
            TauCannonFlash.spawnMuzzleFlash(player);
            level.playLocalSound(player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GENERIC_EXPLODE.value(), player.getSoundSource(), 1.0f, 1.0f, false);
        }

        List<Vec3> rayPoints = new ArrayList<>();
        rayPoints.add(currentPos);

        int[] pierceBlocksLeft = new int[]{maxPierce};
        int bouncesLeft = maxBounces;
        Set<Entity> hitEntities = new HashSet<>();

        while (true) {
            BlockHitResult blockHit = raycastBlock(level, currentPos, currentDir, player);
            Vec3 hitPos = blockHit.getType() != HitResult.Type.MISS
                    ? blockHit.getLocation()
                    : currentPos.add(currentDir.scale(RAY_RANGE));

            if (level.isClientSide) {
                spawnBeamParticles(level, currentPos, hitPos);
                damageEntitiesClient(level, currentPos, hitPos);
                spawnImpactParticles(level, hitPos);
            }

            Vec3 nextStart = currentPos;
            Vec3 nextEnd = blockHit.getType() != HitResult.Type.MISS
                    ? blockHit.getLocation()
                    : currentPos.add(currentDir.scale(RAY_RANGE));

            if (!level.isClientSide) {
                damageEntities(level, player, nextStart, nextEnd, damage, recoilStrength * 2, hitEntities, Integer.MAX_VALUE);
            }

            rayPoints.add(hitPos);

            if (blockHit.getType() == HitResult.Type.BLOCK) {
                Vec3 normal = getHitNormal(blockHit, currentDir);
                boolean bounced = false;

                if (bouncesLeft > 0 && canBounce(currentDir, normal)) {
                    currentDir = reflect(currentDir, normal).normalize();
                    currentPos = hitPos.add(currentDir.scale(0.01));
                    bouncesLeft--;
                    bounced = true;
                }

                if (!bounced && handlePiercing(level, hitPos, currentDir, pierceBlocksLeft)) {
                    currentPos = hitPos.add(currentDir.scale(1.1));
                    continue;
                }

                if (!bounced) break;
            } else {
                break;
            }
        }

        if (level.isClientSide) {
            activeBeams.put(player.getUUID(), rayPoints);
            beamTimers.put(player.getUUID(), 10);
        } else {
            applyRecoil(player, recoilStrength);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GENERIC_EXPLODE, player.getSoundSource(), 1.0f, 1.0f);
        }
    }

    private static void applyRecoil(Player player, float force) {
        Vec3 recoil = player.getLookAngle().scale(-force);
        player.push(recoil.x, recoil.y * 0.5f, recoil.z);
        player.hurtMarked = true;
    }

    private static BlockHitResult raycastBlock(Level level, Vec3 start, Vec3 direction, Player player) {
        Vec3 end = start.add(direction.scale(RAY_RANGE));
        ClipContext context = new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        return level.clip(context);
    }

    private static boolean canBounce(Vec3 direction, Vec3 normal) {
        double dot = Mth.clamp(direction.dot(normal), -1.0, 1.0);
        double angle = Math.toDegrees(Math.acos(dot));

        return angle > 45 && angle < 135;
    }

    private static boolean handlePiercing(Level level, Vec3 hitPos, Vec3 direction, int pierceBlocksLeftWrapper[]) {
        if (pierceBlocksLeftWrapper[0] > 0) {
            pierceBlocksLeftWrapper[0]--;
            return true;
        }
        return false;
    }

    private static Vec3 reflect(Vec3 v, Vec3 normal) {
        return v.subtract(normal.scale(2 * v.dot(normal)));
    }

    private static Vec3 getHitNormal(BlockHitResult hit, Vec3 rayDirection) {
        Vec3 normal = switch (hit.getDirection()) {
            case UP -> new Vec3(0, 1, 0);
            case DOWN -> new Vec3(0, -1, 0);
            case NORTH -> new Vec3(0, 0, -1);
            case SOUTH -> new Vec3(0, 0, 1);
            case WEST -> new Vec3(-1, 0, 0);
            case EAST -> new Vec3(1, 0, 0);
        };

        if (normal.dot(rayDirection) > 0)
            normal = normal.scale(-1);
        return normal;
    }

    private static void damageEntities(Level level, Player shooter, Vec3 start, Vec3 end,
                                       float damage, float recoilStrength,
                                       Set<Entity> alreadyHit, int maxHits) {
        Vec3 direction = end.subtract(start).normalize();
        AABB beamAABB = new AABB(start, end).inflate(1.5);
        int hits = 0;

        for (Entity entity : level.getEntities(shooter, beamAABB, e -> e != shooter && e.isPickable() && e instanceof LivingEntity && !alreadyHit.contains(e))) {
            if (hits >= maxHits) break;

            AABB bb = entity.getBoundingBox();
            Optional<Vec3> intersection = bb.clip(start, end);
            if (intersection.isPresent()) {
                entity.hurt(shooter.damageSources().playerAttack(shooter), damage);

                if (entity instanceof LivingEntity livingEntity && livingEntity.isDeadOrDying()) {
                    TauCannonDropHandler.markHitByTauCannon(livingEntity, true);
                    if (!level.isClientSide) {
                        List<ItemEntity> drops = level.getEntitiesOfClass(ItemEntity.class, entity.getBoundingBox());
                        TauCannonDropHandler.replaceDrops(level, livingEntity, drops);
                    }
                }

                Vec3 knockback = direction.scale(recoilStrength);
                entity.push(knockback.x, knockback.y * 0.25f, knockback.z);
                entity.hurtMarked = true;
                alreadyHit.add(entity);
                hits++;
            }
        }
    }

    private static void damageEntitiesClient(Level level, Vec3 start, Vec3 end) {
        AABB beamAABB = new AABB(start, end).inflate(1.0);
        for (Entity entity : level.getEntities((Entity) null, beamAABB, e -> e.isPickable() && e
                instanceof LivingEntity)) {
            if (entity.getBoundingBox().clip(start, end).isPresent())
                spawnSparkParticles(level, entity.position());
        }
    }

    private static void spawnBeamParticles(Level level, Vec3 from, Vec3 to) {
        int steps = (int)(from.distanceTo(to) * 2);
        Vec3 step = to.subtract(from).scale(1.0 / steps);
        Vec3 pos = from;

        for (int i = 0; i < steps; i++) {
            level.addParticle(ORANGE_BEAM_PARTICLE, pos.x, pos.y, pos.z, 0, 0, 0);
            pos = pos.add(step);
        }
    }

    private static void spawnImpactParticles(Level level, Vec3 pos) {
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.FLAME, pos.x, pos.y, pos.z, 5, 0.05, 0.05, 0.05, 0.05);
            serverLevel.sendParticles(ParticleTypes.SMOKE, pos.x, pos.y, pos.z, 10, 0.15, 0.15, 0.15, 0.15);
        }
    }

    private static void spawnSparkParticles(Level level, Vec3 pos) {
        level.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y + 0.5, pos.z, 0, 0, 0);
    }
}
