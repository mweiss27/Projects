package com.weiss.remote_connect.packets.mouse;

import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;

public class MouseDraggedPacket extends MouseEventPacket {

	public MouseDraggedPacket(final int x, final int y) {
		super(MouseEventType.MOUSE_DRAGGED, x, y);
	}

	@Override
	public void handleEvent(Robot robot) {
		robot.mousePress(InputEvent.getMaskForButton(MouseEvent.BUTTON1));
		robot.mouseMove(this.x, this.y);
		robot.mouseRelease(InputEvent.getMaskForButton(MouseEvent.BUTTON1));
	}
	
}
