package com.fluent.framework.persistence;

import java.util.*;

import com.fluent.framework.core.*;
import com.fluent.framework.events.*;


public interface PersisterService<E extends FluentEvent> extends FluentLifecycle{

    public List<E> retrieveAll( );

}
