import junit.framework.TestCase;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.*;
import java.util.*;
import java.util.List;

import static org.junit.Assert.*;
// I have separated tests from pyramids to L2 (everything) here.

public class PieceTest {
	// You can create data to be used in the your
	// test cases like this. For each run of a test method,
	// a new PieceTest object is created and setUp() is called
	// automatically by JUnit.
	// For example, the code below sets up some
	// pyramid and s pieces in instance variables
	// that can be used in tests.

	private static final int ROTATIONS_NUM = 5;
	private Random rand = new Random();
	private static Piece[] arr = Piece.getPieces();

	//
	// Pyramid - with rotations...
	// Most detailed (not common, too) cases are in this part.
	//
	private static Piece[] pyrs; // rotated versions of Pyramid

	@BeforeClass
	public static void setUpPyramid() {
		// super.setUp();
		pyrs = new Piece[ROTATIONS_NUM];
		pyrs[0] = new Piece(Piece.PYRAMID_STR); // start piece
		for(int i = 1; i < pyrs.length; i++)
			pyrs[i] = pyrs[i - 1].computeNextRotation();
	}

	// Here are some tests about width and height
	@Test
	public void testPyramidSize() {
		// Check width of pyr pieces
		// after rotations too
		for(int i = 0; i < pyrs.length; i += 2){
			assertEquals(3, pyrs[i].getWidth());
			assertEquals(2, pyrs[i].getHeight());
		}
		for(int i = 1; i < pyrs.length; i += 2){
			assertEquals(2, pyrs[i].getWidth());
			assertEquals(3, pyrs[i].getHeight());
		}
	}

	// some tests about pyramid-skirt of all rotations
	@Test
	public void testPyramidSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		// needs 4 rotation to reach its start position, so first and fifth skirts are same
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyrs[0].getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, pyrs[4].getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 0, 1}, pyrs[2].getSkirt()));

		assertTrue(Arrays.equals(new int[] {1, 0}, pyrs[1].getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 1}, pyrs[3].getSkirt()));
	}

	// tests above this, checks correctness of constructor
	// and getBody is part of it, so its almost checks that method.
	// I think equal is the best way to check it too
	@Test
	public void testEqualsForPyramids(){
		// easy to notice that they are equal
		assertTrue(pyrs[0].equals(pyrs[0]));
		assertTrue(pyrs[0].equals(pyrs[4]));
		assertFalse(pyrs[0].equals(new String[4]));
		// choose randomly one shape
		for(int i = 0; i < 100; i ++) {
			int chosenPyramid = rand.nextInt(ROTATIONS_NUM);
			TPoint[] oldPyramidBody = pyrs[chosenPyramid].getBody(); // takes body
			List<TPoint> ls = swapListElementsRandomly(Arrays.asList(oldPyramidBody));
			TPoint[] newPyramidBody = new TPoint[ls.size()];
			ls.toArray(newPyramidBody);
			assertTrue(pyrs[chosenPyramid].equals(new Piece(newPyramidBody))); //!!!!
			// here takes both bodies and checks them.
		}
	}

	// helps to swap elements in the arraylist randomly
	private List<TPoint> swapListElementsRandomly(List<TPoint> ls) {
		List<TPoint> randSwappedList = new ArrayList<>(ls);
		for(int i = 0; i < 100; i ++){
			int first = rand.nextInt(ls.size());
			int second = rand.nextInt(ls.size());
			Collections.swap(randSwappedList, first, second);
		}
		return randSwappedList;
	}

	// It tests if equal TPoints are erased from body.
	// It is not depend on the piece type and is general...
	@Test
	public void testBodySamePoints(){
		// Here is tests which checks if same points are illegal in the piece.
		Piece samePoints = new Piece(Piece.PYRAMID_STR + "  " + Piece.PYRAMID_STR);
		TPoint[] newPyramidBody = samePoints.getBody(); // takes body
		TPoint[] body = pyrs[0].getBody();
		assertEquals(body.length, newPyramidBody.length);
		for(int i = 0; i < body.length; i++)
			assertEquals(body[i].toString(), newPyramidBody[i].toString());
		ArrayList<TPoint> samePointList = new ArrayList<TPoint>(Arrays.asList(newPyramidBody));
		samePointList.addAll(samePointList);
		TPoint[] samePointArr = new TPoint[samePointList.size()];
		samePointList.toArray(samePointArr);
		samePoints = new Piece(samePointArr);
		assertEquals(pyrs[0].getBody().length, newPyramidBody.length);
	}

	// It checks if null TPoints are erased from body
	// It is not depend on the piece type, so this test is general...
	@Test
	public void testNullAndMixedPoints(){
		// my implementation checks if null point is in the body array and miss them.
		TPoint[] arr = new TPoint[]{pyrs[0].getBody()[0], pyrs[0].getBody()[1], null, null };
		// contains only two elements in real
		assertEquals(2, new Piece(arr).getBody().length);
		boolean equalPieces = true;
		for(int i = 0; i < pyrs[0].getBody().length; i++)
			equalPieces &= pyrs[0].getBody()[i].equals(arr[i]);
		assertEquals(equalPieces, pyrs[0].equals(new Piece(arr)));
	}

	// It checks if array elements, which was used for creating piece
	// contains same elements' pointers.
	// It is not depend on the piece type, so this test is general...
	@Test
	public void testIsImmutable(){
		TPoint[] pyramidZeroBody = pyrs[0].getBody();
		Piece newPiece = new Piece(pyramidZeroBody);
		TPoint[] newBody = newPiece.getBody();
		for(int i = 0; i < pyramidZeroBody.length; i++) {
			// can't access TPoints from the old array.
			assertTrue(pyramidZeroBody[i].equals(newBody[i]));
			pyramidZeroBody[i].x ++;
			assertFalse(pyramidZeroBody[i].equals(newBody[i]));
			newBody[i].x ++;
			assertFalse(newBody[i].equals(newPiece.getBody()[i]));
			assertTrue(pyramidZeroBody[i].equals(newBody[i]));
			// Check that equal works for the same coordinates too and
			// that up result wasn't accident.
			assertTrue(newBody[i].equals(newBody[i]));
		}
	}

	@Test
	public void testPyramidFastRotation(){
		// Piece.getPieces() call it one more time, it should return same static array.
		assertTrue(Arrays.equals(arr, Piece.getPieces()));
		assertTrue(Arrays.deepEquals(arr, Piece.getPieces()));
		// I already show that computeNextRotation works great and its rotations are
		// appropriate. Now compare that saved rotation to this fast Rotations!
		// If they are equal, it means that getPieces() works perfectly for pyramids too
		Piece root = arr[Piece.PYRAMID];
		for(int i = 0; i < pyrs.length; i ++, root = root.fastRotation())
			assertTrue(pyrs[i].equals(root));
	}

	//
	// s1 piece start and its rotations
	//
	private static Piece sOne[];

	@BeforeClass
	public static void setUpSOne() {
		// super.setUp();
		sOne = new Piece[ROTATIONS_NUM];
		sOne[0] = new Piece(Piece.S1_STR); // start piece
		for(int i = 1; i < sOne.length; i++)
			sOne[i] = sOne[i - 1].computeNextRotation();
	}

	@Test
	public void testSOneSize() {
		// Check width of pyr pieces
		// after rotations too
		for(int i = 0; i < sOne.length; i += 2){
			assertEquals(3, sOne[i].getWidth());
			assertEquals(2, sOne[i].getHeight());
		}
		for(int i = 1; i < sOne.length; i += 2){
			assertEquals(2, sOne[i].getWidth());
			assertEquals(3, sOne[i].getHeight());
		}
	}

	@Test
	public void testSOneSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		// needs only two rotation to reach its start state, so first, third and fifth skirts - equal
		for(int i = 0; i < sOne.length; i += 2)
			assertTrue(Arrays.equals(new int[] {0, 0, 1}, sOne[i].getSkirt()));

		// needs only two rotation to reach its start state, so second and fourth skirts - equal
		for(int i = 1; i < sOne.length; i += 2)
			assertTrue(Arrays.equals(new int[] {1, 0}, sOne[i].getSkirt()));
	}

	// tests above this, checks correctness of constructor
	// and getBody is part of it, so its almost checks that method.
	// I think equal is the best way to check it too
	@Test
	public void testEqualsForSOne(){
		// easy to notice that they are equal
		assertTrue(sOne[0].equals(sOne[0]));
		assertTrue(sOne[0].equals(sOne[4]));
		assertFalse(sOne[0].equals(new Integer[4]));
		// choose randomly one shape
		for(int i = 0; i < 100; i ++) {
			int chosenSOne = rand.nextInt(ROTATIONS_NUM);
			TPoint[] oldSOneBody = sOne[chosenSOne].getBody(); // takes body
			List<TPoint> ls = swapListElementsRandomly(Arrays.asList(oldSOneBody));
			TPoint[] newSOneBody = new TPoint[ls.size()];
			ls.toArray(newSOneBody);
			assertTrue(sOne[chosenSOne].equals(new Piece(newSOneBody))); //!!!!
			// here takes both bodies and checks them.
		}
	}

	@Test
	public void testSOneFastRotation(){
		// I already show that computeNextRotation works great and its rotations are
		// appropriate. Now compare that saved rotation to this fast Rotations!
		// If they are equal, it means that getPieces() works perfectly for s1 too
		Piece root = arr[Piece.S1];
		for(int i = 0; i < sOne.length; i ++, root = root.fastRotation())
			assertTrue(sOne[i].equals(root));
	}

	//
	// s2 piece start and its rotations
	//
	private static Piece sTwo[];

	@BeforeClass
	public static void setUpSTwo() {
		// super.setUp();
		sTwo = new Piece[ROTATIONS_NUM];
		sTwo[0] = new Piece(Piece.S2_STR); // start piece
		for(int i = 1; i < sTwo.length; i++)
			sTwo[i] = sTwo[i - 1].computeNextRotation();
	}

	@Test
	public void testSTwoSize() {
		// Check width of pyr pieces
		// after rotations too
		for(int i = 0; i < sTwo.length; i += 2){
			assertEquals(3, sTwo[i].getWidth());
			assertEquals(2, sTwo[i].getHeight());
		}
		for(int i = 1; i < sOne.length; i += 2){
			assertEquals(2, sTwo[i].getWidth());
			assertEquals(3, sTwo[i].getHeight());
		}
	}

	@Test
	public void testSTwoSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		// these situations are symmetric of s1
		for(int i = 0; i < sTwo.length; i += 2)
			assertTrue(Arrays.equals(new int[] {1, 0, 0}, sTwo[i].getSkirt()));

		// these situations are symmetric of s1
		for(int i = 1; i < sOne.length; i += 2)
			assertTrue(Arrays.equals(new int[] {0, 1}, sTwo[i].getSkirt()));
	}

	// tests above this, checks correctness of constructor
	// and getBody is part of it, so its almost checks that method.
	// I think equal is the best way to check it too
	@Test
	public void testEqualsForSTwo(){
		// easy to notice that they are equal
		assertTrue(sTwo[0].equals(sTwo[0]));
		assertTrue(sTwo[0].equals(sTwo[4]));
		assertFalse(sTwo[0].equals(new Integer[4]));
		// choose randomly one shape
		for(int i = 0; i < 100; i ++) {
			int chosenSTwo = rand.nextInt(ROTATIONS_NUM);
			TPoint[] oldSTwoBody = sTwo[chosenSTwo].getBody(); // takes body
			List<TPoint> ls = swapListElementsRandomly(Arrays.asList(oldSTwoBody));
			TPoint[] newSTwoBody = new TPoint[ls.size()];
			ls.toArray(newSTwoBody);
			assertTrue(sTwo[chosenSTwo].equals(new Piece(newSTwoBody))); //!!!!
			// here takes both bodies and checks them.
		}
	}

	@Test
	public void testSTwoFastRotation(){
		// I already show that computeNextRotation works great and its rotations are
		// appropriate. Now compare that saved rotation to this fast Rotations!
		// If they are equal, it means that getPieces() works perfectly for S2 too
		Piece root = arr[Piece.S2];
		for(int i = 0; i < sTwo.length; i ++, root = root.fastRotation())
			assertTrue(sTwo[i].equals(root));
	}

	//
	// Square piece start and its rotations
	//
	private static Piece squares[];

	@BeforeClass
	public static void setUpSquare() {
		// super.setUp();
		squares = new Piece[ROTATIONS_NUM];
		squares[0] = new Piece(Piece.SQUARE_STR); // start piece
		for(int i = 1; i < squares.length; i++)
			squares[i] = squares[i - 1].computeNextRotation();
	}

	@Test
	public void testSquareSize() {
		// Check width of pyr pieces
		// after rotations too --- width, height = 2 always
		for(int i = 0; i < squares.length; i ++){
			assertEquals(2, squares[i].getWidth());
			assertEquals(2, squares[i].getHeight());
		}
	}

	@Test
	public void testSquareSkirt() {
		// needs only one rotaion to reach start state, so skirts all same.
		for(int i = 0; i < squares.length; i ++)
			assertTrue(Arrays.equals(new int[] {0, 0}, squares[i].getSkirt()));
	}

	// tests above this, checks correctness of constructor
	// and getBody is part of it, so its almost checks that method.
	// I think equal is the best way to check it too
	@Test
	public void testEqualsForSquare(){
		// easy to notice that they are equal
		assertTrue(squares[0].equals(squares[0]));
		assertTrue(squares[0].equals(squares[4]));
		assertFalse(squares[0].equals(new Integer[4]));
		// choose randomly one shape
		for(int i = 0; i < 100; i ++) {
			int chosenSquare = rand.nextInt(ROTATIONS_NUM);
			TPoint[] oldSquareBody = squares[chosenSquare].getBody(); // takes body
			List<TPoint> ls = swapListElementsRandomly(Arrays.asList(oldSquareBody));
			TPoint[] newSquareBody = new TPoint[ls.size()];
			ls.toArray(newSquareBody);
			assertTrue(squares[chosenSquare].equals(new Piece(newSquareBody))); //!!!!
			// here takes both bodies and checks them.
		}
	}

	@Test
	public void testSquareFastRotation(){
		// I already show that computeNextRotation works great and its rotations are
		// appropriate. Now compare that saved rotation to this fast Rotations!
		// If they are equal, it means that getPieces() works perfectly for Square too
		Piece root = arr[Piece.SQUARE];
		for(int i = 0; i < squares.length; i ++, root = root.fastRotation())
			assertTrue(squares[i].equals(root));
	}

	//
	// Stick piece start and its rotations
	//
	private static Piece sticks[];

	@BeforeClass
	public static void setUpStick() {
		// super.setUp();
		sticks = new Piece[ROTATIONS_NUM];
		sticks[0] = new Piece(Piece.STICK_STR); // start piece
		for(int i = 1; i < sticks.length; i++)
			sticks[i] = sticks[i - 1].computeNextRotation();
	}

	@Test
	public void testStickSize() {
		// Check width of pyr pieces
		// after rotations too
		for(int i = 0; i < sticks.length; i += 2){
			assertEquals(1, sticks[i].getWidth());
			assertEquals(4, sticks[i].getHeight());
		}
		for(int i = 1; i < sticks.length; i += 2){
			assertEquals(4, sticks[i].getWidth());
			assertEquals(1, sticks[i].getHeight());
		}
	}

	@Test
	public void testStickSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		// needs only two rotation to reach its start state, so first, third and fifth skirts - equal
		// and skirt is all 0 'case its stick ;d
		for(int i = 0; i < sticks.length; i += 2)
			assertTrue(Arrays.equals(new int[] {0}, sticks[i].getSkirt()));

		for(int i = 1; i < sticks.length; i += 2)
			assertTrue(Arrays.equals(new int[] {0, 0, 0, 0}, sticks[i].getSkirt()));
	}

	// tests above this, checks correctness of constructor
	// and getBody is part of it, so its almost checks that method.
	// I think equal is the best way to check it too
	@Test
	public void testEqualsForStick(){
		// easy to notice that they are equal
		assertTrue(sticks[0].equals(sticks[0]));
		assertTrue(sticks[0].equals(sticks[4]));
		assertFalse(sticks[0].equals(new Point[4]));
		// choose randomly one shape
		for(int i = 0; i < 100; i ++) {
			int chosenStick = rand.nextInt(ROTATIONS_NUM);
			TPoint[] oldStickBody = sticks[chosenStick].getBody(); // takes body
			List<TPoint> ls = swapListElementsRandomly(Arrays.asList(oldStickBody));
			TPoint[] newStickBody = new TPoint[ls.size()];
			ls.toArray(newStickBody);
			assertTrue(sticks[chosenStick].equals(new Piece(newStickBody))); //!!!!
			// here takes both bodies and checks them.
		}
	}

	@Test
	public void testStickFastRotation(){
		// I already show that computeNextRotation works great and its rotations are
		// appropriate. Now compare that saved rotation to this fast Rotations!
		// If they are equal, it means that getPieces() works perfectly for Stick too
		Piece root = arr[Piece.STICK];
		for(int i = 0; i < sticks.length; i ++, root = root.fastRotation())
			assertTrue(sticks[i].equals(root));
	}

	//
	// l1 piece start and its rotations
	//
	private static Piece lOne[];

	@BeforeClass
	public static void setUpLOne() {
		// super.setUp();
		lOne = new Piece[ROTATIONS_NUM];
		lOne[0] = new Piece(Piece.L1_STR); // start piece
		for(int i = 1; i < lOne.length; i++)
			lOne[i] = lOne[i - 1].computeNextRotation();
	}

	@Test
	public void testLOneSize() {
		// Check width of pyr pieces
		// after rotations too
		for(int i = 0; i < lOne.length; i += 2){
			assertEquals(2, lOne[i].getWidth());
			assertEquals(3, lOne[i].getHeight());
		}
		for(int i = 1; i < sOne.length; i += 2){
			assertEquals(3, lOne[i].getWidth());
			assertEquals(2, lOne[i].getHeight());
		}
	}

	@Test
	public void testLOneSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		assertTrue(Arrays.equals(new int[] {0, 0}, lOne[0].getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0}, lOne[4].getSkirt()));
		assertTrue(Arrays.equals(new int[] {2, 0}, lOne[2].getSkirt()));

		assertTrue(Arrays.equals(new int[] {0, 1, 1}, lOne[3].getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0, 0}, lOne[1].getSkirt()));
	}

	// tests above this, checks correctness of constructor
	// and getBody is part of it, so its almost checks that method.
	// I think equal is the best way to check it too
	@Test
	public void testEqualsForLOne(){
		// easy to notice that they are equal
		assertTrue(lOne[0].equals(lOne[0]));
		assertTrue(lOne[0].equals(lOne[4]));
		assertFalse(lOne[0].equals(new Point[4]));
		// choose randomly one shape
		for(int i = 0; i < 100; i ++) {
			int chosenLOne = rand.nextInt(ROTATIONS_NUM);
			TPoint[] oldLOneBody = lOne[chosenLOne].getBody(); // takes body
			List<TPoint> ls = swapListElementsRandomly(Arrays.asList(oldLOneBody));
			TPoint[] newLOneBody = new TPoint[ls.size()];
			ls.toArray(newLOneBody);
			assertTrue(lOne[chosenLOne].equals(new Piece(newLOneBody))); //!!!!
			// here takes both bodies and checks them.
		}
	}

	@Test
	public void testLOneFastRotation(){
		// I already show that computeNextRotation works great and its rotations are
		// appropriate. Now compare that saved rotation to this fast Rotations!
		// If they are equal, it means that getPieces() works perfectly for l1 too
		Piece root = arr[Piece.L1];
		for(int i = 0; i < lOne.length; i ++, root = root.fastRotation())
			assertTrue(lOne[i].equals(root));
	}

	//
	// l2 piece start and its rotations
	//
	private static Piece lTwo[];

	@BeforeClass
	public static void setUpLTwo() {
		// super.setUp();
		lTwo = new Piece[ROTATIONS_NUM];
		lTwo[0] = new Piece(Piece.L2_STR); // start piece
		for(int i = 1; i < lTwo.length; i++)
			lTwo[i] = lTwo[i - 1].computeNextRotation();
	}

	@Test
	public void testLTwoSize() {
		// Check width of pyr pieces
		// after rotations too
		for(int i = 0; i < lTwo.length; i += 2){
			assertEquals(2, lTwo[i].getWidth());
			assertEquals(3, lTwo[i].getHeight());
		}
		for(int i = 1; i < sOne.length; i += 2){
			assertEquals(3, lTwo[i].getWidth());
			assertEquals(2, lTwo[i].getHeight());
		}
	}

	@Test
	public void testLTwoSkirt() {
		// Note must use assertTrue(Arrays.equals(... as plain .equals does not work
		// right for arrays.
		// these situations are symmetric of l1
		assertTrue(Arrays.equals(new int[] {0, 0}, lTwo[0].getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 0}, lTwo[4].getSkirt()));
		assertTrue(Arrays.equals(new int[] {0, 2}, lTwo[2].getSkirt()));

		assertTrue(Arrays.equals(new int[] {0, 0, 0}, lTwo[3].getSkirt()));
		assertTrue(Arrays.equals(new int[] {1, 1, 0}, lTwo[1].getSkirt()));
	}

	// tests above this, checks correctness of constructor
	// and getBody is part of it, so its almost checks that method.
	// I think equal is the best way to check it too
	@Test
	public void testEqualsForLTwo(){
		// easy to notice that they are equal
		assertTrue(lTwo[0].equals(lTwo[0]));
		assertTrue(lTwo[0].equals(lTwo[4]));
		assertFalse(lTwo[0].equals(new Point[4]));
		// choose randomly one shape
		for(int i = 0; i < 100; i ++) {
			int chosenLTwo = rand.nextInt(ROTATIONS_NUM);
			TPoint[] oldLTwoBody = lTwo[chosenLTwo].getBody(); // takes body
			List<TPoint> ls = swapListElementsRandomly(Arrays.asList(oldLTwoBody));
			TPoint[] newLTwoBody = new TPoint[ls.size()];
			ls.toArray(newLTwoBody);
			assertTrue(lTwo[chosenLTwo].equals(new Piece(newLTwoBody))); //!!!!
			// here takes both bodies and checks them.
		}
	}

	@Test
	public void testLTwoFastRotation(){
		// I already show that computeNextRotation works great and its rotations are
		// appropriate. Now compare that saved rotation to this fast Rotations!
		// If they are equal, it means that getPieces() works perfectly for l2 too
		Piece root = arr[Piece.L2];
		for(int i = 0; i < lTwo.length; i ++, root = root.fastRotation())
			assertTrue(lTwo[i].equals(root));
	}

	//
	// exceptions
	// Checks if Exception is thrown when constructor's argument(string)
	// is invalid for the piece class.
	//
	@Test(expected = Exception.class)
	public void testInvalidFormat(){
		String STICK_INVALID_STR = "0 0	0 1	D 0 2  0 3";
		Piece newPiece = new Piece(STICK_INVALID_STR);
	}
}
