package com.fluent.framework.core;
/*@formatter:off */

import com.fluent.framework.admin.core.*;
import com.fluent.framework.events.in.*;
import com.fluent.framework.events.out.*;
import com.fluent.framework.market.core.*;
import com.fluent.framework.persistence.*;
import com.fluent.framework.reference.*;


public final class FluentServices{

    private final FluentConfigManager          cfgManager;
    private final StateManager                 stateManager;
    private final FluentInEventDispatcher      inDispatcher;
    private final ChroniclePersisterService    persister;
    private final FluentOutEventDispatcher     outDispatcher;
    private final MarketDataManager            mdManager;
    private final ReferenceDataManager         refManager;


    public FluentServices( String configFileName ) throws FluentException{
        this.cfgManager     = new FluentConfigManager( configFileName );

        this.persister      = new ChroniclePersisterService( cfgManager );
        this.inDispatcher   = new FluentInEventDispatcher( cfgManager );
        this.outDispatcher  = new FluentOutEventDispatcher( cfgManager );

        this.refManager     = new ReferenceDataManager( cfgManager );
        this.stateManager   = new StateManager( cfgManager, inDispatcher );
        this.mdManager      = new MarketDataManager( cfgManager, inDispatcher );
        
    }


    public final FluentConfigManager getCfgManager( ){
        return cfgManager;
    }


    public final StateManager getStateManager( ){
        return stateManager;
    }
    

    public final FluentInEventDispatcher getInDispatcher( ){
        return inDispatcher;
    }
    

    public final ChroniclePersisterService getPersister( ){
        return persister;
    }
    

    public final FluentOutEventDispatcher getOutDispatcher( ){
        return outDispatcher;
    }
        
    
    public final ReferenceDataManager getReferenceManager( ){
        return refManager;
    }
    

    public final MarketDataManager getMdManager( ){
        return mdManager;
    }

    // private final OrderManager orderManager;


}
