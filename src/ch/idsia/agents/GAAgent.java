package ch.idsia.agents;

import java.util.Random;

import ch.idsia.agents.controllers.BasicMarioAIAgent;
import ch.idsia.benchmark.mario.engine.GeneralizerLevelScene;
import ch.idsia.benchmark.mario.engine.sprites.Mario;
import ch.idsia.benchmark.mario.environments.Environment;
import ch.idsia.evolution.Evolvable;

/*
 * シフト演算子 x << y
 * xをyビットだけ左シフトする。空いた下位ビット(右側)には0を挿入。
 */
public class GAAgent extends BasicMarioAIAgent
implements Agent,Evolvable,Comparable,Cloneable{
	
	boolean height = false;
	boolean height2 = false;
	int heightValue = 0;
	boolean heightValue2 = false;
	boolean heightValue3 = false;
	int deadEnd = 0;
	static String name = "GAAgent";

	/* 遺伝子情報 */
	public byte[] gene;

	/* 各個体の評価値保存用変数 */
	public int fitness;

	/* 環境から取得する入力数 */
	public int inputNum = 16;

	/* 乱数用変数 r */
	Random r = new Random();

	/* コンストラクタ */
	public GAAgent(){

		super(name);

		/* 16ビットの入力なので，65536(=2^16)個用意する */
		gene = new byte[(1 << inputNum)];

		/* 出力は32(=2^5)パターン */
		int num = 1 << (Environment.numberOfKeys -1);

		int random;
		int flag = 1;

		/* geneの初期値は乱数(0から31)で取得 */
		for(int i=0; i<gene.length; i++){
			if(flag == 1){
			switch(random = r.nextInt(8)){
				case 0:
					gene[i] = 0; break;
				case 1:
					gene[i] = 2; break;
				case 2:
					gene[i] = 8; break;
				case 3:
					gene[i] = 10; break;
				case 4:
					gene[i] = 16; break;
				case 5:
					gene[i] = 18; break;
				case 6:
					gene[i] = 24; break;
				case 7:
					gene[i] = 26; break;

			}
			}else{
				gene[i]=(byte)r.nextInt(num);
			}


//			gene[i] = (byte)r.nextInt(17);
		}

		/* 評価値を0で初期化 */
		fitness = 0;


	}

	/* compfit()追加記述 */

	int distance;

	public void setFitness(int fitness){
		this.fitness = fitness;
	}

	public int getFitness(){
		return fitness;
	}

	/* 降順にソート */
	public int compareTo(Object obj){
	   	GAAgent otherUser = (GAAgent) obj;
    	return -(this.fitness - otherUser.getFitness());
	}

	/* compFit()追加記述ここまで */


	public boolean[] getAction(){
		int y = marioEgoRow;
		int x = marioEgoCol;
		int dp = distancePassedPhys;
		int dc = distancePassedCells;

		int input = 0;

		/* 環境情報から input を決定
		 * 上位ビットから周辺近傍の状態を格納していく
		 */
		detectDeadEnd(y, x);
		
//		input += deadEnd * (1 << 16);
		/* enemies情報(上位7桁) */
		input += probe(2,2,enemies) * (1 << 15); //probe * 2^15
		input += probe(0 ,2,enemies) * (1 << 14);
		input += probe(1 ,2,enemies) * (1 << 13);
		input += probe(2,0 ,enemies) * (1 << 12);
		input += probe(1 ,0 ,enemies) * (1 << 11);
		input += probe(2,1 ,enemies) * (1 << 10);
		input += probe(1 ,1 ,enemies) * (1 <<  9);

		/* levelScene情報 */
		input += probe(-1,-1,levelScene) * (1 << 8);
		input += probe(0 ,2,levelScene) * (1 << 7);
		input += probe(1 ,2,levelScene) * (1 << 6);
		input += probe(2,0 ,levelScene) * (1 << 5);
		input += probe(1 ,0 ,levelScene) * (1 << 4);
		input += probe(2,1 ,levelScene) * (1 << 3);
		input += probe(1 ,1 ,levelScene) * (1 << 2);

		input += (isMarioOnGround ? 1: 0) * (1 << 1);
		input += (isMarioAbleToJump ? 1: 0) * (1 << 0);

//		System.out.println("enemies : "+probe(1,0,enemies));

		/* input から output(act)を決定する */
		int act = gene[input];	//遺伝子のinput番目の数値を読み取る
		for(int i=0; i<Environment.numberOfKeys; i++){
			action[i] = (act %2 == 1);	//2で割り切れるならtrue
			act /= 2;
		}
		//if(dp <= 1500){
		    action[Mario.KEY_RIGHT] = true;
			action[Mario.KEY_SPEED] = true;
		//}
		if(dc >= 100 && dc <= 101 && isObstacle(y - 2, x + 1)){
			action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
		}
		if(dc == 107){
			if(isObstacle(y+1, x) && !isObstacle(y+2, x) && getReceptiveFieldCellValue(y+3, x) != 0){
				height =true;
			}
		}
		if(dc >= 103 && dc <= 105 && isObstacle(y - 3, x + 1) && isObstacle( y + 2, x )){
			action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
		}
		if(dc == 101){
			if(isObstacle(y+1, x) && !isObstacle(y+2, x)){
				height2 = true;
			}
		}
//		if(deadEnd == 1){
//			action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
//		}
		if(dc >= 110 && dc <= 120 ){
			if(marioFloatPos[1] < 95){
				heightValue = 2;
			}else if(marioFloatPos[1] >= 95 && marioFloatPos[1] <= 105){
				heightValue = 1;
			}
		}
		if(dc <= 131 && dc >= 123){
			if(marioFloatPos[1] < 120){
				heightValue3 = false;
			}else{
				heightValue3 = true;
			}
		}
		if(dc >=150 && dc <= 160 && marioFloatPos[1] > 130){
			heightValue2 = true;
		}
//		else if(dc >=150 && dc <= 162){
//			action[Mario.KEY_LEFT] = true;
//			action[Mario.KEY_RIGHT] = false;
//		}
		if(dc >=115 && dc <= 117 && marioFloatPos[1] < 100){
			action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
		}
		if(dc >= 101 && dc <= 131){
			action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
		}
		if(isObstacle(y , x + 1)){
			action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
		}
		if(getEnemiesCellValue(y, x+1) != 0 || getEnemiesCellValue(y, x+2) != 0){
			action[Mario.KEY_JUMP] = isMarioAbleToJump || !isMarioOnGround;
		}
		
//		System.out.println(deadEnd);
		return action;
	}
	
	// 行き止まりを検出
	public void detectDeadEnd(int y, int x){
		int l = 0;
		for(int i = 0; i < 4; ++i){
			if(getReceptiveFieldCellValue(y - i + 3, x + 1) != 0){
				l++;
			}
		}
		if(l >= 5){
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
		

	private double probe(int x, int y, byte[][] scene){
	    int realX = x + 11;
	    int realY = y + 11;
	    return (scene[realX][realY] != 0) ? 1 : 0;
	}

	public byte getGene(int i){
		return gene[i];
	}

	public void setGene(int j,byte gene){
		this.gene[j] = gene;
	}

	public void setDistance(int distance){
		this.distance = distance;
	}
	public int getDistance(){
		return distance;
	}


	@Override
	public Evolvable getNewInstance() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	@Override
	public GAAgent clone(){

		GAAgent res = null;
		try{
			res = (GAAgent)super.clone();
		}catch(CloneNotSupportedException e){
			throw new InternalError(e.toString());
		}

		return res;
	}

	@Override
	public void mutate() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public Evolvable copy() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

    public boolean getHeight(){
    	return height;
    }
    
    public boolean getHeight2(){
    	return height2;
    }
    
    public int getHeightValue(){
    	return heightValue;
    }
    
    public boolean getHeightValue2(){
    	return heightValue2;
    }
    
    public boolean getHeightValue3(){
    	return heightValue3;
    }
}
