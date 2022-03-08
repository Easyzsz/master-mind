package src;

import codedraw.CodeDraw;
import codedraw.Palette;
import codedraw.textformat.HorizontalAlign;
import codedraw.textformat.TextFormat;
import codedraw.textformat.VerticalAlign;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.util.Arrays;

public class MasterMind {
    private static final int NUMBER_OF_TURNS = 10;
    private static final int CODE_LENGTH = 4;
    // private static final int NUMBER_OF_COLUMNS = CODE_LENGTH * 2 + 1;
    private static final Color[] COLORS = new Color[] { Color.BLUE, Color.CYAN, Color.GREEN, Color.MAGENTA,
            Color.ORANGE, Color.DARK_GRAY, Color.RED, Color.PINK, Color.YELLOW };

    private static int[][] playField = null;
    private static int[][] tips = null;
    private static int turn = 0;
    // private static int pin = 0;
    private static int[] solution = null;

    private static void initGame() {
        playField = new int[NUMBER_OF_TURNS][CODE_LENGTH];
        tips = new int[NUMBER_OF_TURNS][CODE_LENGTH]; // 1 == red; 2 == white
        turn = 0;
        // pin = 0;
        solution = generateCode();
    }

    // generates random solution array with numbers from 1 to 9
    private static int[] generateCode() {
        int[] codeArray = new int[CODE_LENGTH];
        int index = 0;
        while (index < codeArray.length) {
            int value = (int) ((Math.random() * (COLORS.length)) + 1);
            boolean unique = true;
            for (int element : codeArray) {
                if (element == value) {
                    unique = false;
                    break;
                }
            }

            if (unique) {
                codeArray[index] = value;
                index++;
            }
        }
        return codeArray;
    }

    private static void updateTips() {
        if (turn >= 0 && turn < NUMBER_OF_TURNS && tips[turn] != null && playField[turn] != null && solution != null
                && tips[turn].length == CODE_LENGTH && solution.length == CODE_LENGTH
                && playField[turn].length == CODE_LENGTH) {

            int counter = 0;
            boolean flag = false;
            for (int i = 0; i < solution.length; i++) {
                if (solution[i] == playField[turn][i]) {
                    tips[turn][counter] = 1;
                    counter++;
                } else {
                    flag = true;
                }
            }

            if (flag) {
                for (int i = 0; i < solution.length; i++) {
                    for (int j = 0; j < solution.length; j++) {
                        if (solution[i] == playField[turn][j] && j != i) {
                            tips[turn][counter] = 2;
                            counter++;
                        }
                    }
                }
            }
        }
    }

    private static void drawGame(CodeDraw myDrawObj) {
        if (playField != null && tips != null && playField.length == tips.length
                && playField[0].length == tips[0].length) {

            int playSpace = myDrawObj.getHeight();
            int elementSize = playSpace / NUMBER_OF_TURNS;
            int radius = elementSize / 2;
            int SIDES = 10;
            int codeSpace = radius * 2 * CODE_LENGTH + SIDES * 2;
            int spacing = (playSpace / 2 - codeSpace) / (CODE_LENGTH - 1);

            myDrawObj.setColor(Color.lightGray);
            myDrawObj.fillRectangle(0, 0, playSpace, playSpace);

            for (int i = 0; i < playField.length; i++) {
                for (int j = 0; j < playField[i].length; j++) {
                    if (playField[i][j] == 0) {
                        myDrawObj.setColor(Color.WHITE);
                    } else {
                        myDrawObj.setColor(COLORS[playField[i][j] - 1]);
                    }
                    int xCord = SIDES + radius + (spacing + elementSize) * j;
                    int yCord = playSpace - (radius + elementSize * i);
                    myDrawObj.fillCircle(xCord, yCord, radius);
                    myDrawObj.setColor(Color.BLACK);
                    myDrawObj.drawCircle(xCord, yCord, radius);
                }
            }

            for (int i = 0; i < tips.length; i++) {
                for (int j = 0; j < tips[i].length; j++) {
                    int xCord = SIDES + radius + (spacing + elementSize) * j + (playSpace / 2);
                    int yCord = playSpace - (radius + elementSize * i);
                    if (tips[i][j] == 1) {
                        myDrawObj.setColor(Color.RED);
                        myDrawObj.fillCircle(xCord, yCord, spacing);
                    } else if (tips[i][j] == 2) {
                        myDrawObj.setColor(Color.WHITE);
                        myDrawObj.fillCircle(xCord, yCord, spacing);
                    }
                }
            }

            for (int i = 0; i < COLORS.length; i++) {
                myDrawObj.setColor(COLORS[i]);
                myDrawObj.fillRectangle(playSpace, elementSize * i, elementSize, elementSize);
            }
            myDrawObj.drawImage(playSpace, elementSize * (COLORS.length), elementSize, elementSize,
                    "./src/back_button.png");

            myDrawObj.show();
        }
    }

    private static void processGameStep(CodeDraw myDrawObj, MouseEvent me) {
        int[] clickPos = new int[2];
        clickPos[0] = me.getX();
        clickPos[1] = me.getY();

        // int width = myDrawObj.getWidth();
        int height = myDrawObj.getHeight();
        int elementSize = height / NUMBER_OF_TURNS;

        int chosenColor = 0;
        for (int i = 0; i < playField[turn].length; i++) {
            for (int j = 0; j < COLORS.length; j++) {
                if (clickPos[0] >= height && clickPos[1] < elementSize * (j + 1) && clickPos[1] >= elementSize * j) {
                    chosenColor = j + 1;
                } else if (clickPos[0] >= height && clickPos[1] < height && clickPos[1] >= height - elementSize) {
                    chosenColor = 0;
                }

            }
            if (playField[turn][i] == 0) {
                if (i > 0 && chosenColor == 0) {
                    playField[turn][i - 1] = chosenColor;
                }
                if (chosenColor != 0) {
                    boolean newColor = true;
                    for (int j = 0; j < playField[turn].length; j++) {
                        if (chosenColor == playField[turn][j]) {
                            newColor = false;
                            break;
                        }
                    }
                    if (newColor) {
                        playField[turn][i] = chosenColor;
                    }
                }
                break;
            }
        }

        boolean turnComplete = true;
        for (int i = 0; i < playField[turn].length; i++) {
            if (playField[turn][i] == 0) {
                turnComplete = false;
                break;
            }
        }
        if (turnComplete) {
            updateTips();

            boolean gameSolved = true;
            for (int i = 0; i < tips[i].length; i++) {
                if (tips[turn][i] != 1) {
                    gameSolved = false;
                    break;
                }
            }
            if (turn < NUMBER_OF_TURNS) {
                turn++;
            }
            if (gameSolved) {
                drawMessage(myDrawObj, Palette.GREEN, "You WON!!");
                clearBoard(myDrawObj);
            } else if (turn == NUMBER_OF_TURNS) {
                drawMessage(myDrawObj, Palette.RED, "YOU LOST!!");
                clearBoard(myDrawObj);
            }
        }

        drawGame(myDrawObj);

    }

    private static void drawMessage(CodeDraw myDrawObj, Color color, String message) {
        drawGame(myDrawObj);

        int width = myDrawObj.getWidth() / 2;
        int height = myDrawObj.getHeight() / 2;
        int elementSize = height / NUMBER_OF_TURNS;

        myDrawObj.setColor(Color.lightGray);
        myDrawObj.fillRectangle(width / 2., height - elementSize, width, elementSize * 2);
        myDrawObj.setColor(Color.BLACK);
        myDrawObj.drawRectangle(width / 2., height - elementSize, width, elementSize * 2);
        myDrawObj.setColor(color);
        TextFormat textFont = new TextFormat();
        textFont.isBold(true);
        textFont.setFontSize(40);
        textFont.setHorizontalAlign(HorizontalAlign.CENTER);
        textFont.setVerticalAlign(VerticalAlign.MIDDLE);
        myDrawObj.setTextFormat(textFont);
        myDrawObj.drawText(width, height, message);
        myDrawObj.show(5000);
    }

    private static void clearBoard(CodeDraw myDrawObj) {
        if (turn >= 0 && turn <= NUMBER_OF_TURNS && playField != null && tips != null && playField.length == tips.length
                && playField[0].length == tips[0].length) {
            while (turn > 0) {
                turn--;
                for (int i = 0; i < playField[0].length; i++) {
                    playField[turn][i] = 0;
                    tips[turn][i] = 0;
                }
                drawGame(myDrawObj);
                myDrawObj.show(500);
            }
        }
        solution = null;
        initGame(); // not sure if a good spot
    }

    public static void main(String[] args) {
        int height = 800;
        int width = height + height / (COLORS.length + 1);

        CodeDraw myDrawObj = new CodeDraw(width, height);
        myDrawObj.setTitle("MasterMind Game");

        initGame();

        // print solution for testing
        // System.out.println(Arrays.toString(solution));

        drawGame(myDrawObj);
        myDrawObj.onMouseClick(MasterMind::processGameStep);
    }
}
