package db;

import java.util.List;

import target.Target;

/**
 * Created by ruslanthakohov on 06/11/15.
 */
public interface FetchTargetsTaskClient {
    void targetsAreReady(List<Target> targets);
}
