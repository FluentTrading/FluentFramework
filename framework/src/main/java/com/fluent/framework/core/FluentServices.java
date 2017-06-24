package com.fluent.framework.core;
/*@formatter:off */

import com.fluent.framework.admin.core.*;
import com.fluent.framework.events.in.*;
import com.fluent.framework.events.out.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.reference.core.*;


public final class FluentServices{

    private final FluentConfiguration          cfgManager;
    private final StateManager                 stateManager;
    private final FluentInEventDispatcher      inDispatcher;
   // private final ChroniclePersisterService    persister;
    private final FluentOutEventDispatcher     outDispatcher;
    private final MarketDataManager            mdManager;
    private final RefDataManager         refManager;


    /*
    public FluentServices( String configFileName ) throws FluentException{
        this.cfgManager     = new FluentConfiguration( configFileName );

      //  this.persister      = new ChroniclePersisterService( cfgManager );
        this.inDispatcher   = new FluentInEventDispatcher( cfgManager );
        this.outDispatcher  = new FluentOutEventDispatcher( cfgManager );

        this.refManager     = new ReferenceDataManager( cfgManager );
        this.stateManager   = new StateManager( cfgManager, inDispatcher );
        this.mdManager      = new MarketDataManager( cfgManager, inDispatcher );
        
    }
    */

    public FluentServices( FluentConfiguration cfgManager, 
                            FluentInEventDispatcher inDispatcher, FluentOutEventDispatcher outDispatcher ,
                            RefDataManager refManager, StateManager stateManager, MarketDataManager mdManager ){

        this.cfgManager     = cfgManager;
      //  this.persister      = new ChroniclePersisterService( cfgManager );
        this.inDispatcher   = inDispatcher;
        this.outDispatcher  = outDispatcher;
        this.refManager     = refManager;
        this.stateManager   = stateManager;
        this.mdManager      = mdManager;
        
    }
    
    public final FluentConfiguration getCfgManager( ){
        return cfgManager;
    }


    public final StateManager getStateManager( ){
        return stateManager;
    }
    

    public final FluentInEventDispatcher getInDispatcher( ){
        return inDispatcher;
    }
    

//    public final ChroniclePersisterService getPersister( ){
//        return persister;
//    }
    

    public final FluentOutEventDispatcher getOutDispatcher( ){
        return outDispatcher;
    }
        
    
    public final RefDataManager getReferenceManager( ){
        return refManager;
    }
    

    public final MarketDataManager getMdManager( ){
        return mdManager;
    }

    // private final OrderManager orderManager;


}
