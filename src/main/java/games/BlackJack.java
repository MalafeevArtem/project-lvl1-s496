package games;

import java.io.IOException;
import org.slf4j.*;


public class BlackJack {

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(BlackJack.class);

    private static int[] cards;
    private static int cursor;

    private static int[][] playersCards;
    private static int[] playersCursors;

    private static final int MAX_VALUE = 21;
    private static final int MAX_CARDS_COUNT = 8;

    private static final int MIN_VALUE_PLAYER = 11;
    private static final int MIN_VALUE_COMP = 16;

    private static final int PLAYER = 0;
    private static final int COMPUTER = 1;

    private static final int BET = 10;

    private static int[] playersMoney = {100, 100};

    private static int value(int card) {
        switch (CardUtils.getPar(card)) {
            case JACK: return 2;
            case QUEEN: return 3;
            case KING: return 4;
            case SIX: return 6;
            case SEVEN: return 7;
            case EIGHT: return 8;
            case NINE: return 9;
            case TEN: return 10;
            case AVE:
            default: return 11;
        }
    }

    public static void main(String... __) throws IOException {

        do {

            initRound();

            int sumPlayer = getSumPlayer();
            int sumComputer = getSumComputer();

            log.info("Сумма ваших очков - {}, компьютера {} очков", sumPlayer,sumComputer);

            bankCalculation(sumPlayer, sumComputer);

            log.info("У вас {}$, у компьютера {}$ ", playersMoney[PLAYER], playersMoney[COMPUTER]);
        }
        while (!moneyCheck());

        if (playersMoney[PLAYER] > 0)
            log.info("Вы выиграли! Поздравляем!");
        else
            log.info("Вы проиграли. Соболезнуем...");

    }

    private static int getSumComputer() {
        int sumComputer;

        do {
            giveOneCard(COMPUTER);
            sumComputer = getFinalSum(COMPUTER);
        }
        while (sumComputer != 0 && sumComputer < MIN_VALUE_COMP);

        return sumComputer;
    }

    private static int getSumPlayer() throws IOException {
        int sumPlayer;

        do {
            giveOneCard(PLAYER);
            sumPlayer = getFinalSum(PLAYER);
        }
        while (sumPlayer != 0 && sumPlayer < MIN_VALUE_PLAYER || sumPlayer < MAX_VALUE
            && sumPlayer > MIN_VALUE_PLAYER && confirm("Берем еще ?"));

        return sumPlayer;
    }

    private static void giveOneCard(int player) {
            int card = addCard2Player(player);

            if (player == PLAYER)
                log.info("Вам выпала карта: {}", CardUtils.toString(card));
            else
                log.info("Компьютеру выпала карта: {}", CardUtils.toString(card));
    }

    private static void bankCalculation(int sumPlayer, int sumComputer) {
        if (sumPlayer > sumComputer) {
            log.info("Вы выиграли раунд, получаете " + BET + "$");
            playersMoney[PLAYER] += BET;
            playersMoney[COMPUTER] -= BET;
        } else if (sumPlayer == sumComputer) {
            log.info("В это раунде ничья");
        } else {
            log.info("В этом раунде побеждает компьютер");
            playersMoney[PLAYER] -= BET;
            playersMoney[COMPUTER] += BET;
        }
    }

    private static void initRound() {
        log.info("\nУ Вас {}$, у компьютера - {}$. Начинаем новый раунд!", playersMoney[PLAYER], playersMoney[COMPUTER]);
        cards = CardUtils.getShuffledCards();
        playersCards = new int[2][MAX_CARDS_COUNT];
        playersCursors = new int[2];
        cursor = 0;
    }

    private static int addCard2Player(int player) {
        int cardIndex = cards[cursor];
        playersCards[player][playersCursors[player]] = cardIndex;
        cursor++;
        playersCursors[player]++;

        return cardIndex;
    }


    private static int sumPointsPlayer(int player) {
        int sumPoints = 0;

        for (int index = 0; index < playersCursors[player]; index++)
            sumPoints += value(playersCards[player][index]);

        return  sumPoints;
    }

    private static int getFinalSum(int player) {
        int finalSum = sumPointsPlayer(player);

        return finalSum <= MAX_VALUE ? finalSum : 0;
    }

    private static boolean confirm(String message) throws IOException {
        log.info(" {} \"Y\" - Да, {любой другой символ} - нет (Что бы выйти из игры, нажмите Ctrl + C)", message);
        switch (Choice.getCharacterFromUser()) {
            case 'Y':
            case 'y': return true;
            default: return false;
        }
    }

    private static boolean moneyCheck() {
        return playersMoney[PLAYER] - BET < 0 || playersMoney[COMPUTER] - BET < 0;
    }
}
