package maxpowa.codebase.client;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Set;

import maxpowa.codebase.common.CommonUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.multiplayer.NetClientHandler;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class ClientUtils {

    public static EntityPlayer getClientPlayer() {
        return CommonUtils.getMc().thePlayer;
    }

    public static boolean isConnectedToServer() {
        return !CommonUtils.getMc().isSingleplayer();
    }

    public static ServerData getServerData() {
        Minecraft mc = Minecraft.getMinecraft();
        ServerData serverData = null;
        for (Field field : Minecraft.class.getDeclaredFields()) {
            if (field.getType() == ServerData.class) {
                field.setAccessible(true);
                try {
                    serverData = (ServerData)field.get(mc);
                } catch (Exception e) {
                    System.out.println("[TukMC] Unable to find server information (" + e.getCause().toString()+")");
                }
            }
        }

        return serverData;
    }

    public static String getFPS() {
        String debug = CommonUtils.getMc().debug;
        String fps = debug.split("[( f]")[0];
        return fps;
    }

    public static String getPing() {
        EntityPlayer player = getClientPlayer();
        String username = player.username;
        NetClientHandler clientHandler = ((EntityClientPlayerMP) player).sendQueue;
        @SuppressWarnings("unchecked")
        List<GuiPlayerInfo> playerList = clientHandler.playerInfoList;
        int time = -1;

        for (GuiPlayerInfo p : playerList) {
            if (!p.name.equals(username)) {
                continue;
            }

            time = p.responseTime;
            if (time >= 0)
                return "" + time;
        }
        return "N/A";
    }

    public static Set<Entity> getWorldEntityList(WorldClient world) {
        return ReflectionHelper.<Set<Entity>, WorldClient> getPrivateValue(
                WorldClient.class, world, 3);
    }

    @SuppressWarnings("unchecked")
    public static Entity getEntityLookedAt(float renderTick) {
        Entity foundEntity = null;
        Minecraft mc = CommonUtils.getMc();

        if (mc.renderViewEntity != null && mc.theWorld != null) {
            final double finalDistance = 12;
            double distance = finalDistance;
            mc.objectMouseOver = mc.renderViewEntity.rayTrace(finalDistance,
                    renderTick);
            Vec3 positionVector = mc.renderViewEntity.getPosition(renderTick);

            if (mc.objectMouseOver != null) {
                distance = mc.objectMouseOver.hitVec.distanceTo(positionVector);
            }

            Vec3 lookVector = mc.renderViewEntity.getLook(renderTick);
            Vec3 reachVector = positionVector.addVector(lookVector.xCoord
                    * finalDistance, lookVector.yCoord * finalDistance,
                    lookVector.zCoord * finalDistance);
            Entity lookedEntity = null;
            List<Entity> entitiesInBoundingBox = mc.theWorld
                    .getEntitiesWithinAABBExcludingEntity(
                            mc.renderViewEntity,
                            mc.renderViewEntity.boundingBox.addCoord(
                                    lookVector.xCoord * finalDistance,
                                    lookVector.yCoord * finalDistance,
                                    lookVector.zCoord * finalDistance).expand(
                                            1.0F, 1.0F, 1.0F));
            double minDistance = distance;

            for (Entity entity : entitiesInBoundingBox)
                if (entity.canBeCollidedWith()) {
                    float collisionBorderSize = entity.getCollisionBorderSize();
                    AxisAlignedBB hitbox = entity.boundingBox.expand(
                            collisionBorderSize, collisionBorderSize,
                            collisionBorderSize);
                    MovingObjectPosition interceptPosition = hitbox
                            .calculateIntercept(positionVector, reachVector);

                    if (hitbox.isVecInside(positionVector)) {
                        if (0.0D < minDistance || minDistance == 0.0D) {
                            lookedEntity = entity;
                            minDistance = 0.0D;
                        }
                    } else if (interceptPosition != null) {
                        double distanceToEntity = positionVector
                                .distanceTo(interceptPosition.hitVec);

                        if (distanceToEntity < minDistance
                                || minDistance == 0.0D) {
                            lookedEntity = entity;
                            minDistance = distanceToEntity;
                        }
                    }
                }

            if (lookedEntity != null
                    && (minDistance < distance || mc.objectMouseOver == null)) {
                foundEntity = lookedEntity;
            }
        }

        return foundEntity;
    }

}
