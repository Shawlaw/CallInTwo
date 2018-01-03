package github.shawlaw.app.callintwo.utils;

/**
 * 用于帮助开发者在数据结构中使用Int值存储二元属性的工具类
 * Created by Shawlaw on 2016/12/4.
 */

public class BitUtils {
    /**
     * 给指定的int数值的特定位置0或置1
     * @param src 用于存储二元值的int数
     * @param bitMask 指定位的掩码，可通过{@link #getDeterminedBitMask(int)}取得指定位的对应掩码
     * @param bitValue 要给指定位设定的值
     * @return 置值完毕后的int数
     */
    public static int setDeterminedBit(int src, int bitMask, boolean bitValue){
        if (bitValue) {
            src |= bitMask;
        } else {
            src &= (~bitMask);
        }
        return src;
    }

    /**
     * 取得指定的int数值中的特定位上的值
     * @param src 用于存储二元值的int数
     * @param bitMask 指定位的掩码，可通过{@link #getDeterminedBitMask(int)}取得指定位的对应掩码
     * @return 指定位的值是否为1，为1则返回true，为0则返回false
     */
    public static boolean getDeterminedBit(int src, int bitMask){
        return (src & bitMask ) != 0;
    }

    /**
     * 获取int数的指定位的位运算掩码，一般配合{@link #getDeterminedBit(int, int)}或{@link #setDeterminedBit(int, int, boolean)}方法一起使用
     * @param reverseIndex 从最右即最低位为1数起的位数，最大为32，因为int数为4字节最大32位。
     * @return 位运算掩码
     */
    public static int getDeterminedBitMask(int reverseIndex){
        if (reverseIndex > 32 || reverseIndex < 1) {
            throw new RuntimeException("Index must between 1 to 32. The current index is "+reverseIndex);
        }
        return 1 << (reverseIndex - 1);
    }
}
