package com.lzf.waterpolicy.util;

public class FormatMatch {
	// �ֻ���������ʽ��11λ���֣�
	// 13[0-9]
	// 14[0-9]
	// 15[0-9]
	// 17[013678]
	// 18[0-9]
	private static String phoneRegex1 = "1[3458]\\d\\d{8}";
	private static String phoneRegex2 = "17[013678]\\d{8}";

	public static boolean phone(String phone) {
		if (phone.matches(phoneRegex1) || phone.matches(phoneRegex2)) {
			return true;
		} else {
			return false;
		}
	}

	// IP��ַ������ʽ
	// A���ַ��������������������ŷ�Χ��1.0.0.0---126.0.0.0��127.X.X.X�Ǳ�����ַ������ѭ�������õ�
	// B���ַ����ŷ�Χ��128.0.0.0---191.255.0.0��191.255.255.255�ǹ㲥��ַ�����ܷ��䡣
	// C���ַ����ŷ�Χ��192.0.0.0---223.255.255.0��
	// D���ַ��Χ��224.0.0.0---239.255.255.255
	// E���ַ��Χ��240.0.0.0---255.255.255.254
	// IP λַ��Ϊ xxx.xxx.xxx.xxx ��������̬�����У� xxx Ϊ 1-255 �������
	// �˿ں�ֻ����������Χ�Ǵ�0 ��65535\
	private static String ipRegex = "\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}:\\d{1,5}";

	public static boolean ip(String ip) {
		if (ip.matches(ipRegex)) {
			String[] temp = ip.split("\\.");
			for (String str : temp) {
				if (str.contains(":")) {
					String[] tmp = str.split(":");
					for (int j = 0; j < tmp.length; j++) {
						int k = Integer.parseInt(tmp[j]);
						if (j == 0) {
							if (k < 0 || k > 255) {
								return false;
							}
						} else {
							if (k < 0 || k > 65535) {
								return false;
							}
						}
					}
				} else {
					int i = Integer.parseInt(str);
					if (i < 0 || i > 255) {
						return false;
					}
				}
			}
		} else {
			return false;
		}
		return true;
	}
}
