package com.weiss.games.snake.wrappers.block;

import com.weiss.games.snake.wrappers.Direction;
import com.weiss.games.snake.wrappers.field.GameField;

public class Block {

	public static final int SIZE = 15;
	
	public final int x, y;
	public BlockType type;
	public Direction direction;
	public Block parent;
	public boolean isAutomaticPath = false;

	public Block(GameField field, int x, int y) {
		this.x = x;
		this.y = y;
		this.type = BlockType.EMPTY;
		this.direction = Direction.NONE;
	}
	
	@Override
	public String toString() {
		return "(" + this.x + ", " + this.y + ")";
	}

}
