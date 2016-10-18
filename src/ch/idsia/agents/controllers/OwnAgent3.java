package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;
import java.util.Random;

public class OwnAgent3 extends BasicMarioAIAgent implements Agent{
	int trueJumpCounter = 0;
	int trueSpeedCounter = 0;

	public OwnAgent3()
	{
	    super("OwnAgent3");
	    reset();
	}

	public void reset()
	{
	    action = new boolean[Environment.numberOfKeys];
	    action[Mario.KEY_RIGHT] = true;
	}

	public boolean[] getAction()
	{
		int row = marioEgoRow;
		int col = marioEgoCol;
		if(!(isObstacle(row + 1, col + 1) || 
			   isObstacle(row + 2, col + 1) ||
				 isObstacle(row + 3, col + 1))){
			action[Mario.KEY_JUMP] = isMarioAbleToJump || ! isMarioOnGround;
		}
		if(isObstacle(row, col + 1) || 
				getEnemiesCellValue(row, col + 2) != Sprite.KIND_NONE 
				|| getEnemiesCellValue(row, col + 1) != Sprite.KIND_NONE){
			action[Mario.KEY_JUMP] = isMarioAbleToJump || ! isMarioOnGround;
		}
		if(getEnemiesCellValue( row, col + 1) != 0 || 
				getEnemiesCellValue( row, col + 2) != 0){
			action[Mario.KEY_RIGHT] = false;
		}else{
			action[Mario.KEY_RIGHT] = true;
		}
		if(getEnemiesCellValue( row + 1, col + 1) != 0 || 
				getEnemiesCellValue( row + 1, col + 2) != 0){
			action[Mario.KEY_JUMP] = isMarioAbleToJump;
		}
		if(	getEnemiesCellValue( row - 1, col + 1) == Sprite.KIND_ENEMY_FLOWER ||
				getEnemiesCellValue( row, col + 1) == Sprite.KIND_ENEMY_FLOWER ||
				     getEnemiesCellValue( row + 1, col + 2) == Sprite.KIND_ENEMY_FLOWER){
			action[Mario.KEY_SPEED] = true;
			action[Mario.KEY_JUMP] = isMarioAbleToJump || ! isMarioOnGround;
		}else{
			action[Mario.KEY_SPEED] = false;
		}
		
		return action;
	}

	public boolean isObstacle(int r, int c){
		return getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.BRICK
				|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH
				|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.FLOWER_POT_OR_CANNON
				|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.LADDER;
	}
}
