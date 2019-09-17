package com.fluent.framework.service;

import com.fluent.framework.core.*;

/*@formatter:off */

public interface FluentService extends FluentLifecycle{

    public boolean isRunning( );
    public FluentServiceType getType();
    
    /*
    private final FluentConfiguration cfgManager;
    private final StateManager        stateManager;
    private final FluentInDispatcher  inDispatcher;
   // private final ChroniclePersisterService    persister;
    private final FluentOutDispatcher outDispatcher;
    private final MarketDataManager   mdManager;
    private final OrderManager        orderManager;
    private final RefDataManager      refManager;


    public FluentServices(  FluentConfiguration config, FluentInDispatcher in, FluentOutDispatcher out,
                            StateManager state, RefDataManager ref, OrderManager order,
                            MarketDataManager mdManager ){


        //  this.persister      = new ChroniclePersisterService( cfgManager );
        this.cfgManager     = config;
        this.inDispatcher   = in;
        this.outDispatcher  = out;
        this.stateManager   = state;
        this.refManager     = ref;
        this.orderManager   = order;
        this.mdManager      = mdManager;

    }

    public final FluentConfiguration getCfgManager( ){
        return cfgManager;
    }


    public final StateManager getStateManager( ){
        return stateManager;
    }
    

    public final FluentInDispatcher getInDispatcher( ){
        return inDispatcher;
    }
    

    public final ChroniclePersisterService getPersister( ){
        return persister;
    }
    

    public final FluentOutDispatcher getOutDispatcher( ){
        return outDispatcher;
    }
        
    
    public final RefDataManager getReferenceManager( ){
        return refManager;
    }
    

    public final MarketDataManager getMdManager( ){
        return mdManager;
    }

    public final OrderManager getOrderManager( ){
        return orderManager;
    }

    */
}
