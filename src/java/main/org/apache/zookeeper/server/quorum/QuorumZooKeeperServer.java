package org.apache.zookeeper.server.quorum;

import java.io.PrintWriter;

import org.apache.zookeeper.server.ZKDatabase;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.persistence.FileTxnSnapLog;

/**
 * Abstract base class for all ZooKeeperServers that participate in
 * a quorum.
 */
public abstract class QuorumZooKeeperServer extends ZooKeeperServer {
    protected final QuorumPeer self;

    protected QuorumZooKeeperServer(FileTxnSnapLog logFactory, int tickTime,
            int minSessionTimeout, int maxSessionTimeout,
            DataTreeBuilder treeBuilder, ZKDatabase zkDb, QuorumPeer self)
    {
        super(logFactory, tickTime, minSessionTimeout, maxSessionTimeout,
                treeBuilder, zkDb);
        this.self = self;
    }

    @Override
    public void dumpConf(PrintWriter pwriter) {
        super.dumpConf(pwriter);

        pwriter.print("initLimit=");
        pwriter.println(self.getInitLimit());
        pwriter.print("syncLimit=");
        pwriter.println(self.getSyncLimit());
        pwriter.print("electionAlg=");
        pwriter.println(self.getElectionType());
        pwriter.print("electionPort=");
        pwriter.println(self.quorumPeers.get(self.getId()).electionAddr
                .getPort());
        pwriter.print("quorumPort=");
        pwriter.println(self.quorumPeers.get(self.getId()).addr.getPort());
        pwriter.print("peerType=");
        pwriter.println(self.getLearnerType().ordinal());
    }

    @Override
    protected void setState(State state) {
        this.state = state;
    }
}
