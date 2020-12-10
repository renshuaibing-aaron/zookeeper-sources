package org.apache.zookeeper.server.quorum;

import java.io.IOException;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.zookeeper.KeeperException.Code;
import org.apache.zookeeper.ZooDefs.OpCode;
import org.apache.zookeeper.proto.ReplyHeader;
import org.apache.zookeeper.server.Request;
import org.apache.zookeeper.server.RequestProcessor;
import org.apache.zookeeper.server.ZooKeeperCriticalThread;
import org.apache.zookeeper.server.ZooKeeperServer;
import org.apache.zookeeper.server.ZooTrace;
import org.apache.zookeeper.server.quorum.Leader.XidRolloverException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 是ReadOnlyZooKeeperServer请求处理链的第一个处理器，将只读请求传递给下个处理器，抛弃改变状态的请求。
 * This processor is at the beginning of the ReadOnlyZooKeeperServer's
 * processors chain. All it does is, it passes read-only operations (e.g.
 * OpCode.getData, OpCode.exists) through to the next processor, but drops
 * state-changing operations (e.g. OpCode.create, OpCode.setData).
 */
public class ReadOnlyRequestProcessor extends ZooKeeperCriticalThread implements RequestProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(ReadOnlyRequestProcessor.class);

    private LinkedBlockingQueue<Request> queuedRequests = new LinkedBlockingQueue<Request>();

    private boolean finished = false;

    private RequestProcessor nextProcessor;

    private ZooKeeperServer zks;

    public ReadOnlyRequestProcessor(ZooKeeperServer zks,
            RequestProcessor nextProcessor) {
        super("ReadOnlyRequestProcessor:" + zks.getServerId(), zks
                .getZooKeeperServerListener());
        this.zks = zks;
        this.nextProcessor = nextProcessor;
    }

    public void run() {
        try {
            while (!finished) {
                Request request = queuedRequests.take();

                // log request
                long traceMask = ZooTrace.CLIENT_REQUEST_TRACE_MASK;
                if (request.type == OpCode.ping) {
                    traceMask = ZooTrace.CLIENT_PING_TRACE_MASK;
                }
                if (LOG.isTraceEnabled()) {
                    ZooTrace.logRequest(LOG, traceMask, 'R', request, "");
                }
                if (Request.requestOfDeath == request) {
                    break;
                }

                // filter read requests
                switch (request.type) {
                case OpCode.sync:
                case OpCode.create:
                case OpCode.delete:
                case OpCode.setData:
                case OpCode.setACL:
                case OpCode.multi:
                case OpCode.check:
                    ReplyHeader hdr = new ReplyHeader(request.cxid, zks.getZKDatabase()
                            .getDataTreeLastProcessedZxid(), Code.NOTREADONLY.intValue());
                    try {
                        request.cnxn.sendResponse(hdr, null, null);
                    } catch (IOException e) {
                        LOG.error("IO exception while sending response", e);
                    }
                    continue;
                }

                // proceed to the next processor
                if (nextProcessor != null) {
                    nextProcessor.processRequest(request);
                }
            }
        } catch (RequestProcessorException e) {
            if (e.getCause() instanceof XidRolloverException) {
                LOG.info(e.getCause().getMessage());
            }
            handleException(this.getName(), e);
        } catch (Exception e) {
            handleException(this.getName(), e);
        }
        LOG.info("ReadOnlyRequestProcessor exited loop!");
    }

    @Override
    public void processRequest(Request request) {
        if (!finished) {
            queuedRequests.add(request);
        }
    }

    @Override
    public void shutdown() {
        finished = true;
        queuedRequests.clear();
        queuedRequests.add(Request.requestOfDeath);
        nextProcessor.shutdown();
    }

}
