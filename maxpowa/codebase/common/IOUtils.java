package maxpowa.codebase.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.ISaveHandler;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class IOUtils {

    public static void initFiles() {
        File TukMCFolder = getTukMCFolder();
        if (TukMCFolder.exists())
            return;

        TukMCFolder.mkdirs();
    }

    public static File getWorldDir(World world) {
        try {
            ISaveHandler worldsaver = world.getSaveHandler();
            IChunkLoader loader = worldsaver.getChunkLoader(world.provider);
            File file = ReflectionHelper
                    .<File, AnvilChunkLoader> getPrivateValue(
                            AnvilChunkLoader.class, (AnvilChunkLoader) loader,
                            3);
            return file.getName().contains("DIM") ? file.getParentFile() : file;
        } catch (Exception e) {
            return null;
        }
    }

    public static File createAndGetNBTFile(File f) {
        try {
            CompressedStreamTools.readCompressed(new FileInputStream(f));
        } catch (Exception e) {
            NBTTagCompound cmp = new NBTTagCompound();
            try {
                CompressedStreamTools.writeCompressed(cmp,
                        new FileOutputStream(f));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

        return f;
    }

    public static File createAndGetFile(File f) {
        if (!f.exists()) {
            try {
                f.getParentFile().mkdirs();
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return f;
    }

    public static File getMcDir() {
        return Minecraft.getMinecraft().mcDataDir;
    }

    public static File getTukMCFolder() {
        return new File(Minecraft.getMinecraft().mcDataDir, "tukmc");
    }

    public static File getCacheFile(String mod) {
        return createAndGetNBTFile(createAndGetFile(new File(new File(
                getTukMCFolder(), "/cache"), String.format("%s.dat", mod))));
    }

    public static File getConfigFile(String mod) {
        return createAndGetFile(new File(new File(getTukMCFolder(), "/config"),
                String.format("%s.cfg", mod)));
    }

    public static NBTTagCompound getTagCompoundInFile(File f) {
        try {
            NBTTagCompound cmp = CompressedStreamTools
                    .readCompressed(new FileInputStream(f));
            return cmp;
        } catch (IOException e) {
            NBTTagCompound cmp = new NBTTagCompound();
            try {
                CompressedStreamTools.writeCompressed(cmp,
                        new FileOutputStream(f));
                return getTagCompoundInFile(f);
            } catch (IOException e1) {
                return null;
            }
        }
    }

    public static boolean injectNBTToFile(NBTTagCompound cmp, File f) {
        try {
            CompressedStreamTools.writeCompressed(cmp, new FileOutputStream(f));
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
