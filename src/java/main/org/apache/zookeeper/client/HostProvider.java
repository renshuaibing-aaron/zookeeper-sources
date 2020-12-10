package org.apache.zookeeper.client;

import org.apache.yetus.audience.InterfaceAudience;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;

/**
 * A set of hosts a ZooKeeper client should connect to.
 * 
 * Classes implementing this interface must guarantee the following:
 * 
 * * Every call to next() returns an InetSocketAddress. So the iterator never
 * ends.
 * 
 * * The size() of a HostProvider may never be zero.
 * 
 * A HostProvider must return resolved InetSocketAddress instances on next(),
 * but it's up to the HostProvider, when it wants to do the resolving.
 * 
 * Different HostProvider could be imagined:
 * 
 * * A HostProvider that loads the list of Hosts from an URL or from DNS 
 * * A HostProvider that re-resolves the InetSocketAddress after a timeout. 
 * * A HostProvider that prefers nearby hosts.
 */
@InterfaceAudience.Public
public interface HostProvider {
    public int size();

    /**
     * The next host to try to connect to.
     *
     * For a spinDelay of 0 there should be no wait.
     *
     * @param spinDelay Milliseconds to wait if all hosts have been tried once.
     * @return The next host to try to connect to with resolved address. If the host is not resolvable, the unresolved
     * address will be returned.
     */
    public InetSocketAddress next(long spinDelay);

    /**
     * Notify the HostProvider of a successful connection.
     * 
     * The HostProvider may use this notification to reset it's inner state.
     */
    public void onConnected();
}
