package edu.wm.cs.cs301.TessieBaumann.gui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.core.content.ContextCompat;

import edu.wm.cs.cs301.TessieBaumann.R;

import static android.graphics.Color.rgb;

public class MazePanel extends View {


    private static final long serialVersionUID = 2787329533730973905L;
    /* Panel operates a double buffer see
     * http://www.codeproject.com/Articles/2136/Double-buffer-in-standard-Java-AWT
     * for details
     */

    private Bitmap.Config config; //bitmap configuration settings
    private Bitmap bitmap;  //bitmap with graphics drawn on it
    private Canvas canvas;  //canvas to draw images on the bitmap
    private Paint paint;  //paint being used at the time
    private static final String TAG = "MazePanel";  //string message

    private int color;  //int color that paint is currently set on
    private static final int greenWM = Color.parseColor("#115740");
    private static final int goldWM = Color.parseColor("#916f41");
    private static final int yellowWM = Color.parseColor("#FFFF99");
    private boolean top = true;  //boolean value that tells if top or bottom background rectangle is being drawn
    private String markerFont;  //current font of the marker
    private Paint shaderPaint;


    /**
     * This method scales the bitmap so that it fits into the view that is
     * in playManuallyActivity and PlayAnimationActivity, then paints the
     * scaled bitmap onto the canvas of the view that was passed in
     * @param canvas
     */
    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);
        //addBackground(3);
        Bitmap myBitmap = Bitmap.createScaledBitmap(bitmap, 1050, 1050, true);
        canvas.drawBitmap(myBitmap, 0, 0, paint);
    }

    /**
     * This method tests to make sure that the different drawing properties of
     * MazePanel work in android studio by drawing practice lines and shapes
     *
     * it is only used for testing purposes
     */
    private void myTestImage(){
        canvas.drawColor(Color.BLACK);
        setColor(Color.RED);
        addFilledOval(0, 0, 150, 150);
        setColor(Color.GREEN);
        addFilledOval(50, 200, 100, 300);
        setColor(Color.YELLOW);
        addFilledRectangle(300, 300, 200, 200);
        setColor(Color.BLUE);
        int[] temp = new int[]{20, 0, 30, 80, 45};
        int[] yp = new int[]{0, 20, 400, 20, 0};
        addFilledPolygon(temp, yp, 5);
        setColor(Color.WHITE);
        addPolygon(temp, yp, 5);
        addLine(100, 0, 200, 200);
        addArc(200, 200, 300, 300, 30, 50);
        addLine(250, 250, 300, 300);
    }


    /**
     * Constructor. Object is not focusable.
     */
    public MazePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(false);
        config = Bitmap.Config.ARGB_8888;
        bitmap = Bitmap.createBitmap(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, config);
        canvas = new Canvas(bitmap);
        paint = new Paint();
        shaderPaint = new Paint();
        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.fish3);
        BitmapShader sandShader = new BitmapShader(bitmap2, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        shaderPaint.setShader(sandShader);
    }


    /**
     * Obtains a canvas object that can be used for drawing.
     * This MazePanel object internally stores the canvas object
     * and will return the same canvas object over multiple method calls.
     * The graphics object acts like a notepad where all clients draw
     * on to store their contribution to the overall image that is to be
     * delivered later.
     * @return Canvas object to draw on, null if impossible to obtain image
     */
    public Canvas getBitmap() {
        // if necessary instantiate and store a graphics object for later use
        if (null == canvas) {
            if (null == bitmap) {
                config = Bitmap.Config.ARGB_8888;
                bitmap = Bitmap.createBitmap(Constants.VIEW_WIDTH, Constants.VIEW_HEIGHT, config);
                if (null == bitmap)
                {
                    System.out.println("Error: creation of buffered image failed, presumedly container not displayable");
                    return null; // still no buffer image, give up
                }
            }
            canvas = new Canvas(bitmap);
            if (null == canvas) {
                System.out.println("Error: creation of graphics for buffered image failed, presumedly container not displayable");
            }
            else {
                // System.out.println("MazePanel: Using Rendering Hint");
                // For drawing in FirstPersonDrawer, setting rendering hint
                // became necessary when lines of polygons
                // that were not horizontal or vertical looked ragged
                //canvas.setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING,
                  //      java.awt.RenderingHints.VALUE_ANTIALIAS_ON);
                //graphics.setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION,
                  //      java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            }
        }
        return canvas;
    }

    /**
     * Commits all accumulated drawings to the UI.
     * Substitute for MazePanel.update method.
     */
    public void commit() {
        Log.v(TAG, "Updating maze panel");
        invalidate();
    }

    /**
     * Tells if instance is able to draw. This ability depends on the
     * context, for instance, in a testing environment, drawing
     * may be not possible and not desired.
     * Substitute for code that checks if graphics object for drawing is not null.
     * @return true if drawing is possible, false if not.
     */
    public boolean isOperational() {
        if(canvas != null) {
            return true;
        }
        return false;
    }

    /**
     * Sets the color for future drawing requests. The color setting
     * will remain in effect till this method is called again and
     * with a different color.
     * @param rgb gives the red green and blue encoded value of the color
     */
    public void setColor(int rgb) {
        paint.setColor(rgb);
        color = rgb;

    }

    /**
     * Returns the RGB value for the current color setting.
     * @return integer RGB value
     */
    public int getColor() {
        return color;
    }


    /**
     * Default minimum value for RGB values.
     */
    private static final int RGB_DEF = 20;
    private static final int RGB_DEF_GREEN = 60;

    /**
     * Determines the color for a wall.
     * Supports color determination for the Wall.initColor method.
     * @param distance is the distance to the exit
     * @param cc is an obscure parameter used in Wall for color determination, just passed in here
     * @param extensionX is the wall's length and direction (sign), horizontal dimension
     * @return the rgb value for the color of the wall
     */
    public static int getWallColor(int distance, int cc, int extensionX) {
        final int d = distance / 4;
        // mod used to limit the number of colors to 6
        final int part1 = distance & 7;
        final int add = (extensionX != 0) ? 1 : 0;
        final int rgbValue = ((part1 + 2 + add) * 70) / 8 + 80;
        int wallColor = 0;
        switch (((d >> 3) ^ cc) % 6) {
            case 0:
                wallColor = rgb(rgbValue, RGB_DEF, RGB_DEF);
                break;
            case 1:
                wallColor = rgb(RGB_DEF, RGB_DEF_GREEN, RGB_DEF);
                break;
            case 2:
                wallColor = rgb(RGB_DEF, RGB_DEF, rgbValue);
                break;
            case 3:
                wallColor = rgb(rgbValue, RGB_DEF_GREEN, RGB_DEF);
                break;
            case 4:
                wallColor = rgb(RGB_DEF, RGB_DEF_GREEN, rgbValue);
                break;
            case 5:
                wallColor = rgb(rgbValue, RGB_DEF, rgbValue);
                break;
            default:
                wallColor = rgb(RGB_DEF, RGB_DEF, RGB_DEF);
                break;
        }
        return wallColor;
    }


    /**
     * Draws two solid rectangles to provide a background.
     * Note that this also erases any previous drawings.
     * The color setting adjusts to the distance to the exit to
     * provide an additional clue for the user.
     * @param percentToExit gives the distance to exit
     */
    public void addBackground(float percentToExit) {
        paint.setColor(getBackgroundColor(percentToExit, top));
        //paint.setColor(ContextCompat.getColor(getContext(), R.color.skyBlue));
        if(!top){
            paint.setColor(ContextCompat.getColor(getContext(), R.color.sand));
        }
        top = !top;
    }

    /**
     * Determine the background color for the top and bottom
     * rectangle as a blend between starting color settings
     * of black and grey towards gold and green as final
     * color settings close to the exit
     * @param percentToExit
     * @param top is true for the top triangle, false for the bottom
     * @return the color to use for the background rectangle
     */
    private int getBackgroundColor(float percentToExit, boolean top) {
        return top? blend(Color.valueOf(Color.parseColor("#1FCDF8")), Color.valueOf(Color.parseColor("#031985")), percentToExit) :
                blend(Color.valueOf(Color.LTGRAY), Color.valueOf(greenWM), percentToExit);
    }

    /**
     * Calculates the weighted average of the two given colors
     * @param c0 the one color
     * @param c1 the other color
     * @param weight0 of c0
     * @return blend of colors c0 and c1 as weighted average
     */
    private int blend(Color c0, Color c1, float weight0) {
        if (weight0 < 0.1)
            return c1.toArgb();
        if (weight0 > 0.95)
            return c0.toArgb();
        float r = weight0 * c0.red() + (1-weight0) * c1.red();
        float g = weight0 * c0.green() + (1-weight0) * c1.green();
        float b = weight0 * c0.blue() + (1-weight0) * c1.blue();
        float a = Math.max(c0.alpha(), c1.alpha());

        return Color.valueOf(r, g, b, a).toArgb();
    }

    /**
     * Adds a filled rectangle.
     * The rectangle is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis.
     * @param x is the x-coordinate of the top left corner
     * @param y is the y-coordinate of the top left corner
     * @param width is the width of the rectangle
     * @param height is the height of the rectangle
     */
    public void addFilledRectangle(float x, float y, float width, float height) {
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(x, y, x + width, y + height, paint);

    }

    /**
     * Adds a filled polygon.
     * The polygon is specified with {@code (x,y)} coordinates
     * for the n points it consists of. All x-coordinates
     * are given in a single array, all y-coordinates are
     * given in a separate array. Both arrays must have
     * same length n. The order of points in the arrays
     * matter as lines will be drawn from one point to the next
     * as given by the order in the array.
     * Substitute for Graphics.fillPolygon() method
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    public void addFilledPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        paint.setStyle(Paint.Style.FILL);
        Path path = new Path();
        path.reset();
        if(xPoints != null & yPoints != null){
            path.moveTo(xPoints[0], yPoints[0]);
            for(int i = 1; i < nPoints; i++){
                path.lineTo(xPoints[i], yPoints[i]);
            }
            canvas.drawPath(path, shaderPaint);
        }
    }

    /**
     * Adds a polygon.
     * The polygon is not filled.
     * The polygon is specified with {@code (x,y)} coordinates
     * for the n points it consists of. All x-coordinates
     * are given in a single array, all y-coordinates are
     * given in a separate array. Both arrays must have
     * same length n. The order of points in the arrays
     * matter as lines will be drawn from one point to the next
     * as given by the order in the array.
     * Substitute for Graphics.drawPolygon method
     * @param xPoints are the x-coordinates of points for the polygon
     * @param yPoints are the y-coordinates of points for the polygon
     * @param nPoints is the number of points, the length of the arrays
     */
    public void addPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        paint.setStyle(Paint.Style.STROKE);
        Path path = new Path();
        path.reset();
        if(xPoints != null & yPoints != null){
            path.moveTo(xPoints[0], yPoints[0]);
            for(int i = 1; i < nPoints; i++){
                path.lineTo(xPoints[i], yPoints[i]);
            }
            canvas.drawPath(path, paint);
        }

    }

    /**
     * Adds a line.
     * A line is described by {@code (x,y)} coordinates for its
     * starting point and its end point.
     * @param startX is the x-coordinate of the starting point
     * @param startY is the y-coordinate of the starting point
     * @param endX is the x-coordinate of the end point
     * @param endY is the y-coordinate of the end point
     */
    public void addLine(float startX, float startY, float endX, float endY) {
        canvas.drawLine(startX, startY, endX, endY, paint);

    }

    /**
     * Adds a filled oval.
     * The oval is specified with the {@code (x,y)} coordinates
     * of the upper left corner and then its width for the
     * x-axis and the height for the y-axis. An oval is
     * described like a rectangle.
     * @param left is the x-coordinate of the top left corner
     * @param top is the y-coordinate of the top left corner
     * @param right is the width of the oval
     * @param bottom is the height of the oval
     */
    public void addFilledOval(float left, float top, float right, float bottom) {
        paint.setStyle(Paint.Style.FILL);
        canvas.drawOval(left, top, right, bottom, paint);

    }

    /**
     * Adds the outline of a circular or elliptical arc covering the specified rectangle.
     * The resulting arc begins at startAngle and extends for arcAngle degrees,
     * using the current color. Angles are interpreted such that 0 degrees
     * is at the 3 o'clock position. A positive value indicates a counter-clockwise
     * rotation while a negative value indicates a clockwise rotation.
     * The center of the arc is the center of the rectangle whose origin is
     * (x, y) and whose size is specified by the width and height arguments.
     * The resulting arc covers an area width + 1 pixels wide
     * by height + 1 pixels tall.
     * The angles are specified relative to the non-square extents of
     * the bounding rectangle such that 45 degrees always falls on the
     * line from the center of the ellipse to the upper right corner of
     * the bounding rectangle. As a result, if the bounding rectangle is
     * noticeably longer in one axis than the other, the angles to the start
     * and end of the arc segment will be skewed farther along the longer
     * axis of the bounds.
     * @param left the x coordinate of the upper-left corner of the arc to be drawn.
     * @param top the y coordinate of the upper-left corner of the arc to be drawn.
     * @param right the width of the arc to be drawn.
     * @param bottom the height of the arc to be drawn.
     * @param startAngle the beginning angle.
     * @param sweepAngle the angular extent of the arc, relative to the start angle.
     */
    public void addArc(float left, float top, float right, float bottom, float startAngle, float sweepAngle) {
        canvas.drawArc(left, top, right, bottom, startAngle, sweepAngle, true, paint);

    }

    /**
     * Adds a string at the given position.
     * @param x the x coordinate
     * @param y the y coordinate
     * @param str the string
     */
    public void addMarker(float x, float y, String str) {
        canvas.drawText(str, x, y, paint);
    }


    /**
     * Sets the marker font to the
     * value passed in and repaints the
     * compass rose with that font
     * @param fontName name of font
     */
    public void setFont(String fontName) {
        markerFont = fontName;
        paint.setFontFeatureSettings(fontName);
    }


    /**
     * Takes the hex string value of a color
     * and returns that color's int value
     *
     * @param str string of a color
     * @return int value of the color passed in
     */
    public static int decodeColor(String str) {
        return Color.parseColor(str);
    }


    /**
     * Takes the float values of a color and
     * returns that color's int value
     *
     * @param r value of the red value of the color
     * @param g value of the green value of the color
     * @param b value of the blue value of the color
     * @param a value of the alpha value of the color
     * @return int value of the color passed in
     */
    public static int createNewColor(float r, float g, float b, float a) {
        return Color.valueOf(r, g, b, a).toArgb();
    }
}
