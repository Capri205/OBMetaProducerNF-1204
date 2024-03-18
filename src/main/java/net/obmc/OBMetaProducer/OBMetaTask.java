package net.obmc.OBMetaProducer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Random;
import com.mojang.logging.LogUtils;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;

import net.neoforged.neoforge.server.ServerLifecycleHooks;

import org.slf4j.Logger;


public class OBMetaTask {

	private PrintWriter pw = null;

    private static final Logger Log = LogUtils.getLogger();
	
	public void run(String metafile, Boolean obsfucate, Integer range) {
		
		// open the meta file for writing
		File mfile = new File(metafile);
		FileWriter writer;
		try {
			writer = new FileWriter(mfile, false);
			pw = new PrintWriter(writer);
		} catch(IOException e) {
			Log.atError().log( "Failed to open metafile for writing (" + metafile + ")" );
			e.printStackTrace();
			return;
		}
    	
		// get players on server and write out dimension and location data
		List<ServerPlayer> playerlist = ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
		if (playerlist.size() != 0) {
			pw.print("["); int i=0;
			for (ServerPlayer player : playerlist) {
				
				//get player location
				int x = (int) player.getX(); int y = (int) player.getY(); int z = (int) player.getZ();

				// add any required player position obsfucation
				if (obsfucate) {
					Random rand = new Random();
					int xob = rand.nextInt(range); if (xob%2 == 0) {x+=xob;}else{x-=xob;} 
					int zob = rand.nextInt(range); if (zob%2 == 0) {z+=zob;}else{z-=zob;}
					int yob = rand.nextInt(40); if(yob%2 == 0) {y+=yob;}else{y-=yob;} if (y > 256) y=255; if (y<0) y=0; 
				}

				ResourceKey<net.minecraft.world.level.Level> rkey = player.level().dimension();
				String world = rkey.location().getNamespace();
				String dimension = rkey.location().getPath();

				pw.print(
					"{"+
					"\"sys\":\"forge\"," +
					"\"msg\":\""+player.getDisplayName().getString() + "\"," +
				    "\"uuid\":\"" + player.getStringUUID() + "\"," +
					"\"world\":\"" + world + "\"," +
					"\"dimension\":\"" + dimension + "\"," +
					"\"x\":\""+x+"\"," +
					"\"y\":\""+y+"\"," +
					"\"z\":\""+z+"\"" +
					"}"
				);
				if (i < playerlist.size()-1 ) {
					pw.print(",");
				}				
				i++;
			}
			pw.print("]");
		} else {
			pw.print("[]");
		}
   		pw.close();
	}
}