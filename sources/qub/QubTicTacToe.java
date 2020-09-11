package qub;

import java.util.Arrays;

/**
 * The main tic-tac-toe application driver.
 */
public interface QubTicTacToe
{
    String applicationName = "qub-tictactoe";
    String applicationDescription = "Play the game tic-tac-toe.";

    static void main(String[] args)
    {
        QubProcess.run(args, QubTicTacToe::getParameters, QubTicTacToe::run);
    }

    static QubTicTacToeParameters getParameters(QubProcess process)
    {
        PreCondition.assertNotNull(process, "process");

        final CommandLineParameters parameters = process.createCommandLineParameters()
            .setApplicationName(QubTicTacToe.applicationName)
            .setApplicationDescription(QubTicTacToe.applicationDescription);
        final CommandLineParameterProfiler profiler = parameters.addProfiler(process, QubTicTacToe.class);
        final CommandLineParameterHelp help = parameters.addHelp();

        QubTicTacToeParameters result = null;
        if (!help.showApplicationHelpLines(process).await())
        {
            profiler.await();

            final CharacterWriteStream output = process.getOutputWriteStream();
            final CharacterReadStream input = process.getInputReadStream();
            result = QubTicTacToeParameters.create(output, input);
        }

        return result;
    }

    static void run(QubTicTacToeParameters parameters)
    {
        PreCondition.assertNotNull(parameters, "parameters");

        final CharacterWriteStream output = parameters.getOutputWriteStream();
        final CharacterReadStream input = parameters.getInputReadStream();

        output.writeLine("Welcome to Qub Tic-Tac-Toe!").await();
        output.writeLine().await();

        final QubTicTacToeModel model = QubTicTacToeModel.create();

        while (!model.isGameDone())
        {
            QubTicTacToe.writeBoard(model, output);

            QubTicTacToe.makeMove(model, output, input);
        }

        QubTicTacToe.showGameResults(model, output);
    }

    static void writeBoard(QubTicTacToeModel model, CharacterWriteStream writeStream)
    {
        PreCondition.assertNotNull(model, "model");
        PreCondition.assertNotNull(writeStream, "writeStream");

        final CharacterTableFormat boardFormat = CharacterTableFormat.create()
            .setColumnSeparator(" | ")
            .setRowSeparator('-')
            .setNewLine('\n');

        final CharacterTable board = CharacterTable.create();

        final List<String> topRow = List.create(" ");
        for (int column = 0; column < model.getColumnCount(); ++column)
        {
            topRow.add(Integers.toString(column + 1));
        }
        board.addRow(topRow);

        for (int row = 0; row < model.getRowCount(); ++row)
        {
            final List<String> rowCells = List.create(Characters.toString((char)('A' + row)));
            for (int column = 0; column < model.getColumnCount(); ++column)
            {
                rowCells.add(model.getBoardCell(row, column));
            }
            board.addRow(rowCells);
        }
        board.toString(writeStream, boardFormat).await();
        writeStream.writeLine().await();
        writeStream.writeLine().await();
    }

    static void makeMove(QubTicTacToeModel model, CharacterWriteStream output, CharacterReadStream input)
    {
        PreCondition.assertNotNull(model, "model");
        PreCondition.assertNotNull(output, "output");
        PreCondition.assertNotNull(input, "input");

        boolean validMove = false;
        while (!model.isGameDone() && !validMove)
        {
            output.write(model.getTurnNumber() + ") " + model.getCurrentPlayer() + "'s move: ").await();

            final String moveText = input.readLine().await().trim();
            if (moveText.equalsIgnoreCase("exit"))
            {
                model.setGameDone(true);
            }
            else if (!Strings.isNullOrEmpty(moveText))
            {
                Integer row = null;
                Integer column = null;
                for (final char moveCharacter : Strings.iterate(moveText))
                {
                    if (!Characters.isWhitespace(moveCharacter))
                    {
                        final char lowerMoveCharacter = Characters.toLowerCase(moveCharacter);
                        if ('1' <= lowerMoveCharacter && lowerMoveCharacter <= '3')
                        {
                            if (column == null)
                            {
                                column = (lowerMoveCharacter - '1');
                                validMove = (row != null);
                            }
                            else
                            {
                                break;
                            }
                        }
                        else if ('a' <= lowerMoveCharacter && lowerMoveCharacter <= 'c')
                        {
                            if (row == null)
                            {
                                row = (lowerMoveCharacter - 'a');
                                validMove = (column != null);
                            }
                            else
                            {
                                break;
                            }
                        }
                        else
                        {
                            break;
                        }
                    }
                }

                if (!validMove)
                {
                    output.writeLine(Strings.escapeAndQuote(moveText) + " is not a valid space on the board.").await();
                }
                else
                {
                    final String tokenAtSpace = model.getBoardCell(row, column);
                    if (!tokenAtSpace.equals(" "))
                    {
                        output.writeLine(Strings.escapeAndQuote(moveText) + " is already taken by " + Strings.escapeAndQuote(tokenAtSpace) + ".").await();
                        validMove = false;
                    }
                    else
                    {
                        model.makeMove(row, column);
                    }
                }

                output.writeLine().await();
            }
        }
    }

    static void showGameResults(QubTicTacToeModel model, CharacterWriteStream output)
    {
        PreCondition.assertNotNull(model, "model");
        PreCondition.assertNotNull(output, "output");

        if (model.hasWinner())
        {
            QubTicTacToe.writeBoard(model, output);
            output.writeLine(model.getWinner() + " wins!").await();
        }
        else if (model.isTie())
        {
            QubTicTacToe.writeBoard(model, output);
            output.writeLine("No more moves. It's a tie!").await();
        }
        output.writeLine().await();
    }
}
