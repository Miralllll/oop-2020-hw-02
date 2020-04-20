// Board.java

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 CS108 Tetris Board.
 Represents a Tetris board -- essentially a 2-d grid
 of booleans. Supports tetris pieces and row clearing.
 Has an "undo" feature that allows clients to add and remove pieces efficiently.
 Does not do any drawing or have any idea of pixels. Instead,
 just represents the abstract 2-d board.
*/
public class Board{
	// Some ivars are stubbed out for you:
	private int width;
	private int height;
	private boolean[][] grid;
	private boolean DEBUG = true;
	boolean committed;

	private int[] widths; // array saves widths for every row
	private int[] heights; // array saves heights for every column
	private int maxHeight;

	// for backup
	private int[] xWidthsBackUp;
	private int[] yHeightsBackUp;
	private boolean[][] gridBackUp;
	private int maxHeightBackUp;
	// Here a few trivial methods are provided:
	
	/**
	 Creates an empty board of the given width and height
	 measured in blocks.
	*/
	public Board(int width, int height) {
		ValidBoardSizes(width, height);
		InitAllInstances(width, height);
		InitBackUps();
	}

	/**
	 * Initializes helper-arrays for storing a pre-version of the table.
	 */
	private void InitBackUps() {
		xWidthsBackUp = new int[height];
		yHeightsBackUp = new int[width];
		gridBackUp = new boolean[width][height];
	}

	/**
	 * Initializes important structures for storing board data.
	 * This information is state, generally.
	 */
	private void InitAllInstances(int width, int height) {
		this.width = width;
		this.height = height;
		grid = new boolean[width][height];
		committed = true;
		widths = new int[height];
		heights = new int[width];
	}

	/**
	 * Checks if given arguments(width and height) are valid.
	 */
	private void ValidBoardSizes(int width, int height) {
		if(width <= 0 || height <= 0) throw new IllegalArgumentException("Your width or height is not valid.");
	}


	/**
	 Returns the width of the board in blocks.
	*/
	public int getWidth() { return width; }
	
	
	/**
	 Returns the height of the board in blocks.
	*/
	public int getHeight() {
		return height;
	}
	
	
	/**
	 Returns the max column height present in the board.
	 For an empty board this is 0.
	*/
	public int getMaxHeight() { return maxHeight; }

	/**
	 Checks the board for internal consistency -- used
	 for debugging.
	*/
	public void sanityCheck() {
		if (DEBUG) {
			System.out.println(toString());
			int checkMaxHeight = 0;
			boolean validHeights = true;
			int[] validWidths = new int[getHeight()];
			for(int w = 0; w < getWidth(); w ++) {
				int currHeight = 0;
				for (int h = 0; h < getHeight(); h++)
					if (grid[w][h]) {
						currHeight = h + 1;
						validWidths[h] ++;
					}
				validHeights &= (heights[w] == currHeight);
			    checkMaxHeight = Math.max(checkMaxHeight, currHeight);
			}
			String text = "Description: ";
			if(!validHeights) text += " Heights are incorrect. ";
			if(!Arrays.equals(validWidths, widths)) text += " Widths are incorrect. ";
			if(checkMaxHeight != getMaxHeight()) text += " MaxHeight is incorrect. ";
			if(!text.equals("Description: ")) throw new RuntimeException(text);
		}
	}
	
	/**
	 Given a piece and an x, returns the y
	 value where the piece would come to rest
	 if it were dropped straight down at that x.
	 
	 <p>
	 Implementation: use the skirt and the col heights
	 to compute this fast -- O(skirt length).
	*/
	// I add new Exception here.
	public int dropHeight(Piece piece, int x) {
		if(!isInBounds(x, 0))
			throw new IllegalArgumentException("Your x coordinate is invalid for this board.");
		int firstTouchY = 0;
		int[] skirt = piece.getSkirt();
		// w - is actual column index. We are searching possible touch points from x.
		for(int w = x; w < skirt.length + x; w ++) {
			// heights[w] - skirt[w - x] - this is possible collapse's Y coordinate.
			firstTouchY = Math.max(firstTouchY, heights[w] - skirt[w - x]);
		}
		return firstTouchY;
	}
	
	
	/**
	 Returns the height of the given column --
	 i.e. the y value of the highest block + 1.
	 The height is 0 if the column contains no blocks.
	*/
	// I add new Exception here.
	public int getColumnHeight(int x) {
		if(!isInBounds(x, 0))
			throw new IllegalArgumentException("Your x coordinate is invalid for this board.");
		return heights[x];
	}
	
	
	/**
	 Returns the number of filled blocks in
	 the given row.
	*/
	// I add new Exception here.
	public int getRowWidth(int y) {
		if(!isInBounds(0, y))
			throw new IllegalArgumentException("Your y coordinate is invalid for this board.");
		return widths[y];
	}
	
	
	/**
	 Returns true if the given block is filled in the board.
	 Blocks outside of the valid width/height area
	 always return true.
	*/
	public boolean getGrid(int x, int y) { return !isInBounds(x, y) || grid[x][y]; }
	
	
	public static final int PLACE_OK = 0;
	public static final int PLACE_ROW_FILLED = 1;
	public static final int PLACE_OUT_BOUNDS = 2;
	public static final int PLACE_BAD = 3;
	
	/**
	 Attempts to add the body of a piece to the board.
	 Copies the piece blocks into the board grid.
	 Returns PLACE_OK for a regular placement, or PLACE_ROW_FILLED
	 for a regular placement that causes at least one row to be filled.
	 
	 <p>Error cases:
	 A placement may fail in two ways. First, if part of the piece may falls out
	 of bounds of the board, PLACE_OUT_BOUNDS is returned.
	 Or the placement may collide with existing blocks in the grid
	 in which case PLACE_BAD is returned.
	 In both error cases, the board may be left in an invalid
	 state. The client can use undo(), to recover the valid, pre-place state.
	*/
	// I add new Exception here.
	public int place(Piece piece, int x, int y) {
		// flag !committed problem
		if(piece == null) throw new IllegalArgumentException("Your piece is invlaid");
		if (!committed) throw new RuntimeException("place commit problem");
		committed = false;
		doBackUp();
		int result = PLACE_OK;
		TPoint[] body = piece.getBody();
		for(int i = 0; i < body.length; i ++) {
			int currStatus = getTPointStatus(body[i], x, y);
			result = Math.max(result, currStatus);
			if(result >= PLACE_OUT_BOUNDS) break;
		}
		sanityCheck();
		return result;
	}

	/**
	 * Copies widths, heights and grid for the backup information.
	*/
	private void doBackUp() {
		System.arraycopy(widths,0, xWidthsBackUp,0, height);
		System.arraycopy(heights,0, yHeightsBackUp,0, width);
		for(int i = 0; i < grid.length; i ++)
			System.arraycopy(grid[i],0,gridBackUp[i],0, grid[i].length); // deep copy
		maxHeightBackUp = maxHeight;
	}

	/**
	 * Checks everything for one point. Finds its status.
	 * If it is out of bound or bad point returns int meanings for them.
	 * else, it updates current information in grids and arrays(widths and heights)
	 * and then checks if y coordinates row is filled at this point - returns status
	 * according to this information.
	 */
	private int getTPointStatus(TPoint p, int x, int y) {
		int pointXCord = p.x + x, pointYCord = p.y + y;
		if(!isInBounds(pointXCord, pointYCord)) return PLACE_OUT_BOUNDS;
		if(grid[pointXCord][pointYCord]) return PLACE_BAD;
		// so there can't be more problems
		putNewPoint(pointXCord, pointYCord);
		if(widths[pointYCord] == width) return PLACE_ROW_FILLED;
		return PLACE_OK;
	}

	/**
	 * Updates current information in grids and arrays(widths and heights) in this point
	 * Heights arr is considered wisely, it saves maximum value from old and current y + 1 values.
	 */
	// t t t t
	// t       t - This is a new piece, for example,
	// t   t t t
	// t t      // in heights, it should save max y coordinate from old height and this point y.
	private void putNewPoint(int pointXCord, int pointYCord) {
		grid[pointXCord][pointYCord] = true;
		heights[pointXCord] = Math.max(heights[pointXCord], pointYCord + 1);
		maxHeight = Math.max(maxHeight, heights[pointXCord]); // update maxHeight too
		widths[pointYCord] ++;
	}

	/**
	 * Checks if coordinates(x,y) is inside of the board.
	 */
	private boolean isInBounds(int pointXCord, int pointYCord) {
		return pointXCord >= 0 && pointXCord < width
				&& pointYCord >= 0 && pointYCord < height;
	}

	/**
	 Deletes rows that are filled all the way across, moving
	 things above down. Returns the number of rows cleared.
	*/
	public int clearRows() {
		if(committed) { committed = false; doBackUp(); }
		int rowsCleared = 0;
		for(int h = 0; h < maxHeight; h ++){
			if(widths[h] == width) rowsCleared ++;
			else moveLinesDown(h, rowsCleared);
		}
		addEmptyLines(rowsCleared);
		updateHeights(rowsCleared);
		sanityCheck();
		return rowsCleared;
	}

	/**
	 * Updates heights array after adding new piece.
	 * It knows that new heights can't be more than old height - rowsCleared,
	 * so it starts searching of new height down exactly from this coordinate.
	 */
	private void updateHeights(int rowsCleared) {
		maxHeight = 0;
		for(int w = 0; w < width; w ++){
			heights[w] -= rowsCleared;
			// if there is no more filled place under this, automatically height = 0
			while(heights[w] > 0){
				if(grid[w][heights[w] - 1]) break;
				heights[w] --; // going down step by step
			}
			maxHeight = Math.max(maxHeight, heights[w]);
		}
	}

	/**
	 * Add some empty lines because of cleared rows in grid.
	 */
	private void addEmptyLines(int rowsCleared) {
		for(int h = maxHeight - rowsCleared; h < maxHeight; h ++) {
			for (int w = 0; w < width; w ++)
				grid[w][h] = false;
			widths[h] = 0;
		}
	}

	/**
	 * Organised one line copying process from old index to new one(index - removedNum).
	 */
	private void moveLinesDown(int index, int removedNum) {
		if(removedNum == 0) return;
		for(int w = 0; w < width; w ++) {
			grid[w][index - removedNum] = grid[w][index];
			widths[index - removedNum] = widths[index];
		}
	}

	/**
	 Reverts the board to its state before up to one place
	 and one clearRows();
	 If the conditions for undo() are not met, such as
	 calling undo() twice in a row, then the second undo() does nothing.
	 See the overview docs.
	*/
	public void undo() {
		if(!committed)
			swapSomeAndItsBackUp();
		commit();
		sanityCheck();
	}

	/**
	 * Swapping process means getting old information again.
	 * Here, method makes old (backup info) information new
	 * and new (actual, current info) - > old
	 */
	private void swapSomeAndItsBackUp() {
		swapGridAndBackUp();
		swapWidthsAndBackUp();
		swapHeightsAndBackUp();
		swapMaxHeightAndBackUp();
	}

	/**
	 * It organizes swapping maxHeight information.
	 */
	private void swapMaxHeightAndBackUp() {
		int tmpMaxHeight = maxHeightBackUp;
		maxHeightBackUp = maxHeight;
		maxHeight = tmpMaxHeight;
	}

	/**
	 * It organizes swapping heights information.
	 */
	private void swapHeightsAndBackUp() {
		int[] heightsTmp = yHeightsBackUp;
		yHeightsBackUp = heights;
		heights = heightsTmp;
	}

	/**
	 * It organizes swapping widths information.
	 */
	private void swapWidthsAndBackUp() {
		int[] widthsTmp = xWidthsBackUp;
		xWidthsBackUp = widths;
		widths = widthsTmp;
	}

	/**
	 * It organizes swapping grids information.
	 */
	private void swapGridAndBackUp() {
		boolean[][] gridTmp = gridBackUp;
		gridBackUp = grid;
		grid = gridTmp;
	}


	/**
	 Puts the board in the committed state.
	*/
	public void commit() { committed = true; }

	/**
	 * It is my personal idea to give a client chance want to use debugger or not.
	 */
	public void changeDebugStatus(){ DEBUG = DEBUG ? false : true; }

	/*
	 Renders the board state as a big String, suitable for printing.
	 This is the sort of print-obj-state utility that can help see complex
	 state change over time.
	 (provided debugging utility) 
	 */
	public String toString() {
		StringBuilder buff = new StringBuilder();
		for (int y = height-1; y>=0; y--) {
			buff.append('|');
			for (int x=0; x<width; x++) {
				if (getGrid(x,y)) buff.append('+');
				else buff.append(' ');
			}
			buff.append("|\n");
		}
		for (int x=0; x<width+2; x++) buff.append('-');
		return(buff.toString());
	}
}


