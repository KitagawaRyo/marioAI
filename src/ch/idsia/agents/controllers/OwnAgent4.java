package ch.idsia.agents.controllers;

import ch.idsia.agents.Agent;
import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;
//import ch.idsia.benchmark.mario.engine.LevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.engine.sprites.Sprite;
import ch.idsia.benchmark.mario.environments.Environment;
import java.util.Random;

public class OwnAgent4 extends BasicMarioAIAgent implements Agent{
	int trueJumpCounter = 0;
	int trueSpeedCounter = 0;
	
	
	int deadEnd = 0;

	public OwnAgent4()
	{
	    super("OwnAgent4");
	    reset();
	}

	public void reset()
	{
	    action = new boolean[Environment.numberOfKeys];
	    action[Mario.KEY_RIGHT] = true;
	}

	public boolean[] getAction()
	{
		int y = marioEgoRow;
		int x = marioEgoCol;
		if(!(isObstacle(y + 1, x + 1) || 
			   isObstacle(y + 2, x + 1) ||
				 isObstacle(y + 3, x + 1) ||
				   isObstacle(y + 4, x + 1))){
			action[Mario.KEY_JUMP] = isMarioAbleToJump || ! isMarioOnGround;
		}
		if(isObstacle(y, x + 1) || 
				getEnemiesCellValue(y, x + 2) != Sprite.KIND_NONE 
				|| getEnemiesCellValue(y, x + 1) != Sprite.KIND_NONE){
			action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
		}
		if(isObstacle(y, x+1) && !isObstacle(y, x+3)){
			action[Mario.KEY_JUMP] = isMarioAbleToJump;
		}
		if(getEnemiesCellValue( y, x + 1) != 0 || 
				getEnemiesCellValue( y, x + 2) != 0){
			action[Mario.KEY_RIGHT] = false;
		}else{
			action[Mario.KEY_RIGHT] = true;
		}
		if(getEnemiesCellValue( y + 1, x + 1) != 0 || 
				getEnemiesCellValue( y + 1, x + 2) != 0){
			action[Mario.KEY_JUMP] = isMarioAbleToJump;
		}
		if(isObstacle(y - 2, x + 3)){
			action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
			action[Mario.KEY_RIGHT] = isMarioOnGround;
		}
		detectDeadEnd(y, x);
		if(deadEnd == 1){
			action[Mario.KEY_LEFT] = true;
		}
		System.out.println(distancePassedCells);
		
		return action;
	}
	
	public void detectDeadEnd(int y,int x){
		int l = 0;
		for(int i = 0; i < 5; ++i){
			if(getReceptiveFieldCellValue(y - i, x + 1) != 0){
				l++;
			}
		}
		if(l >= 4){
			deadEnd = 1;
		}else{
			deadEnd = 0;
		}
	}

	public boolean isObstacle(int r, int c){
		return getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.BRICK
				|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.BORDER_CANNOT_PASS_THROUGH
				|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.FLOWER_POT_OR_CANNON
				|| getReceptiveFieldCellValue(r, c)==GeneralizerLevelScene.LADDER;
	}

}
