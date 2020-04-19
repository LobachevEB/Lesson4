import java.lang.reflect.Array;
import java.util.Random;
import java.util.Scanner;

public class Lesson4 {
    public static int SIZE = 5;
    public static int DOTS_TO_WIN = 4;
    public static int LASTIND = SIZE - 1;
    //Общее к-во диагоналей, в которые поместится последовательность из DOTS_TO_WIN клеток
    public static int DIAGQTY = (SIZE - DOTS_TO_WIN) * LASTIND + 2;
    public static final char DOT_EMPTY = '•';
    public static final char DOT_X = 'X';
    public static final char DOT_O = 'O';
    public static char[][] map;
    public static Scanner sc = new Scanner(System.in);
    public static Random rand = new Random();
    public static void main(String[] args) {
        //System.out.println(SIZE % 5);
        //return;
        initMap();
        printMap();
        while (true) {
            humanTurn();
            printMap();
            if (checkWin(DOT_X)) {
                System.out.println("Победил человек");
                break;
            }
            if (isMapFull()) {
                System.out.println("Ничья");
                break;
            }
            aiTurn();
            printMap();
            if (checkWin(DOT_O)) {
                System.out.println("Победил Искуственный Интеллект. Убить всех человеков!");
                break;
            }
            if (isMapFull()) {
                System.out.println("Ничья");
                break;
            }
        }
        System.out.println("Игра закончена");

    }
    public static boolean checkWin(char symb) {
        int[] rowCase = new int[SIZE];
        int colCase = 0;
        int diag1Case = 0;
        int diag2Case = 0;
        for(int x = 0; x < SIZE; x++){
            for(int y = 0; y < SIZE; y++){
                if(map[x][y] == symb) {
                    colCase++;
                    rowCase[y]++;
                }
                if(x == y && map[x][y] == symb)
                    diag1Case++;
                if(x + y == SIZE - 1 && map[x][y] == symb)
                    diag2Case++;
            }
            if(colCase == DOTS_TO_WIN || diag1Case == DOTS_TO_WIN || diag2Case == DOTS_TO_WIN)
                return true;
            colCase = 0;
        }
        for(int i = 0; i < SIZE; i++)
            if(rowCase[i] == DOTS_TO_WIN)
                return true;
        return false;
    }
    public static boolean isMapFull() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (map[i][j] == DOT_EMPTY) return false;
            }
        }
        return true;
    }

    public static void aiTurn() {
        int[] myPaths = new int[SIZE * 2 + 2];
        int[] enemyPaths = new int[SIZE * 2 + 2];
        int[] xy = {0,0};
        findAccessiblePaths(myPaths,DOT_O,enemyPaths,DOT_X); //Оцениваем позиции
        xy = findBestVariant(myPaths,enemyPaths); //Выбираем лучший вариант
        System.out.println("Компьютер походил в точку " + (xy[0] + 1) + " " + (xy[1] + 1));
        map[xy[1]][xy[0]] = DOT_O;
    }

    public static int[] findBestVariant(int[] myPaths, int[] enemyPaths){
        //Оцениваем позиции - наши и противника. Если наше положение лучше, работаем на победу, если хуже - на ничью
        int[] retVal = {0,0};
        int myBest = 0;
        int[] myBestPos = new int[SIZE * 2 + 2];
        int posQty = 0;
        int posToTake;
        int enemyBest = 0, enemyBestPos = -1;
        int myWorth = 0, myWorthPos = 0;
        int enemyWorth = 0, enemyWorthPos = -1;
        for(int i = 0; i < SIZE * 2 + 2; i++){
            if(myPaths[i] >= enemyPaths[i] && pathIsOpen(i,DOT_O)) {
                if (myPaths[i] >= myBest) {
                    myBest = myPaths[i];
                    if(myPaths[i] > myBest && posQty > 0)
                        posQty--;
                    myBestPos[posQty] = i;
                    posQty++;
                }
                if(myPaths[i] - enemyPaths[i] > enemyWorth){
                    enemyWorth = myPaths[i] - enemyPaths[i];
                    enemyWorthPos = i;
                }
            }
            else if(enemyPaths[i] > myPaths[i]  && pathIsOpen(i,DOT_X)){
                if (enemyPaths[i] > enemyBest) {
                    enemyBest = enemyPaths[i];
                    enemyBestPos = i;
                }
                if(enemyPaths[i] - myPaths[i] > myWorth){
                    myWorth = enemyPaths[i] - myPaths[i];
                    myWorthPos = i;
                }
            }
        }
        //Сравниваем лучшие позиции
        if(enemyWorth > 0 && myWorth +1 > enemyWorth)
            retVal = getFirstFreeCell(myWorthPos,DOT_O);
        else if(myBest >= enemyBest) {//По возможности разнообразим ответы на однотипные ходы игрока
            posToTake = rand.nextInt(posQty);
            retVal = getFirstFreeCell(myBestPos[posToTake], DOT_O);
        }
        else
            retVal = getFirstFreeCell(enemyBestPos,DOT_O);
        return retVal;
    }

    public static int[] getFirstFreeCell(int pathNo, char symb){
        int[] retVal = {0,0};
        if (pathNo < SIZE){ //Ищем в строке pathNo
            for (int i = 0; i < SIZE; i++){
                if(map[pathNo][i] == DOT_EMPTY){
                    retVal[0] = i;
                    retVal[1] = pathNo;
                    break;
                }
            }
        }
        else if(pathNo < SIZE * 2) { //Ищем в столбце pathNo
            for (int i = 0; i < SIZE; i++) {
                if (map[i][pathNo - SIZE] == DOT_EMPTY){
                    retVal[0] = pathNo - SIZE;
                    retVal[1] = i;
                    break;
                }
            }
        }
        else if(pathNo == SIZE * 2){ //Ищем в прямой диагонали
            for (int i = 0; i < SIZE; i++) {
                if (map[i][i] == DOT_EMPTY){
                    retVal[0] = i;
                    retVal[1] = i;
                    break;
                }
            }

        }
        else { //Ищем в обратной диагонали
            int j = 0;
            for (int i = SIZE - 1; i >= 0; i--) {
                if (map[i][j] == DOT_EMPTY){
                    retVal[0] = j;
                    retVal[1] = i;
                    break;
                }
                j++;
            }

        }
        return retVal;
    }

    public static boolean pathIsOpen(int pathNo, char symb){
        int metric = 0;
        if (pathNo < SIZE){ //Ищем в строке pathNo
            for (int i = 0; i < SIZE; i++){
                if(map[pathNo][i] == symb || map[pathNo][i] == DOT_EMPTY)
                    metric++;
                else
                    if(metric > 0)
                        break;
            }
        }
        else if(pathNo < SIZE * 2) { //Ищем в столбце pathNo
            for (int i = 0; i < SIZE; i++) {
                if (map[i][pathNo - SIZE] == symb || map[i][pathNo - SIZE] == DOT_EMPTY)
                    metric++;
                else if (metric > 0)
                    break;
            }
        }
        else if(pathNo == SIZE * 2){ //Ищем в прямой диагонали
            for (int i = 0; i < SIZE; i++) {
                if (map[i][i] == symb || map[i][i] == DOT_EMPTY)
                    metric++;
                else if (metric > 0)
                    break;
            }

        }
        else { //Ищем в обратной диагонали
            int j = 0;
            for (int i = SIZE - 1; i >= 0; i--) {
                if (map[i][j] == symb || map[i][j] == DOT_EMPTY)
                    metric++;
                else if (metric > 0)
                    break;
                j++;
            }

        }
        return metric >= DOTS_TO_WIN;
    }

    public static void findAccessiblePaths(int[] myPaths, char mySymb, int[] enemyPaths, char enemySymb){
        //Ищем сколько в каждой строке, столбце, диагонали заполнено элементов нами и противником
        int[] myRow,myCol;
        int myDiag1,myDiag2;
        int[] enemyRow,enemyCol;
        int enemyDiag1,enemyDiag2;
        myRow = new int[SIZE];
        myCol = new int[SIZE];
        enemyRow = new int[SIZE];
        enemyCol = new int[SIZE];
        myDiag1 = 0;
        myDiag2 = 0;
        enemyDiag1 = 0;
        enemyDiag2 = 0;
        for (int x = 0; x < SIZE; x++){
            for (int y = 0; y < SIZE; y++){
                if(map[x][y] == mySymb) {
                    myRow[y]++;
                    myCol[x]++;
                }
                else if(map[x][y] == enemySymb){
                    enemyRow[y]++;
                    enemyCol[x]++;
                }
                else { //Пустой символ, пишем в плюс и себе, и противнику
                    myRow[y]++;
                    myCol[x]++;
                    enemyRow[y]++;
                    enemyCol[x]++;
                }
                if(x == y ){
                    if(map[y][x] == mySymb)
                        myDiag1++;
                    else if(map[y][x] == enemySymb)
                        enemyDiag1++;
                    else {
                        myDiag1++;
                        enemyDiag1++;
                    }
                }
                if(x + y == SIZE - 1){
                    if(map[y][x] == mySymb)
                        myDiag2++;
                    else if(map[y][x] == enemySymb)
                        enemyDiag2++;
                    else {
                        myDiag2++;
                        enemyDiag2++;
                    }
                }

            }
        }
        int len = SIZE * 2 + 2;
        for(int i = 0; i < len; i++){
            if(i < SIZE){
                myPaths[i] = myCol[i ];
                enemyPaths[i] = enemyCol[i ];
            }
            else if(i >= SIZE && i < SIZE * 2){
                myPaths[i] = myRow[i- SIZE];
                enemyPaths[i] = enemyRow[i- SIZE];

            }
            else {
                if(i == SIZE * 2){
                    myPaths[i] = myDiag1;
                    enemyPaths[i] = enemyDiag1;
                }
                else {
                    myPaths[i] = myDiag2;
                    enemyPaths[i] = enemyDiag2;
                }
            }
        }
    }

    public static void humanTurn() {
        int x, y;
        do {
            System.out.println("Введите координаты в формате X Y");
            x = sc.nextInt() - 1;
            y = sc.nextInt() - 1;
        } while (!isCellValid(x, y)); // while(isCellValid(x, y) == false)
        map[y][x] = DOT_X;
    }
    public static boolean isCellValid(int x, int y) {
        if (x < 0 || x >= SIZE || y < 0 || y >= SIZE) return false;
        if (map[y][x] == DOT_EMPTY) return true;
        return false;
    }
    public static void initMap() {
        map = new char[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                map[i][j] = DOT_EMPTY;
            }
        }
    }
    public static void printMap() {
        for (int i = 0; i <= SIZE; i++) {
            System.out.print(i + " ");
        }
        System.out.println();
        for (int i = 0; i < SIZE; i++) {
            System.out.print((i + 1) + " ");
            for (int j = 0; j < SIZE; j++) {
                System.out.print(map[j][i] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

}
