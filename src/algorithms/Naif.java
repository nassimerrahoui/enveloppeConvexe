package algorithms;

import java.awt.Point;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Random;
import supportGUI.Circle;
import supportGUI.Line;

public class Naif {

	// calculDiametre: ArrayList<Point> --> Line
	// renvoie une pair de points de la liste, de distance maximum.
	public Line calculDiametre(ArrayList<Point> points) {
		if (points.size() < 3) {
			if (points.size() < 2) {
				return null;
			} else {
				return new Line(points.get(0), points.get(1));
			}
		}

		Point p = points.get(0);
		Point q = points.get(1);
		double distance_max = distance(p, q);

		for (int i = 0; i < points.size(); i++) {
			for (int j = i + 1; j < points.size(); j++) {

				double d = distance(points.get(i), points.get(j));
				if (distance_max < d) {
					distance_max = d;
					p = points.get(i);
					q = points.get(j);
				}
			}

		}
		return new Line(p, q);
	}

	// calculDiametreOptimise: ArrayList<Point> --> Line
	// renvoie une pair de points de la liste, de distance maximum.
	public Line calculDiametreOptimise(ArrayList<Point> points) {
		if (points.size() < 3) {
			return null;
		}

		Point p = points.get(1);
		Point q = points.get(2);

		/*******************
		 * PARTIE A ECRIRE *
		 *******************/
		return new Line(p, q);
	}

	// calculCercleMin: ArrayList<Point> --> Circle
	// renvoie un cercle couvrant tout point de la liste, de rayon minimum.
	public Circle calculCercleMin(ArrayList<Point> points) {

		if (points.isEmpty()) {
			return null;
		}

		Point center = points.get(0);
		double radius = 100;

		/** Algorithme Ritter ******************/

		// Copie pour garder les points sur affichage
		ArrayList<Point> pointsAlgo = new ArrayList<>(points);

		// Etape 1
		Random rand = new Random();
		int randomElement = rand.nextInt(pointsAlgo.size());
		Point dummy = pointsAlgo.get(randomElement);

		// Etape 2:
		double distance_max = 0;
		Point p = new Point();

		for (Point point : pointsAlgo) {
			double d = distance(dummy, point);
			if (d > distance_max) {
				distance_max = d;
				p = point;
			}
		}

		// Etape 3
		distance_max = 0;
		Point q = new Point();
		for (Point point : pointsAlgo) {
			double d = distance(p, point);
			if (d > distance_max) {
				distance_max = d;
				q = point;
			}
		}

		// Etape 4
		center = barycentre(p, q);

		// Etape 5
		radius = distance(p, q) / 2;

		// Etape 6
		pointsAlgo.remove(p);
		pointsAlgo.remove(q);

		// Etapes...
		while (!pointsAlgo.isEmpty()) {

			randomElement = rand.nextInt(pointsAlgo.size());
			Point s = pointsAlgo.get(randomElement);
			double distance_cs = distance(center, s);

			if (distance_cs <= radius) {
				pointsAlgo.remove(s);
			} else {
				radius = (distance_cs + radius) / 2;
				double alpha = radius / distance_cs;
				double beta = 1 - alpha;

				// Calcul du nouveau centre
				double x = alpha * center.getX() + beta * s.getX();
				double y = alpha * center.getY() + beta * s.getY();
				center.setLocation(x, y);

				pointsAlgo.remove(s);
			}
		}

		/*************************************/

		return new Circle(center, (int) radius);
	}

	// enveloppeConvexe: ArrayList<Point> --> ArrayList<Point>
	// renvoie l'enveloppe convexe de la liste.
	public ArrayList<Point> enveloppeConvexe(ArrayList<Point> points) {
		if (points.size() < 3) {
			return null;
		}

		ArrayList<Point> enveloppe = new ArrayList<Point>();

		/** Algorithme naif en O(n^3) **/

		ArrayList<Point> pointsAlgo = triPixel(points);

		for (int i = 0; i < pointsAlgo.size(); i++) {
			Point p = pointsAlgo.get(i);
			for (int j = 0; j < pointsAlgo.size(); j++) {
				Point q = pointsAlgo.get(j);
				if (q.getX() == p.getX() && q.getY() == p.getY()) {
					continue;
				}

				// point r non colineaire a pq
				double signeR = 0;
				do {
					Random rand = new Random();
					int randomElement = rand.nextInt(pointsAlgo.size());
					Point r = pointsAlgo.get(randomElement);
					signeR = crossProduct(p, q, p, r);
				} while (new BigDecimal(signeR).compareTo(BigDecimal.ZERO) == 0);

				// test cote
				boolean estCote = true;
				for (Point s : pointsAlgo) {
					double signeS = crossProduct(p, q, p, s);
					if (signeS * signeR < 0) {
						estCote = false;
						break; // met algo en O(n)
					}
				}

				if (estCote) {
					enveloppe.add(p);
					enveloppe.add(q);
				}

			}

		}

		return enveloppe;
	}

	/** Ajout pour tme **/
	private double distance(Point p, Point q) {
		return Math.sqrt((q.getX() - p.getX()) * (q.getX() - p.getX()) + (q.getY() - p.getY()) * (q.getY() - p.getY()));
	}

	/** Ajout pour tme **/
	private Point barycentre(Point p, Point q) {
		double x = (p.getX() + q.getX()) / 2;
		double y = (p.getY() + q.getY()) / 2;
		Point res = new Point();
		res.setLocation(x, y);
		return res;
	}

	/** Ajout pour tme **/
	private double crossProduct(Point p, Point q, Point s, Point t) {
		return ((q.getX() - p.getX()) * (t.getY() - s.getY()) - (q.getY() - p.getY()) * (t.getX() - s.getX()));
	}

	/** Ajout pour tme **/
	private ArrayList<Point> triPixel(ArrayList<Point> points) {

		Point[] tabMin = new Point[20000];
		Point[] tabMax = new Point[20000];

		for (Point p : points) {
			if (tabMin[p.x] == null || tabMin[p.x].y > p.y)
				tabMin[p.x] = p;
			if (tabMax[p.x] == null || tabMax[p.x].y < p.y)
				tabMax[p.x] = p;
		}
		ArrayList<Point> result = new ArrayList<>();
		for (int i = 0; i < tabMin.length; i++)
			if (tabMin[i] != null)
				result.add(tabMin[i]);
		for (int i = tabMax.length - 1; i >= 0; i--)
			if (tabMax[i] != null)
				result.add(tabMax[i]);

		for (int i = 0; i < result.size() + 10; i++)
			if (result.get(i % result.size()).equals(result.get((i + 1) % result.size()))) {
				result.remove(i % result.size());
				i--;
			}

		return result;

	}

}