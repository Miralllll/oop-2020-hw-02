// Piece.java

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.lang.reflect.Array;
import java.util.*;

/**
 An immutable representation of a tetris piece in a particular rotation.
 Each piece is defined by the blocks that make up its body.
 
 Typical client code looks like...
 <pre>
 Piece pyra = new Piece(PYRAMID_STR);		// Create piece from string
 int width = pyra.getWidth();			// 3
 Piece pyra2 = pyramid.computeNextRotation(); // get rotation, slow way
 
 Piece[] pieces = Piece.getPieces();	// the array of root pieces
 Piece stick = pieces[STICK];
 int width = stick.getWidth();		// get its width
 Piece stick2 = stick.fastRotation();	// get the next rotation, fast way
 </pre>
*/
public class Piece {
	// Starter code specs out a few basic things, leaving
	// the algorithms to be done.
	private TPoint[] body;
	private int[] skirt;
	private int width;
	private int height;
	private Piece next; // "next" rotation // it's connection between other pieces
	// in this static array ->
	static private Piece[] pieces;	// singleton static array of first rotations

	/**
	 Defines a new piece given a TPoint[] array of its body.
	 Makes its own copy of the array and the TPoints inside it.
	*/
	public Piece(TPoint[] points) {
		InitBody(points);
		InitWidthAndHeight();
		InitSkirt();
	}

	/**
	 Defines a new skirt array, which is depend on the body array
	 Skirt's length is the width of the piece.
	 It stores information about the lowest y point for every (y predefined.x).
	 -- skirt stores the lowest y value that appears in the body for each x value in the piece
	 */
	private void InitSkirt() {
		skirt = new int[width];
		Arrays.fill(skirt, Integer.MAX_VALUE);
		for(TPoint p : body)
			skirt[p.x] = Math.min(skirt[p.x], p.y);
	}

	/**
	 Width is the distance between 0 and the furthest x value from o point in the body.
	 Height is the distance between 0 and the furthest y value from o point in the body.
	 */
	private void InitWidthAndHeight() {
		int maxI = 0, maxJ = 0;
		for(TPoint p : body){
			maxI = Math.max(maxI, p.x);
			maxJ = Math.max(maxJ, p.y);
		}
		width = ++ maxI;
		height = ++ maxJ;
	}

	/**
	 Copies some points from points to the body.
	 Saves only unique points in the array.
	 */
	private void InitBody(TPoint[] points) {
		HashSet<TPoint> bodySet = new HashSet<TPoint>();
		for(TPoint p : points)
			if(p != null) bodySet.add(new TPoint(p));
		body = new TPoint[bodySet.size()];
		bodySet.toArray(body);
	}

	/**
	 * Alternate constructor, takes a String with the x,y body points
	 * all separated by spaces, such as "0 0  1 0  2 0	1 1".
	 * (provided)
	 */
	public Piece(String points) {
		this(parsePoints(points));
	}

	/**
	 Returns the width of the piece measured in blocks.
	*/
	public int getWidth() {
		return width;
	}

	/**
	 Returns the height of the piece measured in blocks.
	*/
	public int getHeight() {
		return height;
	}

	/**
	 Returns a pointer to the piece's body. The caller
	 should not modify this array.
	*/
	public TPoint[] getBody() {
		TPoint[] copyBody = new TPoint[body.length];
		for(int i = 0; i < body.length; i ++)
			copyBody[i] = new TPoint(body[i]);
		return copyBody;
	}

	/**
	 Returns a pointer to the piece's skirt. For each x value
	 across the piece, the skirt gives the lowest y value in the body.
	 This is useful for computing where the piece will land.
	 The caller should not modify this array.
	*/
	public int[] getSkirt() {
		return Arrays.copyOf(skirt, skirt.length);
	}

	/**
	 Returns a new piece that is 90 degrees counter-clockwise
	 rotated from the receiver.
	 */
	public Piece computeNextRotation() {
		TPoint[] nextRotation = new TPoint[body.length];
		for(int currInd = 0; currInd < body.length; currInd ++)
			nextRotation[currInd] = rotatedPoint(body[currInd]);
		return new Piece(nextRotation);
	}

	/**
	 Calculates x and y values for the rotated point.
	 */
	private TPoint rotatedPoint(TPoint tPoint) {
		int x = tPoint.x, y = tPoint.y; // calculate new coordinates for this point
		y *= -1; // symmetric to x axis (x, y) / (b) mirroring the body horizontally
		int shiftUp = height - 1; // delta y
		y += shiftUp;
		return new TPoint(y, x); // (a) swapping the x and y for each point
	}

	/**
	 Returns a pre-computed piece that is 90 degrees counter-clockwise
	 rotated from the receiver.	 Fast because the piece is pre-computed.
	 This only works on pieces set up by makeFastRotations(), and otherwise
	 just returns null.
	*/	
	public Piece fastRotation() {
		return next;
	}

	/**
	 Returns true if two pieces are the same --
	 their bodies contain the same points.
	 Interestingly, this is not the same as having exactly the
	 same body arrays, since the points may not be
	 in the same order in the bodies. Used internally to detect
	 if two rotations are effectively the same.
	*/
	public boolean equals(Object obj) {
		// standard equals() technique 1
		if (obj == this) return true;
		// standard equals() technique 2
		// (null will be false)
		if (!(obj instanceof Piece)) return false;
		Piece other = (Piece)obj;
		if(other.getBody().length != body.length) return false;
		ArrayList<TPoint> thisBody = new ArrayList<>(Arrays.asList(body));
		ArrayList<TPoint> otherBody = new ArrayList<>(Arrays.asList(other.getBody()));
		return thisBody.containsAll(otherBody);
	}

	// String constants for the standard 7 tetris pieces
	public static final String STICK_STR	= "0 0	0 1	 0 2  0 3";
	public static final String L1_STR		= "0 0	0 1	 0 2  1 0";
	public static final String L2_STR		= "0 0	1 0 1 1	 1 2";
	public static final String S1_STR		= "0 0	1 0	 1 1  2 1";
	public static final String S2_STR		= "0 1	1 1  1 0  2 0";
	public static final String SQUARE_STR	= "0 0  0 1  1 0  1 1";
	public static final String PYRAMID_STR	= "0 0  1 0  1 1  2 0";
	
	// Indexes for the standard 7 pieces in the pieces array
	public static final int STICK = 0;
	public static final int L1	  = 1;
	public static final int L2	  = 2;
	public static final int S1	  = 3;
	public static final int S2	  = 4;
	public static final int SQUARE	= 5;
	public static final int PYRAMID = 6;
	
	/**
	 Returns an array containing the first rotation of
	 each of the 7 standard tetris pieces in the order
	 STICK, L1, L2, S1, S2, SQUARE, PYRAMID.
	 The next (counterclockwise) rotation can be obtained
	 from each piece with the {@link #fastRotation()} message.
	 In this way, the client can iterate through all the rotations
	 until eventually getting back to the first rotation.
	 (provided code)
	*/
	public static Piece[] getPieces() {
		// lazy evaluation -- create static array if needed
		if (Piece.pieces==null) {
			// use makeFastRotations() to compute all the rotations for each piece
			Piece.pieces = new Piece[] {
				makeFastRotations(new Piece(STICK_STR)),
				makeFastRotations(new Piece(L1_STR)),
				makeFastRotations(new Piece(L2_STR)),
				makeFastRotations(new Piece(S1_STR)),
				makeFastRotations(new Piece(S2_STR)),
				makeFastRotations(new Piece(SQUARE_STR)),
				makeFastRotations(new Piece(PYRAMID_STR)),
			};
		}
		return Piece.pieces;
	}

	/**
	 Given the "first" root rotation of a piece, computes all
	 the other rotations and links them all together
	 in a circular list. The list loops back to the root as soon
	 as possible. Returns the root piece. fastRotation() relies on the
	 pointer structure setup here.
	*/
	/*
	 Implementation: uses computeNextRotation()
	 and Piece.equals() to detect when the rotations have gotten us back
	 to the first piece.
	*/
	private static Piece makeFastRotations(Piece root) {
		Piece curr = root, next = null; // need to have two iteration process together
		while (true){
			next = curr.computeNextRotation();
			// if start point equals this next point,
			// next point of the curr point should become start point. The End.
			if(root.equals(next)){ curr.next = root; break; }
			curr.next = next;
			curr = next;
		}
		return root;
	}

	/**
	 Given a string of x,y pairs ("0 0	0 1 0 2 1 0"), parses
	 the points into a TPoint[] array.
	 (Provided code)
	*/
	private static TPoint[] parsePoints(String string) {
		List<TPoint> points = new ArrayList<TPoint>();
		StringTokenizer tok = new StringTokenizer(string);
		try {
			while(tok.hasMoreTokens()) {
				int x = Integer.parseInt(tok.nextToken());
				int y = Integer.parseInt(tok.nextToken());
				
				points.add(new TPoint(x, y));
			}
		}
		catch (NumberFormatException e) {
			throw new RuntimeException("Could not parse x,y string:" + string);
		}
		
		// Make an array out of the collection
		TPoint[] array = points.toArray(new TPoint[0]);
		return array;
	}
}
