package kdtree;

import java.util.List;


public class KDTreePointSet implements PointSet {
    private KDNode root;

    /**
     * Instantiates a new KDTree with the given points.
     * @param points a non-null, non-empty list of points to include
     *               (makes a defensive copy of points, so changes to the list
     *               after construction don't affect the point set)
     */
    public KDTreePointSet(List<Point> points) {
        for (Point point: points) {
            root = add(root, point, true);
        }
        //print(root);
    }

    /*public void print(KDNode r) {
        if (r!=null){
            print(r.ld);
            System.out.println(r.point);
            print(r.ru);
        }
    }*/

    private KDNode add(KDNode n, Point p, boolean oddLevel) {
        if (n == null) {
            return new KDNode(p);
        } else if (oddLevel && n.point.x() <= p.x()) {
            n.ru = add(n.ru, p, !oddLevel);
        } else if (oddLevel && n.point.x() > p.x()) {
            n.ld = add(n.ld, p, !oddLevel);
        } else if (!oddLevel && n.point.y() <= p.y()) {
            n.ru = add(n.ru, p, !oddLevel);
        } else if (!oddLevel && n.point.y() > p.y()) {
            n.ld = add(n.ld, p, !oddLevel);
        }
        return n;
    }
    /**
     * Returns the point in this set closest to (x, y) in (usually) O(log N) time,
     * where N is the number of points in this set.
     */
    @Override
    public Point nearest(double x, double y) {
        if (root == null) {
            return null;
        }
        Point p = new Point(x, y);
        return nearest(root, p, root.point, true);
    }

    private Point nearest(KDNode n, Point p, Point result, boolean oddLevel) {
        if (n == null) {
            return result;
        }
        if (n.point.equals(p)) {
            return p;
        }
        if (n.point.distanceSquaredTo(p) < result.distanceSquaredTo(p)) {
            result = n.point;
        }
        double line = getLine(p, n.point, oddLevel);
        if (line < 0) {
            result = nearest(n.ld, p, result, !oddLevel);
            if (result.distanceSquaredTo(p) >= line*line) {
                result = nearest(n.ru, p, result, !oddLevel);
            }
        } else {
            result = nearest(n.ru, p, result, !oddLevel);
            if (result.distanceSquaredTo(p) >= line*line) {
                result = nearest(n.ld, p, result, !oddLevel);
            }
        }
        return result;
    }

    private double getLine(Point p1, Point p2, boolean oddLevel) {
        if (oddLevel) {
            return p1.x()-p2.x();
        } else {
            return p1.y()-p2.y();
        }
    }

    private static final class KDNode{
        private Point point;
        private KDNode ld;
        private KDNode ru;

        private KDNode(Point p) {
            this.point = p;
        }
    }

}
