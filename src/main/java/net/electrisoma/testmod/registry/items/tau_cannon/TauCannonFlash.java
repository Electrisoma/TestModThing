package net.electrisoma.testmod.registry.items.tau_cannon;

import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.PointLight;
import foundry.veil.platform.VeilEventPlatform;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class TauCannonFlash {
    private static final int FLASH_DURATION_TICKS = 2;
    private static final int FADE_DURATION_TICKS = 6;

    private static final Map<UUID, TimedLight> activeMuzzleFlashes = new HashMap<>();
    private static final Map<UUID, ChargingLight> chargingLights = new HashMap<>();
    private static final Set<UUID> recentFlashes = new HashSet<>();
    private static int lastTick = -1;

    private static class TimedLight {
        final PointLight light;
        final UUID playerId;
        int age = 0;

        TimedLight(PointLight light, UUID playerId) {
            this.light = light;
            this.playerId = playerId;
        }
    }

    private record ChargingLight(PointLight light, UUID playerId) { }

    public static void init() {
        VeilEventPlatform.INSTANCE.onVeilRenderLevelStage((stage, levelRenderer, bufferSource, matrixStack,
                                                           frustumMatrix, projectionMatrix, renderTick,
                                                           deltaTracker, camera, frustum) -> {

            Minecraft mc = Minecraft.getInstance();
            ClientLevel world = mc.level;
            if (world == null) return;

            int currentTick = (int) world.getGameTime();

            if (currentTick != lastTick) {
                lastTick = currentTick;

                for (TimedLight timed : activeMuzzleFlashes.values())
                    timed.age++;

                recentFlashes.clear();
            }

            Iterator<Map.Entry<UUID, TimedLight>> it = activeMuzzleFlashes.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<UUID, TimedLight> entry = it.next();
                TimedLight timed = entry.getValue();

                Player player = world.getPlayerByUUID(timed.playerId);
                if (player == null) {
                    VeilRenderSystem.renderer().getLightRenderer().removeLight(timed.light);
                    it.remove();
                    continue;
                }

                Vec3 offset = player.getLookAngle().scale(0.5);
                Vec3 pos = player.getEyePosition(1.0f).add(offset);
                timed.light.setPosition(pos.x, pos.y, pos.z);

                float brightness = 0f;
                if (timed.age <= FLASH_DURATION_TICKS) {
                    brightness = 7.5f;
                } else if (timed.age <= FLASH_DURATION_TICKS + FADE_DURATION_TICKS) {
                    float fadeRatio = 1.0f - (timed.age - FLASH_DURATION_TICKS) / (float) FADE_DURATION_TICKS;
                    brightness = 7.5f * fadeRatio;
                } else {
                    VeilRenderSystem.renderer().getLightRenderer().removeLight(timed.light);
                    it.remove();
                    continue;
                }

                timed.light.setBrightness(brightness);
            }

            for (ChargingLight charging : chargingLights.values()) {
                Player player = world.getPlayerByUUID(charging.playerId);
                if (player == null) {
                    VeilRenderSystem.renderer().getLightRenderer().removeLight(charging.light);
                    continue;
                }

                Vec3 offset = player.getLookAngle().scale(0.5);
                Vec3 pos = player.getEyePosition(1.0f).add(offset);
                charging.light.setPosition(pos.x, pos.y, pos.z);
            }
        });
    }

    public static void spawnTestLight() {
        PointLight testLight = new PointLight();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        Vec3 playerPos = mc.player.position();
        testLight.setPosition(playerPos.x, playerPos.y + 5.0, playerPos.z);

        testLight.setColor(0xFFFF0000);
        testLight.setRadius(15.0f);
        testLight.setBrightness(5.0f);

        VeilRenderSystem.renderer().getLightRenderer().addLight(testLight);
    }

    public static void spawnMuzzleFlash(Player player) {
        if (!player.level().isClientSide || player != Minecraft.getInstance().player) return;

        UUID playerId = player.getUUID();

        if (recentFlashes.contains(playerId)) return;
        recentFlashes.add(playerId);

        removeChargingLight(player);

        Vec3 offset = player.getLookAngle().scale(0.5);
        Vec3 pos = player.getEyePosition(1.0f).add(offset);

        PointLight flash = new PointLight();
        flash.setPosition(pos.x, pos.y, pos.z);
        flash.setColor(0xFF33CCFF);
        flash.setRadius(8.0f);
        flash.setBrightness(7.5f);

        VeilRenderSystem.renderer().getLightRenderer().addLight(flash);
        activeMuzzleFlashes.put(playerId, new TimedLight(flash, playerId));
    }

    public static boolean hasChargingLight(Player player) {
        return chargingLights.containsKey(player.getUUID());
    }

    public static void addChargingLight(Player player) {
        UUID playerId = player.getUUID();

        if (chargingLights.containsKey(playerId)) return;

        PointLight light = new PointLight();
        light.setColor(0xFF33CCFF);
        light.setRadius(6.0f);
        light.setBrightness(0.0f);

        ChargingLight charging = new ChargingLight(light, playerId);
        chargingLights.put(playerId, charging);

        VeilRenderSystem.renderer().getLightRenderer().addLight(light);
    }

    public static void updateChargingLight(Player player, int heldTicks) {
        ChargingLight charging = chargingLights.get(player.getUUID());
        if (charging == null) return;

        float chargeRatio = Math.min(heldTicks / 40f, 1.0f);
        float brightness = chargeRatio * 2.0f;
        float radius = net.minecraft.util.Mth.lerp(chargeRatio, 1.0f, 4.0f);

        charging.light.setBrightness(brightness);
        charging.light.setRadius(radius);

        Vec3 offset = player.getLookAngle().scale(0.5);
        Vec3 pos = player.getEyePosition(1.0f).add(offset);
        charging.light.setPosition(pos.x, pos.y, pos.z);
    }

    public static void removeChargingLight(Player player) {
        UUID playerId = player.getUUID();
        ChargingLight charging = chargingLights.remove(playerId);
        if (charging != null)
            VeilRenderSystem.renderer().getLightRenderer().removeLight(charging.light);
    }
}
