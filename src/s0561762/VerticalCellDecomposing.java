package s0561762;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import lenz.htw.ai4g.ai.Info;

public class VerticalCellDecomposing {

	private ArrayList<Point> obstaclePoints;
	private ArrayList<Polygon> verticalCells=new ArrayList<Polygon>();
	private ArrayList<Point2D> vertCellWalkThrough=new ArrayList<Point2D>();
	private ArrayList<Polygon> actualCells=new ArrayList<Polygon>();

	public VerticalCellDecomposing(Polygon[] obstacles, Info info) {

		/*
		 * each obstacle has a Pathiterator, defining all edge-points those can be used
		 * for the vertical cell decomposing, creating vertical lines from each one each
		 * point in the path iterator is given, as long as the pathiterator isn't empty
		 */
	//or (int i = 0; i < obstacles.length; i++) {
			PathIterator pi = obstacles[0].getPathIterator(null);
			obstaclePoints = new ArrayList<Point>();

			while (!pi.isDone()) {
				float[] coordinates = new float[6];
				int type = pi.currentSegment(coordinates);
				switch (type) {

				case PathIterator.SEG_MOVETO:

					System.out.println("move to " + coordinates[0] + ", " + coordinates[1]);
					Point segM = new Point((int) coordinates[0], (int) coordinates[1]);
					obstaclePoints.add(segM);
					pi.next();
					break;

				case PathIterator.SEG_LINETO:

					System.out.println("line to " + coordinates[0] + ", " + coordinates[1]);
					Point segL = new Point((int) coordinates[0], (int) coordinates[1]);
					obstaclePoints.add(segL);
					pi.next();
					break;

				case PathIterator.SEG_QUADTO:
					pi.next();
					break;

				case PathIterator.SEG_CUBICTO:
					pi.next();
					break;

				case PathIterator.SEG_CLOSE:

					Point last = obstaclePoints.get(0);
					obstaclePoints.add(last);

					System.out.println("close"+ last.toString());
					pi.next();
					break;

				default:

					break;
				}
			}

			/*
			 * each obstacle point passed by the pathiterator has an x and y coordinate to
			 * create cells, therefore i first created vertical polygons, those create a
			 * "line" depending on wether a line upwards or line downwards from a specific
			 * point intersects with another obstacle, the line gets shortened final outcome
			 * is a 3-point composed polygon (this could aswell be one that consists of two
			 * points and has no further connection downwards in freespace but this point is
			 * of no use and won't be taken into consideration for creating the cells
			 */
			for (int c = 0; c < obstaclePoints.size(); c++) {

				Point currentObstaclePoint = obstaclePoints.get(c);
				double pointXCoord = currentObstaclePoint.getX();
				double pointYCoord = currentObstaclePoint.getY();

				int fullHeight = info.getTrack().getHeight();

				double height = fullHeight - currentObstaclePoint.getY();

				Rectangle lineUp = new Rectangle();
				lineUp.setFrame(pointXCoord, pointYCoord, 0, height);

				Rectangle lineDown = new Rectangle();
				lineDown.setFrame(pointXCoord, pointYCoord, 0,-pointYCoord);

				for (int z = 0; z < obstacles.length; z++) {

					if (obstacles[z].intersects(lineUp)) {
						Rectangle2D yDeterminator = obstacles[z].getBounds2D().createIntersection(lineUp);
						double loweredY = yDeterminator.getMinY();
						double loweredHeight = height - loweredY;
						lineUp.setFrame(pointXCoord, loweredHeight, 0, 2000000);
					} else if (obstacles[z].intersects(lineDown)) {
						Rectangle2D yDeterminator = obstacles[z].getBounds2D().createIntersection(lineDown);
						double shortenedY = yDeterminator.getMaxY();
						double shortenedHeight = pointYCoord - shortenedY;
						lineDown.setFrame(pointXCoord, shortenedY, 0, 200000000);
					}
				}
				System.out.println( "current point: " +
						currentObstaclePoint.getX() +"   ; " +
						currentObstaclePoint.getY());
				System.out.println(" current point upper cell measuring: " +
						"   ; "
				+lineUp.getMaxY());
				
				System.out.println(" current point  lower"
						+ " cell measuring: " + 
						
						"  ;  "+	lineDown.getMinY());

				double cellUpperX = lineUp.getFrame().getX();
				double cellUpperY = lineUp.getFrame().getY();

				double cellLowerX = lineDown.getFrame().getX();
				double cellLowerY = lineDown.getFrame().getY();

				Polygon currentCell = new Polygon();
				currentCell.addPoint((int) cellUpperX, (int) cellUpperY);
				currentCell.addPoint((int) pointXCoord, (int) pointYCoord);
				currentCell.addPoint((int) cellLowerX, (int) cellLowerY);
				
				verticalCells.add(currentCell);

				double upperYCenter = (cellUpperY + pointYCoord) / 2;
				double lowerYCenter = (cellLowerY + pointYCoord) / 2;

				Point2D upperwalkthrough = new Point2D.Double(pointXCoord, upperYCenter);
				Point2D lowerwalkthrough = new Point2D.Double(pointXCoord, lowerYCenter);
				
				vertCellWalkThrough.add(upperwalkthrough);
				vertCellWalkThrough.add(lowerwalkthrough);

				
				

			}

			/*
			 * each verticalcell, or morelike, polygon - line, has a minimum and maximum.
			 * therefore, each previous linesegment and the following one get looked at
			 * their min and max y-values using those, a new cell gets created > using the
			 * previous y values as a "lowerleftbound" and "upperleftbound" same counts for
			 * the current linesegment, using "lowerrightbound" and "upperrightbound"
			 */

			for (int z = 1; z < verticalCells.size(); z++) {

				int[] prevLineYCoords = verticalCells.get(z - 1).ypoints;
				int[] currentLineYCoords = verticalCells.get(z).ypoints;
				int genericXCoord = verticalCells.get(z).xpoints[1];

				int upperBoundleft = 0;
				int lowerBoundleft = 1000;
				int upperBoundright = 0;
				int lowerBoundright = 1000;

				for (int d = 0; d < prevLineYCoords.length; d++) {
					if (prevLineYCoords[d] > upperBoundleft) {
						upperBoundleft = prevLineYCoords[d];
					} else if (prevLineYCoords[d] < lowerBoundleft) {
						lowerBoundleft = prevLineYCoords[d];
					} else if (currentLineYCoords[d] > upperBoundright) {
						upperBoundright = currentLineYCoords[d];
					} else if (currentLineYCoords[d] < lowerBoundright) {
						lowerBoundright = currentLineYCoords[d];
					}

				}
				Polygon cell = new Polygon();
				cell.addPoint(genericXCoord, lowerBoundleft);
				cell.addPoint(genericXCoord, upperBoundleft);
				cell.addPoint(genericXCoord, upperBoundright);
				cell.addPoint(genericXCoord, lowerBoundright);

				actualCells.add(cell);

				/*
				 * respectively, every cell has a center which will be used for finding the best
				 * path for our car. this points will be stored and after checking the best path
				 * using an A*, those will guide to the target
				 */

				double cellCenterX = cell.getBounds2D().getCenterX();
				double cellCenterY = cell.getBounds2D().getCenterY();

				Point2D cellWThru = new Point2D.Double(cellCenterX, cellCenterY);
				vertCellWalkThrough.add(cellWThru);
			}

		}
	}


