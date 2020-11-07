package edu.wm.cs.cs301.TessieBaumann.gui;

import edu.wm.cs.cs301.TessieBaumann.generation.CardinalDirection;

/**
 * A component that draws a compass rose.  This class has no other functionality, but superclasses
 * may add functionality to it.
 *
 * @author Sampo Niskanen <sampo.niskanen@iki.fi>
 * Code copied from http://www.soupwizard.com/openrocket/code-coverage/eclemma-20121216/OpenRocket/src/net.sf.openrocket.gui.components.compass/CompassRose.java.html
 * adjusted for Maze setting by
 * @author Peter Kemper
 */
public class CompassRose {
    private static final long serialVersionUID = 1916497172430988388L;
    private static final int greenWM = MazePanel.decodeColor("#115740");
    private static final int goldWM = MazePanel.decodeColor("#916f41");


    private static final int MAIN_COLOR = greenWM; //new Color(0.4f, 0.4f, 1.0f);
    private static final float MAIN_LENGTH = 0.95f;
    private static final float MAIN_WIDTH = 0.15f;

    private static final int CIRCLE_BORDER = 2;
    private static final int CIRCLE_HIGHLIGHT = MazePanel.createNewColor(1.0f, 1.0f, 1.0f, 0.8f);
    private static final int CIRCLE_SHADE = MazePanel.createNewColor(1.0f, 1.0f, 1.0f, 0.3f); //new Color(0.0f, 0.0f, 0.0f, 0.2f);

    private static final int BLACK = 0;
    private static final int MARKER_COLOR = BLACK; //Color.WHITE; //Color.BLACK;
    private static final int WHITE = 16777215;



    private double scaler;

    private double markerRadius;
    private String markerFont;

    // (x,y) coordinates of center point on overall area
    private int centerX; // x coordinate of center point
    private int centerY; // y coordinate of center point
    private int size; // size of compass rose
    private CardinalDirection currentDir;

    /**
     * Construct a compass rose with the default settings.
     */
    public CompassRose() {
        this(0.9, 1.7, "Serif-PLAIN-16");
    }


    /**
     * Construct a compass rose with the specified settings.
     *
     * @param scaler        The scaler of the rose.  The bordering circle will be this portion of the component dimensions.
     * @param markerRadius  The radius for the marker positions (N/E/S/W), or NaN for no markers.  A value greater than one
     *                      will position the markers outside of the bordering circle.
     * @param markerFont    The font used for the markers.
     */
    public CompassRose(double scaler, double markerRadius, String markerFont) {
        this.scaler = scaler;
        this.markerRadius = markerRadius;
        this.markerFont = markerFont;
    }

    public void setPositionAndSize(int x, int y, int size) {
        centerX = x;
        centerY = y;
        this.size = size;
    }
    /**
     * Set the current direction such that it can
     * be highlighted on the display
     * @param cd
     */
    public void setCurrentDirection(CardinalDirection cd) {
        currentDir = cd;
    }

    //@Override
    public void paintComponent(MazePanel panel) {

        //final Graphics2D g2 = (Graphics2D) g;


        /* Original code
        Dimension dimension = this.getSize();
        int width = Math.min(dimension.width, dimension.height);
        int mid = width / 2;
        width = (int) (scaler * width);
        */
        setMarkerFont(panel, markerFont);
        int width = (int) (scaler * size);
        final int mainLength = (int) (width * MAIN_LENGTH / 2);
        final int mainWidth = (int) (width * MAIN_WIDTH / 2);

        //panel.setRenderingHint(P5Panel.RenderingHints.KEY_RENDERING, P5Panel.RenderingHints.VALUE_RENDER_QUALITY);
        //panel.setRenderingHint(P5Panel.RenderingHints.KEY_ANTIALIASING, P5Panel.RenderingHints.VALUE_ANTIALIAS_ON);
        // draw background disc
        drawBackground(panel, width);
        // draw main part in all 4 directions in same color
        // x, y arrays used for drawing polygons
        // starting point is always (centerX, centerY)
        panel.setColor(MAIN_COLOR);
        final int[] x = new int[3];
        final int[] y = new int[3];
        x[0] = centerX;
        y[0] = centerY;
        drawMainNorth(panel, mainLength, mainWidth, x, y);
        drawMainEast(panel, mainLength, mainWidth, x, y);
        drawMainSouth(panel, mainLength, mainWidth, x, y);
        drawMainWest(panel, mainLength, mainWidth, x, y);

        drawBorderCircle(panel, width);

        drawDirectionMarker(panel, width);
    }


    private void drawBackground(MazePanel panel, int width) {
        panel.setColor(WHITE);
        final int x = centerX - size;
        final int y = centerY - size;
        final int w = 2 * size;// - 2 * CIRCLE_BORDER;
        panel.addFilledOval(x,y,w,w);
    }


    private void drawMainWest(MazePanel panel, int length, int width, int[] x, int[] y) {
        x[1] = centerX - length;
        y[1] = centerY;
        x[2] = centerX - width;
        y[2] = centerY + width;
        panel.addFilledPolygon(x, y, 3);

        y[2] = centerY - width;
        panel.addPolygon(x, y, 3);
    }
    private void drawMainEast(MazePanel panel, int length, int width, int[] x, int[] y) {
        // observation: the 2 triangles to the right are drawn the same
        // way as for the left if one inverts the sign for length and width
        // i.e. exchanges addition and subtraction
        drawMainWest(panel, -length, -width, x, y);
    }
    private void drawMainSouth(MazePanel panel, int length, int width, int[] x, int[] y) {
        x[1] = centerX;
        y[1] = centerY + length;
        x[2] = centerX + width;
        y[2] = centerY + width;
        panel.addFilledPolygon(x, y, 3);

        x[2] = centerX - width;
        panel.addPolygon(x, y, 3);
    }
    private void drawMainNorth(MazePanel panel, int length, int width, int[] x, int[] y) {
        // observation: the 2 triangles to the top are drawn the same
        // way as for the bottom if one inverts the sign for length and width
        // i.e. exchanges addition and subtraction
        drawMainSouth(panel, -length, -width, x, y);
    }

    private void drawBorderCircle(MazePanel panel, int width) {
        final int x = centerX - width / 2 + CIRCLE_BORDER;
        final int y = centerY - width / 2 + CIRCLE_BORDER;
        final int w = width - 2 * CIRCLE_BORDER;
        panel.setColor(CIRCLE_SHADE);
        panel.addArc(x, y, w, w, 45, 180);
        panel.setColor(CIRCLE_HIGHLIGHT);
        panel.addArc(x, y, w, w, 180 + 45, 180);
    }


    private void drawDirectionMarker(MazePanel panel, int width) {
        if (!Double.isNaN(markerRadius) && markerFont != null) {

            int pos = (int) (width * markerRadius / 2);

            panel.setColor(MARKER_COLOR);
            /* original code
            drawMarker(g2, mid, mid - pos, trans.get("lbl.north"));
            drawMarker(g2, mid + pos, mid, trans.get("lbl.east"));
            drawMarker(g2, mid, mid + pos, trans.get("lbl.south"));
            drawMarker(g2, mid - pos, mid, trans.get("lbl.west"));
            */
            /* version with color highlighting but stable orientation
             * Highlighting with MarkerColor which is white
             * use gold as color for others */
            // WARNING: north south confusion
            // currendDir South is going upward on the map
            if (CardinalDirection.South == currentDir)
                panel.setColor(MARKER_COLOR);
            else
                panel.setColor(goldWM);
            drawMarker(panel, centerX, centerY - pos, "N");
            if (CardinalDirection.East == currentDir)
                panel.setColor(MARKER_COLOR);
            else
                panel.setColor(goldWM);
            drawMarker(panel, centerX + pos, centerY, "E");
            // WARNING: north south confusion
            // currendDir North is going downwards on the map
            if (CardinalDirection.North == currentDir)
                panel.setColor(MARKER_COLOR);
            else
                panel.setColor(goldWM);
            drawMarker(panel, centerX, centerY + pos, "S");
            if (CardinalDirection.West == currentDir)
                panel.setColor(MARKER_COLOR);
            else
                panel.setColor(goldWM);
            drawMarker(panel, centerX - pos, centerY, "W");
        }
    }

    private void drawMarker(MazePanel panel, float x, float y, String str) {
        panel.addMarker(x, y, str);

    }

    //public double getScaler() {
    //return scaler;
    //}

    //public void setScaler(MazePanel panel, double scaler) {
    //this.scaler = scaler;
    //panel.repaint();
    //}

    //public double getMarkerRadius() {
    //return markerRadius;
    //}

    //public void setMarkerRadius(MazePanel panel, double markerRadius) {
    //this.markerRadius = markerRadius;
    //panel.repaint();
    //}

    //public String getMarkerFont() {
    // return markerFont;
    //}


    public void setMarkerFont(MazePanel panel, String markerFont) {
        this.markerFont = markerFont;
        panel.setFont(markerFont);
    }

    //@Override
    //public Dimension getPreferredSize() {
    //  Dimension dim = super.getPreferredSize();
    ///* original code
    //int min = Math.min(dim.width, dim.height);
    //*/
    //int min = size; // simply use given size
    //dim.setSize(min, min);
    //return dim;
    //}

}
