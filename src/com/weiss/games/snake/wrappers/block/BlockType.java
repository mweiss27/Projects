package com.weiss.games.snake.wrappers.block;

import java.awt.Color;

public enum BlockType {

	EMPTY(Color.black),
	SNAKE(Color.lightGray),
	FOOD(Color.yellow);
	
	private Color drawColor;
	
	BlockType(Color drawColor) {
		this.drawColor = drawColor;
	}
	
	public Color getColor() {
		return this.drawColor;
	}
	
}
