package student;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class WaltzGeneratorTest {
    WaltzGenerator genTest;
    List<String> stringList;

    @BeforeEach
    public void setup() {
        genTest = new WaltzGenerator(23456);
        stringList = List.of(
                "1",
                "1, 2, 3, 4, 5",
                "2, 3, 4, 5, 6",
                "",
                "4, 5, 6, 7, 8",
                "5, 6, 7, 8, 9",
                "6, 7, 8, 9, 0"
        );
    }

    @Test
    public void rollDiceTestWithinBounds() {
        for (int i = 1; i < 8; i++) {
            assertTrue(genTest.rollDice(i) <= i * 6 && genTest.rollDice(i) > 0);
        }
    }

    @Test
    public void rollDiceTestThrowsException() {
        for (int i = -1; i < 1; i++) {
            int n = i;
            assertThrows(IllegalArgumentException.class, () -> genTest.rollDice(n));
        }
    }

    @Test
    public void buildTableTest() {
        for (int i = 1; i < stringList.size(); i++)
            assertArrayEquals(stringList.get(i).split(", "), genTest.buildTable(stringList)[i]);
    }
}