import java.util.Random;
import java.util.Scanner;

public class Lesson4 {
    public static int SIZE = 3;
    public static int DOTS_TO_WIN = 3;
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
                System.out.println("Победил Искуственный Интеллект");
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
        int rowCase = 0;
        int diag1Case = 0;
        int diag2Case = 0;
        for(int x = 0; x < SIZE; x++){
            for(int y = 0; y < SIZE; y++){
                if(map[x][y] == symb)
                    rowCase++;
                if(x == y && map[x][y] == symb)
                    diag1Case++;
                if(x + y == SIZE - 1 && map[x][y] == symb)
                    diag2Case++;
            }
            if(rowCase == SIZE || diag1Case == SIZE || diag2Case == SIZE)
                return true;
            rowCase = 0;
        }
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
        findAccessiblePaths(myPaths,DOT_O,enemyPaths,DOT_X);
        xy = findBestVariant(myPaths,enemyPaths);
        System.out.println("Компьютер походил в точку " + (xy[0] + 1) + " " + (xy[1] + 1));
        map[xy[1]][xy[0]] = DOT_O;

        /*
        int x, y;
        do {
            x = rand.nextInt(SIZE);
            y = rand.nextInt(SIZE);
        } while (!isCellValid(x, y));
        System.out.println("Компьютер походил в точку " + (x + 1) + " " + (y + 1));
        map[y][x] = DOT_O;*/
    }
    public static int[] findBestVariant(int[] myPaths, int[] enemyPaths){
        //Оцениваем позиции - наши и противника. Если наше положение лучше, работаем на победу, если хуже - на ничью
        int[] retVal = {0,0};
        int myBest = 0, myBestPos = -1;
        int enemyBest = 0, enemyBestPos = -1;
        for(int i = 0; i < SIZE * 2 + 2; i++){
            if(myPaths[i] - enemyPaths[i] >= DOTS_TO_WIN && pathIsOpen(i,DOT_O)) {
                if (myPaths[i] > myBest) {
                    myBest = myPaths[i];
                    myBestPos = i;
                }
            }
            else if(enemyPaths[i] - myPaths[i] >= DOTS_TO_WIN && pathIsOpen(i,DOT_O)){
                if (enemyPaths[i] > enemyBest) {
                    enemyBest = enemyPaths[i];
                    enemyBestPos = i;
                }
            }
        }
        //Сравниваем лучшие позиции
        if(myBest == 0 && myBest == enemyBest){
            retVal[0] = rand.nextInt(SIZE);
            retVal[1] = rand.nextInt(SIZE);
            return retVal;
        }
        else if(myBest >= enemyBest)
            retVal = getFirstFreeCell(myBest,DOT_O);
        else
            retVal = getFirstFreeCell(enemyBest,DOT_O);
        return retVal;
    }

    public static int[] getFirstFreeCell(int pathNo, char symb){
        int[] retVal = {0,0};
        if (pathNo < SIZE){ //Ищем в строке pathNo
            for (int i = 0; i < SIZE; i++){
                if(map[pathNo][i] == DOT_EMPTY){
                    retVal[0] = pathNo;
                    retVal[1] = i;
                    break;
                }
            }
        }
        else if(pathNo < SIZE * 2) { //Ищем в столбце pathNo
            for (int i = 0; i < SIZE; i++) {
                if (map[i][pathNo] == DOT_EMPTY){
                    retVal[0] = i;
                    retVal[1] = pathNo;
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
                    retVal[0] = i;
                    retVal[1] = j;
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
                if (map[i][pathNo] == symb || map[i][pathNo] == DOT_EMPTY)
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
                    myRow[x]++;
                    myCol[y]++;
                }
                else if(map[x][y] == enemySymb){
                    enemyRow[x]++;
                    enemyCol[y]++;
                }
                else { //Пустой символ, пишем в плюс и себе, и противнику
                    myRow[x]++;
                    myCol[y]++;
                    enemyRow[x]++;
                    enemyCol[y]++;
                }
                if(x == y ){
                    if(map[x][y] == mySymb)
                        myDiag1++;
                    else if(map[x][y] == enemySymb)
                        enemyDiag1++;
                    else {
                        myDiag1++;
                        enemyDiag1++;
                    }
                }
                if(x + y == SIZE - 1){
                    if(map[x][y] == mySymb)
                        myDiag2++;
                    else if(map[x][y] == enemySymb)
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
                myPaths[i] = myRow[i];
                enemyPaths[i] = enemyRow[i];
            }
            else if(i >= SIZE && i < SIZE * 2){
                myPaths[i] = myCol[i - SIZE];
                enemyPaths[i] = enemyCol[i - SIZE];

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
                System.out.print(map[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

}
