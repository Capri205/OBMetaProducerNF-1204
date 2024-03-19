package net.obmc.OBMetaProducer;

import com.mojang.logging.LogUtils;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import org.slf4j.Logger;

@Mod(OBMetaProducer.MODID)

public class OBMetaProducer {

	//public static OBMetaProducer instance = new OBMetaProducer();
	public static final String MODID = "obmetaproducer";

    private static final Logger LOGGER = (Logger) LogUtils.getLogger();

	public static final String OBMFILE = "MetaFile";
	public static final String OBMOPTS = "Options";

    private String metafile;
    private String trackerfile;
    private Boolean obsfucate;
    private Integer range;
    
    public OBMetaProducer( IEventBus modEventBus ) {

        // Register the setup method for modloading
		modEventBus.addListener( this::commonSetup );
	}
	
    public void commonSetup (final FMLCommonSetupEvent event) {

   		metafile = "/overviewer/ob-twilight/markers.json";
   		trackerfile = "/overviewer/serverquery/tracker.dat";
   		obsfucate = false;
   		range = 1000;

   		LOGGER.info("[OBMetaProducer] Metadata output in "+metafile);
   		LOGGER.info("[OBMetaProducer] Player positon masking is set to "+obsfucate);
   		if (obsfucate) {
   			LOGGER.info("[OBMetaProducer] Masking range is "+range+" blocks");
   		}
   		
   		NeoForge.EVENT_BUS.register( new OBTicker( metafile, obsfucate, range));
   		NeoForge.EVENT_BUS.register( new OBListener( trackerfile ) );
    }
}