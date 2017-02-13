package com.lzf.waterpolicy.util;

public class FormatMatch {
	// 手机号正则表达式（11位数字）
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

	// IP地址正则表达式
	// A类地址保留给政府机构，网络号范围：1.0.0.0---126.0.0.0；127.X.X.X是保留地址，用做循环测试用的
	// B类地址网络号范围：128.0.0.0---191.255.0.0。191.255.255.255是广播地址，不能分配。
	// C类地址网络号范围：192.0.0.0---223.255.255.0。
	// D类地址范围：224.0.0.0---239.255.255.255
	// E类地址范围：240.0.0.0---255.255.255.254
	// IP 位址仅为 xxx.xxx.xxx.xxx 的资料型态，其中， xxx 为 1-255 间的整数
	// 端口号只有整数，范围是从0 到65535\
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
