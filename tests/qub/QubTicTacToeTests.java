package qub;

public interface QubTicTacToeTests
{
    static void test(TestRunner runner)
    {
        runner.testGroup(QubTicTacToe.class, () ->
        {
            runner.testGroup("main(String[])", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> QubTicTacToe.main(null),
                        new PreConditionFailure("arguments cannot be null."));
                });
            });

            runner.testGroup("getParameters(QubProcess)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> QubTicTacToe.getParameters(null),
                        new PreConditionFailure("process cannot be null."));
                });

                runner.test("with -?", (Test test) ->
                {
                    try (final QubProcess process = QubProcess.create("-?"))
                    {
                        final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                        final InMemoryCharacterToByteStream input = InMemoryCharacterToByteStream.create().endOfStream();

                        process.setOutputWriteStream(output);
                        process.setInputReadStream(input);

                        test.assertNull(QubTicTacToe.getParameters(process));

                        test.assertEqual(
                            Iterable.create(
                                "Usage: qub-tictactoe [--profiler] [--help]",
                                "  Play the game tic-tac-toe.",
                                "  --profiler: Whether or not this application should pause before it is run to allow a profiler to be attached.",
                                "  --help(?):  Show the help message for this application."
                            ),
                            Strings.getLines(output.getText().await()));
                    }
                });

                runner.test("with no command-line arguments", (Test test) ->
                {
                    try (final QubProcess process = QubProcess.create())
                    {
                        final InMemoryCharacterToByteStream output = InMemoryCharacterToByteStream.create();
                        final InMemoryCharacterToByteStream input = InMemoryCharacterToByteStream.create().endOfStream();

                        process.setOutputWriteStream(output);
                        process.setInputReadStream(input);

                        final QubTicTacToeParameters parameters = QubTicTacToe.getParameters(process);

                        test.assertNotNull(parameters);
                        test.assertSame(output, parameters.getOutputWriteStream());
                        test.assertSame(input, parameters.getInputReadStream());

                        test.assertEqual("", output.getText().await());
                    }
                });
            });

            runner.testGroup("run(QubTicTacToeParameters)", () ->
            {
                runner.test("with null", (Test test) ->
                {
                    test.assertThrows(() -> QubTicTacToe.run(null),
                        new PreConditionFailure("parameters cannot be null."));
                });

                runner.test("with empty move", (Test test) ->
                {
                    final InMemoryCharacterStream output = InMemoryCharacterStream.create();
                    final QubTicTacToeParameters parameters = QubTicTacToeTests.createParameters(output, Iterable.create(
                        "",
                        "exit"));

                    QubTicTacToe.run(parameters);

                    test.assertEqual(
                        Iterable.create(
                            "Welcome to Qub Tic-Tac-Toe!",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A |   |   |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "1) X's move: ",
                            "1) X's move: exit",
                            ""),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with empty then valid move", (Test test) ->
                {
                    final InMemoryCharacterStream output = InMemoryCharacterStream.create();
                    final QubTicTacToeParameters parameters = QubTicTacToeTests.createParameters(output, Iterable.create(
                        "",
                        "a1",
                        "exit"));

                    QubTicTacToe.run(parameters);

                    test.assertEqual(
                        Iterable.create(
                            "Welcome to Qub Tic-Tac-Toe!",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A |   |   |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "1) X's move: ",
                            "1) X's move: a1",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | X |   |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "2) O's move: exit",
                            ""),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with invalid board space (d1)", (Test test) ->
                {
                    final InMemoryCharacterStream output = InMemoryCharacterStream.create();
                    final QubTicTacToeParameters parameters = QubTicTacToeTests.createParameters(output, Iterable.create(
                            "d1",
                            "exit"));

                    QubTicTacToe.run(parameters);

                    test.assertEqual(
                        Iterable.create(
                            "Welcome to Qub Tic-Tac-Toe!",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A |   |   |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "1) X's move: d1",
                            "\"d1\" is not a valid space on the board.",
                            "",
                            "1) X's move: exit",
                            ""),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with invalid board space (2c)", (Test test) ->
                {
                    final InMemoryCharacterStream output = InMemoryCharacterStream.create();
                    final QubTicTacToeParameters parameters = QubTicTacToeTests.createParameters(output, Iterable.create(
                            "d1",
                            "exit"));

                    QubTicTacToe.run(parameters);

                    test.assertEqual(
                        Iterable.create(
                            "Welcome to Qub Tic-Tac-Toe!",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A |   |   |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "1) X's move: d1",
                            "\"d1\" is not a valid space on the board.",
                            "",
                            "1) X's move: exit",
                            ""),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with already taken board space", (Test test) ->
                {
                    final InMemoryCharacterStream output = InMemoryCharacterStream.create();
                    final QubTicTacToeParameters parameters = QubTicTacToeTests.createParameters(output, Iterable.create(
                            "2b",
                            "2b",
                            "exit"));

                    QubTicTacToe.run(parameters);

                    test.assertEqual(
                        Iterable.create(
                            "Welcome to Qub Tic-Tac-Toe!",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A |   |   |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "1) X's move: 2b",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A |   |   |  ",
                            "-------------",
                            "B |   | X |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "2) O's move: 2b",
                            "\"2b\" is already taken by \"X\".",
                            "",
                            "2) O's move: exit",
                            ""),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with tie game", (Test test) ->
                {
                    final InMemoryCharacterStream output = InMemoryCharacterStream.create();
                    final QubTicTacToeParameters parameters = QubTicTacToeTests.createParameters(output, Iterable.create(
                            "1a",
                            "2a",
                            "3a",
                            "2b",
                            "1b",
                            "3b",
                            "2c",
                            "1c",
                            "3c"));

                    QubTicTacToe.run(parameters);

                    test.assertEqual(
                        Iterable.create(
                            "Welcome to Qub Tic-Tac-Toe!",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A |   |   |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "1) X's move: 1a",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | X |   |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "2) O's move: 2a",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | X | O |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "3) X's move: 3a",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | X | O | X",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "4) O's move: 2b",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | X | O | X",
                            "-------------",
                            "B |   | O |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "5) X's move: 1b",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | X | O | X",
                            "-------------",
                            "B | X | O |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "6) O's move: 3b",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | X | O | X",
                            "-------------",
                            "B | X | O | O",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "7) X's move: 2c",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | X | O | X",
                            "-------------",
                            "B | X | O | O",
                            "-------------",
                            "C |   | X |  ",
                            "",
                            "8) O's move: 1c",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | X | O | X",
                            "-------------",
                            "B | X | O | O",
                            "-------------",
                            "C | O | X |  ",
                            "",
                            "9) X's move: 3c",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | X | O | X",
                            "-------------",
                            "B | X | O | O",
                            "-------------",
                            "C | O | X | X",
                            "",
                            "No more moves. It's a tie!",
                            ""),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with top row winning game", (Test test) ->
                {
                    final InMemoryCharacterStream output = InMemoryCharacterStream.create();
                    final QubTicTacToeParameters parameters = QubTicTacToeTests.createParameters(output, Iterable.create(
                            "1a",
                            "1b",
                            "2a",
                            "2b",
                            "3a"));

                    QubTicTacToe.run(parameters);

                    test.assertEqual(
                        Iterable.create(
                            "Welcome to Qub Tic-Tac-Toe!",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A |   |   |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "1) X's move: 1a",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | X |   |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "2) O's move: 1b",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | X |   |  ",
                            "-------------",
                            "B | O |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "3) X's move: 2a",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | X | X |  ",
                            "-------------",
                            "B | O |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "4) O's move: 2b",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | X | X |  ",
                            "-------------",
                            "B | O | O |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "5) X's move: 3a",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | X | X | X",
                            "-------------",
                            "B | O | O |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "X wins!",
                            ""),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with middle row winning game", (Test test) ->
                {
                    final InMemoryCharacterStream output = InMemoryCharacterStream.create();
                    final QubTicTacToeParameters parameters = QubTicTacToeTests.createParameters(output, Iterable.create(
                            "1b",
                            "1a",
                            "2b",
                            "2a",
                            "3b"));

                    QubTicTacToe.run(parameters);

                    test.assertEqual(
                        Iterable.create(
                            "Welcome to Qub Tic-Tac-Toe!",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A |   |   |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "1) X's move: 1b",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A |   |   |  ",
                            "-------------",
                            "B | X |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "2) O's move: 1a",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | O |   |  ",
                            "-------------",
                            "B | X |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "3) X's move: 2b",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | O |   |  ",
                            "-------------",
                            "B | X | X |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "4) O's move: 2a",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | O | O |  ",
                            "-------------",
                            "B | X | X |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "5) X's move: 3b",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | O | O |  ",
                            "-------------",
                            "B | X | X | X",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "X wins!",
                            ""),
                        Strings.getLines(output.getText().await()));
                });

                runner.test("with bottom row winning game", (Test test) ->
                {
                    final InMemoryCharacterStream output = InMemoryCharacterStream.create();
                    final QubTicTacToeParameters parameters = QubTicTacToeTests.createParameters(output, Iterable.create(
                            "1c",
                            "1a",
                            "2c",
                            "2a",
                            "3c"));

                    QubTicTacToe.run(parameters);

                    test.assertEqual(
                        Iterable.create(
                            "Welcome to Qub Tic-Tac-Toe!",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A |   |   |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C |   |   |  ",
                            "",
                            "1) X's move: 1c",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A |   |   |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C | X |   |  ",
                            "",
                            "2) O's move: 1a",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | O |   |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C | X |   |  ",
                            "",
                            "3) X's move: 2c",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | O |   |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C | X | X |  ",
                            "",
                            "4) O's move: 2a",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | O | O |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C | X | X |  ",
                            "",
                            "5) X's move: 3c",
                            "",
                            "  | 1 | 2 | 3",
                            "-------------",
                            "A | O | O |  ",
                            "-------------",
                            "B |   |   |  ",
                            "-------------",
                            "C | X | X | X",
                            "",
                            "X wins!",
                            ""),
                        Strings.getLines(output.getText().await()));
                });
            });
        });
    }

    static QubTicTacToeParameters createParameters(CharacterWriteStream writeStream, Iterable<String> inputLines)
    {
        PreCondition.assertNotNull(writeStream, "writeStream");
        PreCondition.assertNotNull(inputLines, "inputLines");

        final String inputText = Strings.join(inputLines.map(line -> line + '\n'));
        final CharacterReadStream input = EchoCharacterReadStream.create(
                        InMemoryCharacterStream.create(inputText).endOfStream(),
                        writeStream);
        return QubTicTacToeParameters.create(writeStream, input);
    }
}
