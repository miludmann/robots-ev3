package orwell.tank.exception;

import orwell.tank.config.RobotFileBom;

/**
 * Created by Michaël Ludmann on 09/09/16.
 */
public class FileBomException extends Exception {
    public FileBomException(RobotFileBom robotFileBom) {
        super("Robot ini file is incomplete");
    }
}
