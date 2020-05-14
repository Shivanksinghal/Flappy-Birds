package com.shivank.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture gameOver;
	Texture[] birds;

	Texture topPipe[];
	Texture bottomPipe[];

	int birdState;
	float birdY;

	float tubeX[];
	float tubeY[];
	float tubeY2[];

	float velocity;
	int gameState;
	float dist = 800;
	float speedofgame;

	Circle birdCircle;
	Rectangle topPipes[];
	Rectangle bottomPipes[];

	ShapeRenderer shapeRenderer;
	BitmapFont font;
	int score;
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameOver = new Texture("gameover.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
		birds[1] = new Texture("bird2.png");

		topPipe = new Texture[4];
        bottomPipe = new Texture[4];
		tubeX = new float[4];
		tubeY = new float[4];
		tubeY2 = new float[4];
//        for(int i = 0; i < 4; ++i) {
//            topPipe[i]= new Texture("toptube.png");
//            bottomPipe[i] = new Texture("bottomtube.png");
//            tubeX[i] = Gdx.graphics.getWidth() + dist*i;
//            tubeY[i] = ((float) (Math.random()) * (float)(Gdx.graphics.getHeight()-640))+510;
//        }
		velocity = 0.0f;
		birdState = 0;
		birdY = (Gdx.graphics.getHeight() - birds[birdState].getHeight())/2;

		gameState = 0;
		birdCircle = new Circle();
		topPipes = new Rectangle[4];
		bottomPipes = new Rectangle[4];
		for (int i = 0; i < 4; ++i) {
			topPipes[i] = new Rectangle();
			bottomPipes[i] = new Rectangle();
		}

		shapeRenderer = new ShapeRenderer();
		font = new BitmapFont();
		font.setColor(Color.WHITE);
		font.getData().setScale(10);
		score = 0;
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if(gameState == 0) {
			birdY = (Gdx.graphics.getHeight() - birds[birdState].getHeight())/2;
			batch.draw(birds[birdState], (Gdx.graphics.getWidth() - birds[birdState].getWidth())/2, birdY);
			score = 0;
			speedofgame = 4;
			if(Gdx.input.justTouched()) {
				for(int i = 0; i < 4; ++i) {
					topPipe[i]= new Texture("toptube.png");
					bottomPipe[i] = new Texture("bottomtube.png");

					tubeX[i] = Gdx.graphics.getWidth() + dist*i;
					tubeY[i] = ((float) (Math.random()) * (float)(topPipe[i].getHeight()))+Gdx.graphics.getHeight()-topPipe[i].getHeight();
					tubeY2[i] = (float) (Math.random() * (Math.min(tubeY[i] - 300, bottomPipe[i].getHeight()))) - bottomPipe[i].getHeight();
				}
				gameState = 1;
			}
		} else if(gameState == 1) {
//			if(speedofgame < 7)
//				speedofgame += 0.01;
			if (birdState == 0) {
				birdState = 1;
			} else {
				birdState = 0;
			}

			for (int i = 0; i < 4; ++i) {
				batch.draw(topPipe[i], tubeX[i], tubeY[i]);
				batch.draw(bottomPipe[i], tubeX[i], tubeY2[i]);

				tubeX[i] -= speedofgame;
				if (tubeX[i] < -topPipe[i].getWidth()) {
					tubeX[i] = tubeX[(((i - 1) % 4) + 4) % 4] + dist;
					tubeY[i] = ((float) (Math.random()) * (float)(topPipe[i].getHeight()))+Gdx.graphics.getHeight()-topPipe[i].getHeight();
					tubeY2[i] = (float) (Math.random() * (Math.min(tubeY[i] - 300, bottomPipe[i].getHeight()))) - bottomPipe[i].getHeight();
				}
				topPipes[i] = new Rectangle(tubeX[i], tubeY[i], topPipe[i].getWidth(), topPipe[i].getHeight());
				bottomPipes[i] = new Rectangle(tubeX[i], tubeY2[i], bottomPipe[i].getWidth(), bottomPipe[i].getHeight());

				if(tubeX[i] == (Gdx.graphics.getWidth() - birds[birdState].getWidth())/2 - topPipe[0].getWidth())
					score ++;
			}
			if (Gdx.input.justTouched()) {
				velocity = -18;
			}
			birdY -= velocity;
			velocity ++;
			if(birdY < -birds[0].getHeight() || birdY > Gdx.graphics.getHeight()) {
				birdY = velocity;
				velocity = 0;
				gameState = 2;
			}

			batch.draw(birds[birdState], (Gdx.graphics.getWidth() - birds[birdState].getWidth())/2, birdY);
			for(int i = 0; i < 4; ++i) {
				if (Intersector.overlaps(birdCircle, topPipes[i]) || Intersector.overlaps(birdCircle, bottomPipes[i])) {
					gameState = 2;
					break;
				}
			}
			birdCircle.set(Gdx.graphics.getWidth()/2, birdY+50, birds[0].getHeight()-48);
			font.draw(batch, String.valueOf(score), 100, 200);
		} else if(gameState == 2) {
			batch.draw(gameOver, (Gdx.graphics.getWidth() - gameOver.getWidth())/2, (Gdx.graphics.getHeight() - gameOver.getHeight())/2);
			if (Gdx.input.justTouched()) {
				gameState = 0;
			}
		}


		batch.end();

//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(Color.BLUE);
//		shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);
//
//		shapeRenderer.end();

	}
	
//	@Override
//	public void dispose () {
//		batch.dispose();
//		background.dispose();
//		birds[0].dispose();
//		birds[1].dispose();
//	}
}
