package orwell.tank.actions;

import lejos.hardware.Sound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import orwell.tank.RemoteRobot;

import java.util.List;

/**
 * Created by Michaël Ludmann on 10/07/16.
 */
public class Move implements IInputAction {
    private final static Logger logback = LoggerFactory.getLogger(Move.class);
    private boolean hasMove = false;
    private double leftMove;
    private double rightMove;

    public Move(List<String> moveInput) {
        logback.debug("Move: " + moveInput);

        if (hasTwoInput(moveInput)) {
            parseInput(moveInput);
        }
    }

    private boolean hasTwoInput(List<String> moveInput) {
        return moveInput != null && moveInput.size() == 2;
    }

    private void parseInput(List<String> moveInput) {
        String leftMoveString = moveInput.get(0);
        String rightMoveString = moveInput.get(1);
        try {
            leftMove = Double.parseDouble(leftMoveString);
            rightMove = Double.parseDouble(rightMoveString);
            hasMove = true;
        } catch (NumberFormatException e) {
            logback.warn("Cannot parse Move input into double: " + moveInput);
        }
    }

    public void stop(RemoteRobot remoteRobot) {
        remoteRobot.getTracks().stop();
    }

    public boolean hasMove() {
        return hasMove;
    }

    @Override
    public void performAction(RemoteRobot remoteRobot) {
        if (hasMove()) {
            remoteRobot.getTracks().setPower(leftMove, rightMove);
        }
    }
}
