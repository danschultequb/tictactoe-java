package qub;

public class QubTicTacToeParameters
{
    private final CharacterWriteStream outputWriteStream;
    private final CharacterReadStream inputReadStream;

    private QubTicTacToeParameters(CharacterWriteStream outputWriteStream, CharacterReadStream inputReadStream)
    {
        PreCondition.assertNotNull(outputWriteStream, "outputWriteStream");
        PreCondition.assertNotNull(inputReadStream, "inputReadStream");

        this.outputWriteStream = outputWriteStream;
        this.inputReadStream = inputReadStream;
    }

    public static QubTicTacToeParameters create(CharacterWriteStream outputWriteStream, CharacterReadStream inputReadStream)
    {
        return new QubTicTacToeParameters(outputWriteStream, inputReadStream);
    }

    /**
     * Get the CharacterWriteStream that output will be written to.
     * @return The CharacterWriteStream that output will be written to.
     */
    public CharacterWriteStream getOutputWriteStream()
    {
        return this.outputWriteStream;
    }

    /**
     * Get the CharacterReadStream that input will be read from.
     * @return The CharacterReadStream that input will be read from.
     */
    public CharacterReadStream getInputReadStream()
    {
        return this.inputReadStream;
    }
}
