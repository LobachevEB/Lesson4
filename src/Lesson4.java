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
        int[] rowCaseBad = new int[SIZE];
        int colCaseBad = 0;
        for(int x = 0; x < SIZE; x++){
            for(int y = 0; y < SIZE; y++){
                if(map[x][y] == symb) {
                    colCase++;
                    rowCase[y]++;
                    if(colCase == DOTS_TO_WIN || rowCase[y] == DOTS_TO_WIN)
                        return true;

                }
                else{
                    colCase = 0;
                    rowCase[y] = 0;
                }
            }
            colCase = 0;
        }
        for (int i = 0; i < DIAGQTY; i++){
            int[] coord = {0,0};
            int pos = 0;
            int dots = 0;
            int dotsBad = 0;
            do {
                pos = runDiagonal(i,pos,coord);
                if(pos > -1){
                    int x = coord[0];
                    int y = coord[1];
                    if(map[x][y] == symb) {
                        dots++;
                        if(dots >= DOTS_TO_WIN)
                            return true;
                    }
                    else {
                        dots = 0;
                    }
                }
            }while (pos > -1);
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
        int[] myPaths = new int[SIZE * 2 + DIAGQTY];
        int[] enemyPaths = new int[SIZE * 2 + DIAGQTY];
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
        //int[] myBestPos = new int[SIZE * 2 + DIAGQTY];
        int myBestPos = 0;
        int posQty = 0;
        int posToTake;
        int enemyBest = 0;
        int enemyBestPos = 0;
        int myWorth = 0, myWorthPos = 0;
        int enemyWorth = 0;
        for(int i = 0; i < SIZE * 2 + DIAGQTY; i++){
            int j = i;
                if (myPaths[i] >= enemyPaths[j] && pathIsOpen(i, DOT_O)) {
                    if (myPaths[i] > myBest) {
                        myBest = myPaths[i];
                        //if (myPaths[i] > myBest && posQty > 0)
                        //    posQty--;
                        //myBestPos[posQty] = i;
                        myBestPos = i;
                        //posQty++;
                    }
                    if (myPaths[i] - enemyPaths[i] > enemyWorth) {
                        enemyWorth = myPaths[i] - enemyPaths[i];
                    }
                } else if (enemyPaths[j] > myPaths[i] && pathIsOpen(j, DOT_X)) {
                    if (enemyPaths[j] > enemyBest) {
                        enemyBest = enemyPaths[j];
                        enemyBestPos = j;
                    }
                    if (enemyPaths[j] - myPaths[i] > myWorth) {
                        myWorth = enemyPaths[j] - myPaths[i];
                        myWorthPos = i;
                    }
                }
        }
        //Сравниваем лучшие позиции
        if(DOTS_TO_WIN - myBest == 1)
            retVal = getFirstFreeCell(myBestPos,DOT_X);
        else if(DOTS_TO_WIN - enemyBest == 1)
            retVal = getFirstFreeCell(enemyBestPos,DOT_X);
        else {
            if(myBest > 0)
                retVal = getFirstFreeCell(myBestPos,DOT_X);
            else {
                do{
                    retVal = getFirstFreeCell(rand.nextInt(SIZE * 2 + DIAGQTY),DOT_X);
                } while (!isCellValid(retVal[0], retVal[1]));
            }
        }
        return retVal;
    }

    public static int[] getFirstFreeCell(int pathNo, char symb){
        int[] retVal = {0,0};
        int lastGoodI = -1, lastBadI = -1;
        int lastGoodX = -1, lastBadX = -1;
        int lastGoodY = -1, lastBadY = -1;
        if (pathNo < SIZE){ //Ищем в строке pathNo
            retVal[1] = pathNo;
            for (int i = 0; i < SIZE; i++){
                if(map[pathNo][i] == DOT_EMPTY){
                    if(lastBadI > -1) {
                        retVal[0] = i;
                        retVal[1] = pathNo;
                        break;
                    }
                    lastGoodI = i;
                }
                else {
                    lastBadI = i;
                    if(lastGoodI > -1){
                        retVal[0] = lastGoodI;
                        retVal[1] = pathNo;
                        break;
                    }
                }
            }
        }
        else if(pathNo < SIZE * 2) { //Ищем в столбце pathNo
            retVal[0] = pathNo - SIZE;
            for (int i = 0; i < SIZE; i++) {
                if (map[i][pathNo - SIZE] == DOT_EMPTY){
                    if(lastBadI > -1) {
                        retVal[0] = pathNo - SIZE;
                        retVal[1] = i;
                        break;
                    }
                    lastGoodI = i;
                }
                else {
                    lastBadI = i;
                    if(lastGoodI > -1){
                        retVal[0] = pathNo - SIZE;
                        retVal[1] = lastGoodI;
                        break;
                    }
                }
            }
        }
        else{
            int[] coord = {0, 0};
            int pos = 0;
            int i = pathNo - SIZE * 2;
            do {
                pos = runDiagonal(i,pos,coord);
                if(pos > -1){
                    int x = coord[0];
                    int y = coord[1];
                    if(map[x][y] == DOT_EMPTY){
                        if(lastBadX > -1 && lastBadY > -1) {
                            retVal[0] = y;
                            retVal[1] = x;
                            return retVal;
                        }
                        lastGoodX = x;
                        lastGoodY = y;
                    }
                    else {
                        lastBadX = x;
                        lastBadY = y;
                        if(lastGoodX > -1 && lastGoodY > -1){
                            retVal[0] = lastGoodY;
                            retVal[1] = lastGoodX;
                            return retVal;
                        }
                    }
                }
            } while (pos > -1);
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
        else {
            int[] coord = {0, 0};
            int pos = 0;
            int i = pathNo - SIZE * 2;
            do {
                pos = runDiagonal(i,pos,coord);
                if(pos > -1){
                    int x = coord[0];
                    int y = coord[1];
                    if (map[y][x] == symb || map[y][x] == DOT_EMPTY)
                        metric++;
                    else if (metric > 0)
                        return metric >= DOTS_TO_WIN;
                }
            } while (pos > -1);

        }
        return metric >= DOTS_TO_WIN;
    }

    public static void findAccessiblePaths(int[] myPaths, char mySymb, int[] enemyPaths, char enemySymb){
        //Ищем сколько в каждой строке, столбце, диагонали заполнено элементов нами и противником
        int[] myRow,myCol;
        int[] myDiags = new int[DIAGQTY];
        int[] enemyRow,enemyCol;
        int[] enemyDiags = new int[DIAGQTY];
        myRow = new int[SIZE];
        myCol = new int[SIZE];
        enemyRow = new int[SIZE];
        enemyCol = new int[SIZE];
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
            }
        }
        for (int i = 0; i < DIAGQTY; i++) {
            int[] coord = {0, 0};
            int pos = 0;
            do {
                pos = runDiagonal(i, pos, coord);
                if(pos > -1){
                    int x = coord[0];
                    int y = coord[1];
                    if(map[y][x] == mySymb)
                        myDiags[i]++;
                    else if(map[y][x] == enemySymb)
                        enemyDiags[i]++;
                }
            } while (pos > -1);
        }

        int len = SIZE * 2;
        for(int i = 0; i < len; i++){
            if(i < SIZE){
                myPaths[i] = myCol[i ];
                enemyPaths[i] = enemyCol[i ];
            }
            else if(i >= SIZE && i < SIZE * 2){
                myPaths[i] = myRow[i- SIZE];
                enemyPaths[i] = enemyRow[i- SIZE];

            }
        }
        for (int i = 0; i < DIAGQTY; i++){
            myPaths[len + i] = myDiags[i];
            enemyPaths[len + i] = enemyDiags[i];
        }
    }

    public static int runDiagonal(int diagNo,int pos, int[] coord){
        int x = 0, y = 0;
        int n = 0;

        if(diagNo < DIAGQTY/2){
            for(int i = 0; i <= diagNo; i++){
                if(i % 2 != 0) {
                    x = 0;
                    y = n;
                    n++;
                }
                else {
                    x = i/2;
                    y = 0;
                    n++;
                }
            }
        }
        else {
            x = SIZE - 1;
            y = 0;
            for (int i = DIAGQTY/2; i < diagNo; i++){
                if(i % 2 == 0){
                    n++;
                    x = LASTIND - n;
                    y = 0;
                }
                else{
                    x = LASTIND;
                    y =  n;
                }
            }
        }
        for (int i = 0; i < pos; i++){
            if(diagNo < DIAGQTY/2) {
                x++;
                y++;
            }
            else{
                x--;
                y++;
            }

        }
        if(x > LASTIND || y > LASTIND || x < 0 || y < 0)
            return -1;
        coord[0] = x;
        coord[1] = y;
        return ++pos;

    }

    public static void humanTurn() {
        int x, y;
        do {
            System.out.println("Введите координаты в формате X Y");
            x = sc.nextInt() - 1;
            y = sc.nextInt() - 1;
        } while (!isCellValid(x, y));
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
