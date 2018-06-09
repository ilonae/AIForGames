package s0561762;

import java.awt.List;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.sound.sampled.Line;

import org.lwjgl.util.vector.Vector2f;

import lenz.htw.ai4g.ai.DriverAction;
import lenz.htw.ai4g.ai.Info;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import static org.lwjgl.opengl.GL11.*;

public class AI2 extends lenz.htw.ai4g.ai.AI {

	public AI2(Info info) {
		super(info);
		enlistForTournament(561762);

		Polygon[] obstacles = info.getTrack().getObstacles();

		float startingxCoord = info.getX();
		float startingyCoord = info.getY();

		VerticalCellDecomposing decomp = new VerticalCellDecomposing(obstacles, info);

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "RIDEROFTHESTORM";
	}

	@Override
	public DriverAction update(boolean arg0) {

		Polygon[] obstacles = info.getTrack().getObstacles();

		int trackWidth = info.getTrack().getWidth();
		int trackHeight = info.getTrack().getHeight();

		int tileWidth = trackWidth / 24;
		int tileHeight = trackHeight / 24;

		ArrayList<Point> vertices = new ArrayList<Point>();
		ArrayList<Line2D> edges = new ArrayList<Line2D>();

		float throttle = 0;
		float steering = 0;
		throttle = info.getMaxVelocity();

		Vector2f position = new Vector2f(info.getX(), info.getY());

		Point2D infoPos = new Point2D.Double();
		infoPos.setLocation(info.getX(), info.getY());

		Vector2f checkpoint = new Vector2f((float) info.getCurrentCheckpoint().getX(),
				(float) info.getCurrentCheckpoint().getY());

		Point2D infoCheckpoint = new Point2D.Double();
		infoPos.setLocation(info.getCurrentCheckpoint().getX(), info.getCurrentCheckpoint().getY());

		float xDir = (float) (checkpoint.getX() - position.getX());
		float yDir = (float) (checkpoint.getY() - position.getY());

		Vector2f direction = new Vector2f(xDir, yDir);
		direction.normalise();

		Line2D dir = new Line2D.Float();
		dir.setLine(info.getX(), info.getY(), info.getCurrentCheckpoint().getX(), info.getCurrentCheckpoint().getY());
		ArrayList<Point> obstaclePoints = new ArrayList<Point>();

		return new DriverAction(throttle, steering);
	}

	// ArrayList<Line2D> obstacleEdges = new ArrayList<Line2D>();

	// for (int z = 0; z < obstaclePoints.size(); z++) {
	// Point start = obstaclePoints.get(z);
	// Point end = obstaclePoints.get(z + 1);
	// Line2D current = new Line2D.Float(start, end);
	// obstacleEdges.add(current);
	// }
	//
	// int i = 0;
	// Point last=new Point(0,0);
	//
	// vertices.add(last);
	//
	// for (int x = 0; x < trackWidth; x++) {
	// for (int y = 0; y < trackHeight; y++) {
	// int pos = y * trackWidth + x;
	// if ((x % tileWidth == 0) && (y % tileHeight == 0)) {
	//
	// last = vertices.get(i);
	//
	// Point current = new Point(x, y);
	// vertices.add(current);
	//
	// Line2D edge = new Line2D.Float();
	// edge.setLine(last, current);
	// edges.add(edge);
	//
	// i++;
	// }
	// for (int z = 0; z < obstacleEdges.size(); z++) {
	// Line2D currentObstacleEdge=obstacleEdges.get(z);
	// if (dir.intersectsLine(currentObstacleEdge)) {
	// dir.setLine(info.getX(), info.getY(), info.getCurrentCheckpoint().getX(),
	// info.getCurrentCheckpoint().getY());
	// }
	// }
	// }
	// }
	//
	// return new DriverAction(throttle, steering);
	//
	// }
	//
	// public void dijkstra(ArrayList<Point> vertices, Point2D position, Point2D
	// checkpoint) {
	// ArrayList<Point> reachable = new ArrayList<Point>();
	// ArrayList<Point> done = new ArrayList<Point>();
	// Point2D start=position;
	// Point2D goal=checkpoint;
	// Point2D nearestFirst=new Point2D.Double();
	// double distanceToCar=100000;
	//
	// for(int i=0;i<vertices.size();i++) {
	// if(vertices.get(i).distance(position)<distanceToCar) {
	// distanceToCar= vertices.get(i).distance(position);
	// nearestFirst=vertices.get(i);
	// for(int x=0; x<info.getTrack().getWidth();x++) {
	// for(int y=0;y<info.getTrack().getHeight();y++) {
	// if(vertices.contains(nearestFirst.getX()+(info.getTrack().getWidth()/24)) ||
	// vertices.contains(nearestFirst.getX()-(info.getTrack().getWidth()/24)) ||
	// vertices.contains(nearestFirst.getY()+(info.getTrack().getWidth()/24)) ||
	// vertices.contains(nearestFirst.getY()-(info.getTrack().getWidth()/24))) {
	// reachable.add(vertices.get(index));
	// }
	// }
	// }
	// }
	// }
	// while(!reachable.isEmpty()) {
	//
	// }
	// }

	@Override
	public void doDebugStuff() {
		float xCoord = info.getX();
		float yCoord = info.getY();

		float checkpointX = (float) (info.getCurrentCheckpoint().getX());
		float checkpointY = (float) (info.getCurrentCheckpoint().getY());

		double blick = info.getOrientation();
		float blickX = (float) Math.cos(blick);
		float blickY = (float) Math.sin(blick);

		Rectangle2D bounds = new Rectangle(0, 0, 0, 0);
		Rectangle2D[] obstacle = new Rectangle2D[2];

		float leftX = (float) Math.cos((1 * Math.PI / 3) + blick);
		float rightX = (float) Math.cos((-1 * Math.PI / 3) + blick);

		float leftY = (float) Math.sin((1 * Math.PI / 3) + blick);
		float rightY = (float) Math.sin((-1 * Math.PI / 3) + blick);

		// Checkpoint
		glBegin(GL_LINES);
		glColor3f(0, 0, 1);
		glVertex2f(xCoord, yCoord);
		glVertex2f(checkpointX, checkpointY);
		glEnd();

		// Orientation
		glBegin(GL_LINES);
		glColor3f(1, 0, 0);
		glVertex2f(xCoord, yCoord);
		glVertex2f(xCoord + 15 * blickX, yCoord + 15 * blickY);
		glEnd();

		// Forward
		glBegin(GL_LINES);
		glColor3f(1, 1, 0);
		glVertex2f(xCoord, yCoord);
		glVertex2f(xCoord + 30 * blickX, yCoord + 30 * blickY);
		glEnd();

		// Left
		glBegin(GL_LINES);
		glColor3f(1, 1, 0);
		glVertex2f(xCoord, yCoord);
		glVertex2f(xCoord + 30 * leftX, yCoord + 30 * leftY);
		glEnd();

		// Right
		glBegin(GL_LINES);
		glColor3f(1, 1, 0);
		glVertex2f(xCoord, yCoord);
		glVertex2f(xCoord + 30 * rightX, yCoord + 30 * rightY);
		glEnd();
	}
}
