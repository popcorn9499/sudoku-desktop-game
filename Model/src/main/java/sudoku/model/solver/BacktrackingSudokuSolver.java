package sudoku.model.solver;

import sudoku.model.exceptions.FillingBoardSudokuException;
import sudoku.model.models.SudokuBoard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BacktrackingSudokuSolver implements SudokuSolver {

    private static final Logger logger = LoggerFactory.getLogger(BacktrackingSudokuSolver.class);
    final int sudokuBoardSize = SudokuBoard.BOARD_SIZE;

    @Override
    public void solve(SudokuBoard board) throws FillingBoardSudokuException {
        logger.info("solver called");
        fillBoard(board);
    }

    @Override
    public void fillBoard(SudokuBoard board) throws FillingBoardSudokuException {
        logger.info("fillBoard called");
        if (!fillBoardRecursive(0, 0, board)) {
            throw new FillingBoardSudokuException();
        }
    }

    private List<Integer> generateRandomNumbers() {
        List<Integer> nums = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Collections.shuffle(nums);
        return nums;
    }

    private boolean fillBoardRecursive(int row, int col, SudokuBoard board) {
        // if we have filled all the rows for the current column, move to the next
        // column, if we have filled all the columns, we are done
        if (row == sudokuBoardSize) {
            row = 0;
            if (++col == sudokuBoardSize) {
                return true;
            }
        }

        // if the current cell is already filled, move to the next cell
        if (board.getField(col, row).getValue() != 0) {
            return fillBoardRecursive(row + 1, col, board);
        }

        List<Integer> randomNumbers = generateRandomNumbers();
        for (int num : randomNumbers) {
            if (isValidPlacement(row, col, num, board)) {
                board.getField(col, row).setValue(num);
                /*
                 * if the number is valid, number stays in that cell and move to the next cell,
                 * if based on number in current cell, we can't fill the rest of board, we
                 * backtrack and
                 * try a different number for the current cell
                 */
                if (fillBoardRecursive(row + 1, col, board)) {
                    return true;
                }
                board.getField(col, row).setValue(0);
            }
        }
        return false;
    }

    private boolean isValidPlacement(int row, int col, int num, SudokuBoard board) {
        // when dividing on ints, we truncate the result
        final int boxIndex = (col / 3) + (row / 3) * 3;

        int previousValue = board.getField(col, row).getValue();
        board.getField(col, row).setValue(num);

        boolean validPlacement = board.getRow(row).verify()
                && board.getColumn(col).verify()
                && board.getBox(boxIndex).verify();

        // bring back the value
        board.getField(col, row).setValue(previousValue);

        return validPlacement;
    }
}
