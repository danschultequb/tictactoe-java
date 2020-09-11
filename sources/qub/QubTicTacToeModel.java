package qub;

public class QubTicTacToeModel
{
    private boolean gameDone;
    private String winner;
    private int turnNumber;
    private final Array<String> players;
    private final int boardWidth;
    private final int boardHeight;
    private final Array<Array<String>> boardData;

    private QubTicTacToeModel()
    {
        this.turnNumber = 1;
        this.players = Array.create("X", "O");

        this.boardWidth = 3;
        this.boardHeight = 3;
        this.boardData = Array.createWithLength(this.boardHeight);
        for (int row = 0; row < this.boardHeight; ++row)
        {
            final Array<String> boardRowData = Array.createWithLength(this.boardWidth);
            for (int column = 0;  column < this.boardWidth; ++column)
            {
                boardRowData.set(column, " ");
            }
            this.boardData.set(row, boardRowData);
        }
    }

    public static QubTicTacToeModel create()
    {
        return new QubTicTacToeModel();
    }

    public int getRowCount()
    {
        return this.boardHeight;
    }

    public int getColumnCount()
    {
        return this.boardWidth;
    }

    public void setGameDone(boolean gameDone)
    {
        this.gameDone = gameDone;
    }

    public boolean isGameDone()
    {
        return this.gameDone;
    }

    public int getTurnNumber()
    {
        return this.turnNumber;
    }

    public String getCurrentPlayer()
    {
        final int turnIndex = this.turnNumber - 1;
        return this.players.get(turnIndex % this.players.getCount());
    }

    public String getBoardCell(int row, int column)
    {
        PreCondition.assertBetween(0, row, this.boardHeight, "row");
        PreCondition.assertBetween(0, column, this.boardWidth, "column");

        return this.boardData.get(row).get(column);
    }

    public void makeMove(int row, int column)
    {
        this.setBoardCell(row, column, this.getCurrentPlayer());
    }

    public void setBoardCell(int row, int column, String player)
    {
        PreCondition.assertBetween(0, row, this.boardHeight, "row");
        PreCondition.assertBetween(0, column, this.boardWidth, "column");
        PreCondition.assertEqual(this.getCurrentPlayer(), player, "player");
        PreCondition.assertEqual(" ", this.getBoardCell(row, column), "this.getBoardCell(row, column)");
        PreCondition.assertFalse(this.isGameDone(), "this.isGameDone()");

        this.boardData.get(row).set(column, player);

        if (this.isWinner(player))
        {
            this.winner = player;
            this.gameDone = true;
        }
        else if (this.isTie())
        {
            this.gameDone = true;
        }
        else
        {
            this.turnNumber++;
        }
    }

    public boolean hasWinner()
    {
        return !Strings.isNullOrEmpty(this.winner);
    }

    public String getWinner()
    {
        return this.winner;
    }

    public boolean isTie()
    {
        return (this.boardWidth * this.boardHeight) == this.turnNumber;
    }

    public boolean isWinner(String player)
    {
        PreCondition.assertOneOf(player, this.players, "player");

        boolean result = false;
        if (!result)
        {
            for (int row = 0; row < this.boardHeight; ++row)
            {
                boolean isWinningRow = true;
                for (int column = 0; column < this.boardWidth; ++column)
                {
                    if (!player.equals(this.getBoardCell(row, column)))
                    {
                        isWinningRow = false;
                        break;
                    }
                }
                if (isWinningRow)
                {
                    result = true;
                    break;
                }
            }
        }
        if (!result)
        {
            for (int column = 0; column < this.boardWidth; ++column)
            {
                boolean isWinningColumn = true;
                for (int row = 0; row < this.boardHeight; ++row)
                {
                    if (!player.equals(this.getBoardCell(row, column)))
                    {
                        isWinningColumn = false;
                        break;
                    }
                }
                if (isWinningColumn)
                {
                    result = true;
                    break;
                }
            }
        }
        if (!result)
        {
            result = player.equals(this.getBoardCell(0, 0)) &&
                player.equals(this.getBoardCell(1, 1)) &&
                player.equals(this.getBoardCell(2, 2));
        }
        if (!result)
        {
            result = player.equals(this.getBoardCell(0, 2)) &&
                player.equals(this.getBoardCell(1, 1)) &&
                player.equals(this.getBoardCell(2, 0));
        }

        return result;
    }
}
