package AllA.apps;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import android.graphics.Typeface;
import android.util.Log;
import android.widget.Toast;

public class LevelDetailActivity extends SimpleBaseActivity{

	/* variable */
	private int MAX_LEVEL = StartActivity.MAX_LEVEL;
	private Layer levelLayer[] = new Layer[MAX_LEVEL+1];

	private Sprite backgroundSprite;
	private Sprite levelDetailSprite[][] = new Sprite[MAX_LEVEL+1][30];

	/** 레벨 옮기는 버튼 **/
	private Sprite levelLeftButton;
	private Sprite levelRightButton;

	/** 레벨 옮기는 애니메이션 관련 변수 **/
	private int direction;
	private float Distance = Width*0.8f;
	private float movedDistance = 0f;
	private float Velocity = 0f;


	private int level = -1;
	Text Title;
	

	/* Constructor */
	LevelDetailActivity(int width, int height,VertexBufferObjectManager pVertexBufferObjectManager) {
		super(width,height,pVertexBufferObjectManager);
	}

	/* methods */

	@Override
	public void loadResources()
	{	
		ResourceManager.loadImage("back3", "back3.jpg", 1280, 720);
		for(int i=1;i<=MAX_LEVEL;i++)	ResourceManager.loadImage("leveldetailbutton"+i, "level"+i+"/leveldetailbutton.png", 200, 200);
		ResourceManager.loadFont("font2", 256, 256, Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD), 100);

		/** 버튼 부분 **/
		ResourceManager.loadImage("levelleftbutton", "levelleft.png", 100, 200);
		ResourceManager.loadImage("levelrightbutton", "levelright.png", 100, 200);
		/** 버튼 부분 **/

		super.loadResources();
	}

	@Override
	public void loadScene()
	{
		if(isLoaded())
			return;
		
		
		// background
		backgroundSprite = new Sprite(0, 0, ResourceManager.getRegion("back3"), vertexBufferObjectManager);
		mainLayer.attachChild(backgroundSprite);

		// components

		Title = new Text(0,0,ResourceManager.getFont("font2"),"level"+level,vertexBufferObjectManager);
		Title.setPosition((Width-Title.getWidth())/2, 25);
		mainLayer.attachChild(Title);



		for(int i=1;i<=MAX_LEVEL;i++){
			levelLayer[i] = new Layer();
			for(int j=0;j<13;j++){
				levelDetailSprite[i][j] = new Sprite(0,0,ResourceManager.getRegion("leveldetailbutton"+level),vertexBufferObjectManager){
					@Override
					public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
							float pTouchAreaLocalX, float pTouchAreaLocalY) {

						if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP) {
							
							if(Velocity==0f){
								SimpleBaseActivity nextActivity = SceneManager.getActivity("gameActivity");
								
								int back_out_Effect = SceneManager.EFFECT_MOVE_UP;
								int back_in_Effect  = SceneManager.EFFECT_MOVE_UP;
								nextActivity.setBackActivity(thisActivity,back_out_Effect, back_in_Effect);
	
								int out_Effect = SceneManager.EFFECT_MOVE_DOWN;
								int in_Effect  = SceneManager.EFFECT_MOVE_DOWN;
								
								SceneManager.setActivity(nextActivity, out_Effect, in_Effect);
							}
						}

						return true;
					}
				};

				levelDetailSprite[i][j].setScale(0.95f);
				float X = (j%5)*((Width-levelDetailSprite[i][j].getWidth())/4);
				float Y = (j/5)*(levelDetailSprite[i][j].getHeight()+60);
				levelDetailSprite[i][j].setPosition(X, Y);
				levelLayer[i].attachChild(levelDetailSprite[i][j]);

				Text levelText = new Text(0,0,ResourceManager.getFont("font1"),""+(j+1),vertexBufferObjectManager);
				float TX = X+(levelDetailSprite[i][j].getWidthScaled()-levelText.getWidth())/2;
				float TY = Y+(levelDetailSprite[i][j].getHeightScaled()-levelText.getHeight())/2;
				levelText.setPosition(TX, TY);
				levelLayer[i].attachChild(levelText);
			}

			mainLayer.attachChild(levelLayer[i]);

			resetPosition(i);
		}

		/** 레벨 선택 버튼 **/

		levelLeftButton = new Sprite(0,0,ResourceManager.getRegion("levelleftbutton"),vertexBufferObjectManager){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP){
					if(Velocity==0f)
						animateLevelChange(level,-1);
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			};


		};
		levelLeftButton.setPosition(Width*0.06f, (Height-levelLeftButton.getHeight())/2+10);
		mainLayer.attachChild(levelLeftButton);

		levelRightButton = new Sprite(0,0,ResourceManager.getRegion("levelrightbutton"),vertexBufferObjectManager){
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pSceneTouchEvent.getAction() == TouchEvent.ACTION_UP){
					if(Velocity==0f)
						animateLevelChange(level,1);
				}
				return super.onAreaTouched(pSceneTouchEvent, pTouchAreaLocalX, pTouchAreaLocalY);
			}


		};
		levelRightButton.setPosition((Width-levelRightButton.getWidth())*0.94f, (Height-levelRightButton.getHeight())/2+10);
		mainLayer.attachChild(levelRightButton);

		super.loadScene();
	}


	@Override
	public void deleteSprites() {

		super.deleteSprites();
	}

	@Override
	public void registerTouchAreatoSceneManager(){

		SceneManager.registerTouchArea(levelLeftButton);
		SceneManager.registerTouchArea(levelRightButton);
		
		for(int i=1;i<=MAX_LEVEL;i++){
			for(int j=0;j<13;j++){
				SceneManager.registerTouchArea(levelDetailSprite[i][j]);
			}
		}


		super.registerTouchAreatoSceneManager();
	}
	

	@Override
	public void updateActivity(){

		if(isFocused()){
			if(Title != null)
				Title.setText("level"+level);
	
			if(levelLeftButton!=null)
				resetButtonVisible();
	
			if(levelLeftButton!=null)
				animEffect();
		}
		
		if(SceneManager.isPresentActivity("levelDetailActivity")==false)
		{
			Velocity=0f;
			movedDistance=0;
		}
	}


	/* new methods */
	public int getLevel(){
		return level;
	}

	public void setLevel(int pLevel){
		level = pLevel;
		if(levelLeftButton!=null)
		{
			for(int i=1;i<=MAX_LEVEL;i++)
				resetPosition(i);
		}
	}

	public void resetPosition(int i){
		if(i==level){
			levelLayer[i].setScale(0.7f);
			SceneManager.setAlphaAll(1.0f, levelLayer[i]);
		}
		else{
						levelLayer[i].setScale(0.7f);
						SceneManager.setAlphaAll(0.5f, levelLayer[i]);
		}

		levelLayer[i].setVisible(true);

		if(i==level+1)
			levelLayer[i].setPosition(Width*(1-0.7f)/2+(Width*0.8f), 150);
		else if(i==level)
			levelLayer[i].setPosition(Width*(1-0.7f)/2, 150);
		else if(i==level-1)
			levelLayer[i].setPosition(Width*(1-0.7f)/2-(Width*0.8f), 150);
		else
			levelLayer[i].setVisible(false);

//		levelLayer[i].setPosition(Width*(1-0.7f)/2 + (Width*0.8f)*(level-i),150);

	}

	private void resetButtonVisible()
	{
		/** 끝 레벨일 경우 보이지 않음 **/
		if(level==1){
			levelLeftButton.setVisible(false);
			levelRightButton.setVisible(true);
		}else if(level==MAX_LEVEL){
			levelLeftButton.setVisible(true);
			levelRightButton.setVisible(false);
		}else{
			levelLeftButton.setVisible(true);
			levelRightButton.setVisible(true);
		}
	}

	private void animateLevelChange(int currentlevel,int curdirection)    //direction -1 : left, +1 : right
	{
		if(currentlevel+curdirection<1 || currentlevel+curdirection>MAX_LEVEL) return;

		Velocity=38.00f * curdirection;
		direction = curdirection;
	}

	public void animEffect()
	{
		int goinglevel = level + direction;
		if(Velocity==0f) return;
		else if(Velocity<0) 	direction = -1;
		else direction = 1;

		movedDistance += Velocity;
		if(movedDistance * direction >= Distance){
			Velocity=0f;
			movedDistance=0;

			level = level + direction;

			for(int i=1;i<=MAX_LEVEL;i++)
			{
				resetPosition(i);
			}
		}

		for(int i=level-2;i<=level+2;i++)
		{
			if(i>=1 && i<=MAX_LEVEL)
			{
				levelLayer[i].setX(levelLayer[i].getX()-Velocity);
				if(i==goinglevel && level == goinglevel - direction)
					SceneManager.setAlphaAll(1.0f-0.5f*(Distance - movedDistance * direction)/Distance, levelLayer[i]);
				else if(level == goinglevel - direction)
					SceneManager.setAlphaAll(0.5f+0.5f*(Distance - movedDistance * direction)/Distance, levelLayer[i]);
			}
		}
	}

}