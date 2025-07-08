package net.electrisoma.testmod.registry.items.tau_cannon;

import net.electrisoma.testmod.registry.items.util.CustomArmPoseItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TauCannonItem extends Item implements CustomArmPoseItem {
    private static final int MAX_CHARGE_TICKS = 40;
    private static final float MAX_CHARGE_FORCE = 2.0f;
    public static final float BASE_DAMAGE = 4.0f;
    private static final float MAX_CHARGE_DAMAGE = 12.0f;
    private static final double RAY_RANGE = 100.0;
    private static final double MAX_BOUNCE_ANGLE = 75;

    private static final int COOLDOWN_TICKS = 10;
    private static final Map<UUID, Integer> playerLastShotTick = new HashMap<>();


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
        if (!level.isClientSide) return;

        Minecraft mc = Minecraft.getInstance();
        if (player != mc.player) return;

        int useDuration = getUseDuration(stack, entity);
        int heldTicks = useDuration - remainingUseDuration;

        if (heldTicks > 0 && heldTicks % 5 == 0) {
            float ratio = Math.min(heldTicks / (float) MAX_CHARGE_TICKS, 1.0f);
            float pitch = 1.0f + ratio;

            level.playLocalSound(player.getX(), player.getY(), player.getZ(),
                    SoundEvents.UI_BUTTON_CLICK.value(), SoundSource.PLAYERS,
                    0.5f, pitch, false);
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (!(entity instanceof Player player)) return;

        int currentTick = (int) level.getGameTime();
        UUID playerId = player.getUUID();
        Integer lastShotTick = playerLastShotTick.get(playerId);

        if (lastShotTick != null && currentTick - lastShotTick < COOLDOWN_TICKS)
            return;

        int chargeTime = getUseDuration(stack, entity) - timeLeft;
        float chargeRatio = Mth.clamp(chargeTime / (float) MAX_CHARGE_TICKS, 0.0f, 1.0f);

        float damage = Mth.lerp(chargeRatio, BASE_DAMAGE, MAX_CHARGE_DAMAGE);
        float recoil = chargeRatio * MAX_CHARGE_FORCE;

        int maxBounces = 1 + Mth.floor(chargeRatio * 3);     // 1–4
        int maxPierce = 1 + Mth.floor(chargeRatio * 3);      // 1–4

        shootRay(level, player, damage, recoil, maxBounces, maxPierce);

        playerLastShotTick.put(playerId, currentTick);
    }

    public static void shootRay(Level level, Player player, float damage, float recoilStrength, int maxBounces, int maxPierce) {
        Vec3 start = player.getEyePosition();
        Vec3 direction = player.getLookAngle().normalize();

        if (level.isClientSide) {
            TauCannonFlash.spawnMuzzleFlash(player);
            level.playLocalSound(player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GENERIC_EXPLODE.value(), player.getSoundSource(), 1.0f, 1.0f, false);
        }

        Vec3 currentPos = start;
        Vec3 currentDir = direction;
        List<Vec3> rayPoints = new ArrayList<>();
        rayPoints.add(currentPos);

        for (int bounce = 0; bounce <= maxBounces; bounce++) {
            Vec3 rayEnd = currentPos.add(currentDir.scale(RAY_RANGE));
            ClipContext context = new ClipContext(currentPos, rayEnd, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
            HitResult hit = level.clip(context);

            Vec3 segmentEnd = hit.getType() == HitResult.Type.MISS ? rayEnd : hit.getLocation();
            rayPoints.add(segmentEnd);

            if (hit.getType() == HitResult.Type.MISS) break;

            if (level.isClientSide) spawnImpactParticles(level, segmentEnd);

            Vec3 normal = getHitNormal(hit);
            double incidence = Math.abs(currentDir.dot(normal));
            if (incidence <= Math.cos(MAX_BOUNCE_ANGLE)) {
                currentDir = reflect(currentDir, normal).normalize();
                currentPos = segmentEnd.add(currentDir.scale(0.01));
            } else break;
        }

        Set<Entity> hitEntities = new HashSet<>();
        int pierceCount = 0;

        for (int i = 0; i < rayPoints.size() - 1; i++) {
            Vec3 segStart = rayPoints.get(i);
            Vec3 segEnd = rayPoints.get(i + 1);

            if (!level.isClientSide && pierceCount < maxPierce) {
                pierceCount += damageEntitiesAlongBeam(level, player, segStart, segEnd, damage, recoilStrength * 2, hitEntities, maxPierce - pierceCount);
            }

            if (level.isClientSide) {
                spawnBeamParticles(level, segStart, segEnd);
                damageEntitiesAlongBeamClient(level, segStart, segEnd);
            }
        }

        if (!level.isClientSide) {
            applyRecoil(player, recoilStrength);
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    SoundEvents.GENERIC_EXPLODE, player.getSoundSource(), 1.0f, 1.0f);
        }
    }

    private static int damageEntitiesAlongBeam(Level level, Player shooter, Vec3 start, Vec3 end,
                                               float damage, float recoilStrength, Set<Entity> alreadyHit, int maxHits) {
        Vec3 direction = end.subtract(start).normalize();
        AABB beamAABB = new AABB(start, end).inflate(1.0);
        int hits = 0;

        for (Entity entity : level.getEntities(shooter, beamAABB, e -> e != shooter && e.isPickable() && e instanceof LivingEntity && !alreadyHit.contains(e))) {
            if (hits >= maxHits) break;

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

        return hits;
    }

    private static void damageEntitiesAlongBeamClient(Level level, Vec3 start, Vec3 end) {
        AABB beamAABB = new AABB(start, end).inflate(1.0);
        for (Entity entity : level.getEntities((Entity) null, beamAABB, e -> e.isPickable() && e instanceof LivingEntity)) {
            if (entity.getBoundingBox().clip(start, end).isPresent())
                spawnSparkParticles(level, entity.position());
        }
    }

    private static void spawnBeamParticles(Level level, Vec3 from, Vec3 to) {
        int steps = (int)(from.distanceTo(to) * 2);
        Vec3 step = to.subtract(from).scale(1.0 / steps);
        for (int i = 0; i < steps; i++) {
            Vec3 pos = from.add(step.scale(i));
            level.addParticle(ParticleTypes.CRIT, pos.x, pos.y, pos.z, 0, 0, 0);
        }
    }

    private static void spawnImpactParticles(Level level, Vec3 pos) {
        for (int i = 0; i < 15; i++) {
            level.addParticle(ParticleTypes.LAVA, pos.x, pos.y, pos.z,
                    (level.random.nextDouble() - 0.5) * 0.4,
                    (level.random.nextDouble() - 0.5) * 0.4,
                    (level.random.nextDouble() - 0.5) * 0.4);
        }
        level.addParticle(ParticleTypes.FLASH, pos.x, pos.y, pos.z, 0, 0, 0);
    }

    private static void spawnSparkParticles(Level level, Vec3 pos) {
        for (int i = 0; i < 20; i++) {
            level.addParticle(ParticleTypes.ELECTRIC_SPARK, pos.x, pos.y, pos.z,
                    (level.random.nextDouble() - 0.5) * 0.4,
                    (level.random.nextDouble() - 0.5) * 0.4,
                    (level.random.nextDouble() - 0.5) * 0.4);
        }
    }

    private static void applyRecoil(Player player, float force) {
        Vec3 recoil = player.getLookAngle().scale(-force);
        player.push(recoil.x, recoil.y * 0.5f, recoil.z);
        player.hurtMarked = true;
    }

    private static Vec3 reflect(Vec3 v, Vec3 n) {
        return v.subtract(n.scale(2 * v.dot(n)));
    }

    private static Vec3 getHitNormal(HitResult hit) {
        if (hit instanceof BlockHitResult bhr) {
            return switch (bhr.getDirection()) {
                case UP -> new Vec3(0, 1, 0);
                case DOWN -> new Vec3(0, -1, 0);
                case NORTH -> new Vec3(0, 0, -1);
                case SOUTH -> new Vec3(0, 0, 1);
                case WEST -> new Vec3(-1, 0, 0);
                case EAST -> new Vec3(1, 0, 0);
            };
        }
        return new Vec3(0, 1, 0);
    }
}
