package pl.edu.agh.tkk17.sample;

import javax.swing.text.Style;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class Scanner implements Iterator<Token>, Iterable<Token>
{
    private InputStream input;
    private int position;
    private char character;
    private boolean end;
    private String tempString;

    public String getTempString() {
        return this.tempString;
    }

    public void setTempString(String t) {
        this.tempString = t;
    }

    public Scanner(InputStream input)
    {
        this.input = input;
        this.position = -1;
        this.end = false;
        this.readChar();
    }

    private void readChar()
    {
        try {
            int character = this.input.read();
            this.position += 1;
            boolean end = character < 0;
            this.end = end;
            if (!end) {
                this.character = (char) character;
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Scanner input exception.", e);
        }
    }

    public boolean hasNext()
    {
        return !this.end;
    }

    public Token next()
    {
        if (this.end) {
            throw new NoSuchElementException("Scanner input ended.");
        }
        while(Character.isSpaceChar(this.character)) {
            this.readChar();
        }

        char character = this.character;
        Token token;

        if (character == '+') {
            token = this.makeToken(TokenType.ADD);
            this.readChar();
        } else if (character == '-') {
            token = this.makeToken(TokenType.SUB);
            this.readChar();
        } else if (character == '*') {
            token = this.makeToken(TokenType.MUL);
            this.readChar();
        } else if (character >= '0' && character <= '9') {
            while ((this.character >= '0' && this.character <= '9') || this.character == '.') {
                String value = String.valueOf(this.character);
                this.setTempString(this.getTempString() != null ? this.getTempString() + value : value);
                this.readChar();
            }
            token = this.makeToken(TokenType.NUM, getTempString());
            setTempString(null);
        } else if (character == '/') {
            token = this.makeToken(TokenType.DIV);
            this.readChar();
        } else if (character == '(') {
            token = this.makeToken(TokenType.LBR);
            this.readChar();
        } else if (character == ')') {
            token = this.makeToken(TokenType.RBR);
            this.readChar();
        } else if (character == '\n' || character == '\u0000') {
            token = this.makeToken(TokenType.END);
            this.readChar();
        } else {
            String location = this.locate(this.position);
            throw new UnexpectedCharacterException(character, location);
        }

        return token;

    }

    public Iterator<Token> iterator()
    {
        return this;
    }

    private Token makeToken(TokenType type)
    {
        return new Token(type, this.position);
    }

    private Token makeToken(TokenType type, String value)
    {
        return new Token(type, this.position, value);
    }

    private static String locate(int position)
    {
        return String.valueOf(position);
    }
}
