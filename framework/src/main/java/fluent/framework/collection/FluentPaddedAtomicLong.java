package fluent.framework.collection;

import static fluent.framework.util.Constants.*;

import java.util.concurrent.atomic.*;


public class FluentPaddedAtomicLong extends AtomicLong{

    public volatile long      p1, p2, p3, p4, p5, p6 = 7L;

    private static final long serialVersionUID = ONE;


    public FluentPaddedAtomicLong( ){
        this( ZERO );
    }

    public FluentPaddedAtomicLong( final long initialValue ){
        super( initialValue );
    }


    public final long foilJVMOptimization( ) {
        return p1 + p2 + p3 + p4 + p5 + p6;
    }


}
