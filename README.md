# UnPassant

Un Passant is a prototype tool for assisting in solving retrograde analysis chess problems created in Java.

Given a FEN, it will attempt to find the last _n_ single moves, where _n_ is the number input.

Its functionality is detailed herein.

1. Illegal positions
2. Additional Criteria
3. Usage



1. Illegal positions

Un Passant checks for the following:
    1. Pawn structures that require too many captures to have taken place
    2. Pieces that can neither be original nor promoted
    3. Pieces impossibly captured by pawns
    4. Promoted pieces with no way of promoting
    5. Pieces that have been moved by promoting pieces

There will be many illegal positions that Un Passant will fail to detect.

2. Additional Criteria

If the retro you are attempting to solve has additional criteria, such as that no piece has promoted during the game, Un Passant can take these as inputs.

Supported additional criteria include:
Un-move criteria - any restrictions on how pieces have moved in the last few turns
Board state criteria - restrictions either on the number of promotions or specifying that a given side can castle

How to input these is detailed in part 3.

3. Usage
Un Passant in its current form is run from the command line.

To compile Un Passant, go to UnPassant/UnPassant/src, and enter the following into the command line:
   javac ReverseChess/StandardChess/*.java ReverseChess/StandardChess/StandardPieces/*.java ChessHeuristics/Heuristics/*.java ChessHeuristics/Heuristics/Deductions/*.java ChessHeuristics/Heuristics/Detector/*.java ChessHeuristics/Heuristics/Detector/Data/*.java ChessHeuristics/SolverAlgorithm/*.java ChessHeuristics/SolverAlgorithm/UnMoveConditions/*.java

From the same folder run ChessHeuristics/SolverAlgorithm/Main

The user will be prompted to enter a FEN. This should include at a minimum the piece positions, followed by ‘b’ or ‘w’ for whose turn it is next to un-move.


If the board state is immediately found to be illegal, the user will be told so. Otherwise, all immediately deducible information will be printed.
The user will then be prompted to input the depth, additional depth, and number of solutions to be found.

Depth is the number of previous single moves found in a solution.

Additional depth is the amount of extra previous single moves to be checked, to ensure that a found solution still has possible previous single moves.

The number of solutions to be found the maximum number of solutions Un Passant will return, if there are mulitple possible solutions.

Following that the user will be asked if they want to input an un-move condition. If so, for the prototype it should be formatted as follows:

1. The depth being checked from, starting at 0.
2. The depth being checked to. This is non-inclusive.
3. The player whose move is in question, ‘b’, ‘w’, or ‘-‘ if it is either player.
4. The starting coordinate in question, if an un-move from a specific location is being queried. Formatted ‘a1’, ‘h7’, etc. ‘-‘ should be used if the location doesn’t matter.
5. The end coordinate in question, if an un-move to a specific location is being queried. Formatted as above.
6. The type of piece being moved, formatted as the first letter of the piece’s type capitalised, unless it’s a knight, in which case ‘N’. ‘-‘ if the piece does not matter.
7. The type of piece being captured, if any, formatted as above.
8. What kind of move is being checked:
   •	Normal un-move = ‘-‘
   •	Un-capture = ‘x’
   •	En passant un-capture = ‘e.p.’
   •	Kingside castle = ‘O-O’
   •	Queenside castle = ‘O-O-O’
   •	Any kind of move = ‘any’
9. Whether the condition is negative – ‘true’ if the un-move is legal if the criteria are fulfilled, ‘false’ if it is illegal.
   These should be separated by commas and no spaces.
   This works as follows:
   •	If the current depth is not between the values given for 1 and 2, the check is ignored.
   •	Otherwise, if all other criteria are met, return value 9. If all other criteria are not met, return the opposite of value 9.
   The user will be asked again if they want to enter an un-move criterion. Multiple can be given, and will be joined by a logical AND.
   For example, if no piece may capture on un-moves up to a depth of 5:
   0,5,-,-,-,-,-,x,false
   Or if queens cannot move on un-moves up to a depth of 3:
   0,3,w,-,-,Q,-,any,false

The user will then be asked if they want to enter a board state criterion. For the prototype, the user will be asked if this is for castling or promoting.
For castling, the user will be asked which player must be able castle, ‘b’ or ‘w’. The board state will be considered invalid if a player who must be able to castle can be proved to not be able to.
For promotion, the user can either enter ‘n’ if no promotions of any kind by either colour can be made. Otherwise, the criterion should be formatted as follows:

1. The player whose pieces are in question, ‘w’, ‘b’, or ‘-‘ for either.
2. The type of piece in question. This is the piece’s full type, ‘queen’, ‘rook’, etc. Enter ‘-‘ if the type of piece does not matter.
3. The number of pieces of the given type belonging to the given player that may be promoted, e.g. 0 if no pieces may be promoted.

If more pieces of the given kind for the given player are found to be promoted, the board state will be considered illegal.

The program will then be run, and any solutions found will be printed. Otherwise, the user will be told the position is illegal. 
This UI is a proof of concept for demonstrating Un Passant’s functionality and has not been thoroughly tested.

