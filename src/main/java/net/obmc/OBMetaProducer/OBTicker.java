package net.obmc.OBMetaProducer;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.event.TickEvent;

import org.slf4j.Logger;

public class OBTicker {

	Integer tickcount = 0;
	
	String metafile;
	Boolean obsfucate;
	Integer range;

	OBMetaTask task = new OBMetaTask();
	
    private static final Logger LOGGER = LogUtils.getLogger();

	public OBTicker(String mfile, Boolean obsfe, Integer rng) {
		metafile = mfile;
		obsfucate = obsfe;
		range = rng;
	}

	//Called when the server ticks. Usually 20 ticks a second.
	@SubscribeEvent
	public void onServerTick( TickEvent.ServerTickEvent event ) {
		 
		if ( event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END ) {
			tickcount++;
		 	if ( tickcount == 100 ) {
		 		task.run( metafile, obsfucate, range );
		 		tickcount = 0;
		 	}
	 	}
	 }

}