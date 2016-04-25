package com.dm.utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.dm.utils.pinyin.Pinyin;

/**
 * 
 */
public class StringUtils {

	/**
	 * 将字符串数组用分隔符分割
	 * 
	 * @param strArray
	 *            -数组
	 * @param separator
	 *            -分隔符
	 * @return
	 */
	public static String stringArrayJoin(String[] strArray, String separator) {
		StringBuffer strbuf = new StringBuffer("");
		for (int i = 0; i < strArray.length; i++) {
			strbuf.append(separator).append(strArray[i]);
		}
		return strbuf.deleteCharAt(0).toString();
	}

	public static String deleteRepeat(String value, String separator) {
		if (value == null || value.length() == 0)
			return value;
		String[] array = value.split(separator);
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < array.length; i++) {
			if (array[i] == null || array[i].length() == 0)
				continue;
			if (!list.contains(array[i])) {
				list.add(array[i]);
			}
		}
		array = new String[list.size()];
		return stringArrayJoin(list.toArray(array), separator);
	}

	/**
	 * 判断字符串是否为空
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNull(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否为空 null或者长度为0
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isEmpty(CharSequence str) {
		if (str == null || str.length() == 0)
			return true;
		else
			return false;
	}

	/**
	 * 将汉字转化为首字母形式
	 * 
	 * @param inputString
	 *            汉字字符串
	 * @return 首字母字符串
	 */
	public static String getStringPrefix(String inputString) {
		if (DataValidation.isEmpty(inputString)) {
			return "";
		}
		String result = Pinyin.converterToFirstSpell(inputString);
		return result;
	}

	public static void main(String[] args) {
		System.out.println(getStringPrefix("曾美丽"));
	}

	public static int parseInt(Integer value) {
		if (value == null)
			return 0;
		return value;
	}

	public static long parseLong(Long value) {
		if (value == null)
			return 0;
		return value;
	}

	public static String RandomString(int length) {
		String str = "0123456789";
		Random random = new Random();
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int num = random.nextInt(str.length());
			buf.append(str.charAt(num));
		}
		return buf.toString();
	}

	/**
	 * 仅保留2位小数
	 * 
	 * @param value
	 * @return
	 */
	public static String format2String(double value) {
		DecimalFormat df = new DecimalFormat("0.00");
		Double d = Double.valueOf(value);
		return df.format(d);
	}

	/**
	 * 仅保留2位小数
	 * 
	 * @param value
	 * @return
	 */
	public static Double format2Double(double value) {
		DecimalFormat df = new DecimalFormat("0.00");
		Double d = Double.valueOf(value);

		return Double.valueOf(df.format(d));
	}

	/***
	 * 对比两个字符串是否相同
	 */
	public static boolean isSame(String str1, String str2) {
		if (isEmpty(str1) && isEmpty(str2)) {
			return true;
		} else if (!isEmpty(str1) && !isEmpty(str2)) {
			return str1.equals(str2);
		} else {
			return false;
		}

	}

	/**
	 * @Description: 获取大写数字(一...)
	 * @param @param num
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getUperNumStr(int num) {
		// String u[] = {"零","壹","贰","叁","肆","伍","陆","柒","捌","玖","拾"};
		String u[] = {"〇", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十"};
		String rstr = "";
		if (num <= u.length) {
			rstr = u[num];
		}
		return rstr;
	}

	/**
	 * 
	 * @Description: 将数组转为用逗号分隔的字符串
	 * @param @param oo
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getSplitStrFromArray(Object[] oo) {
		if (null == oo) {
			return "";
		}
		String str = Arrays.toString(oo);
		str = str.replace("[", "").replace("]", "");
		return str;
	}
	/**
	 * 截取字符串
	 * 
	 * @param str
	 * @param length
	 * @return
	 */
	public static String cutString(String str, int length) {
		if (DataValidation.isEmpty(str)) {
			return "";
		}
		if (str.length() >= length) {
			return str.substring(length);
		}
		return str;
	}
}
