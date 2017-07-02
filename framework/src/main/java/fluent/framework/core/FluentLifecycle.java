package fluent.framework.core;
/*@formatter:off */

public interface FluentLifecycle{

    public String name( );
    public void start( ) throws Exception;
    public void stop( ) throws Exception;

}
