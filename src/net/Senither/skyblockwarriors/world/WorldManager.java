package net.Senither.skyblockwarriors.world;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import net.Senither.skyblockwarriors.SkyblockWarriors;

public class WorldManager
{

    private SkyblockWarriors _plugin;

    public WorldManager(SkyblockWarriors plugin)
    {
        _plugin = plugin;
    }

    public void resetWorld()
    {
        boolean errors = false;
        File backupDir = new File(_plugin.getDataFolder(), "backups");
        for (File source : backupDir.listFiles()) {
            if (source.isDirectory()) {
                File target = new File(_plugin.getServer().getWorldContainer(), source.getName());
                if ((target.exists()) && (target.isDirectory()) && (!delete(target))) {
                    _plugin.chatManager.LogSevere("Failed to reset world \"" + source.getName() + "\" - could not delete old world folder.");
                    errors = true;
                } else {
                    try {
                        copyDir(source, target);
                    } catch (IOException e) {
                        e.printStackTrace();
                        _plugin.chatManager.LogSevere("Failed to reset world \"" + source.getName() + "\" - could not import the world from backup.");
                        errors = true;
                    }
                    _plugin.chatManager.LogInfo("Import of world \"" + source.getName() + "\" " + (errors ? "failed!" : "succeeded!"));
                    errors = false;
                }
            }
        }

        if (errors) {
            _plugin.chatManager.LogInfo("Failed to rollback the world, retrying by restarting the server..");
            _plugin.getServer().shutdown();
        }
    }

    private boolean delete(File file)
    {
        if (file.isDirectory()) {
            for (File subfile : file.listFiles()) {
                if (!delete(subfile)) {
                    return false;
                }
            }
        }
        if (!file.delete()) {
            return false;
        }
        return true;
    }

    private static void copyDir(File source, File target) throws IOException
    {
        if (source.isDirectory()) {
            if (!target.exists()) {
                target.mkdir();
            }
            String[] files = source.list();
            for (String file : files) {
                File srcFile = new File(source, file);
                File destFile = new File(target, file);
                copyDir(srcFile, destFile);
            }
        } else {
            InputStream in = new FileInputStream(source);
            OutputStream out = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) > 0) {
                out.write(buffer, 0, length);
            }
            in.close();
            out.close();
        }
    }
}
