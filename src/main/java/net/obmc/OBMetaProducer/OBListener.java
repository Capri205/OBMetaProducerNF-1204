package net.obmc.OBMetaProducer;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

public class OBListener {
	
    private static final Logger LOGGER = (Logger) LogUtils.getLogger();
	
	String trackerfile;
	String servername = null;

    public OBListener( String trackerfile ) {
    	this.trackerfile = trackerfile;
    }

    @SubscribeEvent
    public void onPlayerJoin( PlayerLoggedInEvent event ) {
    	
    	if ( this.servername == null ) {
    		String motd = ServerLifecycleHooks.getCurrentServer().getMotd().toLowerCase();
    		if ( motd.contains("forest")) {
    			this.servername = motd.replace( "forest", "" );
    		}
    	}

    	String switchMsg = "ServerSwitchEvent#" + event.getEntity().getDisplayName().getString() + "#" + this.servername + "#" + getTimestamp();
    	logTrackerMsg( switchMsg );
    }

    @SubscribeEvent
    public void onPlayerJoin( PlayerLoggedOutEvent event ) {
    	
    	if ( this.servername == null ) {
    		String motd = ServerLifecycleHooks.getCurrentServer().getMotd().toLowerCase();
    		if ( motd.contains("forest")) {
    			this.servername = motd.replace( "forest", "" );
    		}
    	}

    	String switchMsg = "PlayerDisconnectEvent#" + event.getEntity().getDisplayName().getString() + "#" + this.servername + "#" + getTimestamp();
    	logTrackerMsg( switchMsg );
    }

    private void logTrackerMsg( String msg ) {
    	
    	try (RandomAccessFile stream = new RandomAccessFile( this.trackerfile, "rw" );
    		     FileChannel channel = stream.getChannel()) {

    		    // Use tryLock() or lock() to block until the lock is acquired
    		    FileLock lock = channel.lock();
    		    try {
    		        // Move to the end of the file to append data
    		        channel.position(channel.size());
    		        // Write data
    		        channel.write( ByteBuffer.wrap( ( msg + "\n" ).getBytes() ) );
    		    } finally {
    		        lock.release();
    		    }
    		} catch ( IOException e ) {
    		    e.printStackTrace();
    		}		
	}

    private String getTimestamp() {
    	return new SimpleDateFormat( "MM/dd HH:mm:ss.ms" ).format( new Date() );

    }
}
