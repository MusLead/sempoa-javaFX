package de.uniks.abacus.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static de.uniks.abacus.Constant.*;

public class AppService {

    final private Random random = new Random();
    private int calculate(int firstVal, char operation, int secondVal){
        int resultVal = 0;
        switch (operation) {
            case '+' -> resultVal = firstVal + secondVal;
            case '-' -> resultVal = firstVal - secondVal;
            case '*' -> resultVal = firstVal * secondVal;
            case '/' -> {
                if(secondVal != 0) {
                    resultVal = firstVal / secondVal;
                } else {
                    throw new ArithmeticException("Prohibited operation: secondValue is 0");
                }
            }
        }

        return resultVal;
    }

    /**
     * Check the result whether the user answer correct or wrong
     * either wrong or correct.
     * the player and the history should increase their
     * sum and total of (wrong or correct).
     * This method should only be used if the
     * right result has been calculated and the result has
     * a player and a history
     *
     * @param result object that fulfill the condition
     */
    private void checkResult(Result result) {

        if(result.getPlayer() == null || result.getHistory() == null){
            throw new IllegalArgumentException("The program is incomplete!");
        } else if (result.getRightVal() == 0){
            throw new IllegalArgumentException("The value is 0 or less than 0, checkDivision usage!");
        }

        boolean isCorrect = result.getRightVal() == result.getResultVal();
        Player currentPlayer = result.getPlayer();
        History currentHistory = result.getHistory();
        if(isCorrect){
            currentPlayer.setRightSum(currentPlayer.getRightSum() + 1);
            currentHistory.setRightResultTotal(currentHistory.getRightResultTotal() + 1);
            result.setResultStatus(CORRECT);
        } else {
            currentPlayer.setWrongSum(currentPlayer.getWrongSum() + 1);
            currentHistory.setWrongResultTotal(currentHistory.getWrongResultTotal() + 1);
            result.setResultStatus(WRONG);
        }
    }

    /**
     * This method find a new task if the current Task has the result of not natural numbers
     * REMEMBER! this return value does not return the value back to the player
     *
     * @param origin      the lowest value for the random
     * @param upperBound  the highest value for the random
     * @param firstValue  value of the first value
     * @param secondValue value of the second value
     * @return value of TEMPORARY RETURN!
     */
    public Result checkDivisionNew(int origin, int upperBound, int firstValue, int secondValue) {

        int firstValNow = 0, secondValNow = 0;
        //https://stackoverflow.com/questions/65678297/solve-integer-division-in-floating-point-context
        double resultDouble = (double) firstValue/secondValue;
        int count = 0;
        while (getDecimalPlaces(resultDouble) != 0) {
            /*
             * we want to find a value for the first and the second that
             * first always bigger than the second.
             * we can see that from the secondVal origin,  we only accept
             * value that always less big than the first.
             * */
            firstValNow = random.nextInt(origin + 1,upperBound);
            assert firstValNow > origin : "firstVal: " + firstValNow + "secondVal: " + secondValNow + " Origin:" + origin;
            secondValNow = random.nextInt(origin, firstValNow);
            resultDouble = (double) firstValNow/secondValNow;
            count++;
        }
        if(count != 0) System.out.println("total loop new value round: " + count);


        // after quit from the loop, return two values!
        return new Result().setResultStatus(TEMP_STATUS)
                .setFirstVal(firstValNow)
                .setSecondVal(secondValNow);

    }

    private static int getDecimalPlaces( double resultDouble ) {
        //https://stackoverflow.com/questions/6264576/number-of-decimal-digits-in-a-double
        String text = Double.toString(Math.abs(resultDouble));
        int integerPlaces = text.indexOf('.');
        int decimalPlaces = text.length() - integerPlaces - 1;
        if(decimalPlaces == 1) {
            resultDouble *= 10;
            decimalPlaces = (resultDouble % 10) == 0 ? 0 : 1;
        }
        return decimalPlaces;
    }


    /**
     * This function only be used after the user gives an Answer
     * We want to know everytime the new results is being created, the results should be able
     * to access either from player or from the history.
     * The origin and bound are for the checkDivision, to find another alternative number
     * if the right result is not within the set of Natural Number
     *
     * @param player    current player
     * @param firstVal  first value
     * @param operation operation
     * @param secondVal second value
     * @return current result
     */
    public Result creatNewResult(Player player, int firstVal, char operation, int secondVal, int userInput) {

        Result result = new Result()
                .setPlayer(player)
                .setFirstVal(firstVal)
                .setSecondVal(secondVal)
                .setOperation(operation)
                .setRightVal(calculate(firstVal,operation,secondVal))
                .setResultVal(userInput);

        int currentHistorySize = player.getHistories().size();
        if(currentHistorySize != 0) {
            //take the last history that is being created and put the result in
            History currentHistory = player.getHistories().get(currentHistorySize - 1);
            currentHistory.withResults(result).setTime(currentTime());
        } else {
            History currentHistory = new History().withResults(result).setTime(currentTime());
            player.withHistories(currentHistory);
        }

        // check the result, that has a Player and also a History, whether  correct or wrong
        try {
            checkResult(result);
        } catch (IllegalArgumentException exception) {
            System.err.println(exception.getMessage());
        }



        return result;
    }

    public static String currentTime() {
        Date date = new Date();
        String currDate = new SimpleDateFormat("MM/dd/yyyy (HH:mm - ").format(date);
        return currDate;
    }
}
