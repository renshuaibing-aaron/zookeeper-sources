
package org.apache.zookeeper;

import java.util.Set;

/**
 * 接口类型，表示客户端的Watcher管理者，其定义了materialized方法，需子类实现
 */
public interface ClientWatchManager {
    /**
     * Return a set of watchers that should be notified of the event. The
     * manager must not notify the watcher(s), however it will update it's
     * internal structure as if the watches had triggered. The intent being
     * that the callee is now responsible for notifying the watchers of the
     * event, possibly at some later time.
     *
     * @param state event state
     * @param type event type
     * @param path event path
     * @return may be empty set but must not be null
     */
    public Set<Watcher> materialize(Watcher.Event.KeeperState state,
        Watcher.Event.EventType type, String path);
}
