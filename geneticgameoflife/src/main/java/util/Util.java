package util;

public class Util {


    public static void convert1Dto2D(boolean oneD[], boolean twoD[][]) {
        int index = 0;
        for (int i = 0; i < twoD.length; i++) {
            for (int j = 0; j < twoD[i].length; j++) {
                twoD[i][j] = oneD[index];
                index++;
            }
        }
    }

    public static void convert2Dto1D(boolean twoD[][], boolean oneD[]) {
        int index = 0;
        for (int i = 0; i < twoD.length; i++) {
            for (int j = 0; j < twoD[i].length; j++) {
                oneD[index] = twoD[i][j];
                index++;
            }
        }
    }

}
