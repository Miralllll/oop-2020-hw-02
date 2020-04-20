import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.runners.MethodSorters;

import java.util.Arrays;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

// I am using 4 boards. Their sizes are down with initializations.
// I have all kind of pieces and their rotations.
// I have 3 game simulations with checking all information, after every movement.
// Every test is named according to methods that it calls.
// Boards simulation is easy to see with boards toString/sanityCheck
// (It is all pre-organized, so client can see results as output).

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BoardTest {
	private static Board boards[];
	private static final int BOARDS_NUM = 4;
	private static Piece[] pyrs, lOnes, lTwos, sOnes, sTwos, squares, sticks;
	private static final int PYR_ROT_NUM = 4;
	private static final int L_ROT_NUM = 4;
	private static final int S_ROT_NUM = 2;
	private static final int SQUARE_ROT_NUM = 1;
	private static final int STICK_ROT_NUM = 2;

	// In this case, setUp() makes shapes,
	// and also a 3X6 board ...
	@BeforeClass
	public static void setUpBoards() {
		boards = new Board[BOARDS_NUM];
		boards[0] = new Board(3,6);
		boards[1] = new Board(6, 12);
		boards[2] = new Board(10, 25);
	}
	@BeforeClass
	public static void setUpPyramids(){
		pyrs = new Piece[PYR_ROT_NUM];
		pyrs[0] = new Piece(Piece.PYRAMID_STR);
		for(int i = 1; i < PYR_ROT_NUM; i++)
			pyrs[i] = pyrs[i - 1].computeNextRotation();
	}

	@BeforeClass
	public static void setUpLOne(){
		lOnes = new Piece[L_ROT_NUM];
		lOnes[0] = new Piece(Piece.L1_STR);
		for(int i = 1; i < L_ROT_NUM; i++)
			lOnes[i] = lOnes[i - 1].computeNextRotation();
	}

	@BeforeClass
	public static void setUpLTwo(){
		lTwos = new Piece[L_ROT_NUM];
		lTwos[0] = new Piece(Piece.L2_STR);
		for(int i = 1; i < L_ROT_NUM; i++)
			lTwos[i] = lTwos[i - 1].computeNextRotation();
	}

	@BeforeClass
	public static void setUpSOne(){
		sOnes = new Piece[S_ROT_NUM];
		sOnes[0] = new Piece(Piece.S1_STR);
		sOnes[1] = sOnes[0].computeNextRotation();
	}

	@BeforeClass
	public static void setUpSTwo(){
		sTwos = new Piece[S_ROT_NUM];
		sTwos[0] = new Piece(Piece.S2_STR);
		sTwos[1] = sTwos[0].computeNextRotation();
	}

	@BeforeClass
	public static void setUpSquare(){
		squares = new Piece[SQUARE_ROT_NUM];
		squares[0] = new Piece(Piece.SQUARE_STR);
	}

	@BeforeClass
	public static void setUpSticks(){
		sticks = new Piece[STICK_ROT_NUM];
		sticks[0] = new Piece(Piece.STICK_STR);
		sticks[1] = sticks[0].computeNextRotation();
	}

	// Check the basic width/height/gridTrue/gridFalse
	@Test
	public void testA3X6Sample(){
		assertEquals(3, boards[0].getWidth());
		assertEquals(6, boards[0].getHeight());
		assertTrue(boards[0].getGrid(-1,4));
		assertTrue(boards[0].getGrid(2,6));
		assertTrue(boards[0].getGrid(1,-4));
		assertTrue(boards[0].getGrid(11,2));
	}

	@Test
	public void testB3X6Pyramid0() {
		boards[0].commit();
		assertEquals(Board.PLACE_ROW_FILLED, boards[0].place(pyrs[0], 0,0));
		assertEquals(1, boards[0].getColumnHeight(0));
		assertEquals(2, boards[0].getColumnHeight(1));
		assertEquals(2, boards[0].getMaxHeight());
		assertEquals(3, boards[0].getRowWidth(0));
		assertEquals(1, boards[0].getRowWidth(1));
		assertEquals(0, boards[0].getRowWidth(2));
		// so now some rows are filled. We need to call clearRows()
		assertEquals(1, boards[0].clearRows());
		assertEquals(0, boards[0].getColumnHeight(0));
		assertEquals(1, boards[0].getColumnHeight(1));
		assertEquals(1, boards[0].getMaxHeight());
		assertEquals(1, boards[0].getRowWidth(0));
		assertEquals(0, boards[0].getRowWidth(1));
		// calls clearRows() when commit == true
		boards[0].commit();
		assertEquals(0, boards[0].clearRows());
	}

	@Test
	public void testC3X6Pyramid1() {
		boards[0].commit();
		assertEquals(Board.PLACE_OK, boards[0].place(pyrs[1], 1,0));
		assertEquals(0, boards[0].getColumnHeight(0));
		assertEquals(2, boards[0].getColumnHeight(1));
		assertEquals(3, boards[0].getColumnHeight(2));
		assertEquals(3, boards[0].getMaxHeight());
		assertEquals(2, boards[0].getRowWidth(0));
		assertEquals(2, boards[0].getRowWidth(1));
		assertEquals(1, boards[0].getRowWidth(2));
		assertEquals(0, boards[0].getRowWidth(3));
	}

	@Test
	public void testD3X6Stick0() {
		boards[0].commit();
		assertEquals(Board.PLACE_ROW_FILLED, boards[0].place(sticks[0], 0,0));
		assertEquals(4, boards[0].getColumnHeight(0));
		assertEquals(2, boards[0].getColumnHeight(1));
		assertEquals(4, boards[0].getMaxHeight());
		assertEquals(3, boards[0].getRowWidth(0));
		assertEquals(3, boards[0].getRowWidth(1));
		assertEquals(2, boards[0].getRowWidth(2));
		assertEquals(1, boards[0].getRowWidth(3));
		// so now some rows are filled. We need to call clearRows()
		assertEquals(2, boards[0].clearRows());
		assertEquals(2, boards[0].getColumnHeight(0));
		assertEquals(0, boards[0].getColumnHeight(1));
		assertEquals(2, boards[0].getMaxHeight());
		assertEquals(2, boards[0].getRowWidth(0));
		assertEquals(1, boards[0].getRowWidth(1));
	}

	@Test
	public void testE3X6LOne2() {
		boards[0].commit();
		assertEquals(Board.PLACE_ROW_FILLED, boards[0].place(lOnes[2], 0,0));
		assertEquals(3, boards[0].getColumnHeight(0));
		assertEquals(3, boards[0].getColumnHeight(1));
		assertEquals(3, boards[0].getMaxHeight());
		assertEquals(3, boards[0].getRowWidth(0));
		assertEquals(2, boards[0].getRowWidth(1));
		// so now some rows are filled. We need to call clearRows()
		assertEquals(1, boards[0].clearRows());
		assertEquals(2, boards[0].getColumnHeight(0));
		assertEquals(2, boards[0].getColumnHeight(1));
		assertEquals(2, boards[0].getMaxHeight());
		assertEquals(2, boards[0].getRowWidth(0));
		assertEquals(2, boards[0].getRowWidth(1));
		assertEquals(0, boards[0].getRowWidth(2));
	}

	@Test
	public void testA6X12Sample(){
		assertEquals(6, boards[1].getWidth());
		assertEquals(12, boards[1].getHeight());
		assertTrue(boards[1].getGrid(-1,4));
		assertTrue(boards[1].getGrid(2,12));
		assertTrue(boards[1].getGrid(1,-2));
		assertTrue(boards[1].getGrid(13,2));
	}

	@Test
	public void testB6X12LOne0() {
		boards[1].commit();
		assertEquals(Board.PLACE_OK, boards[1].place(lOnes[0], 0,0));
		assertEquals(3, boards[1].getColumnHeight(0));
		assertEquals(1, boards[1].getColumnHeight(1));
		assertEquals(3, boards[1].getMaxHeight());
		assertEquals(2, boards[1].getRowWidth(0));
		assertEquals(1, boards[1].getRowWidth(2));
	}

	@Test
	public void testC6X12LOne1() {
		boards[1].commit();
		assertEquals(Board.PLACE_OK, boards[1].place(lOnes[1], 2,0));
		assertEquals(1, boards[1].getColumnHeight(2));
		assertEquals(2, boards[1].getColumnHeight(4));
		assertEquals(3, boards[1].getMaxHeight());
		assertEquals(5, boards[1].getRowWidth(0));
		assertEquals(1, boards[1].getRowWidth(2));
	}

	@Test
	public void testD6X12Square() {
		boards[1].commit();
		int droppedY = -1; // default coordinate ---- Check dropHeight
		assertEquals(1, droppedY = boards[1].dropHeight(squares[0], 2));
		assertEquals(Board.PLACE_OK, boards[1].place(squares[0], 2, droppedY));
		assertEquals(3, boards[1].getColumnHeight(2));
		assertEquals(3, boards[1].getColumnHeight(3));
		assertEquals(3, boards[1].getMaxHeight());
		assertEquals(4, boards[1].getRowWidth(1));
		assertEquals(3, boards[1].getRowWidth(2));
	}

	@Test
	public void testE6X12LTwo2() {
		boards[1].commit();
		int droppedY = -1; // default coordinate ---- Check dropHeight
		assertEquals(1, droppedY = boards[1].dropHeight(lTwos[2], 1));
		assertEquals(Board.PLACE_OK, boards[1].place(lTwos[2], 1, droppedY));
		assertEquals(4, boards[1].getColumnHeight(1));
		assertEquals(3, boards[1].getColumnHeight(3));
		assertEquals(4, boards[1].getMaxHeight());
		assertEquals(5, boards[1].getRowWidth(1));
		assertEquals(4, boards[1].getRowWidth(2));
		assertEquals(2, boards[1].getRowWidth(3));
	}

	@Test
	public void testF6X12SOne1() {
		boards[1].commit();
		int droppedY = -1; // default coordinate ---- Check dropHeight
		assertEquals(2, droppedY = boards[1].dropHeight(sOnes[1], 3));
		assertEquals(Board.PLACE_OK, boards[1].place(sOnes[1], 3, droppedY));
		assertEquals(5, boards[1].getColumnHeight(3));
		assertEquals(5, boards[1].getMaxHeight());
		assertEquals(4, boards[1].getRowWidth(3));
	}

	@Test
	public void testG6X12STwo1() {
		boards[1].commit();
		int droppedY = -1; // default coordinate ---- Check dropHeight
		assertEquals(3, droppedY = boards[1].dropHeight(sTwos[1], 0));
		assertEquals(Board.PLACE_OK, boards[1].place(sTwos[1], 0, droppedY));
		assertEquals(5, boards[1].getRowWidth(3));
	}

	@Test
	public void testH6X12Stick0() {
		boards[1].commit();
		int droppedY = -1;  // default coordinate ---- Check dropHeight
		assertEquals(0, droppedY = boards[1].dropHeight(sticks[0], 5));
		assertEquals(Board.PLACE_ROW_FILLED, boards[1].place(sticks[0], 5, droppedY));
		// now its really complicated situation when program should delete 4 rows together!!!!
		assertEquals(4, boards[1].clearRows());
	}

	@Test
	// Its all about undo and testing it.
	public void testI6X12UndoStick0() {
		// when it has not been committed yet
		boards[1].undo();
		assertEquals(6, boards[1].getColumnHeight(1));
		assertEquals(6, boards[1].getMaxHeight());
		assertEquals(3, boards[1].getRowWidth(4));
		assertEquals(0, boards[1].clearRows());
		// add and clear again
		testH6X12Stick0();
	}

	@Test
	// It is debug-status changing methods test.
	public void testJ6X12ChangeDStatus(){
		boards[1].changeDebugStatus(); // debug = false
		int droppedY = -1; // default coordinate ---- Check dropHeight
		assertEquals(1, droppedY = boards[1].dropHeight(lTwos[1], 0));
		boards[1].commit();
		assertEquals(Board.PLACE_OK, boards[1].place(lTwos[1], 0, droppedY));
		boards[1].undo();
		boards[1].changeDebugStatus(); // debug = true
		assertEquals(Board.PLACE_OK, boards[1].place(lTwos[1], 0, droppedY));
		boards[1].commit();
		boards[1].undo();
	}

	@Test
	public void testA10X25Sample(){
		assertEquals(10, boards[2].getWidth());
		assertEquals(25, boards[2].getHeight());
		assertTrue(boards[2].getGrid(-1,4));
		assertTrue(boards[2].getGrid(2,26));
		assertTrue(boards[2].getGrid(1,-2));
		assertTrue(boards[2].getGrid(13,2));
	}

	@Test
	public void testB10X25Sticks1() {
		// here is quick place statements
		assertEquals(Board.PLACE_OK, boards[2].place(sticks[1], 0,0));
		boards[2].commit();
		assertEquals(Board.PLACE_OK, boards[2].place(sticks[1], 4,0));
		boards[2].commit();
	}

	@Test
	public void testC10X25Pyramids2A3() {
		// here is quick place statements
		assertEquals(Board.PLACE_OK, boards[2].place(pyrs[2], 0,1));
		boards[2].commit();
		assertEquals(Board.PLACE_OK, boards[2].place(pyrs[2], 4,1));
		boards[2].commit();
		int droppedY = -1; // default coordinate ---- Check dropHeight
		assertEquals(2, droppedY = boards[2].dropHeight(pyrs[3],3));
		assertEquals(Board.PLACE_OK, boards[2].place(pyrs[3], 3, droppedY));
	}

	@Test
	public void testF10X25Sticks0() {
		// here is more quick place statements
		boards[2].commit();
		assertEquals(Board.PLACE_OK, boards[2].place(sticks[0], 7,1));
		boards[2].commit();
		assertEquals(Board.PLACE_OK, boards[2].place(sticks[0], 8,0));
		boards[2].commit();
		// And here !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! I create the complicated situation.
		assertEquals(Board.PLACE_ROW_FILLED, boards[2].place(sticks[0], 9,0));
	}

	@Test
	// here is important clear moment when there was some gap
	// between removed row and new max-height points
	public void testG10X25ClearAndUndo() {
		// This two rows are not next to each other.
		// Board figure has some gaps between them.
		// So heights and max-height changes here dramatically.
		assertEquals(2, boards[2].clearRows());
		boards[2].undo(); // reUse undo
		assertEquals(0, boards[2].clearRows());
		boards[2].commit();
		assertEquals(Board.PLACE_ROW_FILLED, boards[2].place(sticks[0], 9,0));
		boards[2].undo(); // do it one more time, when board is committed.
	}

	@Test
	public void testZ4X3ClearAndStick1(){
		// It's edge case, when our figure is in the air and clearRows is called.
		// |----|
		// |++++|
		// |----|
		boards[3] = new Board(4, 3);
		assertEquals(Board.PLACE_ROW_FILLED, boards[3].place(sticks[1], 0, 1));
		assertEquals(1, boards[3].clearRows());
		assertEquals(0,boards[3].getMaxHeight());
	}

	@Test(expected = RuntimeException.class)
	public void sanityCheckExceptionMoment(){
		// this check if sanityCheck can find invalid situations.
		Board newBoard = new Board(5,5){
			@Override // this returns always 0
			public int getHeight(){ return 0; }
		};
		newBoard.place(lOnes[3], 0, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testZDropHeightValidArg(){
		boards[0].dropHeight(lTwos[0], -1);
	}

	@Test(expected = RuntimeException.class)
	public void testZPlaceWhenUncommitted(){
		boards[0].place(lOnes[3], 2, 3);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testZEmptyBoard(){
		boards[3] = new Board(0, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testZZeroWidth(){
		boards[3] = new Board(0, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testZZeroHeight(){
		boards[3] = new Board(1, 0);
	}

	@Test
	public void testZNotCorrectPlace(){
		// Checks if place method returns correct int according to different situations.
		boards[3] = new Board(1, 2);
		assertEquals(Board.PLACE_OUT_BOUNDS, boards[3].place(pyrs[0], 0, 0));
		boards[3] = new Board(1, 4);
		assertEquals(Board.PLACE_ROW_FILLED, boards[3].place(sticks[0], 0, 0));
		boards[3] = new Board(2, 4);
		assertEquals(Board.PLACE_OK, boards[3].place(sticks[0], 0, 0));
		boards[3].commit();
		assertEquals(Board.PLACE_BAD, boards[3].place(sticks[0], 0, 0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void validityOfHeightsX(){
		boards[0].getColumnHeight(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void validityOfWidthY(){
		boards[0].getRowWidth(6);
	}

	@Test(expected = IllegalArgumentException.class)
	public void validityOfPieceInPlace(){
		boards[0].place(null, 0,0);
	}
}
